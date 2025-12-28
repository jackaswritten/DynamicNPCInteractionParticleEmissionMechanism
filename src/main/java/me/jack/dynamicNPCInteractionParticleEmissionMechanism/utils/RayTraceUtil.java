package me.jack.dynamicNPCInteractionParticleEmissionMechanism.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;

public class RayTraceUtil {
    private static final double MAX_DISTANCE = 100.0;

    /**
     * Find the entity the player is looking at
     */
    public static Entity getTargetEntity(Player player) {
        return getTargetEntity(player, MAX_DISTANCE);
    }

    /**
     * Find the entity the player is looking at within a specific distance
     */
    public static Entity getTargetEntity(Player player, double maxDistance) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        // Try built-in ray trace first
        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(
                eyeLocation,
                direction,
                maxDistance,
                entity -> entity != player && entity instanceof LivingEntity
        );

        if (rayTraceResult != null && rayTraceResult.getHitEntity() != null) {
            return rayTraceResult.getHitEntity();
        }

        // Fallback to manual check for entities near line of sight
        Collection<Entity> nearbyEntities = player.getWorld().getNearbyEntities(
                eyeLocation, maxDistance, maxDistance, maxDistance,
                entity -> entity != player && entity instanceof LivingEntity
        );

        Entity closest = null;
        double closestDistance = maxDistance;

        for (Entity entity : nearbyEntities) {
            Vector toEntity = entity.getLocation().toVector().subtract(eyeLocation.toVector());
            double distance = toEntity.length();

            if (distance > maxDistance) {
                continue;
            }

            // Check if entity is within cone of vision
            double dot = toEntity.normalize().dot(direction);
            if (dot > 0.99) { // Very narrow cone (~8 degrees)
                if (distance < closestDistance) {
                    closest = entity;
                    closestDistance = distance;
                }
            }
        }

        return closest;
    }

    /**
     * Get the exact location where the player is looking at on an entity
     */
    public static Location getTargetLocation(Player player, Entity entity) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        RayTraceResult result = entity.getBoundingBox().rayTrace(
                eyeLocation.toVector(),
                direction,
                MAX_DISTANCE
        );

        if (result != null) {
            Vector hitPosition = result.getHitPosition();
            return new Location(entity.getWorld(), hitPosition.getX(), hitPosition.getY(), hitPosition.getZ());
        }

        // Fallback to entity location
        return entity.getLocation();
    }
}
