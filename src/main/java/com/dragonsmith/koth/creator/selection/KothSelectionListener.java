package com.dragonsmith.koth.creator.selection;

import java.text.MessageFormat;
import com.dragonsmith.koth.KoTHPlugin;
import com.dragonsmith.koth.util.Config;
import com.dragonsmith.koth.util.Util;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class KothSelectionListener implements Listener {
   @EventHandler
   public void onRightClick(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      KoTHPlugin instance = KoTHPlugin.getInstance();
      if (player.hasPermission("koth.selection")
         && player.getItemInHand().getItemMeta() != null
         && player.getItemInHand().getItemMeta().equals(instance.getSelectionWandItem().getItemMeta())) {
         Action action = event.getAction();
         if (event.getClickedBlock() != null) {
            Location blockLocation = event.getClickedBlock().getLocation();
            player.sendMessage(
               MessageFormat.format(
                  "{0} {1} {2}",
                  event.getClickedBlock().getLocation().getX(),
                  event.getClickedBlock().getLocation().getY(),
                  event.getClickedBlock().getLocation().getZ()
               )
            );
            if (action == Action.LEFT_CLICK_BLOCK) {
               event.setCancelled(true);
               KothSelection.setPos1(player, blockLocation.add(1.0, 0.0, 1.0));
               player.sendMessage(
                  Util.color(
                     Config.getMessage("pos1-selected")
                        .replaceAll("\\{world}", blockLocation.getWorld().getName())
                        .replaceAll("\\{x}", blockLocation.getX() + "")
                        .replaceAll("\\{y}", blockLocation.getY() + "")
                        .replaceAll("\\{z}", blockLocation.getZ() + "")
                  )
               );
            } else if (action == Action.RIGHT_CLICK_BLOCK) {
               event.setCancelled(true);
               KothSelection.setPos2(player, blockLocation.add(1.0, 0.0, 1.0));
               player.sendMessage(
                  Util.color(
                     Config.getMessage("pos2-selected")
                        .replaceAll("\\{world}", blockLocation.getWorld().getName())
                        .replaceAll("\\{x}", blockLocation.getX() + "")
                        .replaceAll("\\{y}", blockLocation.getY() + "")
                        .replaceAll("\\{z}", blockLocation.getZ() + "")
                  )
               );
            }
         }
      }
   }
}
