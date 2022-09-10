package me.phoenixra.russian_roulette.cmds;

import com.google.common.collect.Maps;
import me.phoenixra.core.PhoenixCommand;
import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.files.LangClass;
import me.phoenixra.russian_roulette.game.Arena;
import java.lang.reflect.Method;
import java.util.Map;

public class CommandArena extends PhoenixCommand {
    private final RussianRoulette plugin;
    private final Map<String, Method> methods = Maps.newLinkedHashMap();

    public CommandArena(RussianRoulette plugin){
        this.plugin = plugin;
        this.setAllowConsole(false);
        this.setPermission("rr.create");
        this.setUsage("/rrarena");
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
        if(this.isPlayer) {
            if(!info.permission().equals("") && !this.player.hasPermission(info.permission())) {
                this.reply(false,LangClass.general_no_permission);
                return;
            }
        }
        if (this.getArgLength() < info.minArgs() + 1) {
            this.reply(false,LangClass.general_not_enough_args);
            return;
        }
        try {
            method.invoke(this);
        }
        catch (Exception e) {
            replyException(e, LangClass.general_error);
        }

    }







    @SubCommand(description = "commands list", minArgs = -1, usage = "/rrarena help")
    public void help() {
        String[] s = {"help", "create", "edit", "getitems", "remove","list", "setmainlobby"};
        this.reply("&7[§aRussianRoulette&7_&eArena§7] - §aAvailable commands:");
        for (String entry : s) {

            final SubCommand info = methods.get(entry).getAnnotation(SubCommand.class);
            final String usage = info.usage().isEmpty() ? "" : (" " + (info.usage()));
            final String desc = info.description();
            this.reply("&c " + usage + " &7- &f" + desc);
        }

    }

    @SubCommand(description = "Create new arena", minArgs = 1, usage = "/rrarena create [arena name]")
    public void create() {
        String name=this.getArg(1);
        for(Arena arena : plugin.getGameM().getArenas()) {
            if(name.equalsIgnoreCase(arena.getArenaName())){
                this.reply("&cArena with specified name already exists");
                return;
            }
        }
        if(!RussianRoulette.getInstance().getEditM().isPlayerAllowedToEdit(player)) {
            this.reply("&cThis arena is already being edited");
            return;
        }
        if(name.length()>20 || name.length()<3) {
            this.reply("&cThe name is too long or too small");
            return;
        }
        plugin.getEditM().startEditNewArena(player, name, player.getWorld().getName());


    }

    @SubCommand(description = "Edit existing arena", minArgs = 1, usage = "/rrarena edit [arena name]")
    public void edit() {
        String name=this.getArg(1);
        boolean b = false;
        for(Arena a : plugin.getGameM().getArenas()) {
            if (name.equalsIgnoreCase(a.getArenaName())) {
                b = true;
                break;
            }
        }
        if(!b) {
            this.reply("&cArena with that name doesn't exists");
            return;
        }

        if(!RussianRoulette.getInstance().getEditM().isPlayerAllowedToEdit(this.player)) {
            this.reply("&cThis arena is already being edited");
            return;
        }
        plugin.getEditM().startEditArena(player, name);

    }
    @SubCommand(description = "gives items to edit the arena. Use it if you lost them", minArgs = -1, usage = "/rrarena getitems")
    public void getItems() {
        if(!plugin.getEditM().giveEditItems(player)) {
            this.reply("&cCurrently you aren't editing any arena");
            return;
        }
        this.reply("&aItems has been given");
    }
    @SubCommand(description = "setup main lobby", minArgs = -1, usage = "/rrarena setMainLobby")
    public void setMainLobby() {
        RussianRoulette.getInstance().getConfigFile().setLobby(player.getLocation());
        this.reply("&aMain lobby successfully installed");
    }
    @SubCommand(description = "remove arena", minArgs = -1, usage = "/rrarena remove")
    public void remove() {
        String name=this.getArg(1);
        boolean b = false;
        for(Arena a : plugin.getGameM().getArenas()) {
            if (name.equalsIgnoreCase(a.getArenaName())) {
                b = true;
                break;
            }
        }
        if(!b) {
            this.reply("&cArena with that name doesn't exists");
            return;
        }
        if(!RussianRoulette.getInstance().getEditM().isPlayerAllowedToEdit(this.player)) {
            this.reply("&cThis arena is already being edited");
            return;
        }

        RussianRoulette.getInstance().getGameM().removeGame(name);
        this.reply("&aArena successfully removed");
    }
    @SubCommand(description = "list of loaded arenas", minArgs = -1, usage = "/rrarena list")
    public void list() {
        this.reply("&aLoaded Arenas:");
        for(Arena a : plugin.getGameM().getArenas()) {
            this.reply("&a"+a.getArenaName()+" - "+ RussianRoulette.getInstance().getGameM().getGame(a.getArenaName()).getState().toString());

        }
        this.reply("&7***********************");
        this.reply("&aTotal amount: " +plugin.getGameM().getArenas().size());
    }
}
