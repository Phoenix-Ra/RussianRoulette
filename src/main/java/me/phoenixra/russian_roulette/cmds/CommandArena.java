package me.phoenixra.russian_roulette.cmds;

import me.phoenixra.core.PhoenixCommand;
import me.phoenixra.core.PhoenixUtils;
import me.phoenixra.russian_roulette.RussianRoulette;
import me.phoenixra.russian_roulette.files.LangClass;
import me.phoenixra.russian_roulette.game.Arena;

public class CommandArena extends PhoenixCommand {
    private final RussianRoulette plugin;

    public CommandArena(RussianRoulette plugin){
        this.plugin = plugin;
        this.setAllowConsole(false);
        this.setPermission("rr.create");
        this.setUsage("/rrarena");
        this.setPrefix(PhoenixUtils.colorFormat("&7[&eRussianRoulette&7] "));

        this.setMsg_unknownCommand(LangClass.general_unknown_cmd);
        this.setMsg_noPermission(LangClass.general_no_permission);
        this.setMsg_notEnoughArgs(LangClass.general_not_enough_args);
    }


    @SubCommand(description = "Create new arena", minArgs = 1, usage = "/rrarena create [arena name]", sortOrder = 1)
    public void create() {
        String name=this.getArgument(1);
        for(Arena arena : plugin.getGameM().getArenas()) {
            if(name.equalsIgnoreCase(arena.getArenaName())){
                this.reply("&cArena with specified name already exists");
                return;
            }
        }
        if(!RussianRoulette.getInstance().getEditorM().isPlayerAllowedToEdit(player)) {
            this.reply("&cThis arena is already being edited");
            return;
        }
        if(name.length()>20 || name.length()<3) {
            this.reply("&cThe name is too long or too small");
            return;
        }
        plugin.getEditorM().startEditNewArena(player, name, player.getWorld().getName());


    }

    @SubCommand(description = "Edit existing arena", minArgs = 1, usage = "/rrarena edit [arena name]", sortOrder = 2)
    public void edit() {
        String name=this.getArgument(1);
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

        if(!RussianRoulette.getInstance().getEditorM().isPlayerAllowedToEdit(this.player)) {
            this.reply("&cThis arena is already being edited");
            return;
        }
        plugin.getEditorM().startEditArena(player, name);

    }
    @SubCommand(description = "gives items to edit the arena. Use it if you lost them", minArgs = -1, usage = "/rrarena getitems", sortOrder = 3)
    public void getItems() {
        if(!plugin.getEditorM().giveEditItems(player)) {
            this.reply("&cCurrently you aren't editing any arena");
            return;
        }
        this.reply("&aItems has been given");
    }
    @SubCommand(description = "setup main lobby", minArgs = -1, usage = "/rrarena setMainLobby", sortOrder = 4)
    public void setMainLobby() {
        RussianRoulette.getInstance().getConfigFile().setLobby(player.getLocation());
        this.reply("&aMain lobby successfully installed");
    }
    @SubCommand(description = "remove arena", minArgs = 1, usage = "/rrarena remove", sortOrder = 5)
    public void remove() {
        String name=this.getArgument(1);
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
        if(!RussianRoulette.getInstance().getEditorM().isPlayerAllowedToEdit(this.player)) {
            this.reply("&cThis arena is already being edited");
            return;
        }

        RussianRoulette.getInstance().getGameM().removeGame(name);
        this.reply("&aArena successfully removed");
    }
    @SubCommand(description = "list of loaded arenas", minArgs = -1, usage = "/rrarena list", sortOrder = 6)
    public void list() {
        this.reply("&aLoaded Arenas:");
        for(Arena a : plugin.getGameM().getArenas()) {
            this.reply("&a"+a.getArenaName()+" - "+ RussianRoulette.getInstance().getGameM().getGame(a.getArenaName()).getState().toString());

        }
        this.reply("&7***********************",false);
        this.reply("&aTotal amount: " +plugin.getGameM().getArenas().size(),false);
    }
}
