package me.phoenixra.russian_roulette;

import lombok.Getter;
import me.phoenixra.core.files.PhoenixFileManager;
import me.phoenixra.core.scoreboard.BoardsManager;
import me.phoenixra.russian_roulette.cmds.CommandAdmin;
import me.phoenixra.russian_roulette.cmds.CommandArena;
import me.phoenixra.russian_roulette.cmds.CommandPlayer;
import me.phoenixra.russian_roulette.files.ConfigFile;
import me.phoenixra.russian_roulette.files.LangFile;
import me.phoenixra.russian_roulette.game.ScoreboardBuilder;
import me.phoenixra.russian_roulette.listeners.*;
import me.phoenixra.russian_roulette.playerSit.SeatManager;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class RussianRoulette extends JavaPlugin {
    private static  RussianRoulette instance;
    private PhoenixFileManager fileManager;
    @Getter private GameManager gameM;
    @Getter private SeatManager seatManager;
    @Getter private EditorManager editorM;
    @Getter private BoardsManager boardsManager;

    @Getter private Chat vaultChat;
    @Override
    public void onEnable(){
        instance=this;
        fileManager=new PhoenixFileManager(this);
        fileManager.addFile(new LangFile(fileManager)).addFile(new ConfigFile(fileManager));
        fileManager.loadfiles();
        seatManager = new SeatManager();
        editorM = new EditorManager();
        boardsManager = new BoardsManager(this);
        new ScoreboardBuilder();
        //Vault
        setupChat();

        //Listeners
        new EditorListener();
        new GameListener();
        new PlayerChat();
        new PlayerDamage();
        new PlayerJoin();
        new PlayerLeave();
        new PlayerSpectator();
        new PlayerMiscellaneous();
        new SelectorMenuListener();
        new ServerListener();
        new WorldListener();

        //Commands
        getCommand("rr").setExecutor(new CommandPlayer(this));
        getCommand("rradmin").setExecutor(new CommandAdmin(this));
        getCommand("rrarena").setExecutor(new CommandArena(this));


    }

    @Override
    public void onDisable(){
        if(gameM!=null){
            gameM.clear();
        }
        editorM.clearCache();

    }
    public void loadArenas() {
        Bukkit.getLogger().log(Level.INFO, "Loading RussianRoulette arenas...");
        gameM = new GameManager(GameManager.deserializeArenas());

    }
    public void reloadFiles(){
        fileManager.reloadFiles();
    }

    private void setupChat() {
        if(!getServer().getPluginManager().isPluginEnabled("Vault")) return;
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServicesManager().getRegistration(Chat.class);
        vaultChat = chatProvider != null ? chatProvider.getProvider() : null;

        if(vaultChat!=null){
            Bukkit.getLogger().log(Level.INFO,"[RussianRoulette] successfully hooked to Vault!");
        }else {
            Bukkit.getLogger().log(Level.INFO,"[RussianRoulette] Vault not found");
        }
    }


    public ConfigFile getConfigFile() {
        return (ConfigFile) fileManager.getFile("config");
    }


    public static RussianRoulette getInstance(){
        return instance;
    }
}
