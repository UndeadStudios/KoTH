package com.dragonsmith.koth.scoreboard.koth;

import com.dragonsmith.koth.KoTHPlugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListener implements Listener {
   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      KoTHPlugin plugin = KoTHPlugin.getInstance();
   }

   @EventHandler
   public void onPlayerLeave(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      if (ScoreboardManager.hasScore(player)) {
         ScoreboardManager.removeScore(player);
      }
   }
}
