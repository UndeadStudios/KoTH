package com.dragonsmith.koth.builders;

import org.bukkit.Location;

public class KothBuilder {
   String name;
   String id;
   Location pos1;
   Location pos2;

   public KothBuilder setId(String id) {
      this.id = id;
      return this;
   }

   public KothBuilder setName(String name) {
      this.name = name;
      return this;
   }

   public KothBuilder setPos1(Location pos1) {
      this.pos1 = pos1;
      return this;
   }

   public KothBuilder setPos2(Location pos2) {
      this.pos2 = pos2;
      return this;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Location getPos1() {
      return this.pos1;
   }

   public Location getPos2() {
      return this.pos2;
   }
}
