package com.dragonsmith.koth.schedule;

import com.dragonsmith.koth.KoTHPlugin;
import com.dragonsmith.koth.creator.Koth;
import com.dragonsmith.koth.util.Config;
import org.bukkit.configuration.file.FileConfiguration;

public class KothSchedule {
   private final String kothId;
   private final long start;

   public KothSchedule(String kothId, long millisStart) {
      this.kothId = kothId;
      this.start = millisStart;
   }

   public Koth getKoth() {
      return KoTHPlugin.getInstance().getKothManager().getKothByID(this.kothId);
   }

   public long getStartMillis() {
      return this.start;
   }

   public String getFormattedTimeLeft() {
      long difference = this.getStartMillis() - System.currentTimeMillis();
      long seconds = difference / 1000L % 60L;
      long minutes = difference / 60000L % 60L;
      long hours = difference / 3600000L % 24L;
      long days = difference / 86400000L;
      StringBuilder builder = new StringBuilder();
      FileConfiguration config = Config.getConfig();
      if (days > 0L) {
         builder.append(days).append(days == 1L ? config.getString("next-koth-time-format.day") : config.getString("next-koth-time-format.days"));
      }

      if (hours > 0L) {
         builder.append(hours).append(hours == 1L ? config.getString("next-koth-time-format.hour") : config.getString("next-koth-time-format.hours"));
      }

      if (minutes > 0L) {
         builder.append(minutes).append(minutes == 1L ? config.getString("next-koth-time-format.minute") : config.getString("next-koth-time-format.minutes"));
      }

      if (seconds > 0L) {
         builder.append(seconds).append(seconds == 1L ? config.getString("next-koth-time-format.second") : config.getString("next-koth-time-format.seconds"));
      }

      return builder.toString();
   }
}
