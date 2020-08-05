package dev.edward.cubecraft.listeners;

import dev.edward.cubecraft.manager.CubeletManager;
import dev.edward.cubecraft.util.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class MachineListener implements Listener {

    private final Inventory cubeletInventory;

    private final CubeletManager cubeletManager;

    public MachineListener(CubeletManager cubeletManager) {
        this.cubeletManager = cubeletManager;
        cubeletInventory = Bukkit.getServer().createInventory(null, 27, ChatColor.AQUA + "Cubelets");
        cubeletInventory.setItem(0, new ItemBuilder(Material.CAULDRON_ITEM).setName(ChatColor.GOLD + "Halloween").toItemStack());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (playerInteractEvent.getClickedBlock() == null)
            return;

        if (playerInteractEvent.getClickedBlock().getType() != Material.ENDER_PORTAL_FRAME)
            return;

        playerInteractEvent.setCancelled(true);
        playerInteractEvent.getPlayer().openInventory(cubeletInventory);


    }

    @EventHandler
    public void onClick(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getInventory().getName() == null)
            return;

        if (inventoryClickEvent.getInventory().getName().equalsIgnoreCase(ChatColor.AQUA + "Cubelets")) {
            inventoryClickEvent.setCancelled(true);
            inventoryClickEvent.getWhoClicked().closeInventory();

            if (cubeletManager.isInUse()) {
                inventoryClickEvent.getWhoClicked().sendMessage(ChatColor.RED + "Please wait before using the cubelet machine.");
                return;
            }

            World world = inventoryClickEvent.getWhoClicked().getWorld();
            Location enderPortalFrame = new Location(world, -648, 5, -76);
            cubeletManager.beginCubeletSequence(world, enderPortalFrame);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent blockBreakEvent) {
        System.out.println(blockBreakEvent.getBlock().getLocation());
    }

}
