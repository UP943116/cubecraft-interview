package dev.edward.cubecraft;

import dev.edward.cubecraft.listeners.MachineListener;
import dev.edward.cubecraft.manager.CubeletManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Cubecraft extends JavaPlugin {

    @Override
    public void onEnable() {
        CubeletManager cubeletManager = new CubeletManager(this);
        Bukkit.getServer().getPluginManager().registerEvents(new MachineListener(cubeletManager), this);
    }
}
