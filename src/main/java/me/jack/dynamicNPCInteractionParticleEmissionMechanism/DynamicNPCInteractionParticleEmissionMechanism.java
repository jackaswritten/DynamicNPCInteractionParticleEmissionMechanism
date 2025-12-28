package me.jack.dynamicNPCInteractionParticleEmissionMechanism;

import me.jack.dynamicNPCInteractionParticleEmissionMechanism.commands.DNIPEMCommand;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.database.DatabaseManager;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.database.MySQLManager;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.database.SQLiteManager;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.listeners.NPCClickListener;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.managers.NPCManager;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.managers.ParticleEffectManager;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.utils.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class DynamicNPCInteractionParticleEmissionMechanism extends JavaPlugin {

    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private NPCManager npcManager;
    private ParticleEffectManager particleEffectManager;

    @Override
    public void onEnable() {
        // Initialize configuration
        configManager = new ConfigManager(this);
        getLogger().info("Configuration loaded");

        // Initialize database
        String databaseType = configManager.getDatabaseType().toUpperCase();
        if (databaseType.equals("MYSQL")) {
            databaseManager = new MySQLManager(this, configManager);
            getLogger().info("Using MySQL database");
        } else {
            databaseManager = new SQLiteManager(this);
            getLogger().info("Using SQLite database");
        }

        // Initialize database asynchronously
        databaseManager.initialize().thenRun(() -> {
            getLogger().info("Database initialized successfully");

            // Initialize managers
            npcManager = new NPCManager(this, databaseManager);
            particleEffectManager = new ParticleEffectManager(this);

            // Load NPCs from database
            npcManager.loadAllNPCs().thenRun(() -> {
                getLogger().info("All NPCs loaded successfully");
            }).exceptionally(throwable -> {
                getLogger().severe("Failed to load NPCs: " + throwable.getMessage());
                throwable.printStackTrace();
                return null;
            });

            // Register listener
            getServer().getPluginManager().registerEvents(
                    new NPCClickListener(npcManager, particleEffectManager),
                    this
            );
            getLogger().info("Event listeners registered");

            // Register command
            DNIPEMCommand commandHandler = new DNIPEMCommand(npcManager, configManager);
            getCommand("dnipem").setExecutor(commandHandler);
            getCommand("dnipem").setTabCompleter(commandHandler);
            getLogger().info("Commands registered");

            getLogger().info("Plugin enabled successfully!");
        }).exceptionally(throwable -> {
            getLogger().severe("Failed to initialize database: " + throwable.getMessage());
            throwable.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return null;
        });
    }

    @Override
    public void onDisable() {
        // Close database connection
        if (databaseManager != null) {
            databaseManager.close().thenRun(() -> {
                getLogger().info("Database connection closed");
            }).exceptionally(throwable -> {
                getLogger().severe("Failed to close database: " + throwable.getMessage());
                throwable.printStackTrace();
                return null;
            });
        }

        getLogger().info("Plugin disabled successfully!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public NPCManager getNpcManager() {
        return npcManager;
    }

    public ParticleEffectManager getParticleEffectManager() {
        return particleEffectManager;
    }
}
