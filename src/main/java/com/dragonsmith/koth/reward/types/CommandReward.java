package com.dragonsmith.koth.reward.types;

import com.dragonsmith.koth.reward.api.Reward;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandReward implements Reward {
   private final String id;
   private final String command;
   private final double chances;

   public CommandReward(String id, double chances, String command) {
      this.id = id;
      this.command = command;
      this.chances = chances;
   }

   @Override
   public String getId() {
      return this.id;
   }

   @Override
   public double getChances() {
      return this.chances;
   }

   @Override
   public Object getReward() {
      return this.command;
   }

   @Override
   public void give(Player player) {
      Server server = Bukkit.getServer();
      ConsoleCommandSender console = server.getConsoleSender();
      double r = Math.random() * 100.0;
      if (this.chances >= r) {
         server.dispatchCommand(console, this.command.replaceAll("\\{player}", player.getName()).replaceAll("%player%", player.getName()));
      }
   }
}
