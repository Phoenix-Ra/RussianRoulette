package me.phoenixra.core.compatibility;

import org.bukkit.entity.Player;

public interface Compatibility {
    CompatibilityVersion getVersion();

    void sendTitle(Player var1, String var2);

    void sendSubtitle(Player var1, String var2);

    void sendAction(Player var1, String var2);


}
