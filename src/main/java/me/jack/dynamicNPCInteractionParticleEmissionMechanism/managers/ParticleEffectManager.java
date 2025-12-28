package me.jack.dynamicNPCInteractionParticleEmissionMechanism.managers;

import me.jack.dynamicNPCInteractionParticleEmissionMechanism.models.NPCData;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.models.ParticleShape;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleEffectManager {
    private final JavaPlugin plugin;

    public ParticleEffectManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Spawn a particle effect at the given location based on NPC data
     */
    public void spawnEffect(NPCData npcData, Location targetLocation) {
        ParticleShape shape = npcData.getShape();

        switch (shape) {
            case CIRCLE:
                spawnCircle(npcData, targetLocation);
                break;
            case BOX:
                spawnBox(npcData, targetLocation);
                break;
            case SPHERE:
                spawnSphere(npcData, targetLocation);
                break;
            case SPIRAL:
                spawnSpiral(npcData, targetLocation);
                break;
            case HELIX:
                spawnHelix(npcData, targetLocation);
                break;
            case WAVE:
                spawnWave(npcData, targetLocation);
                break;
            case HEART:
                spawnHeart(npcData, targetLocation);
                break;
            case RING:
                spawnRing(npcData, targetLocation);
                break;
            case TORNADO:
                spawnTornado(npcData, targetLocation);
                break;
            case EXPLOSION:
                spawnExplosion(npcData, targetLocation);
                break;
            case POINT:
                spawnPoint(npcData, targetLocation);
                break;
            default:
                spawnCircle(npcData, targetLocation);
                break;
        }
    }

    private void spawnCircle(NPCData npcData, Location center) {
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
                    double y = center.getY() + 1;

                    Location loc = new Location(center.getWorld(), x, y, z);
                    center.getWorld().spawnParticle(npcData.getParticleType(), loc, 1, 0, 0, 0, npcData.getSpeed());
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnBox(NPCData npcData, Location center) {
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
                    spawnParticleAt(center, -size + 2 * size * t, 0, -size, npcData);
                    spawnParticleAt(center, -size + 2 * size * t, 0, size, npcData);
                    spawnParticleAt(center, -size, 0, -size + 2 * size * t, npcData);
                    spawnParticleAt(center, size, 0, -size + 2 * size * t, npcData);

                    // Top square
                    spawnParticleAt(center, -size + 2 * size * t, npcData.getHeight(), -size, npcData);
                    spawnParticleAt(center, -size + 2 * size * t, npcData.getHeight(), size, npcData);
                    spawnParticleAt(center, -size, npcData.getHeight(), -size + 2 * size * t, npcData);
                    spawnParticleAt(center, size, npcData.getHeight(), -size + 2 * size * t, npcData);

                    // Vertical edges
                    spawnParticleAt(center, -size, npcData.getHeight() * t, -size, npcData);
                    spawnParticleAt(center, -size, npcData.getHeight() * t, size, npcData);
                    spawnParticleAt(center, size, npcData.getHeight() * t, -size, npcData);
                    spawnParticleAt(center, size, npcData.getHeight() * t, size, npcData);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnSphere(NPCData npcData, Location center) {
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
                            center.getY() + 1 + y * npcData.getRadius(),
                            center.getZ() + z * npcData.getRadius()
                    );
                    center.getWorld().spawnParticle(npcData.getParticleType(), loc, 1, 0, 0, 0, npcData.getSpeed());
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnSpiral(NPCData npcData, Location center) {
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
                    center.getWorld().spawnParticle(npcData.getParticleType(), loc, 1, 0, 0, 0, npcData.getSpeed());
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnHelix(NPCData npcData, Location center) {
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
                    center.getWorld().spawnParticle(npcData.getParticleType(), loc1, 1, 0, 0, 0, npcData.getSpeed());

                    // Second helix (offset by 180 degrees)
                    double x2 = center.getX() + npcData.getRadius() * Math.cos(angle + Math.PI);
                    double z2 = center.getZ() + npcData.getRadius() * Math.sin(angle + Math.PI);
                    Location loc2 = new Location(center.getWorld(), x2, y, z2);
                    center.getWorld().spawnParticle(npcData.getParticleType(), loc2, 1, 0, 0, 0, npcData.getSpeed());
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnWave(NPCData npcData, Location center) {
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
                    double y = center.getY() + 1 + Math.sin(waveAngle) * npcData.getHeight() / 2;
                    double z = center.getZ();

                    Location loc = new Location(center.getWorld(), x, y, z);
                    center.getWorld().spawnParticle(npcData.getParticleType(), loc, 1, 0, 0, 0, npcData.getSpeed());
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnHeart(NPCData npcData, Location center) {
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
                            center.getY() + 1 + y * scale,
                            center.getZ()
                    );
                    center.getWorld().spawnParticle(npcData.getParticleType(), loc, 1, 0, 0, 0, npcData.getSpeed());
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnRing(NPCData npcData, Location center) {
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
                    center.getWorld().spawnParticle(npcData.getParticleType(), loc, 1, 0, 0, 0, npcData.getSpeed());
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnTornado(NPCData npcData, Location center) {
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
                    center.getWorld().spawnParticle(npcData.getParticleType(), loc, 1, 0, 0, 0, npcData.getSpeed());
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnExplosion(NPCData npcData, Location center) {
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
                            center.getY() + 1 + y,
                            center.getZ() + z
                    );
                    center.getWorld().spawnParticle(npcData.getParticleType(), loc, 1, 0, 0, 0, npcData.getSpeed());
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnPoint(NPCData npcData, Location center) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= npcData.getDuration()) {
                    cancel();
                    return;
                }

                center.getWorld().spawnParticle(
                        npcData.getParticleType(),
                        center,
                        npcData.getDensity(),
                        0.1, 0.1, 0.1,
                        npcData.getSpeed()
                );

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spawnParticleAt(Location center, double offsetX, double offsetY, double offsetZ, NPCData npcData) {
        Location loc = center.clone().add(offsetX, offsetY, offsetZ);
        center.getWorld().spawnParticle(npcData.getParticleType(), loc, 1, 0, 0, 0, npcData.getSpeed());
    }
}
