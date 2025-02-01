package com.dragonsmith.koth.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import com.dragonsmith.koth.KoTHPlugin;
import com.dragonsmith.koth.manager.koth.KothManager;
import com.dragonsmith.koth.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class ScheduleManager {
   private static final String[] days = new String[]{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
   private final long initTime = System.currentTimeMillis();
   private final LinkedList<KothSchedule> incomingKoths;

   public ScheduleManager() {
      FileConfiguration scheduleConfig = Config.getScheduleFile().get();
      String timeZone = scheduleConfig.getString("options.timezone");
      this.incomingKoths = new LinkedList<>();
      LocalDate localDate = LocalDate.now();
      LocalDateTime localDateTime = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 0, 0);
      ZonedDateTime zonedDateTime = localDateTime.atZone(timeZone.equals("DEFAULT") ? ZoneId.systemDefault() : ZoneId.of(timeZone));
      long currentMillis = zonedDateTime.toInstant().toEpochMilli();
      FileConfiguration fileConfiguration = Config.getScheduleFile().get();

      for(int i = 0; i < 7; ++i) {
         Date date = new Date(zonedDateTime.toInstant().toEpochMilli() + MillisUtil.DAY * (long)i);
         ZonedDateTime zdt = date.toInstant().atZone(zonedDateTime.getZone());
         String dayString = days[zdt.getDayOfWeek().getValue() - 1];

         for(String schedule : fileConfiguration.getStringList("schedule." + dayString)) {
            String[] data = schedule.split(" ");
            String[] timeStr = data[0].split(":");
            int hour = Integer.parseInt(timeStr[0]);
            int minute = Integer.parseInt(timeStr[1]);
            long startHourMillis = (long)hour * MillisUtil.HOUR;
            long startMinuteMillis = (long)minute * MillisUtil.MINUTE;
            long millis = currentMillis + MillisUtil.DAY * (long)i;
            KothSchedule kothSchedule = new KothSchedule(data[1], millis + startHourMillis + startMinuteMillis);
            if (kothSchedule.getStartMillis() > System.currentTimeMillis()) {
               this.incomingKoths.add(kothSchedule);
            }
         }
      }
   }

   public KothSchedule getNextKothSchedule() {
      return this.incomingKoths.size() == 0 ? null : this.incomingKoths.getFirst();
   }

   public List<KothSchedule> getIncomingKoths() {
      return this.incomingKoths;
   }

   public static class Task extends BukkitRunnable {
      private final KoTHPlugin plugin = KoTHPlugin.getInstance();
      private final KothManager kothManager = this.plugin.getKothManager();
      private final ScheduleManager scheduleManager = this.plugin.getScheduleManager();

      public void run() {
         KothSchedule kothSchedule = this.scheduleManager.getNextKothSchedule();
         if (kothSchedule != null) {
            if (System.currentTimeMillis() > this.scheduleManager.initTime + MillisUtil.DAY) {
               KoTHPlugin.getInstance().setScheduleManager(new ScheduleManager());
            }

            if (System.currentTimeMillis() > kothSchedule.getStartMillis() && this.kothManager.getCurrectKoth() == null) {
               if (Bukkit.getOnlinePlayers().size() >= Config.getScheduleFile().get().getInt("options.minimum-players")) {
                  kothSchedule.getKoth().start();
               }

               this.scheduleManager.incomingKoths.removeFirst();
            }
         }
      }
   }
}
