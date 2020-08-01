package dev.edward.cubecraft;

import dev.edward.cubecraft.listeners.MachineListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Cubecraft extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(new MachineListener(this), this);
    }
}
