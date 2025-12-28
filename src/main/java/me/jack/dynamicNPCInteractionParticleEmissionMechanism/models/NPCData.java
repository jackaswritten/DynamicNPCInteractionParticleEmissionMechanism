package me.jack.dynamicNPCInteractionParticleEmissionMechanism.models;

import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.Map;
import java.util.UUID;

public class NPCData {
    private final UUID npcUuid;
    private final Location location;
    private final Particle particleType;
    private final ParticleShape shape;
    private final TargetMode targetMode;
    private final int density;
    private final double speed;
    private final int duration;
    private final double radius;
    private final double height;
    private final int rotations;
    private final double animationSpeed;
    private final String blockState;
    private final int[] dustColor;
    private final float dustSize;
    private final Map<String, Object> particleData;

    public NPCData(UUID npcUuid, Location location, Particle particleType, ParticleShape shape,
                   TargetMode targetMode, int density, double speed, int duration, double radius,
                   double height, int rotations, double animationSpeed, String blockState,
                   int[] dustColor, float dustSize, Map<String, Object> particleData) {
        this.npcUuid = npcUuid;
        this.location = location;
        this.particleType = particleType;
        this.shape = shape;
        this.targetMode = targetMode;
        this.density = density;
        this.speed = speed;
        this.duration = duration;
        this.radius = radius;
        this.height = height;
        this.rotations = rotations;
        this.animationSpeed = animationSpeed;
        this.blockState = blockState;
        this.dustColor = dustColor;
        this.dustSize = dustSize;
        this.particleData = particleData;
    }

    public UUID getNpcUuid() {
        return npcUuid;
    }

    public Location getLocation() {
        return location;
    }

    public Particle getParticleType() {
        return particleType;
    }

    public ParticleShape getShape() {
        return shape;
    }

    public TargetMode getTargetMode() {
        return targetMode;
    }

    public int getDensity() {
        return density;
    }

    public double getSpeed() {
        return speed;
    }

    public int getDuration() {
        return duration;
    }

    public double getRadius() {
        return radius;
    }

    public double getHeight() {
        return height;
    }

    public int getRotations() {
        return rotations;
    }

    public double getAnimationSpeed() {
        return animationSpeed;
    }

    public String getBlockState() {
        return blockState;
    }

    public int[] getDustColor() {
        return dustColor;
    }

    public float getDustSize() {
        return dustSize;
    }

    public Map<String, Object> getParticleData() {
        return particleData;
    }
}
