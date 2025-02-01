package com.dragonsmith.koth.scoreboard.hook.plugin;

import com.massivecraft.factions.Faction;
import me.clip.placeholderapi.PlaceholderAPI;
import com.dragonsmith.koth.KoTHPlugin;
import com.dragonsmith.koth.playeable.CurrentKoth;
import com.dragonsmith.koth.scoreboard.hook.ScoreboardHook;
import com.dragonsmith.koth.scoreboard.koth.ScoreboardManager;
import com.dragonsmith.koth.util.Config;
import com.dragonsmith.koth.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class KoTHScoreboardHook extends ScoreboardHook {
   boolean papiSupport = KoTHPlugin.getInstance().hasSupport("PlaceholderAPI");

   @Override
   public String getHookName() {
      return "KoTHScoreboard";
   }

   @Override
   public void onKothStart(CurrentKoth currentKoth) {
   }

   @Override
   public void onKothEnd(CurrentKoth currentKoth) {
      ScoreboardManager.disableAllScoreboards();
   }

   @Override
   public void update(CurrentKoth currentKoth) {
      CurrentKoth koth = KoTHPlugin.getInstance().getKothManager().getCurrectKoth();
      if (koth != null) {
         FileConfiguration config = Config.getConfig();

         for(Player player : Bukkit.getOnlinePlayers()) {
            Location kothLoc = koth.getCenterLocation();
            ScoreboardManager manager = ScoreboardManager.getByPlayer(player);
            if (manager == null) {
               manager = ScoreboardManager.createScore(player);
            }

            String title = config.getString("scoreboard.title");
            if (this.papiSupport) {
               title = PlaceholderAPI.setPlaceholders(player, title);
            }

            manager.setTitle(title);
            int index = 14;

            for(String line : config.getStringList("scoreboard.body")) {
               line = line.replaceAll("\\{world}", kothLoc.getWorld().getName())
                  .replaceAll("\\{x}", (int)kothLoc.getX() + "")
                  .replaceAll("\\{y}", (int)kothLoc.getY() + "")
                  .replaceAll("\\{z}", (int)kothLoc.getZ() + "")
                  .replaceAll("\\{koth_name}", koth.getDisplayName())
                  .replaceAll("\\{time_left}", koth.getFormattedTimeLeft());
               Faction king = koth.getKingFaction();
               if (king != null) {
                  line = line.replaceAll("\\{king}", king.getTag());
               } else {
                  line = line.replaceAll("\\{king}", Util.color(Config.getConfig().getString("koth-in-progress.king-null-placeholder")));
               }

               if (this.papiSupport) {
                  line = PlaceholderAPI.setPlaceholders(player, line);
               }

               manager.setSlot(index, line);
               --index;
            }
         }
      }
   }
}
