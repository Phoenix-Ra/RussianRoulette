package me.phoenixra.core;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PhoenixHologram {
    private final List<ArmorStand> lines = new ArrayList<>();

    private Location location;
    private final double heightBetweenLines;

    public PhoenixHologram(Location loc, double heightBetweenLines) {
        Preconditions.checkNotNull(loc, "Location cannot be null");
        this.location = loc;
        this.heightBetweenLines = heightBetweenLines;
    }
    public PhoenixHologram(Location loc) {
        Preconditions.checkNotNull(loc, "Location cannot be null");
        this.location = loc;
        this.heightBetweenLines=0.25;
    }


    public void setLines(List<String> lines) {
        if (lines == null)
            return;

        for (int i = 0; i < lines.size(); i++) {
            if (isValidIndex(i)) {
                if (!this.lines.get(i).getCustomName().equals(lines.get(i)))
                    this.lines.get(i).setCustomName(lines.get(i));
            } else {
                addLine(lines.get(i));

            }
        }
        setVisible(true);
    }
    public void addLine(String line){
        location.add(0, heightBetweenLines, 0);
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setCanPickupItems(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(PhoenixUtils.colorFormat(line));
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setSmall(true);

        lines.add(armorStand);

        location.subtract(0, heightBetweenLines, 0);
    }
    public void teleport(Location loc) {
        if (loc == null)
            return;

        Location teleportTo = loc.clone();
        for (ArmorStand armorStand : lines) {
            armorStand.teleport(teleportTo.add(0, this.heightBetweenLines, 0));
        }
        location = teleportTo;

    }
    public void clearLines() {
        for (ArmorStand line : lines) {
            line.remove();
        }

        this.lines.clear();
    }

    public void setVisibleLine(int line, boolean visible) {
        if (isValidIndex(line))
            this.lines.get(line).setCustomNameVisible(visible);
    }
    public void setVisible( boolean visible) {
        for(ArmorStand line: this.lines){
            line.setCustomNameVisible(visible);
        }
    }
    public boolean isVisible(int line) {
        return isValidIndex(line) && this.lines.get(line).isCustomNameVisible();
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < this.lines.size();
    }

    public Location getLocation(){
        return location;
    }
}
