package com.dragonsmith.koth.scoreboard.hook;

import com.dragonsmith.koth.playeable.CurrentKoth;

public abstract class ScoreboardHook {
   public abstract String getHookName();

   public abstract void onKothStart(CurrentKoth var1);

   public abstract void onKothEnd(CurrentKoth var1);

   public abstract void update(CurrentKoth var1);
}
