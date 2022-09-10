package me.phoenixra.russian_roulette;

import me.phoenixra.core.PhoenixHologram;
import me.phoenixra.core.PhoenixUtils;
import me.phoenixra.russian_roulette.game.Arena;
import me.phoenixra.russian_roulette.game.ArenaEditor;
import me.phoenixra.russian_roulette.game.CustomLocation;
import me.phoenixra.russian_roulette.game.Game;
import me.phoenixra.russian_roulette.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditorManager {
    public static HashMap<Player, List<PhoenixHologram>> armorStands = new HashMap<>();
    private final RussianRoulette plugin = RussianRoulette.getInstance();
    private final HashMap<Player, ArenaEditor> edits = new HashMap<>();
    private final HashMap<Player, Location> oldPos = new HashMap<>();

    public boolean isPlayerAllowedToEdit(Player p) {
        return true;

    }

    public boolean giveEditItems(Player p) {
        if (edits.get(p) == null) {
            return false;
        }
        PlayerInventory playerInventory = p.getInventory();
        playerInventory.setItem(3, new ItemBuilder().setCustomOwner(SkullSkin.TARGET.getSkin()).setDisplayName("&aCenter bounds &7(R/L click)").setLores(" ", "&eLeft click - edge point 1", "&eRight click - edge point 2").getItem());
        playerInventory.setItem(4, new ItemBuilder().setCustomOwner(SkullSkin.SETTINGS.getSkin()).setDisplayName("&9Arena settings &7(click)").setLores(" ").getItem());
        playerInventory.setItem(5, new ItemBuilder().setCustomOwner(SkullSkin.TARGET.getSkin()).setDisplayName("&aBlock center &7(click)").setLores(" ", "&eClick to centralize your position").getItem());
        return true;
    }

    public void startEditNewArena(Player player, String name, String world) {
        edits.put(player, new ArenaEditor(name, world));
        player.setGameMode(GameMode.CREATIVE);
        giveEditItems(player);

        openSettings(player);
        showEditorHolograms(player);
    }

    public void startEditArena(Player player, String name) {
        plugin.getGameM().getGame(name).disableGame();
        edits.put(player, new ArenaEditor(plugin.getGameM().getGame(name).getArena()));
        oldPos.put(player, player.getLocation());

        player.teleport(edits.get(player).getSpectatorSpawn().getLocation());
        player.setGameMode(GameMode.CREATIVE);
        giveEditItems(player);
        openSettings(player);
        showEditorHolograms(player);
    }


    public void openSettings(Player player) {
        ArenaEditor editor = edits.get(player);
        if (editor == null) {
            return;
        }
        Inventory inventory;
        inventory = Bukkit.createInventory(null, 27, "Arena Editor - Main");
        inventory.setItem(1, new ItemBuilder().setCustomOwner(SkullSkin.SPAWNS.getSkin()).setDisplayName("&aSeat points").setLores(" ", "&7Right click - add new seat", "&7Left click - remove last seat", " ", "&eSeats: &f" + editor.getSeatPoints().size()).getItem());
        inventory.setItem(3, new ItemBuilder().setCustomOwner(SkullSkin.PLAYERS.getSkin()).setDisplayName("&aMax players").setLores(" ", "&7Right click +1", "&7Left click -1"," ", "&eCurrent: &f" + (editor.getMaxPlayers())).getItem());
        inventory.setItem(5, new ItemBuilder().setCustomOwner(SkullSkin.MIN_PLAYERS.getSkin()).setDisplayName("&aMin players").setLores(" ", "&7Right click +1", "&7Left click -1"," ", "&eCurrent: &f" + (editor.getMinPlayers())).getItem());
        inventory.setItem(7, new ItemBuilder().setCustomOwner(SkullSkin.WAITING.getSkin()).setDisplayName("&aSpectator's spawn point").setLores(" ", "&eStatus: " + (editor.getSpectatorSpawn() != null ? "&a\u2714" : "&c\u2718"), " ", "&eClick to set!").getItem());
        inventory.setItem(21, new ItemBuilder().setCustomOwner(setupReady(player) == null ? SkullSkin.CHECKMARK_GREEN.getSkin() : SkullSkin.CHECKMARK_RED.getSkin()).setDisplayName(setupReady(player) == null ? "&aFinish editing" : "&cFinish editing").setLores(" ", setupReady(player) == null ? "&eClick to finish" : "&cArena wasn't fully configured").getItem());
        inventory.setItem(22, new ItemBuilder().setCustomOwner(SkullSkin.QUESTION.getSkin()).setDisplayName("&aInfo").setLores(" ", setupReady(player) == null ? "&aYou can now finish editing" : "&7Required to finish editing: ", setupReady(player)).getItem());
        inventory.setItem(23, new ItemBuilder().setCustomOwner(SkullSkin.CANCEL.getSkin()).setDisplayName("&4Cancel editing").getItem());
        player.openInventory(inventory);


    }

    public void clearPlayerCache(Player player) {
        ArenaEditor editor = edits.get(player);
        if (editor == null) {
            return;
        }
        edits.remove(player);
        List<PhoenixHologram> list = armorStands.containsKey(player) ? armorStands.get(player) : new ArrayList<>();
        for (PhoenixHologram holo : list) {
            holo.clearLines();
        }
        armorStands.remove(player);

        player.closeInventory();
        player.getInventory().clear();
        if (oldPos.containsKey(player)) {
            player.teleport(oldPos.get(player));
        }
        oldPos.remove(player);
    }

    public void cancel(Player player) {
        ArenaEditor editor = edits.get(player);
        if (editor == null) {
            return;
        }
        Game game = plugin.getGameM().getGame(edits.get(player).getArenaName());
        if (game != null) {
            game.enableGame();
        }

        clearPlayerCache(player);
        player.sendMessage(PhoenixUtils.colorFormat("&aYou successfully cancelled " + (game == null ? "creation" : "changes you made") + " of the arena"));
    }

    public void finish(Player player) {
        ArenaEditor pc1 = edits.get(player);
        if (pc1 == null) {
            return;
        }
        Game game = plugin.getGameM().getGame(edits.get(player).getArenaName());
        if (game != null) plugin.getGameM().getGames().remove(game);

        Arena oldArena = plugin.getGameM().getArena(edits.get(player).getArenaName());
        if (oldArena != null) {
            plugin.getGameM().getArenas().remove(oldArena);
        }

        plugin.getGameM().createGame(new Arena(edits.get(player)));
        plugin.getGameM().serializeArenas();

        clearPlayerCache(player);
        player.sendMessage(PhoenixUtils.colorFormat("&aYou successfully" + (oldArena == null ? "created" : "edited") + "arena"));
    }

    public String setupReady(Player player) {
        ArenaEditor editor = edits.get(player);
        if (editor == null) {
            return "Editor is null";
        }
        if (editor.getMaxPlayers() <= 1) {
            return "&cMax players value have to be at least 2";
        }
        if (editor.getMinPlayers() <= 1) {
            return "&cMin players value have to be at least 2";
        }
        if (editor.getMinPlayers() > editor.getMaxPlayers()) {
            return "&cMax players value cannot be less than Min players value";
        }
        if (editor.getSpectatorSpawn() == null) {
            return "&cSpecify spectator's spawn location";
        }
        if (editor.getSeatPoints().size() < editor.getMaxPlayers()) {
            return "&cAdd more seat points. Left: &l" + (editor.getMaxPlayers() - editor.getSeatPoints().size());
        }

        if (editor.getCenterEdge1() == null) {
            return "&cSpecify edge point 1";
        }
        if (editor.getCenterEdge2() == null) {
            return "&cSpecify edge point 2";
        }
        return null;
    }

    public void showEditorHolograms(Player player) {
        ArenaEditor editor = edits.get(player);
        if (editor == null) {
            return;
        }
        List<PhoenixHologram> list = armorStands.getOrDefault(player, null);

        if (list != null) {
            for (PhoenixHologram hologram : list) {
                hologram.clearLines();
            }
        }
        for (CustomLocation entry : editor.getSeatPoints()) {
            createHolo(player, entry.getLocation(), "&a&lSeat point");
        }


        if (editor.getSpectatorSpawn() != null) {
            createHolo(player, editor.getSpectatorSpawn().getLocation(), "&f&lSpectator's spawn point");
        }
        if (editor.getCenterEdge1() != null) {
            createHolo(player, editor.getCenterEdge1().getLocation(), "&f&lCenter edge 1");
        }
        if (editor.getCenterEdge2() != null) {
            createHolo(player, editor.getCenterEdge2().getLocation(), "&f&lCenter edge 2");
        }

    }

    public void createHolo(final Player player, Location location, String string) {
        PhoenixHologram hologram = new PhoenixHologram(location.clone().add(0.0, 0.3, 0.0));
        hologram.addLine(PhoenixUtils.colorFormat(string));
        List<PhoenixHologram> list = armorStands.containsKey(player) ? armorStands.get(player) : new ArrayList<>();
        list.add(hologram);
        armorStands.put(player, list);
    }


    public ArenaEditor getPlayerEdit(Player player) {
        return edits.get(player);
    }

    public enum SkullSkin {
        WAITING("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWFlODI2ZTdkYjg0NDdmYmQ2Mjk4OGZlZTBlODNiYmRkNjk0Mzc4YWVmMTJkMjU3MmU5NzVmMDU5YTU0OTkwIn19fQ=="),
        PLAYERS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNiMDk4OTY3MzQwZGFhYzUyOTI5M2MyNGUwNDkxMDUwOWIyMDhlN2I5NDU2M2MzZWYzMWRlYzdiMzc1MCJ9fX0="),
        MIN_PLAYERS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI3YWY5ZTQ0MTEyMTdjN2RlOWM2MGFjYmQzYzNmZDY1MTk3ODMzMzJhMWIzYmM1NmZiZmNlOTA3MjFlZjM1In19fQ=="),
        SPAWNS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQ3ZTJlNWQ1NWI2ZDA0OTQzNTE5YmVkMjU1N2M2MzI5ZTMzYjYwYjkwOWRlZTg5MjNjZDg4YjExNTIxMCJ9fX0="),
        QUESTION("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmE0YmJhNjhmYjVlMGU3NjEzYjM2MmU2YTc5MWVjMTMyYmQ1YTZhZTIzZjYxYTlhMjk5ZGRkNzhjNWFmOSJ9fX0="),
        SETTINGS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTEyM2I4ODg0NmQ2NmUxY2ZlMmY2NjRhMzZhZDRhMjJiMWE0YzJmMmU0ZDI5NWY0MWZlNWU5MjliOWU3ZDgifX19"),
        TARGET("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2MxZGYwZGI4ZDQ3NzNjYzI1NWE2M2I4ZjBiMGU0Y2RkN2Q3NGMzYjRjNjc1MzYyMTY4NDYwYmM2N2JiMzAyZSJ9fX0="),
        CANCEL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ=="),
        CHECKMARK_GREEN("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNhNDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIwZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0="),
        CHECKMARK_RED("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY5ZDlkZTYyZWNhZTliNzk4NTU1ZmQyM2U4Y2EzNWUyNjA1MjkxOTM5YzE4NjJmZTc5MDY2Njk4Yzk1MDhhNyJ9fX0=");
        String skin;

        SkullSkin(String string2) {
            this.skin = string2;
        }

        public String getSkin() {
            return this.skin;
        }
    }


}
