package com.example.main.vaultvoucher;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.NumberFormat;

public class VoucherListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // We only care about right-click actions
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = VoucherCommand.VOUCHER_VALUE_KEY;

        // --- SECURE CHECK ---
        // Check if the item has our hidden NBT data. This is unforgeable.
        if (meta.getPersistentDataContainer().has(key, PersistentDataType.DOUBLE)) {
            Player player = event.getPlayer();

            if (!player.hasPermission("vaultvoucher.redeem")) {
                sendMessage(player, VaultVoucher.getInstance().getConfig().getString("messages.no_permission"));
                return;
            }

            // Cancel the event to prevent any other action
            event.setCancelled(true);

            // Get the value from the hidden data
            double amount = meta.getPersistentDataContainer().get(key, PersistentDataType.DOUBLE);
            Economy economy = VaultVoucher.getEconomy();
            economy.depositPlayer(player, amount);

            // Remove one voucher from the player's hand
            item.setAmount(item.getAmount() - 1);

            String message = VaultVoucher.getInstance().getConfig().getString("messages.voucher_redeemed");
            sendMessage(player, message.replace("{value}", NumberFormat.getCurrencyInstance().format(amount)));
        }
    }

    private void sendMessage(Player recipient, String message) {
        String prefix = VaultVoucher.getInstance().getConfig().getString("messages.prefix", "&e[Voucher] &r");
        recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{prefix}", prefix)));
    }
}