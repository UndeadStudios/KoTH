package com.dragonsmith.koth.creator.selection;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class KothSelection {
   private static final Map<Player, Location> pos1 = new HashMap();
   private static final Map<Player, Location> pos2 = new HashMap();

   public static void setPos1(Player player, Location location) {
      pos1.put(player, location);
   }

   public static void setPos2(Player player, Location location) {
      pos2.put(player, location);
   }

   public static Location getPos1(Player player) {
      return (Location)pos1.get(player);
   }

   public static Location getPos2(Player player) {
      return (Location)pos2.get(player);
   }
}
