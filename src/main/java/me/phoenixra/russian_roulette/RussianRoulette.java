package me.phoenixra.russian_roulette;

import lombok.Getter;
import me.phoenixra.core.files.PhoenixFileManager;
import me.phoenixra.core.scoreboard.BoardsManager;
import me.phoenixra.russian_roulette.cmds.CommandAdmin;
import me.phoenixra.russian_roulette.cmds.CommandArena;
import me.phoenixra.russian_roulette.cmds.CommandPlayer;
import me.phoenixra.russian_roulette.files.ConfigFile;
import me.phoenixra.russian_roulette.files.LangFile;
import me.phoenixra.russian_roulette.game.GameScoreboard;
import me.phoenixra.russian_roulette.listeners.*;
import me.phoenixra.russian_roulette.playerSit.SeatManager;
import me.phoenixra.russian_roulette.utils.Metrics;
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
        new GameScoreboard();
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
        CommandPlayer commandPlayer=new CommandPlayer(this);
        getCommand("rr").setExecutor(commandPlayer);
        getCommand("rr").setTabCompleter(commandPlayer);

        CommandArena commandArena=new CommandArena(this);
        getCommand("rrarena").setExecutor(commandArena);
        getCommand("rrarena").setTabCompleter(commandArena);

        CommandAdmin commandAdmin=new CommandAdmin(this);
        getCommand("rradmin").setExecutor(commandAdmin);
        getCommand("rradmin").setTabCompleter(commandAdmin);


        try {
            if ((new Metrics(this, 16625)).isEnabled()) {
                Bukkit.getConsoleSender().sendMessage("??7Metrics loaded successfully");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

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



    public static void doSync(Runnable runnable) {
        instance.getServer().getScheduler().runTask(instance, runnable);
    }
    public static void doAsync(Runnable runnable) {instance.getServer().getScheduler().runTaskAsynchronously(instance, runnable); }

    public ConfigFile getConfigFile() {
        return (ConfigFile) fileManager.getFile("config");
    }


    public static RussianRoulette getInstance(){
        return instance;
    }
}
