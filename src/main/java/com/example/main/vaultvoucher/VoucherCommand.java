package com.example.main.vaultvoucher;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class VoucherCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /voucher <amount>");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[0]);
            if (amount <= 0) {
                player.sendMessage(ChatColor.RED + "The amount must be a positive number.");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "That is not a valid number.");
            return true;
        }

        Economy economy = com.example.main.vaultvoucher.VaultVoucher.getEconomy();
        double balance = economy.getBalance(player);

        if (balance < amount) {
            player.sendMessage(ChatColor.RED + "You do not have enough funds to create this voucher.");
            return true;
        }

        EconomyResponse response = economy.withdrawPlayer(player, amount);
        if (response.transactionSuccess()) {
            ItemStack voucher = createVoucher(amount, player.getName());
            player.getInventory().addItem(voucher);
            player.sendMessage(ChatColor.GREEN + "You have successfully created a voucher for $" + String.format("%,.2f", amount));
        } else {
            player.sendMessage(ChatColor.RED + "An error occurred: " + response.errorMessage);
        }

        return true;
    }

    private ItemStack createVoucher(double amount, String creatorName) {
        ItemStack voucher = new ItemStack(Material.PAPER);
        ItemMeta meta = voucher.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Bank Voucher");
        meta.setLore(Arrays.asList(
                ChatColor.WHITE + "Value: $" + String.format("%,.2f", amount),
                ChatColor.GRAY + "Right-click to redeem.",
                ChatColor.DARK_GRAY + "Created by: " + creatorName
        ));

        voucher.setItemMeta(meta);
        return voucher;
    }
}