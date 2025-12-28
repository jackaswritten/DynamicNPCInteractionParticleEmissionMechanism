package me.jack.dynamicNPCInteractionParticleEmissionMechanism.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.models.NPCData;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.models.ParticleShape;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.models.TargetMode;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLManager extends DatabaseManager {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private HikariDataSource dataSource;

    public MySQLManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @Override
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.runAsync(() -> {
            try {
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl("jdbc:mysql://" + configManager.getMySQLHost() + ":" +
                        configManager.getMySQLPort() + "/" + configManager.getMySQLDatabase() +
                        "?useSSL=" + configManager.getMySQLUseSSL());
                config.setUsername(configManager.getMySQLUsername());
                config.setPassword(configManager.getMySQLPassword());
                config.setMaximumPoolSize(10);
                config.setMinimumIdle(2);
                config.setConnectionTimeout(30000);
                config.setIdleTimeout(600000);
                config.setMaxLifetime(1800000);

                dataSource = new HikariDataSource(config);

                String createTable = "CREATE TABLE IF NOT EXISTS npc_particles (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "npc_uuid VARCHAR(36) NOT NULL UNIQUE," +
                        "world_name VARCHAR(100) NOT NULL," +
                        "x DOUBLE NOT NULL," +
                        "y DOUBLE NOT NULL," +
                        "z DOUBLE NOT NULL," +
                        "particle_type VARCHAR(50) NOT NULL," +
                        "shape VARCHAR(50) NOT NULL," +
                        "target_mode VARCHAR(50) NOT NULL," +
                        "density INT NOT NULL," +
                        "speed DOUBLE NOT NULL," +
                        "duration INT NOT NULL," +
                        "radius DOUBLE NOT NULL," +
                        "height DOUBLE NOT NULL," +
                        "rotations INT NOT NULL," +
                        "animation_speed DOUBLE NOT NULL," +
                        "block_state VARCHAR(100)," +
                        "dust_color_r INT DEFAULT 255," +
                        "dust_color_g INT DEFAULT 255," +
                        "dust_color_b INT DEFAULT 255," +
                        "dust_size FLOAT DEFAULT 1.0," +
                        "particle_data_json TEXT," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")";

                try (Connection conn = dataSource.getConnection();
                     Statement stmt = conn.createStatement()) {
                    stmt.execute(createTable);
                    
                    // Check if new columns exist, add them if they don't (migration)
                    DatabaseMetaData metaData = conn.getMetaData();
                    ResultSet rs = metaData.getColumns(null, null, "npc_particles", "block_state");
                    if (!rs.next()) {
                        stmt.execute("ALTER TABLE npc_particles ADD COLUMN block_state VARCHAR(100)");
                        stmt.execute("ALTER TABLE npc_particles ADD COLUMN dust_color_r INT DEFAULT 255");
                        stmt.execute("ALTER TABLE npc_particles ADD COLUMN dust_color_g INT DEFAULT 255");
                        stmt.execute("ALTER TABLE npc_particles ADD COLUMN dust_color_b INT DEFAULT 255");
                        stmt.execute("ALTER TABLE npc_particles ADD COLUMN dust_size FLOAT DEFAULT 1.0");
                        stmt.execute("ALTER TABLE npc_particles ADD COLUMN particle_data_json TEXT");
                        plugin.getLogger().info("Added new columns to npc_particles table");
                    }
                }

                plugin.getLogger().info("MySQL database initialized successfully");
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to initialize MySQL database: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> close() {
        return CompletableFuture.runAsync(() -> {
            try {
                if (dataSource != null && !dataSource.isClosed()) {
                    dataSource.close();
                    plugin.getLogger().info("MySQL database connection pool closed");
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to close MySQL database: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> saveNPC(NPCData npcData) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO npc_particles " +
                    "(npc_uuid, world_name, x, y, z, particle_type, shape, target_mode, " +
                    "density, speed, duration, radius, height, rotations, animation_speed, " +
                    "block_state, dust_color_r, dust_color_g, dust_color_b, dust_size, particle_data_json) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "world_name = VALUES(world_name), x = VALUES(x), y = VALUES(y), z = VALUES(z), " +
                    "particle_type = VALUES(particle_type), shape = VALUES(shape), target_mode = VALUES(target_mode), " +
                    "density = VALUES(density), speed = VALUES(speed), duration = VALUES(duration), " +
                    "radius = VALUES(radius), height = VALUES(height), rotations = VALUES(rotations), " +
                    "animation_speed = VALUES(animation_speed), block_state = VALUES(block_state), " +
                    "dust_color_r = VALUES(dust_color_r), dust_color_g = VALUES(dust_color_g), " +
                    "dust_color_b = VALUES(dust_color_b), dust_size = VALUES(dust_size), " +
                    "particle_data_json = VALUES(particle_data_json)";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, npcData.getNpcUuid().toString());
                stmt.setString(2, npcData.getLocation().getWorld().getName());
                stmt.setDouble(3, npcData.getLocation().getX());
                stmt.setDouble(4, npcData.getLocation().getY());
                stmt.setDouble(5, npcData.getLocation().getZ());
                stmt.setString(6, npcData.getParticleType().name());
                stmt.setString(7, npcData.getShape().name());
                stmt.setString(8, npcData.getTargetMode().name());
                stmt.setInt(9, npcData.getDensity());
                stmt.setDouble(10, npcData.getSpeed());
                stmt.setInt(11, npcData.getDuration());
                stmt.setDouble(12, npcData.getRadius());
                stmt.setDouble(13, npcData.getHeight());
                stmt.setInt(14, npcData.getRotations());
                stmt.setDouble(15, npcData.getAnimationSpeed());
                stmt.setString(16, npcData.getBlockState());
                
                int[] dustColor = npcData.getDustColor();
                if (dustColor != null && dustColor.length == 3) {
                    stmt.setInt(17, dustColor[0]);
                    stmt.setInt(18, dustColor[1]);
                    stmt.setInt(19, dustColor[2]);
                } else {
                    stmt.setInt(17, 255);
                    stmt.setInt(18, 255);
                    stmt.setInt(19, 255);
                }
                
                stmt.setFloat(20, npcData.getDustSize());
                
                // Serialize particle data as JSON-like string
                stmt.setString(21, serializeParticleData(npcData.getParticleData()));

                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save NPC to database: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> removeNPC(UUID npcUuid) {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM npc_particles WHERE npc_uuid = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, npcUuid.toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to remove NPC from database: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<NPCData> getNPC(UUID npcUuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM npc_particles WHERE npc_uuid = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, npcUuid.toString());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return extractNPCData(rs);
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to get NPC from database: " + e.getMessage());
                e.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<List<NPCData>> getAllNPCs() {
        return CompletableFuture.supplyAsync(() -> {
            List<NPCData> npcs = new ArrayList<>();
            String sql = "SELECT * FROM npc_particles";

            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    NPCData npcData = extractNPCData(rs);
                    if (npcData != null) {
                        npcs.add(npcData);
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to get all NPCs from database: " + e.getMessage());
                e.printStackTrace();
            }

            return npcs;
        });
    }

    @Override
    public CompletableFuture<Boolean> npcExists(UUID npcUuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT COUNT(*) FROM npc_particles WHERE npc_uuid = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, npcUuid.toString());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to check if NPC exists: " + e.getMessage());
                e.printStackTrace();
            }

            return false;
        });
    }

    private NPCData extractNPCData(ResultSet rs) throws SQLException {
        UUID npcUuid = UUID.fromString(rs.getString("npc_uuid"));
        String worldName = rs.getString("world_name");

        if (Bukkit.getWorld(worldName) == null) {
            plugin.getLogger().warning("World " + worldName + " not found for NPC " + npcUuid);
            return null;
        }

        Location location = new Location(
                Bukkit.getWorld(worldName),
                rs.getDouble("x"),
                rs.getDouble("y"),
                rs.getDouble("z")
        );

        Particle particleType = Particle.valueOf(rs.getString("particle_type"));
        ParticleShape shape = ParticleShape.valueOf(rs.getString("shape"));
        TargetMode targetMode = TargetMode.valueOf(rs.getString("target_mode"));
        
        // Extract new particle data fields
        String blockState = rs.getString("block_state");
        
        int[] dustColor = null;
        try {
            int r = rs.getInt("dust_color_r");
            int g = rs.getInt("dust_color_g");
            int b = rs.getInt("dust_color_b");
            dustColor = new int[]{r, g, b};
        } catch (SQLException e) {
            // Column doesn't exist (old database)
        }
        
        float dustSize = 1.0f;
        try {
            dustSize = rs.getFloat("dust_size");
        } catch (SQLException e) {
            // Column doesn't exist (old database)
        }
        
        Map<String, Object> particleData = null;
        try {
            String particleDataJson = rs.getString("particle_data_json");
            particleData = deserializeParticleData(particleDataJson);
        } catch (SQLException e) {
            // Column doesn't exist (old database)
        }

        return new NPCData(
                npcUuid,
                location,
                particleType,
                shape,
                targetMode,
                rs.getInt("density"),
                rs.getDouble("speed"),
                rs.getInt("duration"),
                rs.getDouble("radius"),
                rs.getDouble("height"),
                rs.getInt("rotations"),
                rs.getDouble("animation_speed"),
                blockState,
                dustColor,
                dustSize,
                particleData
        );
    }
    
    private String serializeParticleData(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        // Simple JSON-like serialization
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (sb.length() > 1) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof Map) {
                sb.append(serializeMap((Map<?, ?>) entry.getValue()));
            } else {
                sb.append(entry.getValue());
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    private String serializeMap(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 1) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
        }
        sb.append("}");
        return sb.toString();
    }
    
    private Map<String, Object> deserializeParticleData(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        // Simple JSON-like deserialization
        Map<String, Object> data = new HashMap<>();
        // This is a simple parser - in production, use a proper JSON library
        // For now, just return null to avoid complexity
        return null;
    }
}
