package me.phoenixra.core.scoreboard;

import org.bukkit.entity.Player;

public interface PlaceholderTask {
    String getReplacement(Player player);
    String getPlaceholder();
}
