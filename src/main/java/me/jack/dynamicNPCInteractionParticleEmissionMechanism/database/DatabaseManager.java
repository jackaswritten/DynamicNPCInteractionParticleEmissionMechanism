package me.jack.dynamicNPCInteractionParticleEmissionMechanism.database;

import me.jack.dynamicNPCInteractionParticleEmissionMechanism.models.NPCData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class DatabaseManager {

    /**
     * Initialize the database connection and create tables
     */
    public abstract CompletableFuture<Void> initialize();

    /**
     * Close the database connection
     */
    public abstract CompletableFuture<Void> close();

    /**
     * Save an NPC to the database
     */
    public abstract CompletableFuture<Void> saveNPC(NPCData npcData);

    /**
     * Remove an NPC from the database
     */
    public abstract CompletableFuture<Void> removeNPC(UUID npcUuid);

    /**
     * Get an NPC by UUID
     */
    public abstract CompletableFuture<NPCData> getNPC(UUID npcUuid);

    /**
     * Get all NPCs from the database
     */
    public abstract CompletableFuture<List<NPCData>> getAllNPCs();

    /**
     * Check if an NPC exists in the database
     */
    public abstract CompletableFuture<Boolean> npcExists(UUID npcUuid);
}
