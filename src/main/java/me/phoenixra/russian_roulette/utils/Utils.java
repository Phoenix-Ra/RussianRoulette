package me.phoenixra.russian_roulette.utils;

import me.phoenixra.core.PhoenixUtils;
import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.files.LangClass;
import me.phoenixra.russian_roulette.game.Game;
import me.phoenixra.russian_roulette.listeners.EditorListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static Game getBestGame() {
        Game game = null;
        for (Game g : RussianRoulette.getInstance().getGameM().getGames()) {
            if ((g.getPlayers().size() < g.getArena().getMaxPlayers()) && (g.getState() == Game.GameState.STARTING)) {
                if (game == null) {
                    game = g;
                    continue;
                }
                if (g.getPlayers().size() > game.getPlayers().size()) {
                    game = g;
                }
            }
        }
        return game;
    }

    public static void openPlay(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 36, LangClass.gui_GameSelectorMainPage);
        inventory.setItem(31, new ItemBuilder().setType(Material.BARRIER).setDisplayName(LangClass.gui_closeItem_name).getItem());
        inventory.setItem(12, new ItemBuilder().setType(Material.RED_BED).setDisplayName(LangClass.gui_playItem_name).setLores(LangClass.gui_playItem_lore).getItem());
        inventory.setItem(14, new ItemBuilder().setType(Material.ACACIA_SIGN).setDisplayName(LangClass.gui_chooseArenaItem_name).setLores(LangClass.gui_chooseArenaItem_lore).getItem());
        player.openInventory(inventory);
    }

    public static List<String> replacerInfo(Game g, List<String> list) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (String string : list) {
            string = string.replace("%rr_arena_online%", "" + g.getPlayers().size());
            string = string.replace("%rr_arena_max%", "" + g.getArena().getMaxPlayers());
            string = string.replace("%rr_arena%", g.getArena().getArenaName());
            string = string.replace("%rr_arena_state%", g.getState().toString());
            arrayList.add(string);
        }
        return arrayList;
    }

    public static void openSelector(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 36, LangClass.gui_GameSelectorArenasPage);

        for (Game g : RussianRoulette.getInstance().getGameM().getGames()) {
            inventory.addItem(new ItemBuilder(Material.MAP).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("&a" + g.getArena().getArenaName()).setLores(replacerInfo(g, LangClass.gui_ArenaItem_Lore)).getItem());
        }

        inventory.setItem(35, new ItemBuilder().setType(Material.BARRIER).setDisplayName(LangClass.gui_closeItem_name).getItem());
        inventory.setItem(27, new ItemBuilder().setType(Material.BARRIER).setDisplayName(LangClass.gui_closeItem_name).getItem());

        player.openInventory(inventory);
    }

    public static void confirm(Player player, String string, ItemStack confirmItem, ItemStack denyItem) {
        string=PhoenixUtils.colorFormat(string);
        if (confirmItem == null) {
            confirmItem = new ItemBuilder(Material.LIME_TERRACOTTA).setDisplayName("&aConfirm").getItem();
        }
        if (denyItem == null) {
            denyItem = new ItemBuilder(Material.RED_TERRACOTTA).setDisplayName("&cDeny").getItem();
        }
        Inventory inventory = Bukkit.createInventory(null, 9, string);
        inventory.setItem(3, confirmItem);
        inventory.setItem(5, denyItem);
        player.openInventory(inventory);
        EditorListener.confirmRequests.put(player, string);
    }

    public static void centerView(Player player) {
        Location location = player.getLocation();
        location.setX(location.getBlockX() + 0.5);
        location.setZ(location.getBlockZ() + 0.5);
        float f = location.getYaw();
        if ((f = (float) Math.round(f)) < 0.0f) {
            f += 360.0f;
        }
        if ((f = f / 90.0f * 90.0f) <= 45.0f) {
            location.setYaw(0.0f);
        }
        if (f >= 315.0f) {
            location.setYaw(0.0f);
        }
        if (f >= 45.0f && f <= 135.0f) {
            location.setYaw(90.0f);
        }
        if (f >= 135.0f && f <= 225.0f) {
            location.setYaw(180.0f);
        }
        if (f >= 225.0f && f <= 315.0f) {
            location.setYaw(270.0f);
        }
        location.setPitch(0.0f);
        player.teleport(location);
    }

    @SuppressWarnings("deprecation")
    public static void separatePlayer(Player player) {
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (RussianRoulette.getInstance().getGameM().getPlayerGame(player2) == null && RussianRoulette.getInstance().getGameM().getPlayerGame(player) == null) {
                player.showPlayer(player2);
                player2.showPlayer(player);
                continue;
            }
            if (RussianRoulette.getInstance().getGameM().getPlayerGame(player2) == null) {
                player.hidePlayer(player2);
                player2.hidePlayer(player);
                continue;
            }
            if (RussianRoulette.getInstance().getGameM().getPlayerGame(player) != RussianRoulette.getInstance().getGameM().getPlayerGame(player2)) {
                player.hidePlayer(player2);
                player2.hidePlayer(player);
                continue;
            }
            player.showPlayer(player2);
            player2.showPlayer(player);
        }
        if (RussianRoulette.getInstance().getGameM().getPlayerGame(player) != null && RussianRoulette.getInstance().getGameM().getPlayerGame(player).getSpectators().contains((Object) player)) {
            for (Player player2 : RussianRoulette.getInstance().getGameM().getPlayerGame(player).getPlayers()) {
                player2.hidePlayer(player);
            }
        }
    }
}
