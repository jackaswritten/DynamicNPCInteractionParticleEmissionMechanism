package me.jack.dynamicNPCInteractionParticleEmissionMechanism.listeners;

import me.jack.dynamicNPCInteractionParticleEmissionMechanism.managers.NPCManager;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.managers.ParticleEffectManager;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.models.NPCData;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.models.TargetMode;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.utils.RayTraceUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class NPCClickListener implements Listener {
    private final NPCManager npcManager;
    private final ParticleEffectManager particleEffectManager;

    public NPCClickListener(NPCManager npcManager, ParticleEffectManager particleEffectManager) {
        this.npcManager = npcManager;
        this.particleEffectManager = particleEffectManager;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();

        // Check if the clicked entity is registered
        if (!npcManager.isRegistered(clickedEntity.getUniqueId())) {
            return;
        }

        NPCData npcData = npcManager.getNPC(clickedEntity.getUniqueId());
        if (npcData == null) {
            return;
        }

        Location targetLocation;
        if (npcData.getTargetMode() == TargetMode.CLICK_POINT) {
            // Spawn particles at exact click location
            targetLocation = RayTraceUtil.getTargetLocation(player, clickedEntity);
        } else {
            // Spawn particles around entire NPC
            targetLocation = clickedEntity.getLocation();
        }

        // Spawn the particle effect
        particleEffectManager.spawnEffect(npcData, targetLocation);
    }
}
