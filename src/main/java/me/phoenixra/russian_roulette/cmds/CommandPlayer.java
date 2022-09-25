package me.phoenixra.russian_roulette.cmds;

import com.google.common.collect.Maps;
import me.phoenixra.core.PhoenixCommand;
import me.phoenixra.core.PhoenixUtils;
import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.files.LangClass;
import me.phoenixra.russian_roulette.game.Game;
import me.phoenixra.russian_roulette.utils.GameSounds;
import me.phoenixra.russian_roulette.utils.Utils;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

public class CommandPlayer extends PhoenixCommand {
    private final RussianRoulette plugin;
    private final Map<String, Method> methods = Maps.newLinkedHashMap();

    public CommandPlayer(RussianRoulette plugin){
        this.plugin = plugin;
        this.setAllowConsole(false);
        this.setPermission("rr.player");
        this.setUsage("/rr");
        this.setPrefix(PhoenixUtils.colorFormat("§7[§eRussianRoulette§7]"));

        this.setMsg_unknownCommand(LangClass.general_unknown_cmd);
        this.setMsg_noPermission(LangClass.general_no_permission);
        this.setMsg_notEnoughArgs(LangClass.general_not_enough_args);
    }

    @SubCommand(description = "Join the game", minArgs = 1, usage = "/rr join", sortOrder = 1)
    public void join() {
        String name = this.getArgument(1);
        Game game = plugin.getGameM().getGame(name);
        if(plugin.getGameM().getPlayerGame(player)!=null) {
            this.reply("&cYou are already in game");
            return;

        }
        if(game == null) {
            this.reply("&cThis game doesn't exists");
            return;
        }
        game.playerJoin(player);

    }
    @SubCommand(description = "Game selector menu", minArgs = -1, usage = "/rr play", sortOrder = 2)
    public void play() {
        if(plugin.getGameM().getPlayerGame(this.player)!=null) {
            this.reply("&cYou are already in game");
            return;
        }
        Utils.openPlay(player);
        player.playSound(player.getLocation(), GameSounds.menuOpen(), 1.0f, 1.0f);

    }

    @SubCommand(description = "Join random game", minArgs = -1, usage = "/rr playRandom", sortOrder = 3)
    public void playRandom() {
        Game game;
        if(plugin.getGameM().getPlayerGame(player)!=null) {
            this.reply("&cYou are already in game");
            return;
        }
        game = Utils.getBestGame();
        if(game==null) {
            this.reply("&cUnfortunately, there are no currently available games");
            player.playSound(player.getLocation(), GameSounds.actionDenied(), 1.0f, 1.0f);
            return;
        }
        player.playSound(player.getLocation(), GameSounds.actionAllowed(), 1.0f, 1.0f);
        game.playerJoin(player);

    }

    @SubCommand(description = "Take a seat", minArgs = -1, usage = "/rr sit", sortOrder = 4)
    public void sit() {
        Game g = plugin.getGameM().getPlayerGame(player);
        if(g==null) {
            this.reply("&cYou aren't in game");
            return;
        }
        plugin.getSeatManager().setSitting(player,true);

    }

    @SubCommand(description = "Leave the game", minArgs = -1, usage = "/rr leave", sortOrder = 5)
    public void leave() {
        Game game = plugin.getGameM().getPlayerGame(player);
        if(game == null) {
            this.reply("&cYou aren't in game");
            return;
        }
        game.playerLeave(player);

    }

}
