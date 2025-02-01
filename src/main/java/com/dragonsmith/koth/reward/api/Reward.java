package com.dragonsmith.koth.reward.api;

import org.bukkit.entity.Player;

public interface Reward {
   double chances = 100.0;

   void give(Player var1);

   String getId();

   double getChances();

   Object getReward();
}
