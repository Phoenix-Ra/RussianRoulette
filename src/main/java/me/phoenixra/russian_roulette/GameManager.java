package me.phoenixra.russian_roulette;

import me.phoenixra.russian_roulette.game.Arena;
import me.phoenixra.russian_roulette.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class GameManager {
    private final ArrayList<Arena> arenas = new ArrayList<>();
    private final Set<Game> games = new HashSet<>();
    private final HashMap<Player, Game> players_in_game = new HashMap<>();

    public GameManager(ArrayList<Arena> arenas) {
        this.arenas.addAll(arenas);
        if (arenas.size() > 0) {
            createSavedGames();
        }
    }

    public void createGame(Arena arena) {
        arenas.add(arena);
        games.add(new Game(arena));
        serializeArenas();
    }

    private void createSavedGames() {
        for (Arena arena : arenas) {
            games.add(new Game(arena));
        }
    }

    public void removeGame(String name) {
        Arena arena = this.getArena(name);
        getGame(arena.getArenaName()).disableGame();
        games.remove(getGame(arena.getArenaName()));
        arenas.remove(arena);

    }

    public void removeArena(Arena a) {
        for (Game g : games) {
            if (g.getArena() == a) {
                g.finishGame();
                games.remove(g);
                arenas.remove(a);
                serializeArenas();
            }
        }
    }


    public void serializeArenas(){
        new BukkitRunnable(){
            @Override
            public void run() {
                File f=new File(RussianRoulette.getInstance().getDataFolder(),"arenas.save");
                try {
                    FileOutputStream fos = new FileOutputStream(f);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(arenas);
                    oos.close();
                } catch (FileNotFoundException e) {
                    try {
                        f.createNewFile();
                        FileOutputStream fos = new FileOutputStream(f);
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(arenas);
                        oos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(RussianRoulette.getInstance());

    }
    public static ArrayList<Arena> deserializeArenas(){
        File f=new File(RussianRoulette.getInstance().getDataFolder(),"arenas.save");
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Arena> obj =(ArrayList<Arena>)ois.readObject();
            Bukkit.getLogger().log(Level.INFO, "Loaded arenas: "+obj.size());
            ois.close();
            return obj;
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().log(Level.INFO, "There are no saved arenas. You can create arena using /rrarena create");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void addPlayerToGame(Game g, Player p) {
        players_in_game.put(p, g);
    }
    public void removePlayerFromGame(Player p) {
        players_in_game.remove(p);
    }

    public Arena getArena(String name) {
        for (Arena a : arenas) {
            if (a.getArenaName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }
    public Game getGame(String n) {
        for (Game g : games) {
            if (g.getArena().getArenaName().equalsIgnoreCase(n)) {
                return g;
            }
        }
        return null;
    }
    public Game getPlayerGame(Player p) {
        return players_in_game.getOrDefault(p, null);
    }
    public ArrayList<Arena> getArenas() {
        return arenas;
    }
    public Set<Game> getGames() {
        return games;
    }

}
