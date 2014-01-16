package net.uvnode.uvminecartsloaded;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Keeps chunks loaded around minecarts so that they'll keep moving.
 * 
 * @author jcornwellshiel
 */
public final class UVMinecartsLoaded extends JavaPlugin implements Listener {

    int _loadRadius = 2;
    
    /**
     * Registers the plugin to listen for events.
     */
    @Override
    public void onEnable() {
        // Start listening for events.
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    
    /**
     * Handles bukkit commands from players and console.
     * 
     * @param sender    
     * @param cmd       
     * @param label     
     * @param args      
     * @return 
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("uvminecartsloaded")) {
            // placeholder for later
            return true;
        }
        return false;
    }

    
    /**
     * Listens for vehicle movement events, checks to see if the vehicle is a
     * minecart, and loads chunks immediately around it.
     * 
     * @param event The bukkit vehicle movement event.
     */
    @EventHandler
    private void onVehicleMoveEvent(VehicleMoveEvent event) {
        if (event.getVehicle() instanceof Minecart) {
            Chunk chunk = event.getTo().getChunk();
            loadChunksInRadius(chunk);
            //getLogger().info("Vehicle moved at " + event.getTo().getX() + ", " + event.getTo().getZ());
        }
        
    }
    
    
    /**
     * Catches chunk unloads and checks if they contain minecarts. If so,
     * cancels the unload and loads chunks in a small radius.
     * 
     * @param event     The bukkit chunk unload event.
     */
    @EventHandler
    private void onChunkUnloadEvent(ChunkUnloadEvent event) {
        //getLogger().info("Unloading chunk at " + event.getChunk().getX() + ", " + event.getChunk().getZ() + " with " + event.getChunk().getEntities().length + " entities.");
        //if (event.isCancelled()) return;
        if (checkChunksInRadiusForMinecarts(event.getChunk())) {
            event.setCancelled(true);
        }
    }
    
    
    /**
     * Loads chunks in a preconfigured radius around a center chunk.
     * 
     * @param chunk The center chunk to load around.
     */
    private void loadChunksInRadius(Chunk chunk) {
        for (int x = chunk.getX() - _loadRadius; x <= chunk.getX() + _loadRadius; x++) {
            for (int z = chunk.getZ() - _loadRadius; z <= chunk.getZ() + _loadRadius; z++) {
                chunk.getWorld().getChunkAt(x,z).load();
            }
        }
    }
    
    
    /**
     * Checks for minecarts in a preconfigured radius around a chunk.
     * 
     * @param chunk The center chunk.
     * @return      True if a minecart was found. False if not.
     */
    private boolean checkChunksInRadiusForMinecarts(Chunk chunk) {
        for (int x = chunk.getX() - _loadRadius; x <= chunk.getX() + _loadRadius; x++) {
            for (int z = chunk.getZ() - _loadRadius; z <= chunk.getZ() + _loadRadius; z++) {
                if (chunk.getWorld().isChunkLoaded(x, z)) {
                    for (Entity entity : chunk.getWorld().getChunkAt(x,z).getEntities()) {
                        if (entity instanceof Minecart) {
                            return true;
                        }
                    }                    
                }
            }
        }
        return false;
    }
}
