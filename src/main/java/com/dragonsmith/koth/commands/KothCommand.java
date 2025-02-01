package com.dragonsmith.koth.commands;

import java.util.List;
import com.dragonsmith.koth.KoTHPlugin;
import com.dragonsmith.koth.builders.KothBuilder;
import com.dragonsmith.koth.builders.RewardBuilder;
import com.dragonsmith.koth.creator.Koth;
import com.dragonsmith.koth.creator.selection.KothSelection;
import com.dragonsmith.koth.manager.koth.KothManager;
import com.dragonsmith.koth.manager.reward.RewardManager;
import com.dragonsmith.koth.playeable.CurrentKoth;
import com.dragonsmith.koth.reward.api.Reward;
import com.dragonsmith.koth.reward.types.CommandReward;
import com.dragonsmith.koth.reward.types.ItemReward;
import com.dragonsmith.koth.util.Config;
import com.dragonsmith.koth.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KothCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String s, String[] arg) {
      if (sender.hasPermission("koth.command")) {
         if (arg.length >= 1) {
            if (arg[0].equalsIgnoreCase("create") && sender instanceof Player) {
               this.create(sender, arg);
            } else if (arg[0].equalsIgnoreCase("delete")) {
               this.delete(sender, arg);
            } else if (arg[0].equalsIgnoreCase("tp") && sender instanceof Player) {
               this.teleport(sender, arg);
            } else if (arg[0].equalsIgnoreCase("givewand") && sender instanceof Player) {
               this.giveWand(sender);
            } else if (arg[0].equalsIgnoreCase("start")) {
               this.start(sender, arg);
            } else if (arg[0].equalsIgnoreCase("stop")) {
               this.stop(sender, arg);
            } else if (arg[0].equalsIgnoreCase("list")) {
               this.list(sender, arg);
            } else if (arg[0].equalsIgnoreCase("reward")) {
               this.reward(sender, arg);
            } else if (arg[0].equalsIgnoreCase("reload")) {
               this.reload(sender);
            } else {
               this.help(sender);
            }
         } else {
            this.help(sender);
         }
      } else {
         this.noPermission(sender);
      }

      return true;
   }

   public void help(CommandSender sender) {
      for(String line : Config.getMessageList("commands.koth.help")) {
         sender.sendMessage(line);
      }
   }

   public void reload(CommandSender sender) {
      if (sender.hasPermission("koth.reload")) {
         sender.sendMessage(Config.getMessage("commands.koth.reload.reloaded"));
         KoTHPlugin plugin = KoTHPlugin.getInstance();
         plugin.onDisable();
         plugin.onEnable();
      } else {
         this.noPermission(sender);
      }
   }

   public void giveWand(CommandSender sender) {
      if (sender.hasPermission("koth.givewand")) {
         if (sender instanceof Player) {
            Player player = (Player)sender;
            player.getInventory().addItem(new ItemStack[]{KoTHPlugin.getInstance().getSelectionWandItem()});
            sender.sendMessage(Config.getMessage("commands.koth.givewand.gived"));
         } else {
            sender.sendMessage(Config.getMessage("bad-executor"));
         }
      } else {
         this.noPermission(sender);
      }
   }

   public void create(CommandSender sender, String[] arg) {
      if (sender.hasPermission("koth.create")) {
         if (sender instanceof Player) {
            Player player = (Player)sender;
            Location pos1 = KothSelection.getPos1(player);
            Location pos2 = KothSelection.getPos2(player);
            if (arg.length >= 3) {
               if (pos1 != null && pos2 != null) {
                  KothManager kothManager = KoTHPlugin.getInstance().getKothManager();
                  if (kothManager.getKothByID(arg[1]) == null) {
                     String id = arg[1];
                     String name = "";

                     for(int i = 2; i < arg.length; ++i) {
                        name = name + arg[i] + " ";
                     }

                     name = name.substring(0, name.length() - 1);
                     kothManager.create(new KothBuilder().setId(id).setName(name).setPos1(pos1).setPos2(pos2));
                     sender.sendMessage(Config.getMessage("commands.koth.create.koth-created").replaceAll("\\{name}", name).replaceAll("\\{id}", id));
                  } else {
                     sender.sendMessage(Config.getMessage("commands.koth.create.already-exist").replaceAll("\\{id}", arg[1]));
                  }
               } else {
                  sender.sendMessage(Config.getMessage("commands.koth.create.invalid-pos"));
               }
            } else {
               sender.sendMessage(Config.getMessage("correct-usage").replaceAll("\\{command}", "/koth create <id> <display_name>"));
            }
         } else {
            sender.sendMessage(Config.getMessage("bad-executor"));
         }
      } else {
         this.noPermission(sender);
      }
   }

   public void delete(CommandSender sender, String[] arg) {
      if (sender.hasPermission("koth.delete")) {
         if (arg.length == 2) {
            KothManager kothManager = KoTHPlugin.getInstance().getKothManager();
            Koth koth = kothManager.getKothByID(arg[1]);
            if (koth != null) {
               sender.sendMessage(Config.getMessage("commands.koth.delete.deleted").replaceAll("\\{id}", arg[1]).replaceAll("\\{name}", koth.getDisplayName()));
               kothManager.delete(arg[1]);
            } else {
               sender.sendMessage(Config.getMessage("commands.koth.delete.invalid-koth").replaceAll("\\{id}", arg[1]));
            }
         } else {
            sender.sendMessage(Config.getMessage("correct-usage").replaceAll("\\{command}", "/koth delete <id>"));
         }
      } else {
         this.noPermission(sender);
      }
   }

   public void teleport(CommandSender sender, String[] arg) {
      if (sender.hasPermission("koth.tp")) {
         if (arg.length == 2) {
            KothManager kothManager = KoTHPlugin.getInstance().getKothManager();
            Koth koth = kothManager.getKothByID(arg[1]);
            if (koth != null) {
               Player player = (Player)sender;
               sender.sendMessage(Config.getMessage("commands.koth.tp.teleported").replaceAll("\\{id}", arg[1]).replaceAll("\\{name}", koth.getDisplayName()));
               player.teleport(koth.getCenterLocation());
            } else {
               sender.sendMessage(Config.getMessage("commands.koth.tp.invalid-koth").replaceAll("\\{id}", arg[1]));
            }
         } else {
            sender.sendMessage(Config.getMessage("correct-usage").replaceAll("\\{command}", "/koth tp <id>"));
         }
      } else {
         this.noPermission(sender);
      }
   }

   public void start(CommandSender sender, String[] arg) {
      if (sender.hasPermission("koth.start")) {
         if (arg.length == 2) {
            String id = arg[1];
            KothManager kothManager = KoTHPlugin.getInstance().getKothManager();
            if (kothManager.getCurrectKoth() == null) {
               Koth koth = kothManager.getKothByID(id);
               if (koth != null) {
                  koth.start();
                  sender.sendMessage(
                     Config.getMessage("commands.koth.start.koth-started").replaceAll("\\{name}", kothManager.getKothByID(id).getDisplayName())
                  );
               } else {
                  sender.sendMessage(Config.getMessage("commands.koth.start.invalid-koth").replaceAll("\\{id}", id));
               }
            } else {
               sender.sendMessage(Config.getMessage("commands.koth.start.koth-in-progress"));
            }
         } else {
            sender.sendMessage(Config.getMessage("correct-usage").replaceAll("\\{command}", "/koth start <koth>"));
         }
      } else {
         this.noPermission(sender);
      }
   }

   public void list(CommandSender sender, String[] arg) {
      if (sender.hasPermission("koth.list")) {
         if (arg.length == 1) {
            KothManager kothManager = KoTHPlugin.getInstance().getKothManager();
            List<Koth> koths = kothManager.getKoths();
            if (koths.size() > 0) {
               for(Koth koth : koths) {
                  Location pos1 = koth.getPos1();
                  Location pos2 = koth.getPos2();
                  sender.sendMessage(
                     Config.getMessage("commands.koth.list.koth-display")
                        .replaceAll("\\{id}", koth.getId())
                        .replaceAll("\\{name}", koth.getDisplayName())
                        .replaceAll("\\{pos1}", pos1.getWorld().getName() + ", " + pos1.getX() + ", " + pos1.getY() + ", " + pos1.getZ())
                        .replaceAll("\\{pos2}", pos2.getWorld().getName() + ", " + pos2.getX() + ", " + pos2.getY() + ", " + pos2.getZ())
                  );
               }
            } else {
               sender.sendMessage(Config.getMessage("commands.koth.list.no-koths-found"));
            }
         } else {
            sender.sendMessage(Config.getMessage("correct-usage").replaceAll("\\{command}", "/koth list"));
         }
      } else {
         this.noPermission(sender);
      }
   }

   public void stop(CommandSender sender, String[] arg) {
      if (sender.hasPermission("koth.stop")) {
         KothManager kothManager = KoTHPlugin.getInstance().getKothManager();
         CurrentKoth koth = kothManager.getCurrectKoth();
         if (koth != null) {
            sender.sendMessage(
               Config.getMessage("commands.koth.stop.koth-stopped").replaceAll("\\{id}", koth.getId()).replaceAll("\\{name}", koth.getDisplayName())
            );
            koth.stop();
         } else {
            sender.sendMessage(Config.getMessage("commands.koth.stop.no-koth-in-progress"));
         }
      } else {
         this.noPermission(sender);
      }
   }

   public void reward(CommandSender sender, String[] arg) {
      if (sender.hasPermission("koth.rewards")) {
         RewardManager rewardManager = KoTHPlugin.getInstance().getRewardManager();
         if (arg.length >= 5 && arg[1].equalsIgnoreCase("create")) {
            if (rewardManager.getReward(arg[2]) == null) {
               double chances = 100.0;
               boolean chanceIsValid = true;

               try {
                  chances = Double.parseDouble(arg[4]);
               } catch (NumberFormatException var9) {
                  chanceIsValid = false;
               }

               if (chances >= 0.0 && chances <= 100.0 && chanceIsValid) {
                  if (arg.length >= 6 && arg[3].equalsIgnoreCase("command")) {
                     String command = "";

                     for(int i = 5; i < arg.length; ++i) {
                        command = command + arg[i] + " ";
                     }

                     command = command.substring(0, command.length() - 1);
                     rewardManager.create(new RewardBuilder().setId(arg[2]).setReward(new CommandReward(arg[2], chances, command)));
                     sender.sendMessage(Config.getMessage("commands.koth.reward.create.reward-created").replaceAll("\\{id}", arg[2]));
                  } else if (arg.length == 5 && arg[3].equalsIgnoreCase("item")) {
                     Player player = (Player)sender;
                     if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                        rewardManager.create(new RewardBuilder().setId(arg[2]).setReward(new ItemReward(arg[2], chances, player.getItemInHand().clone())));
                        sender.sendMessage(Config.getMessage("commands.koth.reward.create.reward-created").replaceAll("\\{id}", arg[2]));
                     } else {
                        sender.sendMessage(Config.getMessage("commands.koth.reward.create.invalid-item"));
                     }
                  } else {
                     sender.sendMessage(
                        Config.getMessage("correct-usage").replaceAll("\\{command}", "/koth reward create <id> <item/command> <chances> [<your command>]")
                     );
                  }
               } else {
                  sender.sendMessage(Config.getMessage("commands.koth.reward.create.invalid-chance"));
               }
            } else {
               sender.sendMessage(Config.getMessage("commands.koth.reward.create.already-exist").replaceAll("\\{id}", arg[2]));
            }
         } else if (arg.length == 3 && arg[1].equalsIgnoreCase("delete")) {
            if (rewardManager.getReward(arg[2]) != null) {
               sender.sendMessage(Config.getMessage("commands.koth.reward.delete.reward-deleted").replaceAll("\\{id}", arg[2]));
               rewardManager.delete(arg[2]);
            } else {
               sender.sendMessage(Config.getMessage("commands.koth.reward.delete.invalid-reward").replaceAll("\\{id}", arg[2]));
            }
         } else if (arg.length == 2 && arg[1].equalsIgnoreCase("list")) {
            List<Reward> rewards = rewardManager.getAllRewards();
            if (rewards.size() > 0) {
               for(Reward reward : rewards) {
                  if (reward.getReward() instanceof String) {
                     sender.sendMessage(
                        Config.getMessage("commands.koth.reward.list.command-display")
                           .replaceAll("\\{id}", reward.getId())
                           .replaceAll("\\{chances}", reward.getChances() + "")
                           .replaceAll("\\{command}", (String)reward.getReward())
                     );
                  } else if (reward.getReward() instanceof ItemStack) {
                     ItemStack itemStack = (ItemStack)reward.getReward();
                     sender.sendMessage(
                        Config.getMessage("commands.koth.reward.list.item-display")
                           .replaceAll("\\{id}", reward.getId())
                           .replaceAll("\\{chances}", reward.getChances() + "")
                           .replaceAll("\\{item_type}", itemStack.getType().toString())
                           .replaceAll("\\{item_name}", Util.getItemName(itemStack))
                     );
                  }
               }
            } else {
               sender.sendMessage(Config.getMessage("commands.koth.reward.list.no-rewards-found"));
            }
         } else if (arg.length >= 2 && arg[1].equalsIgnoreCase("create")) {
            sender.sendMessage(Config.getMessage("correct-usage").replaceAll("\\{command}", "/koth reward create <id> <item/command> <id> [<your command>]"));
         } else if (arg.length >= 2 && arg[1].equalsIgnoreCase("delete")) {
            sender.sendMessage(Config.getMessage("correct-usage").replaceAll("\\{command}", "/koth reward delete <id>"));
         } else if (arg.length >= 2 && arg[1].equalsIgnoreCase("list")) {
            sender.sendMessage(Config.getMessage("correct-usage").replaceAll("\\{command}", "/koth reward list"));
         } else {
            sender.sendMessage(Config.getMessage("correct-usage").replaceAll("\\{command}", "/koth reward <create/delete/list>"));
         }
      } else {
         this.noPermission(sender);
      }
   }

   public void noPermission(CommandSender sender) {
      sender.sendMessage(Config.getMessage("no-permission"));
   }
}
