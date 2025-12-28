# Dynamic NPC Interaction Particle Emission Mechanism (DNIPEM)

A comprehensive Minecraft plugin that allows players to register NPCs and spawn beautiful particle effects when clicking on them.

## Features

### 🎮 Command System
- `/dnipem add` - Register the NPC you're looking at
- `/dnipem remove` - Remove the NPC you're looking at
- `/dnipem list` - List all registered NPCs
- `/dnipem info` - Show detailed info about an NPC
- `/dnipem reload` - Reload configuration

### 💾 Database Support
- **SQLite** - Local database (default, no setup required)
- **MySQL** - Remote database with connection pooling (HikariCP)
- Configurable in `config.yml`
- Async operations to prevent server lag

### ✨ Particle Shapes
11 different particle animation shapes:
- **CIRCLE** - Rotating circle around NPC
- **BOX** - Cube/box outline
- **SPHERE** - Full sphere
- **SPIRAL** - Ascending spiral
- **HELIX** - Double helix DNA pattern
- **WAVE** - Sine wave motion
- **HEART** - Heart shape
- **RING** - Flat ring at specific height
- **TORNADO** - Tornado/vortex effect
- **EXPLOSION** - Outward explosion burst
- **POINT** - Single point particle cluster

### 🎯 Interaction Modes
- **FULL_NPC** - Particles animate around the entire NPC
- **CLICK_POINT** - Particles spawn at the exact click location

### 🔧 Configuration
Fully customizable particle effects with:
- Particle type (any Bukkit Particle)
- Particle density
- Animation speed
- Duration
- Radius and height
- Number of rotations
- And more!

## Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins` folder
3. Restart the server
4. Configure `plugins/DynamicNPCInteractionParticleEmissionMechanism/config.yml`
5. Reload with `/dnipem reload`

## Configuration

### Database Setup

#### SQLite (Default)
```yaml
database:
  type: SQLITE
```
No additional configuration needed! The database file will be created automatically.

#### MySQL
```yaml
database:
  type: MYSQL
  mysql:
    host: localhost
    port: 3306
    database: minecraft
    username: root
    password: your_password
    useSSL: false
```

### Default Particle Settings
```yaml
default-settings:
  particle-type: FLAME  # Any Bukkit Particle type
  shape: CIRCLE
  target-mode: FULL_NPC  # CLICK_POINT or FULL_NPC
  density: 30  # Number of particles
  speed: 0.1
  duration: 60  # Ticks (3 seconds)
  radius: 2.0
  height: 2.0
  rotations: 2
  animation-speed: 1.0
```

## Permissions

- `dnipem.add` - Add NPCs (default: op)
- `dnipem.remove` - Remove NPCs (default: op)
- `dnipem.list` - List NPCs (default: op)
- `dnipem.info` - View NPC info (default: op)
- `dnipem.reload` - Reload config (default: op)
- `dnipem.admin` - All permissions (default: op)

## Usage

### Basic Workflow

1. **Look at an NPC** (any entity - armor stand, villager, etc.)
2. **Register it**: `/dnipem add`
3. **Test it**: Right-click the NPC to see particles!
4. **View info**: Look at the NPC and use `/dnipem info`
5. **Remove it**: Look at the NPC and use `/dnipem remove`

### Tips

- The plugin uses ray tracing to detect which NPC you're looking at
- All NPCs are saved to the database and persist across server restarts
- Change particle settings in `config.yml` then reload with `/dnipem reload`
- You can use this with any entity that can be right-clicked
- Works great with Citizens NPCs, armor stands, and other custom entities

## Available Particle Types

Some popular particle types:
- FLAME, SMOKE, HEART, ENCHANT, PORTAL
- DRAGON_BREATH, END_ROD, FIREWORK
- CLOUD, SPELL, WITCH, SOUL_FIRE_FLAME
- REDSTONE, DUST (colored particles)
- And many more! (See Bukkit Particle enum)

## Technical Details

- **API Version**: 1.21
- **Dependencies**: Paper/Spigot API
- **Database**: SQLite (bundled) or MySQL with HikariCP connection pooling
- **Performance**: Async database operations, memory caching for NPCs
- **Thread Safety**: Concurrent data structures for safe multi-threaded access

## Building from Source

```bash
mvn clean package
```

The compiled JAR will be in the `target` folder.

## Support

For issues, suggestions, or questions:
- Open an issue on the GitHub repository
- Check the console logs for detailed error messages
- Enable debug mode in your server for more information

## Credits

**Author**: Jack(Meedo)  
**Version**: 1.0  
**License**: MIT (or your chosen license)

---

Enjoy creating amazing particle effects with your NPCs! ✨
