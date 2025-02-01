package com.dragonsmith.koth.scoreboard.hook.plugin;

import com.dragonsmith.koth.playeable.CurrentKoth;
import com.dragonsmith.koth.scoreboard.hook.ScoreboardHook;

public class DisabledScoreboardHook extends ScoreboardHook {
   @Override
   public String getHookName() {
      return "Scoreboard Disabled";
   }

   @Override
   public void onKothStart(CurrentKoth currentKoth) {
   }

   @Override
   public void onKothEnd(CurrentKoth currentKoth) {
   }

   @Override
   public void update(CurrentKoth currentKoth) {
   }
}
