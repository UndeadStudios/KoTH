package com.dragonsmith.koth.reward.types;

import com.dragonsmith.koth.reward.api.Reward;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemReward implements Reward {
   private final String id;
   private final double chances;
   private final ItemStack itemStack;

   public ItemReward(String id, double chances, ItemStack itemStack) {
      this.id = id;
      this.chances = chances;
      this.itemStack = itemStack;
   }

   @Override
   public void give(Player player) {
      double r = Math.random() * 100.0;
      if (this.chances >= r) {
         // Create a new ItemStack (if not already created)
         ItemStack item = new ItemStack(this.itemStack);

         // Get the ItemMeta from the item stack
         ItemMeta meta = item.getItemMeta();

         // Check if the ItemMeta is not null
         if (meta != null) {
            // Set a custom name if the item doesn't have one already
            if (!meta.hasDisplayName()) {
               // Set the default item name if there's no custom name
               meta.setDisplayName(item.getType().name().replace('_', ' ').toLowerCase());
            }

            // Apply the ItemMeta to the ItemStack
            item.setItemMeta(meta);
         }

         // Add the item to the player's inventory
         player.getInventory().addItem(item);

         // Send a message to the player with the current display name or the item type name
         String itemName = item.getItemMeta().getDisplayName();
         player.sendMessage("The Koth gave you: " + itemName);
      }
   }

   @Override
   public String getId() {
      return this.id;
   }

   @Override
   public double getChances() {
      return this.chances;
   }

   @Override
   public Object getReward() {
      return this.itemStack;
   }
}
