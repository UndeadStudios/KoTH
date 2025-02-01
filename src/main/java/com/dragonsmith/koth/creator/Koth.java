package com.dragonsmith.koth.creator;

import com.dragonsmith.koth.KoTHPlugin;
import com.dragonsmith.koth.manager.koth.KothManager;
import com.dragonsmith.koth.playeable.CurrentKoth;
import com.dragonsmith.koth.util.Config;
import com.dragonsmith.koth.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Koth {
   private final String displayName;
   private final String id;
   private final Location pos1;
   private final Location pos2;
   private final Location centerLocation;

   public Koth(String id, String displayName, Location pos1, Location pos2) {
      this.id = id;
      this.displayName = displayName;
      this.pos1 = pos1;
      this.pos2 = pos2;
      this.centerLocation = Util.getCenterFrom(pos1, pos2);
   }

   public String getId() {
      return this.id;
   }

   public String getDisplayName() {
      return Util.color(this.displayName);
   }

   public Location getPos1() {
      return this.pos1;
   }

   public Location getPos2() {
      return this.pos2;
   }

   public Location getCenterLocation() {
      return this.centerLocation;
   }

   public void start() {
      KoTHPlugin plugin = KoTHPlugin.getInstance();
      KothManager kothManager = plugin.getKothManager();
      kothManager.setCurrectKoth(this);
      CurrentKoth currentKoth = kothManager.getCurrectKoth();
      currentKoth.getTask().runTaskTimer(KoTHPlugin.getInstance(), 0L, 5L);
      plugin.getScoreboardHook().update(currentKoth);
      Bukkit.broadcastMessage(
         Config.getMessage("koth-started")
            .replaceAll("\\{name}", this.getDisplayName())
            .replaceAll("\\{world}", this.centerLocation.getWorld().getName())
            .replaceAll("\\{x}", (int)this.centerLocation.getX() + "")
            .replaceAll("\\{y}", (int)this.centerLocation.getY() + "")
            .replaceAll("\\{z}", (int)this.centerLocation.getZ() + "")
            .replaceAll("\\{time_left}", currentKoth.getFormattedTimeLeft())
      );
   }
}
