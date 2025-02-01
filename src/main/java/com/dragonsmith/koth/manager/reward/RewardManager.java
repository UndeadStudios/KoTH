package com.dragonsmith.koth.manager.reward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import com.dragonsmith.koth.builders.RewardBuilder;
import com.dragonsmith.koth.reward.api.Reward;
import com.dragonsmith.koth.reward.types.CommandReward;
import com.dragonsmith.koth.reward.types.ItemReward;
import com.dragonsmith.koth.util.Config;
import com.dragonsmith.koth.util.YMLFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class RewardManager {
   public HashMap<String, Reward> rewards = new HashMap<>();

   public RewardManager() {
      YMLFile ymlFile = Config.getRewardsFile();
      ymlFile.loadFile();
      FileConfiguration rewardsConfig = ymlFile.get();
      this.rewards.clear();

      for(String key : rewardsConfig.getKeys(false)) {
         double chance = rewardsConfig.getDouble(key + ".chances");
         Object reward = rewardsConfig.get(key + ".reward");
         if (reward instanceof ItemStack) {
            this.rewards.put(key, new ItemReward(key, chance, (ItemStack)reward));
         } else if (reward instanceof String) {
            this.rewards.put(key, new CommandReward(key, chance, (String)reward));
         }
      }
   }

   public void saveAllRewards() {
      YMLFile ymlFile = Config.getRewardsFile();
      FileConfiguration rewardsConfig = ymlFile.get();

      for(Entry<String, Reward> rewardEntry : this.rewards.entrySet()) {
         rewardsConfig.set(rewardEntry.getKey(), rewardEntry.getValue());
      }

      ymlFile.save();
   }

   public void put(String id, Reward reward) {
      this.rewards.put(id, reward);
   }

   public Reward getReward(String id) {
      return this.rewards.get(id);
   }

   public List<Reward> getAllRewards() {
      return new ArrayList<>(this.rewards.values());
   }

   public void create(RewardBuilder builder) {
      YMLFile rewardsFile = Config.getRewardsFile();
      rewardsFile.loadFile();
      FileConfiguration rewardsConfiguration = rewardsFile.get();
      String id = builder.getId();
      Reward reward = builder.getReward();
      rewardsConfiguration.set(id + ".chances", reward.getChances());
      rewardsConfiguration.set(id + ".reward", reward.getReward());
      rewardsFile.save();
      this.rewards.put(id, reward);
   }

   public void delete(String id) {
      YMLFile rewardsFile = Config.getRewardsFile();
      rewardsFile.loadFile();
      FileConfiguration rewardsConfiguration = rewardsFile.get();
      rewardsConfiguration.set(id, null);
      rewardsFile.save();
      this.rewards.remove(id);
   }
}
