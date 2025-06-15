package com.example.main.vaultvoucher;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class VoucherListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.PAPER || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName() || !meta.hasLore()) {
            return;
        }

        if (meta.getDisplayName().equals(ChatColor.GOLD + "" + ChatColor.BOLD + "Bank Voucher")) {
            Player player = event.getPlayer();

            if (!player.hasPermission("vaultvoucher.redeem")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to redeem vouchers.");
                return;
            }

            List<String> lore = meta.getLore();
            if (lore == null || lore.isEmpty()) {
                return;
            }

            String valueLine = ChatColor.stripColor(lore.get(0));
            String valueString = valueLine.replaceAll("[^0-9.]", "");

            double amount;
            try {
                amount = Double.parseDouble(valueString);
            } catch (NumberFormatException e) {
                return; // Not a valid voucher if the number is corrupted
            }

            event.setCancelled(true);

            Economy economy = com.example.main.vaultvoucher.VaultVoucher.getEconomy();
            EconomyResponse response = economy.depositPlayer(player, amount);

            if (response.transactionSuccess()) {
                item.setAmount(item.getAmount() - 1);
                player.sendMessage(ChatColor.GREEN + "You have successfully redeemed a voucher for $" + String.format("%,.2f", amount));
            } else {
                player.sendMessage(ChatColor.RED + "An error occurred: " + response.errorMessage);
            }
        }
    }
}