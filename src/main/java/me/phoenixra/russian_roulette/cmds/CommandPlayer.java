package me.phoenixra.russian_roulette.cmds;

import com.google.common.collect.Maps;
import me.phoenixra.core.PhoenixCommand;
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
        for (final Method method : this.getClass().getMethods()) {
            final SubCommand subCommand = method.getAnnotation(SubCommand.class);
            if (subCommand != null) {
                this.methods.put(subCommand.name().isEmpty() ? method.getName().toLowerCase() : subCommand.name(), method);
            }
        }
    }

    @Override
    public void execute() {
        final String subCommand = (this.getArgLength() > 0) ? this.getArg(0).toLowerCase() : "help";
        final Method method = this.methods.get(subCommand.toLowerCase());
        if (method == null) {
            this.reply(false, LangClass.general_unknown_cmd);
            return;
        }
        final SubCommand info = method.getAnnotation(SubCommand.class);
        if (this.getArgLength() < info.minArgs() + 1) {
            this.reply(false, LangClass.general_not_enough_args);
            return;
        }
        try {
            method.invoke(this);
        }
        catch (Exception e) {
            replyException(e,LangClass.general_error);
        }

    }
    @SubCommand(description = "available commands", minArgs = -1, usage = "/rr help")
    public void help() {
        this.reply("§7[§eRussianRoulette§7] - §aAvailable commands:");
        for (Entry<String, Method> entry : methods.entrySet()) {
            final SubCommand info = entry.getValue().getAnnotation(SubCommand.class);
            final String usage = info.usage().isEmpty() ? "" : (" " + (info.usage()));
            final String desc = info.description();
            this.reply("&c " + usage + " &7- &f" + desc);
        }

    }
    @SubCommand(description = "Join the game", minArgs = 1, usage = "/rr join")
    public void join() {
        String name = this.getArg(1);
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

    @SubCommand(description = "Leave the game", minArgs = -1, usage = "/rr leave")
    public void leave() {
        Game game = plugin.getGameM().getPlayerGame(player);
        if(game == null) {
            this.reply("&cYou aren't in game");
            return;
        }
        game.playerLeave(player);

    }

    @SubCommand(description = "Game selector menu", minArgs = -1, usage = "/rr play")
    public void play() {
        if(plugin.getGameM().getPlayerGame(this.player)!=null) {
            this.reply("&cYou are already in game");
            return;
        }
        Utils.openPlay(player);
        player.playSound(player.getLocation(), GameSounds.menuOpen(), 1.0f, 1.0f);

    }
    @SubCommand(description = "Join random game", minArgs = -1, usage = "/bw join")
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

    @SubCommand(description = "", minArgs = -1, usage = "/rr sit")
    public void sit() {
        Game g = plugin.getGameM().getPlayerGame(player);
        if(g==null) {
            this.reply("&cYou aren't in game");
            return;
        }
        plugin.getSeatManager().setSitting(player,true);

    }

}
