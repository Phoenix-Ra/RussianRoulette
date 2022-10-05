package me.phoenixra.russian_roulette.cmds;

import com.google.common.collect.Maps;
import me.phoenixra.core.PhoenixCommand;
import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.files.LangClass;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class CommandAdmin extends PhoenixCommand {
    private final RussianRoulette plugin;

    public CommandAdmin(RussianRoulette plugin) {
        this.plugin = plugin;
        this.setAllowConsole(true);
        this.setPermission("rr.admin");
        this.setUsage("/rradmin");

        this.setMsg_unknownCommand(LangClass.general_unknown_cmd);
        this.setMsg_noPermission(LangClass.general_no_permission);
        this.setMsg_notEnoughArgs(LangClass.general_not_enough_args);
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
