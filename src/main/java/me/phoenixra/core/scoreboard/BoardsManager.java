package me.phoenixra.core.scoreboard;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BoardsManager extends BukkitRunnable {
	protected  HashMap<String,Board> boards = new HashMap<>();

	public BoardsManager(Plugin plugin){
		runTaskTimer(plugin,0,2);
	}
	@Override
	public void run() {
		for(Board board:boards.values()) {
			try {
				board.update();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public  void addBoard(Board board){
		boards.put(board.getId(),board);
	}
	public  void removePlayer(Player p){
		for(Board b:boards.values()) {
			b.removeScoreboard(p);
		}

	}

	public  void addPlayerToBoard(Player player, String id) {
		try{
			if(player==null) {
				removePlayer(player);
				return;
			}
			if(!player.isOnline()) {
				removePlayer(player);
				return;
			}
			boolean alreadyAdded=false;
			for(Board board:boards.values()) {
				if(board.players.containsKey(player)) {
					if(board.getId().equals(id)){
						alreadyAdded=true;
						continue;
					}
					board.removeScoreboard(player);
				}

			}
			if(!alreadyAdded) {
				boards.get(id).setScoreboard(player);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public  void clearCache(){
		boards.clear();
	}

}
