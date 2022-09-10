package me.phoenixra.russian_roulette.listeners;

import me.phoenixra.core.PhoenixUtils;
import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.files.LangClass;
import me.phoenixra.russian_roulette.game.Game;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;

public class PlayerChat implements Listener {
    public PlayerChat() {
        Bukkit.getPluginManager().registerEvents(this, RussianRoulette.getInstance());
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Game game = RussianRoulette.getInstance().getGameM().getPlayerGame(player);
        Chat vaultChat = RussianRoulette.getInstance().getVaultChat();

        if (game == null && event.getRecipients().size() >= 1) {
            ArrayList<Player> recipients = new ArrayList<>(event.getRecipients());
            for (Player p : recipients) {
                if (RussianRoulette.getInstance().getGameM().getPlayerGame(p) == null) continue;
                event.getRecipients().remove(p);
            }
            return;
        }

        if(game==null) return;

        event.getRecipients().clear();
        for (Player p : game.getPlayers()) {
            event.getRecipients().add(p);
        }

        String chatFormat=game.getSpectators().contains(player) ? LangClass.other_chatFormat_spectator : LangClass.other_chatFormat_player;
        event.setFormat(chatFormat
                .replace("%player%",player.getDisplayName())
                .replace("%vault_prefix%", vaultChat==null?"":vaultChat.getPlayerPrefix(player))
                .replace("%msg%", event.getMessage()));
        ArrayList<Player> recipients = new ArrayList<>(event.getRecipients());
        for (Player p : recipients) {
            if (game == RussianRoulette.getInstance().getGameM().getPlayerGame(p)) continue;
            event.getRecipients().remove(p);
        }

    }
}
