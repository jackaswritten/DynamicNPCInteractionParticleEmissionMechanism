package me.jack.dynamicNPCInteractionParticleEmissionMechanism.managers;

import me.jack.dynamicNPCInteractionParticleEmissionMechanism.database.DatabaseManager;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.models.NPCData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

public class NPCManager {
    private final JavaPlugin plugin;
    private final DatabaseManager databaseManager;
    private final Map<UUID, NPCData> npcCache;

    public NPCManager(JavaPlugin plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.npcCache = new ConcurrentHashMap<>();
    }

    /**
     * Load all NPCs from database into memory cache
     */
    public CompletableFuture<Void> loadAllNPCs() {
        return databaseManager.getAllNPCs().thenAccept(npcs -> {
            npcCache.clear();
            for (NPCData npc : npcs) {
                npcCache.put(npc.getNpcUuid(), npc);
            }
            plugin.getLogger().info("Loaded " + npcs.size() + " NPCs from database");
        });
    }

    /**
     * Register a new NPC
     */
    public CompletableFuture<Void> registerNPC(NPCData npcData) {
        return databaseManager.saveNPC(npcData).thenRun(() -> {
            npcCache.put(npcData.getNpcUuid(), npcData);
            plugin.getLogger().info("Registered NPC: " + npcData.getNpcUuid());
        });
    }

    /**
     * Unregister an NPC
     */
    public CompletableFuture<Void> unregisterNPC(UUID npcUuid) {
        return databaseManager.removeNPC(npcUuid).thenRun(() -> {
            npcCache.remove(npcUuid);
            plugin.getLogger().info("Unregistered NPC: " + npcUuid);
        });
    }

    /**
     * Get an NPC from cache
     */
    public NPCData getNPC(UUID npcUuid) {
        return npcCache.get(npcUuid);
    }

    /**
     * Check if an NPC is registered
     */
    public boolean isRegistered(UUID npcUuid) {
        return npcCache.containsKey(npcUuid);
    }

    /**
     * Get all registered NPCs
     */
    public Map<UUID, NPCData> getAllNPCs() {
        return new ConcurrentHashMap<>(npcCache);
    }

    /**
     * Get the number of registered NPCs
     */
    public int getCount() {
        return npcCache.size();
    }
}
