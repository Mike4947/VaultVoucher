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
        // Check for Vault and hook into it
        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found! Please install Vault.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register command and listener
        this.getCommand("voucher").setExecutor(new VoucherCommand());
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