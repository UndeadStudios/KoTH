package com.dragonsmith.koth.builders;

import com.dragonsmith.koth.reward.api.Reward;

public class RewardBuilder {
   private String id;
   private Reward reward;

   public RewardBuilder setReward(Reward reward) {
      this.reward = reward;
      return this;
   }

   public RewardBuilder setId(String id) {
      this.id = id;
      return this;
   }

   public Reward getReward() {
      return this.reward;
   }

   public String getId() {
      return this.id;
   }
}
