package com.example.main.vaultvoucher;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class VaultVoucher extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            // The setupEconomy() method will now print the specific error.
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Create a single instance of our command handler
        VoucherCommand commandHandler = new VoucherCommand();

        // Register the new handler for BOTH the command execution and tab completion
        this.getCommand("voucher").setExecutor(commandHandler);
        this.getCommand("voucher").setTabCompleter(commandHandler);

        // Register the event listener
        this.getServer().getPluginManager().registerEvents(new VoucherListener(), this);

        getLogger().info("VaultVoucher has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("VaultVoucher has been disabled.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
}