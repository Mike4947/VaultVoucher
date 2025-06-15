package com.example.main.vaultvoucher;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class VoucherCommand implements CommandExecutor, TabCompleter {

    // The unique key for our hidden NBT data. This makes the voucher unforgeable.
    public static final NamespacedKey VOUCHER_VALUE_KEY = new NamespacedKey(VaultVoucher.getInstance(), "voucher-value");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.usage_error"));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                handleCreate(sender, args);
                break;
            case "give":
                handleGive(sender, args);
                break;
            case "reload":
                handleReload(sender);
                break;
            default:
                sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.usage_error"));
                break;
        }

        return true;
    }

    private void handleCreate(CommandSender sender, String[] args) {
        // /voucher create <amount> [player]
        if (args.length < 2) {
            sendMessage(sender, "&cUsage: /voucher create <amount> [player]");
            return;
        }

        double amount = parseAmount(args[1]);
        if (amount <= 0) {
            sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.invalid_amount"));
            return;
        }

        Player target;
        if (args.length > 2) { // Admin creating for another player
            if (!sender.hasPermission("vaultvoucher.create.others")) {
                sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.no_permission"));
                return;
            }
            target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.player_not_found"));
                return;
            }
        } else { // Player creating for themselves
            if (!(sender instanceof Player)) {
                sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.not_a_player"));
                return;
            }
            if (!sender.hasPermission("vaultvoucher.create")) {
                sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.no_permission"));
                return;
            }
            target = (Player) sender;
        }

        Economy economy = VaultVoucher.getEconomy();
        if (economy.getBalance(target) < amount) {
            sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.not_enough_money"));
            return;
        }

        economy.withdrawPlayer(target, amount);
        ItemStack voucher = createVoucher(amount, sender.getName());
        target.getInventory().addItem(voucher);

        sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.voucher_created").replace("{value}", formatCurrency(amount)));
        if (sender != target) {
            sendMessage(target, VaultVoucher.getInstance().getConfig().getString("messages.voucher_received")
                    .replace("{value}", formatCurrency(amount))
                    .replace("{player}", sender.getName()));
        }
    }

    private void handleGive(CommandSender sender, String[] args) {
        // /voucher give <player> <amount>
        if (!sender.hasPermission("vaultvoucher.give")) {
            sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.no_permission"));
            return;
        }

        if (args.length < 3) {
            sendMessage(sender, "&cUsage: /voucher give <player> <amount>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.player_not_found"));
            return;
        }

        double amount = parseAmount(args[2]);
        if (amount <= 0) {
            sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.invalid_amount"));
            return;
        }

        ItemStack voucher = createVoucher(amount, sender.getName());
        target.getInventory().addItem(voucher);

        sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.voucher_given")
                .replace("{value}", formatCurrency(amount))
                .replace("{player}", target.getName()));
        sendMessage(target, VaultVoucher.getInstance().getConfig().getString("messages.voucher_received")
                .replace("{value}", formatCurrency(amount))
                .replace("{player}", sender.getName()));
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("vaultvoucher.reload")) {
            sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.no_permission"));
            return;
        }
        VaultVoucher.getInstance().reloadConfig();
        sendMessage(sender, VaultVoucher.getInstance().getConfig().getString("messages.config_reloaded"));
    }

    public static ItemStack createVoucher(double amount, String creatorName) {
        FileConfiguration config = VaultVoucher.getInstance().getConfig();

        Material material = Material.matchMaterial(config.getString("voucher_item.material", "PAPER"));
        ItemStack voucher = new ItemStack(material != null ? material : Material.PAPER);
        ItemMeta meta = voucher.getItemMeta();

        // Set name and lore from config
        String name = config.getString("voucher_item.name");
        meta.setDisplayName(formatMessage(name));

        List<String> lore = new ArrayList<>();
        for (String line : config.getStringList("voucher_item.lore")) {
            lore.add(formatMessage(line
                    .replace("{value}", formatCurrency(amount))
                    .replace("{creator}", creatorName)));
        }
        meta.setLore(lore);

        // --- SECURE NBT DATA ---
        meta.getPersistentDataContainer().set(VOUCHER_VALUE_KEY, PersistentDataType.DOUBLE, amount);
        voucher.setItemMeta(meta);

        return voucher;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("create");
            suggestions.add("give");
            suggestions.add("reload");
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("give")) {
                Bukkit.getOnlinePlayers().forEach(p -> suggestions.add(p.getName()));
            } else if (subCommand.equals("create")) {
                suggestions.addAll(List.of("100", "1000", "10000"));
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("create")) {
                Bukkit.getOnlinePlayers().forEach(p -> suggestions.add(p.getName()));
            } else if (subCommand.equals("give")) {
                suggestions.addAll(List.of("100", "1000", "10000"));
            }
        }

        String currentArg = args[args.length - 1].toLowerCase();
        for (String s : suggestions) {
            if (s.toLowerCase().startsWith(currentArg)) {
                completions.add(s);
            }
        }
        return completions;
    }

    // Utility methods
    private double parseAmount(String amountStr) {
        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance().format(amount);
    }

    private static String formatMessage(String message) {
        if(message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static void sendMessage(CommandSender recipient, String message) {
        if (message == null || message.isEmpty()) return;
        // Get the prefix string from the config file.
        String prefix = VaultVoucher.getInstance().getConfig().getString("messages.prefix", "&e[Voucher] &r");
        // Correctly REPLACE the placeholder in the message.
        String finalMessage = message.replace("{prefix}", prefix);
        recipient.sendMessage(formatMessage(finalMessage));
    }
}