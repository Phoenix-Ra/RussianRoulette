package me.phoenixra.russian_roulette.listeners;

import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.files.LangClass;
import me.phoenixra.russian_roulette.utils.GameSounds;
import me.phoenixra.russian_roulette.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SelectorMenuListener implements Listener {
    public SelectorMenuListener() {
        Bukkit.getPluginManager().registerEvents(this, RussianRoulette.getInstance());
    }

    @EventHandler
    public void menuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return;
        }
        String itemName = itemStack.getItemMeta().getDisplayName();
        if (event.getView().getTitle().equalsIgnoreCase(LangClass.gui_GameSelectorMainPage)) {
            event.setCancelled(true);
            if (itemName.equalsIgnoreCase(LangClass.gui_playItem_name)) {
                player.performCommand("rr playRandom");
            }
            if (itemName.equalsIgnoreCase(LangClass.gui_chooseArenaItem_name)) {
                Utils.openSelector(player);
                player.playSound(player.getLocation(), GameSounds.cLickGUI(), 1.0f, 1.0f);
            }

            if (itemStack.getType() == Material.BARRIER) {
                player.closeInventory();
            }
        }


        if (event.getView().getTitle().equalsIgnoreCase(LangClass.gui_GameSelectorArenasPage)) {
            event.setCancelled(true);
            if (itemStack.getType() == Material.BARRIER) player.closeInventory();

            if (itemStack.getType() == Material.MAP) {
                String gameName = RussianRoulette.getInstance().getGameM().
                        getGame(ChatColor.stripColor(itemName)).getArena().getArenaName();
                if (gameName == null) {
                    return;
                }
                player.performCommand("rr join " + gameName);
                player.closeInventory();
            }
        }

    }
}
