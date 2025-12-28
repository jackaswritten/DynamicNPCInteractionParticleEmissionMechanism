package me.jack.dynamicNPCInteractionParticleEmissionMechanism.commands;

import me.jack.dynamicNPCInteractionParticleEmissionMechanism.managers.NPCManager;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.models.NPCData;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.utils.ConfigManager;
import me.jack.dynamicNPCInteractionParticleEmissionMechanism.utils.RayTraceUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DNIPEMCommand implements CommandExecutor, TabCompleter {
    private final NPCManager npcManager;
    private final ConfigManager configManager;

    public DNIPEMCommand(NPCManager npcManager, ConfigManager configManager) {
        this.npcManager = npcManager;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§e=== DNIPEM Commands ===");
            sender.sendMessage("§7/dnipem add - Register the NPC you're looking at");
            sender.sendMessage("§7/dnipem remove - Remove the NPC you're looking at");
            sender.sendMessage("§7/dnipem list - List all registered NPCs");
            sender.sendMessage("§7/dnipem info - Show info about the NPC you're looking at");
            sender.sendMessage("§7/dnipem reload - Reload configuration");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "add":
                return handleAdd(sender);
            case "remove":
                return handleRemove(sender);
            case "list":
                return handleList(sender);
            case "info":
                return handleInfo(sender);
            case "reload":
                return handleReload(sender);
            default:
                sender.sendMessage("§cUnknown subcommand. Use /dnipem for help.");
                return true;
        }
    }

    private boolean handleAdd(CommandSender sender) {
        if (!sender.hasPermission("dnipem.add")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        Entity targetEntity = RayTraceUtil.getTargetEntity(player);

        if (targetEntity == null) {
            sender.sendMessage(configManager.getMessage("no-npc-found"));
            return true;
        }

        // Create NPC data with default settings
        NPCData npcData = new NPCData(
                targetEntity.getUniqueId(),
                targetEntity.getLocation(),
                configManager.getDefaultParticleType(),
                configManager.getDefaultShape(),
                configManager.getDefaultTargetMode(),
                configManager.getDefaultDensity(),
                configManager.getDefaultSpeed(),
                configManager.getDefaultDuration(),
                configManager.getDefaultRadius(),
                configManager.getDefaultHeight(),
                configManager.getDefaultRotations(),
                configManager.getDefaultAnimationSpeed()
        );

        npcManager.registerNPC(npcData).thenRun(() -> {
            sender.sendMessage(configManager.getMessage("npc-added"));
        }).exceptionally(throwable -> {
            sender.sendMessage(configManager.getMessage("database-error"));
            throwable.printStackTrace();
            return null;
        });

        return true;
    }

    private boolean handleRemove(CommandSender sender) {
        if (!sender.hasPermission("dnipem.remove")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        Entity targetEntity = RayTraceUtil.getTargetEntity(player);

        if (targetEntity == null) {
            sender.sendMessage(configManager.getMessage("no-npc-found"));
            return true;
        }

        if (!npcManager.isRegistered(targetEntity.getUniqueId())) {
            sender.sendMessage("§cThis NPC is not registered.");
            return true;
        }

        npcManager.unregisterNPC(targetEntity.getUniqueId()).thenRun(() -> {
            sender.sendMessage(configManager.getMessage("npc-removed"));
        }).exceptionally(throwable -> {
            sender.sendMessage(configManager.getMessage("database-error"));
            throwable.printStackTrace();
            return null;
        });

        return true;
    }

    private boolean handleList(CommandSender sender) {
        if (!sender.hasPermission("dnipem.list")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return true;
        }

        Map<UUID, NPCData> npcs = npcManager.getAllNPCs();

        if (npcs.isEmpty()) {
            sender.sendMessage("§eNo NPCs registered.");
            return true;
        }

        sender.sendMessage(configManager.getMessage("npc-list-header"));
        for (NPCData npcData : npcs.values()) {
            String message = configManager.getMessage("npc-list-entry",
                    "uuid", npcData.getNpcUuid().toString(),
                    "world", npcData.getLocation().getWorld().getName(),
                    "x", String.format("%.2f", npcData.getLocation().getX()),
                    "y", String.format("%.2f", npcData.getLocation().getY()),
                    "z", String.format("%.2f", npcData.getLocation().getZ())
            );
            sender.sendMessage(message);
        }
        sender.sendMessage("§7Total: §f" + npcs.size() + " §7NPCs");

        return true;
    }

    private boolean handleInfo(CommandSender sender) {
        if (!sender.hasPermission("dnipem.info")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        Entity targetEntity = RayTraceUtil.getTargetEntity(player);

        if (targetEntity == null) {
            sender.sendMessage(configManager.getMessage("no-npc-found"));
            return true;
        }

        if (!npcManager.isRegistered(targetEntity.getUniqueId())) {
            sender.sendMessage("§cThis NPC is not registered.");
            return true;
        }

        NPCData npcData = npcManager.getNPC(targetEntity.getUniqueId());
        if (npcData == null) {
            sender.sendMessage("§cFailed to retrieve NPC data.");
            return true;
        }

        sender.sendMessage(configManager.getMessage("npc-info-header"));
        sender.sendMessage(configManager.getMessage("npc-info-uuid",
                "uuid", npcData.getNpcUuid().toString()));
        sender.sendMessage(configManager.getMessage("npc-info-location",
                "world", npcData.getLocation().getWorld().getName(),
                "x", String.format("%.2f", npcData.getLocation().getX()),
                "y", String.format("%.2f", npcData.getLocation().getY()),
                "z", String.format("%.2f", npcData.getLocation().getZ())));
        sender.sendMessage(configManager.getMessage("npc-info-particle",
                "particle", npcData.getParticleType().name()));
        sender.sendMessage(configManager.getMessage("npc-info-shape",
                "shape", npcData.getShape().name()));
        sender.sendMessage(configManager.getMessage("npc-info-mode",
                "mode", npcData.getTargetMode().name()));
        sender.sendMessage("§7Density: §f" + npcData.getDensity());
        sender.sendMessage("§7Speed: §f" + npcData.getSpeed());
        sender.sendMessage("§7Duration: §f" + npcData.getDuration() + " ticks");
        sender.sendMessage("§7Radius: §f" + npcData.getRadius());
        sender.sendMessage("§7Height: §f" + npcData.getHeight());
        sender.sendMessage("§7Rotations: §f" + npcData.getRotations());
        sender.sendMessage("§7Animation Speed: §f" + npcData.getAnimationSpeed());

        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("dnipem.reload")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return true;
        }

        configManager.reload();
        npcManager.loadAllNPCs().thenRun(() -> {
            sender.sendMessage(configManager.getMessage("reload-complete"));
        }).exceptionally(throwable -> {
            sender.sendMessage(configManager.getMessage("database-error"));
            throwable.printStackTrace();
            return null;
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("add", "remove", "list", "info", "reload");
            List<String> completions = new ArrayList<>();
            
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
            
            return completions;
        }

        return new ArrayList<>();
    }
}
