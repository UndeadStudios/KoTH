package com.dragonsmith.koth.creator.selection.item;

import java.util.ArrayList;
import java.util.List;
import com.dragonsmith.koth.util.Config;
import com.dragonsmith.koth.util.Util;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KothSelectionWand extends ItemStack {
   public KothSelectionWand() {
      super(Material.STONE);
      Material material = Material.valueOf(Config.getConfig().getString("selection-wand.type"));
      String name = Util.color(Config.getConfig().getString("selection-wand.name"));
      List<String> lore = new ArrayList<>();

      for(String line : Config.getConfig().getStringList("selection-wand.lore")) {
         lore.add(Util.color(line));
      }

      this.setType(material);
      ItemMeta itemMeta = this.getItemMeta();
      itemMeta.setDisplayName(name);
      itemMeta.setLore(lore);
      this.setItemMeta(itemMeta);
   }
}
