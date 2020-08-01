package dev.edward.cubecraft.listeners;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.edward.cubecraft.Cubecraft;
import dev.edward.cubecraft.util.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Stairs;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MachineListener implements Listener {

    private boolean active = false;

    private final Cubecraft cubecraft;
    private final Inventory cubeletInventory;

    private final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

    public MachineListener(Cubecraft cubecraft) {
        this.cubecraft = cubecraft;
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

            if (active) {
                inventoryClickEvent.getWhoClicked().sendMessage(ChatColor.RED + "Please wait before using the cubelet machine.");
                return;
            }

            active = true;
            World world = inventoryClickEvent.getWhoClicked().getWorld();

            changeBlocks(world);
            Location enderPortalFrame = new Location(world, -655, 3.9, -76);
            spinningCauldron(world, enderPortalFrame);
            particleEffects(world, enderPortalFrame);
        }
    }

    private void changeBlocks(World world) {
        Block block;
        for (int x = -653; x >= -657; x--) {
            for (int z = -74; z >= -78; z--) {
                block = world.getBlockAt(x, 4, z);
                if (block.getType() == Material.SANDSTONE && block.getData() == (byte) 2)
                    block.setType(Material.MYCEL);
                if (block.getType() == Material.SANDSTONE_STAIRS) {
                    block.setTypeIdAndData(53, getDirection(((Stairs) block.getState().getData()).getFacing()), false);
                    block.getState().update();
                }
                if (block.getTypeId() == 44 && block.getData() == (byte) 1)
                    block.setTypeIdAndData(44, (byte) 2, false);
                if (block.getType() == Material.WOOD && block.getData() == (byte) 2)
                    block.setTypeIdAndData(5, (byte) 0, false);
            }
        }
    }

    private void revertBlocks(World world) {
        Block block;
        for (int x = -653; x >= -657; x--) {
            for (int z = -74; z >= -78; z--) {
                block = world.getBlockAt(x, 4, z);
                if (world.getBlockAt(x, 4, z).getType() == Material.MYCEL) {
                    block.setType(Material.SANDSTONE);
                    block.setData((byte) 2);
                }
                if (block.getType() == Material.WOOD_STAIRS) {
                    block.setTypeIdAndData(128, getDirection(((Stairs) block.getState().getData()).getFacing()), false);
                    block.getState().update();
                }
                if (block.getTypeId() == 44 && block.getData() == (byte) 2)
                    block.setTypeIdAndData(44, (byte) 1, false);
                if (block.getType() == Material.WOOD && block.getData() == (byte) 0)
                    block.setTypeIdAndData(5, (byte) 2, false);
            }
        }
    }

    private void spinningCauldron(World world, Location location) {
        final ArmorStand armorStand = (ArmorStand) world.spawnEntity(location.add(0.5, 0, 0.5), EntityType.ARMOR_STAND);
        armorStand.setGravity(false);
        armorStand.setHelmet(getSkull());
        armorStand.setVisible(false);
        armorStand.setSmall(false);

        new BukkitRunnable() {
            private double timer = 0;
            private double risenAmount = 0;
            private boolean rising = true;
            @Override
            public void run() {
                if (timer >= 5) {
                    active = false;
                    armorStand.remove();
                    revertBlocks(world);
                    cancel();
                    return;
                }
                Location armorStandLocation = armorStand.getLocation().clone();
                if (rising) {
                    armorStandLocation.setY(armorStandLocation.getY() + 0.05);
                    risenAmount += 0.05;
                    rising = !(risenAmount >= 0.8);
                }
                armorStandLocation.setYaw(armorStandLocation.getYaw() + 6F);
                armorStand.teleport(armorStandLocation);
                timer += 0.05;
            }
        }.runTaskTimer(cubecraft, 0L, 1L);
    }

    private void particleEffects(World world, Location location) {
        new BukkitRunnable() {
            private final Location particleLocation = location.clone().add(0, 1.9, 0);
            private double timer = 1;
            @Override
            public void run() {
                if (timer == 5) {
                    cancel();
                    return;
                }
                world.spigot().playEffect(particleLocation, Effect.FLAME, 0, 0, 0, 0, 0, 0, 30, 50);
                timer += 0.5;
            }
        }.runTaskTimerAsynchronously(cubecraft, 10L, 10L);
    }

    private byte getDirection(BlockFace blockFace) {
        byte direction;
        switch (blockFace) {
            case WEST:
                direction = 0x0;
                break;
            case EAST:
                direction = 0x1;
                break;
            case NORTH:
                direction = 0x2;
                break;
            case SOUTH:
                direction = 0x3;
                break;
            default:
                direction = 0x4;
                break;
        }
        return direction;
    }

    public static ItemStack getSkull() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);


        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", "http://textures.minecraft.net/texture/f955bd511635a77e616a24112c9fc457b27c8a146a5e6de727f17e989882").getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try
        {
            profileField = itemMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(itemMeta, profile);
        }
        catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        item.setItemMeta(itemMeta);
        return item;
    }

}
