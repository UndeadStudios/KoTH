package com.dragonsmith.koth.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import com.dragonsmith.koth.KoTHPlugin;
import com.dragonsmith.koth.playeable.CurrentKoth;
import com.dragonsmith.koth.schedule.ScheduleManager;
import com.dragonsmith.koth.util.Config;
import com.dragonsmith.koth.util.Util;
import org.bukkit.OfflinePlayer;

public class KoTHPlaceholder extends PlaceholderExpansion {
   public String getIdentifier() {
      return "koth";
   }

   public String getAuthor() {
      return "MattyHD0";
   }

   public String getVersion() {
      return "1.0";
   }

   public String onRequest(OfflinePlayer player, String params) {
      KoTHPlugin plugin = KoTHPlugin.getInstance();
      CurrentKoth currentKoth = plugin.getKothManager().getCurrectKoth();
      ScheduleManager scheduleManager = plugin.getScheduleManager();
      switch(params) {
         case "current_name":
            return currentKoth == null ? "" : currentKoth.getDisplayName();
         case "current_world":
            return currentKoth == null ? "" : currentKoth.getCenterLocation().getWorld().getName();
         case "current_x":
            return currentKoth == null ? "" : String.valueOf(currentKoth.getCenterLocation().getX());
         case "current_y":
            return currentKoth == null ? "" : String.valueOf(currentKoth.getCenterLocation().getY());
         case "current_z":
            return currentKoth == null ? "" : String.valueOf(currentKoth.getCenterLocation().getZ());
         case "current_time_left":
            return currentKoth == null ? "" : currentKoth.getFormattedTimeLeft();
         case "current_king":
            return currentKoth == null
               ? ""
               : (
                  currentKoth.getKingFaction() != null
                     ? currentKoth.getKingFaction().getTag()
                     : Util.color(Config.getConfig().getString("koth-in-progress.king-null-placeholder"))
               );
         case "schedule_next_name":
            return scheduleManager.getNextKothSchedule() == null ? "" : scheduleManager.getNextKothSchedule().getKoth().getDisplayName();
         case "schedule_next_time":
            return scheduleManager.getNextKothSchedule() == null ? "" : scheduleManager.getNextKothSchedule().getFormattedTimeLeft();
         default:
            return "";
      }
   }
}
