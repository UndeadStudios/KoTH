package com.dragonsmith.koth;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.dragonsmith.koth.bstats.Metrics;
import com.dragonsmith.koth.commands.KothCommand;
import com.dragonsmith.koth.creator.selection.KothSelectionListener;
import com.dragonsmith.koth.creator.selection.item.KothSelectionWand;
import com.dragonsmith.koth.manager.koth.KothManager;
import com.dragonsmith.koth.manager.reward.RewardManager;
import com.dragonsmith.koth.placeholderapi.KoTHPlaceholder;
import com.dragonsmith.koth.playeable.CurrentKoth;
import com.dragonsmith.koth.schedule.ScheduleManager;
import com.dragonsmith.koth.scoreboard.hook.ScoreboardHook;
import com.dragonsmith.koth.scoreboard.hook.plugin.DisabledScoreboardHook;
import com.dragonsmith.koth.scoreboard.hook.plugin.KoTHScoreboardHook;
import com.dragonsmith.koth.scoreboard.koth.ScoreboardListener;
import com.dragonsmith.koth.util.Config;
import com.dragonsmith.koth.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class KoTHPlugin extends JavaPlugin {
   private static KoTHPlugin instance;
   private ItemStack selectionWandItem;
   private KothManager kothManager;
   private RewardManager rewardManager;
   private ScheduleManager scheduleManager;
   private ScoreboardHook scoreboardHook;
   private final Map<String, Boolean> supportedPlugins = new HashMap<>();
   private boolean loadedListeners = false;

   public void onEnable() {
      setInstance(this);
      new Metrics(this, 13335);
      this.setupCommands();
      this.setupListeners();
      Config.loadConfiguration();
      this.selectionWandItem = new KothSelectionWand();
      this.detectSupport("PlaceholderAPI");
      this.setupScoreboardHook();
      this.kothManager = new KothManager(true);
      this.rewardManager = new RewardManager();
      this.scheduleManager = new ScheduleManager();
      new ScheduleManager.Task().runTaskTimer(this, 0L, 5L);
      if (this.supportedPlugins.get("PlaceholderAPI")) {
         new KoTHPlaceholder().register();
      }
   }

   public void onDisable() {
      CurrentKoth currentKoth = this.kothManager.getCurrectKoth();
      if (currentKoth != null) {
         this.scoreboardHook.onKothEnd(currentKoth);
      }
   }

   public void setupListeners() {
      if (!this.loadedListeners) {
         this.getServer().getPluginManager().registerEvents(new KothSelectionListener(), this);
         this.getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
         this.getServer().getPluginManager().registerEvents(new KothManager(), this);
         this.loadedListeners = true;
      }
   }

   public void setupCommands() {
      ((PluginCommand)Objects.requireNonNull(this.getCommand("koth"))).setExecutor(new KothCommand());
   }

   public void setupScoreboardHook() {
      this.scoreboardHook = new KoTHScoreboardHook();
      if (!Config.getConfig().getBoolean("scoreboard.enable")) {
         this.scoreboardHook = new DisabledScoreboardHook();
      }

      Bukkit.getConsoleSender().sendMessage(Util.color("&8[&cKoTH&8] &7Scoreboard Hook: &c" + this.scoreboardHook.getHookName()));
   }

   private static void setInstance(KoTHPlugin pl) {
      instance = pl;
      Bukkit.getConsoleSender().sendMessage(Util.color("&8[&cKoTH&8] &7Enabling KoTH &cv" + pl.getDescription().getVersion()));
   }

   public static KoTHPlugin getInstance() {
      return instance;
   }

   public void detectSupport(String plugin) {
      boolean hasSupport = Bukkit.getPluginManager().getPlugin(plugin) != null;
      this.supportedPlugins.put(plugin, hasSupport);
      String hasSupportStr = hasSupport ? "&aYes" : "&cNo";
      Bukkit.getConsoleSender().sendMessage(Util.color("&8[&cKoTH&8] &7" + plugin + " Support: " + hasSupportStr));
   }

   public boolean hasSupport(String plugin) {
      return this.supportedPlugins.get(plugin) != null ? this.supportedPlugins.get(plugin) : false;
   }


   public ItemStack getSelectionWandItem() {
      return this.selectionWandItem;
   }

   public void setKothManager(KothManager kothManager) {
      this.kothManager = kothManager;
   }

   public void setRewardManager(RewardManager rewardManager) {
      this.rewardManager = rewardManager;
   }

   public void setScheduleManager(ScheduleManager scheduleManager) {
      this.scheduleManager = scheduleManager;
   }

   public KothManager getKothManager() {
      return this.kothManager;
   }

   public RewardManager getRewardManager() {
      return this.rewardManager;
   }

   public ScoreboardHook getScoreboardHook() {
      return this.scoreboardHook;
   }

   public ScheduleManager getScheduleManager() {
      return this.scheduleManager;
   }

   public void setScoreboardHook(ScoreboardHook scoreboardHook) {
      this.scoreboardHook = scoreboardHook;
   }
}
