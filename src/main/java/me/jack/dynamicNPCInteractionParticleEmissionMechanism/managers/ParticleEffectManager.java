package me.jack.dynamicNPCInteractionParticleEmissionMechanism.managers;

import me.jack.dynamicNPCInteractionParticleEmissionMechanism.models.NPCData;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.models.ParticleShape;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class ParticleEffectManager {
    private final JavaPlugin plugin;

    public ParticleEffectManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Spawn a particle effect at the given location based on NPC data
     */
    public void spawnEffect(NPCData npcData, Location targetLocation, Player viewer) {
        ParticleShape shape = npcData.getShape();

        switch (shape) {
            case CIRCLE:
                spawnCircle(npcData, targetLocation, viewer);
                break;
            case BOX:
                spawnBox(npcData, targetLocation, viewer);
                break;
            case SPHERE:
                spawnSphere(npcData, targetLocation, viewer);
                break;
            case SPIRAL:
                spawnSpiral(npcData, targetLocation, viewer);
                break;
            case HELIX:
                spawnHelix(npcData, targetLocation, viewer);
                break;
            case WAVE:
                spawnWave(npcData, targetLocation, viewer);
                break;
            case HEART:
                spawnHeart(npcData, targetLocation, viewer);
                break;
            case RING:
                spawnRing(npcData, targetLocation, viewer);
                break;
            case TORNADO:
                spawnTornado(npcData, targetLocation, viewer);
                break;
            case EXPLOSION:
                spawnExplosion(npcData, targetLocation, viewer);
                break;
            case POINT:
                spawnPoint(npcData, targetLocation, viewer);
                break;
            case MESS:
                spawnMess(npcData, targetLocation, viewer);
                break;
            default:
                spawnCircle(npcData, targetLocation, viewer);
                break;
        }
    }

    private void spawnCircle(NPCData npcData, Location center, Player viewer) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= npcData.getDuration()) {
                    cancel();
                    return;
                }

                double angle = (ticks * npcData.getAnimationSpeed() * npcData.getRotations() * 2 * Math.PI) / npcData.getDuration();
                int particles = npcData.getDensity();

                for (int i = 0; i < particles; i++) {
                    double particleAngle = (2 * Math.PI * i / particles) + angle;
                    double x = center.getX() + npcData.getRadius() * Math.cos(particleAngle);
                    double z = center.getZ() + npcData.getRadius() * Math.sin(particleAngle);
                    double y = center.getY() + npcData.getHeight() / 2;

                    Location loc = new Location(center.getWorld(), x, y, z);
                    spawnParticleWithData(loc, npcData, viewer);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnBox(NPCData npcData, Location center, Player viewer) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= npcData.getDuration()) {
                    cancel();
                    return;
                }

                double size = npcData.getRadius();
                int particlesPerEdge = npcData.getDensity() / 12;

                // Draw 12 edges of a box
                for (int i = 0; i <= particlesPerEdge; i++) {
                    double t = (double) i / particlesPerEdge;

                    // Bottom square
                    spawnParticleAt(center, -size + 2 * size * t, 0, -size, npcData, viewer);
                    spawnParticleAt(center, -size + 2 * size * t, 0, size, npcData, viewer);
                    spawnParticleAt(center, -size, 0, -size + 2 * size * t, npcData, viewer);
                    spawnParticleAt(center, size, 0, -size + 2 * size * t, npcData, viewer);

                    // Top square
                    spawnParticleAt(center, -size + 2 * size * t, npcData.getHeight(), -size, npcData, viewer);
                    spawnParticleAt(center, -size + 2 * size * t, npcData.getHeight(), size, npcData, viewer);
                    spawnParticleAt(center, -size, npcData.getHeight(), -size + 2 * size * t, npcData, viewer);
                    spawnParticleAt(center, size, npcData.getHeight(), -size + 2 * size * t, npcData, viewer);

                    // Vertical edges
                    spawnParticleAt(center, -size, npcData.getHeight() * t, -size, npcData, viewer);
                    spawnParticleAt(center, -size, npcData.getHeight() * t, size, npcData, viewer);
                    spawnParticleAt(center, size, npcData.getHeight() * t, -size, npcData, viewer);
                    spawnParticleAt(center, size, npcData.getHeight() * t, size, npcData, viewer);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnSphere(NPCData npcData, Location center, Player viewer) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= npcData.getDuration()) {
                    cancel();
                    return;
                }

                int particles = npcData.getDensity();
                double phi = Math.PI * (3.0 - Math.sqrt(5.0)); // golden angle

                for (int i = 0; i < particles; i++) {
                    double y = 1 - (i / (double) (particles - 1)) * 2;
                    double radiusAtY = Math.sqrt(1 - y * y);
                    double theta = phi * i;

                    double x = Math.cos(theta) * radiusAtY;
                    double z = Math.sin(theta) * radiusAtY;

                    Location loc = new Location(
                            center.getWorld(),
                            center.getX() + x * npcData.getRadius(),
                            center.getY() + npcData.getHeight() / 2 + y * npcData.getHeight() / 2,
                            center.getZ() + z * npcData.getRadius()
                    );
                    spawnParticleWithData(loc, npcData, viewer);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnSpiral(NPCData npcData, Location center, Player viewer) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= npcData.getDuration()) {
                    cancel();
                    return;
                }

                int particles = npcData.getDensity();
                double heightStep = npcData.getHeight() / particles;

                for (int i = 0; i < particles; i++) {
                    double angle = (ticks * npcData.getAnimationSpeed() + i * npcData.getRotations() * 360.0 / particles) * Math.PI / 180.0;
                    double radius = npcData.getRadius() * (1 - (double) i / particles);
                    double x = center.getX() + radius * Math.cos(angle);
                    double z = center.getZ() + radius * Math.sin(angle);
                    double y = center.getY() + i * heightStep;

                    Location loc = new Location(center.getWorld(), x, y, z);
                    spawnParticleWithData(loc, npcData, viewer);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnHelix(NPCData npcData, Location center, Player viewer) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= npcData.getDuration()) {
                    cancel();
                    return;
                }

                int particles = npcData.getDensity() / 2;
                double heightStep = npcData.getHeight() / particles;

                for (int i = 0; i < particles; i++) {
                    double angle = (ticks * npcData.getAnimationSpeed() + i * 360.0 / particles) * Math.PI / 180.0;
                    double y = center.getY() + i * heightStep;

                    // First helix
                    double x1 = center.getX() + npcData.getRadius() * Math.cos(angle);
                    double z1 = center.getZ() + npcData.getRadius() * Math.sin(angle);
                    Location loc1 = new Location(center.getWorld(), x1, y, z1);
                    spawnParticleWithData(loc1, npcData, viewer);

                    // Second helix (offset by 180 degrees)
                    double x2 = center.getX() + npcData.getRadius() * Math.cos(angle + Math.PI);
                    double z2 = center.getZ() + npcData.getRadius() * Math.sin(angle + Math.PI);
                    Location loc2 = new Location(center.getWorld(), x2, y, z2);
                    spawnParticleWithData(loc2, npcData, viewer);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnWave(NPCData npcData, Location center, Player viewer) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= npcData.getDuration()) {
                    cancel();
                    return;
                }

                int particles = npcData.getDensity();
                double width = npcData.getRadius() * 2;

                for (int i = 0; i < particles; i++) {
                    double x = center.getX() - width / 2 + (width * i / particles);
                    double waveAngle = (ticks * npcData.getAnimationSpeed() + i * npcData.getRotations() * 360.0 / particles) * Math.PI / 180.0;
                    double y = center.getY() + npcData.getHeight() / 2 + Math.sin(waveAngle) * npcData.getHeight() / 2;
                    double z = center.getZ();

                    Location loc = new Location(center.getWorld(), x, y, z);
                    spawnParticleWithData(loc, npcData, viewer);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnHeart(NPCData npcData, Location center, Player viewer) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= npcData.getDuration()) {
                    cancel();
                    return;
                }

                int particles = npcData.getDensity();

                for (int i = 0; i < particles; i++) {
                    double t = (2 * Math.PI * i / particles);
                    
                    // Parametric heart shape
                    double x = 16 * Math.pow(Math.sin(t), 3);
                    double y = 13 * Math.cos(t) - 5 * Math.cos(2 * t) - 2 * Math.cos(3 * t) - Math.cos(4 * t);

                    double scale = npcData.getRadius() / 20.0;
                    Location loc = new Location(
                            center.getWorld(),
                            center.getX() + x * scale,
                            center.getY() + npcData.getHeight() / 2 + y * scale,
                            center.getZ()
                    );
                    spawnParticleWithData(loc, npcData, viewer);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnRing(NPCData npcData, Location center, Player viewer) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= npcData.getDuration()) {
                    cancel();
                    return;
                }

                int particles = npcData.getDensity();
                double y = center.getY() + npcData.getHeight();

                for (int i = 0; i < particles; i++) {
                    double angle = 2 * Math.PI * i / particles;
                    double x = center.getX() + npcData.getRadius() * Math.cos(angle);
                    double z = center.getZ() + npcData.getRadius() * Math.sin(angle);

                    Location loc = new Location(center.getWorld(), x, y, z);
                    spawnParticleWithData(loc, npcData, viewer);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnTornado(NPCData npcData, Location center, Player viewer) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= npcData.getDuration()) {
                    cancel();
                    return;
                }

                int particles = npcData.getDensity();
                double heightStep = npcData.getHeight() / particles;

                for (int i = 0; i < particles; i++) {
                    double angle = (ticks * npcData.getAnimationSpeed() * 5 + i * npcData.getRotations() * 720.0 / particles) * Math.PI / 180.0;
                    double radius = npcData.getRadius() * (1 - (double) i / particles);
                    double x = center.getX() + radius * Math.cos(angle);
                    double z = center.getZ() + radius * Math.sin(angle);
                    double y = center.getY() + i * heightStep;

                    Location loc = new Location(center.getWorld(), x, y, z);
                    spawnParticleWithData(loc, npcData, viewer);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnExplosion(NPCData npcData, Location center, Player viewer) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= npcData.getDuration()) {
                    cancel();
                    return;
                }

                int particles = npcData.getDensity();
                double progress = (double) ticks / npcData.getDuration();
                double radius = npcData.getRadius() * progress;

                for (int i = 0; i < particles; i++) {
                    double theta = 2 * Math.PI * Math.random();
                    double phi = Math.acos(2 * Math.random() - 1);

                    double x = radius * Math.sin(phi) * Math.cos(theta);
                    double y = radius * Math.sin(phi) * Math.sin(theta);
                    double z = radius * Math.cos(phi);

                    Location loc = new Location(
                            center.getWorld(),
                            center.getX() + x,
                            center.getY() + npcData.getHeight() / 2 + y * npcData.getHeight() / (2 * npcData.getRadius()),
                            center.getZ() + z
                    );
                    spawnParticleWithData(loc, npcData, viewer);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnPoint(NPCData npcData, Location center, Player viewer) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= npcData.getDuration()) {
                    cancel();
                    return;
                }

                // Spawn particles with offsets
                for (int i = 0; i < npcData.getDensity(); i++) {
                    double offsetX = (Math.random() - 0.5) * 0.2;
                    double offsetY = (Math.random() - 0.5) * 0.2;
                    double offsetZ = (Math.random() - 0.5) * 0.2;
                    Location loc = center.clone().add(offsetX, npcData.getHeight() / 2 + offsetY, offsetZ);
                    spawnParticleWithData(loc, npcData, viewer);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnMess(NPCData npcData, Location center, Player viewer) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= npcData.getDuration()) {
                    cancel();
                    return;
                }

                int particleCount = npcData.getDensity();
                double radius = npcData.getRadius();
                double height = npcData.getHeight();
                
                // Spawn particles at random positions within cylinder
                for (int i = 0; i < particleCount; i++) {
                    // Random position in 3D space
                    double randomX = (Math.random() - 0.5) * 2 * radius;
                    double randomY = Math.random() * height;  // USE HEIGHT RANGE
                    double randomZ = (Math.random() - 0.5) * 2 * radius;
                    
                    double x = center.getX() + randomX;
                    double y = center.getY() + randomY;  // Starts at center.y, goes UP by height
                    double z = center.getZ() + randomZ;
                    
                    Location loc = new Location(center.getWorld(), x, y, z);
                    spawnParticleWithData(loc, npcData, viewer);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Spawn particle with proper data handling for particles that require BlockData, DustOptions, etc.
     */
    private void spawnParticleWithData(Location loc, NPCData npcData, Player viewer) {
        Particle particle = npcData.getParticleType();
        
        // Check if particle requires special data
        if (particle == Particle.FALLING_DUST || particle == Particle.BLOCK_CRACK || particle == Particle.BLOCK_MARKER) {
            // Parse block state
            String blockStateName = npcData.getBlockState();
            if (blockStateName != null) {
                blockStateName = blockStateName.replace("minecraft:", "");
                Material material = Material.matchMaterial(blockStateName.toUpperCase());
                if (material != null && material.isBlock()) {
                    BlockData blockData = material.createBlockData();
                    viewer.spawnParticle(particle, loc, 1, 0, 0, 0, 0, blockData);
                    return;
                }
            }
        } else if (particle == Particle.DUST) {
            // Handle DUST particles with color
            int[] rgb = npcData.getDustColor();
            float size = npcData.getDustSize();
            if (rgb != null && rgb.length == 3) {
                Particle.DustOptions dustOptions = new Particle.DustOptions(
                    org.bukkit.Color.fromRGB(rgb[0], rgb[1], rgb[2]), size
                );
                viewer.spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, dustOptions);
                return;
            }
        } else if (particle == Particle.DUST_COLOR_TRANSITION) {
            // Handle color transition
            Map<String, Object> data = npcData.getParticleData();
            if (data != null && data.containsKey("from-color") && data.containsKey("to-color")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> fromColorMap = (Map<String, Object>) data.get("from-color");
                @SuppressWarnings("unchecked")
                Map<String, Object> toColorMap = (Map<String, Object>) data.get("to-color");
                
                int fromR = ((Number) fromColorMap.get("r")).intValue();
                int fromG = ((Number) fromColorMap.get("g")).intValue();
                int fromB = ((Number) fromColorMap.get("b")).intValue();
                
                int toR = ((Number) toColorMap.get("r")).intValue();
                int toG = ((Number) toColorMap.get("g")).intValue();
                int toB = ((Number) toColorMap.get("b")).intValue();
                
                float size = data.containsKey("size") ? ((Number) data.get("size")).floatValue() : 1.0f;
                
                Particle.DustTransition dustTransition = new Particle.DustTransition(
                    org.bukkit.Color.fromRGB(fromR, fromG, fromB),
                    org.bukkit.Color.fromRGB(toR, toG, toB),
                    size
                );
                viewer.spawnParticle(Particle.DUST_COLOR_TRANSITION, loc, 1, 0, 0, 0, 0, dustTransition);
                return;
            }
        } else if (particle == Particle.ITEM_CRACK) {
            // Handle item crack
            String itemName = npcData.getBlockState();
            if (itemName != null) {
                itemName = itemName.replace("minecraft:", "");
                Material material = Material.matchMaterial(itemName.toUpperCase());
                if (material != null && material.isItem()) {
                    ItemStack itemStack = new ItemStack(material);
                    viewer.spawnParticle(Particle.ITEM_CRACK, loc, 1, 0, 0, 0, 0, itemStack);
                    return;
                }
            }
        }
        
        // Default spawning for regular particles
        viewer.spawnParticle(particle, loc, 1, 0, 0, 0, npcData.getSpeed());
    }

    private void spawnParticleAt(Location center, double offsetX, double offsetY, double offsetZ, NPCData npcData, Player viewer) {
        Location loc = center.clone().add(offsetX, offsetY, offsetZ);
        spawnParticleWithData(loc, npcData, viewer);
    }
}
