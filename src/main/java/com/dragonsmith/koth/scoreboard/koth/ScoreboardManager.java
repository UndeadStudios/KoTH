package com.dragonsmith.koth.scoreboard.koth;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import com.dragonsmith.koth.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager {
   private static final HashMap<UUID, ScoreboardManager> players = new HashMap<>();
   private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
   private final Objective sidebar = this.scoreboard.registerNewObjective("sidebar", "dummy");

   public static boolean hasScore(Player player) {
      return players.containsKey(player.getUniqueId());
   }

   public static ScoreboardManager createScore(Player player) {
      return new ScoreboardManager(player);
   }

   public static ScoreboardManager getByPlayer(Player player) {
      return players.get(player.getUniqueId());
   }

   public static ScoreboardManager removeScore(Player player) {
      return players.remove(player.getUniqueId());
   }

   private ScoreboardManager(Player player) {
      this.sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);

      for(int i = 0; i <= 14; ++i) {
         Team team = this.scoreboard.registerNewTeam("KatyLIBSB_" + i);
         team.addEntry(this.genEntry(i));
      }

      player.setScoreboard(this.scoreboard);
      players.put(player.getUniqueId(), this);
   }

   public void setTitle(String title) {
      title = Util.color(title);
      this.sidebar.setDisplayName(title.length() > 32 ? title.substring(0, 32) : title);
   }

   public void setSlot(int slot, String text) {
      Team team = this.scoreboard.getTeam("KatyLIBSB_" + slot);
      String entry = this.genEntry(slot);
      if (!this.scoreboard.getEntries().contains(entry)) {
         this.sidebar.getScore(entry).setScore(slot);
      }

      text = Util.color(text);
      String pre = this.getFirstSplit(text);
      String suf = this.getFirstSplit(ChatColor.getLastColors(pre) + this.getSecondSplit(text));
      team.setPrefix(pre);
      team.setSuffix(suf);
   }

   public void removeSlot(int slot) {
      String entry = this.genEntry(slot);
      if (this.scoreboard.getEntries().contains(entry)) {
         this.scoreboard.resetScores(entry);
      }
   }

   public void setSlotsFromList(List<String> list) {
      while(list.size() > 15) {
         list.remove(list.size() - 1);
      }

      int slot = list.size();
      if (slot < 15) {
         for(int i = slot + 1; i <= 15; ++i) {
            this.removeSlot(i);
         }
      }

      for(String line : list) {
         this.setSlot(slot, line);
         --slot;
      }
   }

   public static void disable(Player player) {
      if (getByPlayer(player) != null) {
         removeScore(player);
         Scoreboard nullBoard = Bukkit.getScoreboardManager().getNewScoreboard();
         player.setScoreboard(nullBoard);
      }
   }

   public static void disableAllScoreboards() {
      for(Player player : Bukkit.getOnlinePlayers()) {
         disable(player);
      }

      players.clear();
   }

   private String genEntry(int slot) {
      return ChatColor.values()[slot].toString();
   }

   private String getFirstSplit(String s) {
      return s.length() > 16 ? s.substring(0, 16) : s;
   }

   private String getSecondSplit(String s) {
      if (s.length() > 32) {
         s = s.substring(0, 32);
      }

      return s.length() > 16 ? s.substring(16) : "";
   }
}
