package me.phoenixra.russian_roulette.cmds;

import com.google.common.collect.Maps;
import me.phoenixra.core.Holo;
import me.phoenixra.core.PhoenixCommand;
import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.files.LangClass;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class CommandAdmin extends PhoenixCommand {
    private final RussianRoulette plugin;
    private final Map<String, Method> methods = Maps.newLinkedHashMap();

    public CommandAdmin(RussianRoulette plugin) {
        this.plugin = plugin;
        this.setAllowConsole(true);
        this.setPermission("rr.admin");
        this.setUsage("/rradmin");
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
        if (this.isPlayer) {
            if (!info.permission().equals("") && !this.player.hasPermission(info.permission())) {
                this.reply(false, LangClass.general_no_permission);
                return;
            }
        }
        if (this.getArgLength() < info.minArgs() + 1) {
            this.reply(false, LangClass.general_not_enough_args);
            return;
        }
        try {
            method.invoke(this);
        } catch (Exception e) {
           replyException(e,LangClass.general_error);
        }

    }

    @SubCommand(description = "", minArgs = -1, usage = "/rradmin help")
    public void help() {
        reply("&7/rradmin reload ");


    }

    @SubCommand(description = "", minArgs = -1, usage = "/rradmin reload")
    public void reload() {
        plugin.reloadFiles();

        reply("&aSuccessfully reloaded");

    }
    @SubCommand(description = "", minArgs = -1, usage = "/rradmin loadArenas")
    public void loadArenas() {
        plugin.loadArenas();

    }
}
