package com.dragonsmith.koth.util;

import java.io.File;
import java.io.IOException;
import com.dragonsmith.koth.KoTHPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YMLFile {
   private final String fileName;
   private final File file;
   private FileConfiguration fileConfiguration;

   public YMLFile(String fileName) {
      this.fileName = fileName;
      this.file = new File(KoTHPlugin.getInstance().getDataFolder(), this.fileName);
      this.check();
   }

   public YMLFile(File file) {
      this.fileName = file.getName();
      this.file = file;
      this.check();
   }

   public FileConfiguration get() {
      return this.fileConfiguration;
   }

   public void check() {
      if (!this.file.exists()) {
         this.createFile();
      }

      this.loadFile();
   }

   public void createFile() {
      this.file.getParentFile().mkdirs();
      KoTHPlugin.getInstance().saveResource(this.fileName, false);
   }

   public void loadFile() {
      this.fileConfiguration = new YamlConfiguration();

      try {
         this.fileConfiguration.load(this.file);
      } catch (InvalidConfigurationException | IOException var2) {
         var2.printStackTrace();
      }
   }

   public void save() {
      try {
         this.get().save(this.file);
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }
}
