package me.phoenixra.russian_roulette.cmds;

import me.phoenixra.core.PhoenixCommand;
import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.files.LangClass;

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
    @SubCommand(description = "use it if you reloaded plugin via PlugMan.", minArgs = -1, usage = "/rradmin loadArenas", permission = "*")
    public void loadArenas() {
        plugin.loadArenas();
    }

}
