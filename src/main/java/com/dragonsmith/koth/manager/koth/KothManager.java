package com.dragonsmith.koth.manager.koth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.dragonsmith.koth.KoTHPlugin;
import com.dragonsmith.koth.builders.KothBuilder;
import com.dragonsmith.koth.creator.Koth;
import com.dragonsmith.koth.playeable.CurrentKoth;
import com.dragonsmith.koth.util.Config;
import com.dragonsmith.koth.util.Util;
import com.dragonsmith.koth.util.YMLFile;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class KothManager implements Listener {
   private final Map<String, Koth> koths = new HashMap<>();
   private CurrentKoth currentKoth = null;

   public KothManager() {
      this(false);
   }

   public KothManager(boolean sayToConsole) {
      YMLFile kothsFile = new YMLFile("koths.yml");
      FileConfiguration configuration = kothsFile.get();
      this.koths.clear();
      boolean canLoadAll = true;

      for(String key : configuration.getKeys(false)) {
         String name = configuration.getString(key + ".name");
         Location pos1 = Util.getLocationFromConfig(kothsFile, key + ".pos1");
         Location pos2 = Util.getLocationFromConfig(kothsFile, key + ".pos2");
         if (pos1 != null && pos2 != null) {
            this.koths.put(key, new Koth(key, name, pos1, pos2));
            if (sayToConsole) {
               Bukkit.getConsoleSender().sendMessage(Util.color("&8[&cKoTH&8] &7The koth &c" + key + " &7was loaded correctly."));
            }
         } else {
            canLoadAll = false;
            if (sayToConsole) {
               Bukkit.getConsoleSender()
                  .sendMessage(
                     Util.color(
                        "&8[&cKoTH&8] &cThe koth "
                           + key
                           + " could not be loaded since the world "
                           + kothsFile.get().getString(key + ".pos1.world")
                           + " is not loaded."
                     )
                  );
            }
         }
      }

      if (!canLoadAll) {
         Bukkit.getConsoleSender()
            .sendMessage(
               Util.color("&8[&cKoTH&8] &cCould not load all the koths because one or more worlds were not loaded, These will be loaded when the worlds load.")
            );
      }
   }

   public List<Koth> getKoths() {
      return new ArrayList<>(this.koths.values());
   }

   public Koth getKothByID(String id) {
      return this.koths.get(id);
   }

   private void loadFromWorld(String worldName) {
      YMLFile kothsFile = Config.getKothsFile();
      FileConfiguration configuration = kothsFile.get();

      for(String key : configuration.getKeys(false)) {
         String name = configuration.getString(key + ".name");
         Location pos1 = Util.getLocationFromConfig(kothsFile, key + ".pos1");
         Location pos2 = Util.getLocationFromConfig(kothsFile, key + ".pos2");
         if (pos1 != null && pos1.getWorld().getName().equals(worldName) && pos2 != null) {
            KoTHPlugin.getInstance().getKothManager().koths.put(key, new Koth(key, name, pos1, pos2));
         }
      }
   }

   private void unloadFromWorld(String worldName) {
      for(Entry<String, Koth> entry : this.koths.entrySet()) {
         String id = entry.getKey();
         Koth koth = entry.getValue();
         if (koth.getCenterLocation().getWorld().getName().equals(worldName)) {
            KoTHPlugin.getInstance().getKothManager().koths.remove(id);
         }
      }
   }

   public void create(KothBuilder builder) {
      // Load and update KOTH configuration
      YMLFile kothsFile = Config.getKothsFile();
      kothsFile.loadFile();
      FileConfiguration kothsConfiguration = kothsFile.get();

      String id = builder.getId();
      String name = builder.getName();
      Location pos1 = builder.getPos1();
      Location pos2 = builder.getPos2();

      // Save KOTH info
      kothsConfiguration.set(id + ".name", name);
      kothsFile.save();
      Util.saveLocationToConfig(kothsFile, id + ".pos1", pos1);
      Util.saveLocationToConfig(kothsFile, id + ".pos2", pos2);

      this.koths.put(id, new Koth(id, name, pos1, pos2));

      // Get world from pos1 (assuming pos1 and pos2 are in the same world)
      World world = pos1.getWorld();

      // Get WorldGuard Region Manager
      RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
      RegionManager regionManager = container.get(BukkitAdapter.adapt(world));

      if (regionManager == null) {
         System.out.println("Could not get the RegionManager for world: " + world.getName());
         return;
      }

      // Expand pos1 and pos2 by 300 blocks in all directions
      int minX = Math.min(pos1.getBlockX(), pos2.getBlockX()) - 300;
      int minY = -64;
      int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ()) - 300;

      int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX()) + 300;
      int maxY = 320;
      int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ()) + 300;

      // Convert to WorldEdit Vectors
      BlockVector3 min = BlockVector3.at(minX, minY, minZ);
      BlockVector3 max = BlockVector3.at(maxX, maxY, maxZ);

      // Create the WorldGuard region with expanded size
      ProtectedCuboidRegion region = new ProtectedCuboidRegion(name, min, max);
      regionManager.addRegion(region);

      System.out.println("Created expanded WorldGuard region '" + name + "' from " + min + " to " + max);
   }

   public void delete(String id) {
      YMLFile kothsFile = Config.getKothsFile();
      kothsFile.loadFile();
      FileConfiguration kothsConfiguration = kothsFile.get();
      kothsConfiguration.set(id, null);
      this.koths.remove(id);
      kothsFile.save();
   }

   public CurrentKoth getCurrectKoth() {
      return this.currentKoth;
   }
   public static Location getNearestKOTH(Location playerLocation) {
      Koth nearestKoth = null;
      double nearestDistance = Double.MAX_VALUE;

      // Loop through all the KOTHs and find the nearest one
      for (Koth koth : KoTHPlugin.getInstance().getKothManager().getKoths()) {
         double distance = playerLocation.distance(koth.getCenterLocation());
         if (distance < nearestDistance) {
            nearestDistance = distance;
            nearestKoth = koth;
         }
      }

      // Return the location of the nearest KOTH, or null if there is none
      return nearestKoth != null ? nearestKoth.getCenterLocation() : null;
   }
   public void setCurrectKoth(Koth koth) {
      if (koth != null) {
         this.currentKoth = new CurrentKoth(koth);
      } else {
         this.currentKoth = null;
      }
   }

   @EventHandler
   public void onWorldLoad(WorldLoadEvent event) {
      this.loadFromWorld(event.getWorld().getName());
   }

   @EventHandler
   public void onWorldUnload(WorldUnloadEvent event) {
      this.unloadFromWorld(event.getWorld().getName());
   }
}
