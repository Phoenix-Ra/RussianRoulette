package me.phoenixra.russian_roulette.listeners;

import me.phoenixra.core.PhoenixUtils;
import me.phoenixra.russian_roulette.EditorManager;
import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.game.ArenaEditor;
import me.phoenixra.russian_roulette.game.CustomLocation;
import me.phoenixra.russian_roulette.utils.ItemBuilder;
import me.phoenixra.russian_roulette.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class EditorListener implements Listener {
    public static HashMap<Player, String> confirmRequests = new HashMap<>();
    private final RussianRoulette plugin = RussianRoulette.getInstance();

    public EditorListener() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(InventoryClickEvent event) {
        try {
            Player player = (Player) event.getWhoClicked();
            ItemStack itemStack = event.getCurrentItem();
            ArenaEditor editor = plugin.getEditorM().getPlayerEdit(player);
            if ( editor == null) return;

            String itemName = itemStack==null ? null : !itemStack.hasItemMeta() ? null : itemStack.getItemMeta().getDisplayName();
            if (itemName!=null&&(itemName.equalsIgnoreCase(PhoenixUtils.colorFormat("&aCenter bounds &7(R/L click)")) || itemName.equalsIgnoreCase(PhoenixUtils.colorFormat("&aBlock center &7(click)")) || itemName.equalsIgnoreCase(PhoenixUtils.colorFormat("&9Arena settings &7(click)")))) {
                event.setCancelled(true);
                event.setCursor(null);
                return;
            }

            if (event.getView().getTitle().equalsIgnoreCase(PhoenixUtils.colorFormat("Arena Editor - Main"))) {
                event.setCancelled(true);
                event.setCursor(null);
                if(itemName==null) return;
                if (itemName.equalsIgnoreCase(PhoenixUtils.colorFormat("&aSpectator's spawn point"))) {
                    editor.setSpectatorSpawn(new CustomLocation(player.getLocation()));
                    player.closeInventory();
                    plugin.getEditorM().showEditorHolograms(player);
                    player.sendMessage(PhoenixUtils.colorFormat("&7Spectator's spawn successfully changed"));
                }
                else if (itemName.equals(PhoenixUtils.colorFormat("&aMax players"))) {
                    if (event.getClick().isRightClick())
                        editor.setMaxPlayers(editor.getMaxPlayers() + 1);
                    if (event.getClick().isLeftClick())
                        editor.setMaxPlayers(editor.getMaxPlayers() != 0 ? editor.getMaxPlayers() - 1 : 0);

                    this.plugin.getEditorM().openSettings(player);
                }
                else if (itemName.equalsIgnoreCase(PhoenixUtils.colorFormat("&aMin players"))) {
                    if (event.getClick().isRightClick())
                        editor.setMinPlayers(editor.getMinPlayers() + 1);
                    if (event.getClick().isLeftClick())
                        editor.setMinPlayers(editor.getMinPlayers() != 0 ? editor.getMinPlayers() - 1 : 0);

                    plugin.getEditorM().openSettings(player);
                }
                else if (itemName.equalsIgnoreCase(PhoenixUtils.colorFormat("&aSeat points"))) {
                    if (event.getClick().isRightClick() && editor.getSeatPoints().size() > 0) {
                        editor.getSeatPoints().remove(editor.getSeatPoints().get(editor.getSeatPoints().size() - 1));
                        player.closeInventory();
                        player.sendMessage(PhoenixUtils.colorFormat("&aLast seat point successfully removed"));
                        plugin.getEditorM().showEditorHolograms(player);
                    }
                    if (editor.getMaxPlayers() <= editor.getSeatPoints().size()) {
                        player.sendMessage(PhoenixUtils.colorFormat("&cAmount of seats cannot be more than max amount of players"));
                        return;
                    }
                    if (event.getClick().isLeftClick()) {
                        editor.getSeatPoints().add(new CustomLocation(player.getLocation()));
                        player.closeInventory();
                        player.sendMessage(PhoenixUtils.colorFormat("&aSeat point successfully added"));
                        plugin.getEditorM().showEditorHolograms(player);
                    }
                }
                else if (itemName.equalsIgnoreCase(PhoenixUtils.colorFormat("&aFinish editing"))) {
                    Utils.confirm(player, "&aFinish editing", null, null);
                }
                else if (itemName.equalsIgnoreCase(PhoenixUtils.colorFormat("&4Cancel editing"))) {
                    Utils.confirm(player, "&cCancel editing", null, null);
                }
            }


            if (confirmRequests.containsKey(player)&&event.getView().getTitle().equalsIgnoreCase(PhoenixUtils.colorFormat(confirmRequests.get(player)))) {
                event.setCancelled(true);
                event.setCursor(null);
                if(itemStack==null) return;
                if (itemStack.getType()==Material.LIME_TERRACOTTA) {
                    if (event.getView().getTitle().equals(PhoenixUtils.colorFormat("&aFinish editing"))) {
                        plugin.getEditorM().finish(player);
                    }
                    else if (event.getView().getTitle().equals(PhoenixUtils.colorFormat("&cCancel editing"))) {
                        plugin.getEditorM().cancel(player);
                    }
                }
                if (itemStack.getType()==Material.RED_TERRACOTTA) {
                    plugin.getEditorM().openSettings(player);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if(!player.getInventory().getItemInMainHand().hasItemMeta()) return;
        if (RussianRoulette.getInstance().getEditorM().getPlayerEdit(player) == null) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (player.getInventory().getItemInMainHand().getItemMeta().
                    getDisplayName().equalsIgnoreCase(PhoenixUtils.colorFormat("&9Arena settings &7(click)"))) {
                RussianRoulette.getInstance().getEditorM().openSettings(player);
                event.setCancelled(true);
            }
            if (player.getInventory().getItemInMainHand().getItemMeta().
                    getDisplayName().equalsIgnoreCase(PhoenixUtils.colorFormat("&aBlock center &7(click)"))) {
                Utils.centerView(player);
                event.setCancelled(true);
            }

        }
        if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(PhoenixUtils.colorFormat("&aCenter bounds &7(R/L click)"))) {
            event.setCancelled(true);

            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
                RussianRoulette.getInstance().getEditorM().getPlayerEdit(player).setCenterEdge1(new CustomLocation(event.getClickedBlock().getLocation()));
                player.sendMessage("§aEdge point 1 successfully changed");
                RussianRoulette.getInstance().getEditorM().showEditorHolograms(player);
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                RussianRoulette.getInstance().getEditorM().getPlayerEdit(player).setCenterEdge2(new CustomLocation(event.getClickedBlock().getLocation()));
                player.sendMessage("§aEdge point 2 successfully changed");
                RussianRoulette.getInstance().getEditorM().showEditorHolograms(player);
            }
        }

    }


}
