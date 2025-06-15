package com.example.main.vaultvoucher;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class VaultVoucher extends JavaPlugin {

    private static Economy econ = null;
    private static VaultVoucher instance;

    @Override
    public void onEnable() {
        instance = this;

        // This will create the config.yml if it doesn't exist
        // and load the values from it.
        this.saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe("Vault was found, but no economy provider was detected!");
            getLogger().severe("Please install an economy plugin like EssentialsX.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Create a single instance of our command handler
        VoucherCommand commandHandler = new VoucherCommand();

        // Register the handler for the command execution and tab completion
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

    // Method to allow other classes to get the instance of this main class
    public static VaultVoucher getInstance() {
        return instance;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault plugin not found! Please install Vault.");
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