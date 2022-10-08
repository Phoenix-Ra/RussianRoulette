package me.phoenixra.russian_roulette.listeners;

import me.phoenixra.core.PhoenixUtils;
import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.files.LangClass;
import me.phoenixra.russian_roulette.game.Game;
import me.phoenixra.russian_roulette.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class GameListener implements Listener {
    private boolean alreadyInteract;

    public GameListener() {
        Bukkit.getPluginManager().registerEvents(this, RussianRoulette.getInstance());
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
        if (game == null) return;
        event.setCancelled(true);

        try {
            if (game.getState() == Game.GameState.STARTING||game.getState() == Game.GameState.PENDING_FOR_PLAYERS) {
                if (player.isOp() && player.getInventory().getHeldItemSlot() == 0) {
                    if (game.getPlayers().size() < 2) {
                        player.sendMessage(PhoenixUtils.colorFormat("&cAt least 2 players required"));
                        return;
                    }
                    game.setState(Game.GameState.STARTING);
                    player.getInventory().clear(0);
                }
                if (player.getInventory().getHeldItemSlot() == 8) player.performCommand("rr leave");
                return;
            }
            if (game.getState() != Game.GameState.ACTIVE || alreadyInteract) return;


            alreadyInteract = true;
            if (game.getBidAlgorithm().isBidStarted()) {
                game.getBidAlgorithm().playerMadeBid(player,
                        player.getInventory().getItemInMainHand().getType() == Material.RED_TERRACOTTA);
            }
            else if (game.getRound() == Game.GameRound.FIRST) {
                if (player.getInventory().getItemInMainHand().getType() == Material.GUNPOWDER) {
                    ItemStack itemStack = new ItemBuilder(Material.HOPPER)
                            .setDisplayName(LangClass.item_removeBullet)
                            .getItem();
                    //Without setting an item on next tick, nothing will change
                    RussianRoulette.doSync(()->{
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), itemStack);
                        game.getGameAlgorithm().changeAmountOfBullets(player);

                        ItemStack itemStack1 = new ItemBuilder(Material.FIRE_CHARGE)
                                .setDisplayName(LangClass.item_shoot
                                        .replace("%chance%",game.getGameAlgorithm().getCurrentChanceToDie(player)+"")
                                        .replace("%bullets%",game.getGameAlgorithm().getBulletsPlaced(player)+""))
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .getItem();
                        player.getInventory().setItem(4, itemStack1);

                        player.playSound(player.getLocation(), Sound.ENTITY_WITCH_AMBIENT, 1.0f, 10.0f);
                    });


                }
                if (player.getInventory().getItemInMainHand().getType() == Material.HOPPER) {
                    ItemStack itemStack = new ItemBuilder(Material.GUNPOWDER)
                            .setDisplayName(LangClass.item_addBullet)
                            .getItem();
                    //Without setting an item on next tick, nothing will change
                    RussianRoulette.doSync(()->{
                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), itemStack);
                        game.getGameAlgorithm().changeAmountOfBullets(player);

                        ItemStack itemStack1 = new ItemBuilder(Material.FIRE_CHARGE)
                                .setDisplayName(LangClass.item_shoot
                                        .replace("%chance%",game.getGameAlgorithm().getCurrentChanceToDie(player)+"")
                                        .replace("%bullets%",game.getGameAlgorithm().getBulletsPlaced(player)+""))
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .getItem();
                        player.getInventory().setItem(4, itemStack1);

                        player.playSound(player.getLocation(), Sound.ENTITY_WITCH_AMBIENT, 1.0f, 10.0f);
                    });



                }

                if (player.getInventory().getItemInMainHand().getType() == Material.FIRE_CHARGE)
                    game.getGameAlgorithm().shootItselfPrepare(player);


            } else if (game.getRound() == Game.GameRound.SECOND && player.getInventory().getItemInMainHand().getType() == Material.FIRE_CHARGE) {
                    game.getGameAlgorithm().shootItselfPrepare(player);

            } else {
                if (player.getInventory().getItemInMainHand().getType() != Material.FIRE_CHARGE) {
                    return;
                }
                Inventory inventory = Bukkit.createInventory(null, 18, LangClass.gui_game_chooseVictim);
                for (Player p : game.getPlayers()) {
                    if (p == player) {
                        continue;
                    }
                    ItemStack itemStack = new ItemBuilder(Material.PLAYER_HEAD)
                            .setDisplayName(ChatColor.GREEN + p.getName())
                            .setOwner(p.getName())
                            .getItem();
                    inventory.addItem(itemStack);
                }
                player.openInventory(inventory);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        alreadyInteract = false;

    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        try {
            Player player = (Player) e.getWhoClicked();
            Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
            if (game == null) return;

            if (e.getAction() == InventoryAction.PICKUP_ALL || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ||
                    e.getAction() == InventoryAction.PICKUP_HALF || e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD ||
                    e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.CLONE_STACK ||
                    e.getAction() == InventoryAction.COLLECT_TO_CURSOR || e.getAction() == InventoryAction.DROP_ALL_CURSOR ||
                    e.getAction() == InventoryAction.DROP_ALL_SLOT || e.getAction() == InventoryAction.DROP_ONE_SLOT ||
                    e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || e.getAction() == InventoryAction.PICKUP_ONE ||
                    e.getAction() == InventoryAction.SWAP_WITH_CURSOR)
                e.setCancelled(true);


            if (game.getPlayers().size() <= 1 || game.getState() == Game.GameState.FINISHING) {
                e.setCancelled(true);
                player.closeInventory();
                return;
            }
            if (e.getView().getTitle().equals(LangClass.gui_game_chooseVictim)) {
                if (e.getClickedInventory().getItem(e.getSlot()) == null) return;

                Player victim = Bukkit.getPlayer(ChatColor.stripColor(e.getClickedInventory().getItem(e.getSlot()).getItemMeta().getDisplayName()));
                if (game.getPlayers().contains(victim) && victim.isOnline()) {
                    game.getGameAlgorithm().shootVictimPrepare(player, victim);
                    game.setVictim(victim);
                } else {
                    player.sendMessage("&c&lPlayer left the game");
                }
                player.closeInventory();
            }
            e.setCancelled(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
