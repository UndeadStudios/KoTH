package com.dragonsmith.koth.playeable;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;

import java.util.HashMap;
import java.util.Map;
import com.dragonsmith.koth.KoTHPlugin;
import com.dragonsmith.koth.creator.Koth;
import com.dragonsmith.koth.manager.reward.RewardManager;
import com.dragonsmith.koth.misc.WinEffect;
import com.dragonsmith.koth.reward.api.Reward;
import com.dragonsmith.koth.schedule.MillisUtil;
import com.dragonsmith.koth.scoreboard.hook.ScoreboardHook;
import com.dragonsmith.koth.util.Config;
import com.dragonsmith.koth.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CurrentKoth extends Koth {
   private final int defaultBroadcastInterval;
   private int lastBroadcast;
   private Faction kingFaction;
   private final long duration;
   private long startMillis = System.currentTimeMillis();
   private long endMillis;
   private final CurrentKoth.Task task;

   public CurrentKoth(Koth koth, long timeMillis) {
      super(koth.getId(), koth.getDisplayName(), koth.getPos1(), koth.getPos2());
      this.lastBroadcast = -1;
      this.defaultBroadcastInterval = Config.getConfig().getInt("koth-in-progress.broadcast-every");
      this.startMillis = System.currentTimeMillis();
      this.endMillis = System.currentTimeMillis() + timeMillis + 999L;
      this.duration = timeMillis;
      KoTHPlugin.getInstance().getScoreboardHook().onKothStart(this);
      this.task = new CurrentKoth.Task();
   }

   public CurrentKoth(Koth koth) {
      this(koth, (long)Config.getConfig().getInt("koth-duration") * MillisUtil.SECOND);
   }

   public void stop() {
      KoTHPlugin plugin = KoTHPlugin.getInstance();
      CurrentKoth currentKoth = plugin.getKothManager().getCurrectKoth();
      plugin.getScoreboardHook().onKothEnd(currentKoth);
      plugin.getKothManager().setCurrectKoth(null);
   }

   public long getTimeLeftMillis() {
      return this.endMillis - System.currentTimeMillis();
   }

   public int getBroadcastInterval() {
      return this.defaultBroadcastInterval;
   }

   public long getStartMillis() {
      return this.startMillis;
   }

   public Faction getKingFaction() {
      return this.kingFaction;
   }

   public CurrentKoth.Task getTask() {
      return this.task;
   }

   public void update() {
      Map<Faction, Integer> factionPresence = new HashMap<>();
      for (Player player : Bukkit.getOnlinePlayers()) {
         if (Util.locationIsInZone(player.getLocation(), this.getPos1(), this.getPos2()) && !player.isDead()) {
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            if (fPlayer.hasFaction()) {
               Faction faction = fPlayer.getFaction();
               factionPresence.put(faction, factionPresence.getOrDefault(faction, 0) + 1);
            }
         }
      }

      Faction leadingFaction = null;
      int maxCount = 0;
      for (Map.Entry<Faction, Integer> entry : factionPresence.entrySet()) {
         if (entry.getValue() > maxCount) {
            maxCount = entry.getValue();
            leadingFaction = entry.getKey();
         }
      }

      if (leadingFaction != null && !leadingFaction.equals(this.kingFaction)) {
         this.kingFaction = leadingFaction;
         if (Config.getConfig().getBoolean("reset-time-on-king-change")) {
            this.endMillis = System.currentTimeMillis() + this.duration;
         }

         // Debugging statement
         Bukkit.getLogger().info("King faction changed to: " + this.kingFaction.getTag());

         // Update scoreboard or any other affected systems

      }
   }


   public String getFormattedTimeLeft() {
      long difference = this.endMillis - System.currentTimeMillis();
      long seconds = difference / 1000L % 60L;
      long minutes = difference / 60000L % 60L;
      return Config.getMessage("time-left-format")
              .replaceAll("\\{minutes}", minutes + "")
              .replaceAll("\\{seconds}", seconds + "");
   }

   public static class Task extends BukkitRunnable {
      private final ScoreboardHook scoreboardHook = KoTHPlugin.getInstance().getScoreboardHook();
      private final RewardManager rewardManager = KoTHPlugin.getInstance().getRewardManager();
      private CurrentKoth currentKoth = KoTHPlugin.getInstance().getKothManager().getCurrectKoth();

      public void run() {
         this.currentKoth = KoTHPlugin.getInstance().getKothManager().getCurrectKoth();
         if (this.currentKoth == null) {
            this.cancel();
         } else {
            this.scoreboardHook.update(this.currentKoth);
            this.currentKoth.update();
            long calc = (System.currentTimeMillis() - this.currentKoth.getStartMillis()) / MillisUtil.SECOND;
            if (calc % (long)this.currentKoth.getBroadcastInterval() == 0L && calc != (long)this.currentKoth.lastBroadcast) {
               this.broadcast();
               this.currentKoth.lastBroadcast = (int)calc;
            }
            if (this.currentKoth.getTimeLeftMillis() <= 0L) {
               this.end();
            }
         }
      }

      private void end() {
         Faction winnerFaction = this.currentKoth.getKingFaction();
         String message = winnerFaction == null ? Config.getMessage("koth-finised.without-winner") : Config.getMessage("koth-finised.with-winner");
         message = message.replaceAll("\\{name}", this.currentKoth.getDisplayName());

         if (winnerFaction != null) {
            message = message.replaceAll("\\{player}", winnerFaction.getTag());
            for (FPlayer mPlayer : FPlayers.getInstance().getOnlinePlayers()) {
               Player winner = mPlayer.getPlayer();
               if (winner != null) {
                  if (Config.getConfig().getBoolean("koth-finish.winner-fireworks")) {
                     WinEffect.apply(winner);
                  }
                  for (Reward reward : this.rewardManager.getAllRewards()) {
                     reward.give(winner);
                  }
               }
            }
         }

         Bukkit.broadcastMessage(message);
         this.currentKoth.stop();
      }

      private void broadcast() {
         String message = this.currentKoth.getKingFaction() == null
                 ? Config.getMessage("koth-in-progress.without-king")
                 : Config.getMessage("koth-in-progress.with-king");
         Location kothLocation = this.currentKoth.getCenterLocation();
         message = message.replaceAll("\\{name}", this.currentKoth.getDisplayName())
                 .replaceAll("\\{world}", kothLocation.getWorld().getName())
                 .replaceAll("\\{x}", (int)kothLocation.getX() + "")
                 .replaceAll("\\{y}", (int)kothLocation.getY() + "")
                 .replaceAll("\\{z}", (int)kothLocation.getZ() + "")
                 .replaceAll("\\{time_left}", this.currentKoth.getFormattedTimeLeft());

         if (this.currentKoth.getKingFaction() != null) {
            message = message.replaceAll("\\{player}", this.currentKoth.getKingFaction().getTag());
         }

         Bukkit.broadcastMessage(message);
      }
   }
}
