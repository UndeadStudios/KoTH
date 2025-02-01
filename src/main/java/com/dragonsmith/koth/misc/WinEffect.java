package com.dragonsmith.koth.misc;

import com.dragonsmith.koth.KoTHPlugin;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class WinEffect {
   public static void apply(final Player player) {
      (new BukkitRunnable() {
         int cycles = 0;

         public void run() {
            World world = player.getWorld();
            Location location = player.getLocation();
            Firework firework = (Firework)world.spawnEntity(location, EntityType.FIREWORK_ROCKET);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            fireworkMeta.setPower(1);
            fireworkMeta.addEffect(FireworkEffect.builder().withColor(new Color[]{Color.RED, Color.WHITE}).trail(true).flicker(false).build());
            firework.setFireworkMeta(fireworkMeta);
            ++this.cycles;
            if (this.cycles >= 10) {
               this.cancel();
            }
         }
      }).runTaskTimer(KoTHPlugin.getInstance(), 0L, 10L);
   }
}
