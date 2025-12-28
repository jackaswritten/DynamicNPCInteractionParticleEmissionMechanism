package me.jack.dynamicNPCInteractionParticleEmissionMechanism.utils;

import me.jack.dynamicNPCInteractionParticleEmissionMechanism.models.ParticleShape;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.models.TargetMode;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public String getDatabaseType() {
        return config.getString("database.type", "SQLITE");
    }

    public String getMySQLHost() {
        return config.getString("database.mysql.host", "localhost");
    }

    public int getMySQLPort() {
        return config.getInt("database.mysql.port", 3306);
    }

    public String getMySQLDatabase() {
        return config.getString("database.mysql.database", "minecraft");
    }

    public String getMySQLUsername() {
        return config.getString("database.mysql.username", "root");
    }

    public String getMySQLPassword() {
        return config.getString("database.mysql.password", "password");
    }

    public boolean getMySQLUseSSL() {
        return config.getBoolean("database.mysql.useSSL", false);
    }

    public Particle getDefaultParticleType() {
        String particleName = config.getString("default-settings.particle-type", "FLAME");
        try {
            return Particle.valueOf(particleName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid particle type: " + particleName + ", using FLAME");
            return Particle.FLAME;
        }
    }

    public ParticleShape getDefaultShape() {
        String shapeName = config.getString("default-settings.shape", "CIRCLE");
        try {
            return ParticleShape.valueOf(shapeName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid shape: " + shapeName + ", using CIRCLE");
            return ParticleShape.CIRCLE;
        }
    }

    public TargetMode getDefaultTargetMode() {
        String modeName = config.getString("default-settings.target-mode", "FULL_NPC");
        try {
            return TargetMode.valueOf(modeName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid target mode: " + modeName + ", using FULL_NPC");
            return TargetMode.FULL_NPC;
        }
    }

    public int getDefaultDensity() {
        return config.getInt("default-settings.density", 30);
    }

    public double getDefaultSpeed() {
        return config.getDouble("default-settings.speed", 0.1);
    }

    public int getDefaultDuration() {
        return config.getInt("default-settings.duration", 60);
    }

    public double getDefaultRadius() {
        return config.getDouble("default-settings.radius", 2.0);
    }

    public double getDefaultHeight() {
        return config.getDouble("default-settings.height", 2.0);
    }

    public int getDefaultRotations() {
        return config.getInt("default-settings.rotations", 2);
    }

    public double getDefaultAnimationSpeed() {
        return config.getDouble("default-settings.animation-speed", 1.0);
    }

    public String getMessage(String key) {
        String message = config.getString("messages." + key, "&cMessage not found: " + key);
        return message.replace("&", "§");
    }

    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
            }
        }
        return message;
    }
}
