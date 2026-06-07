package quest.yuzhou.portalsystem;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import quest.yuzhou.portalsystem.Commands.CommandManager;
import quest.yuzhou.portalsystem.Commands.TabCompleter;
import quest.yuzhou.portalsystem.Menu.MenuHandler;
import quest.yuzhou.portalsystem.Listeners.*;
import quest.yuzhou.portalsystem.Modal.Portal;
import quest.yuzhou.portalsystem.Utilities.PortalStorageUtil;

import java.io.File;
import java.io.IOException;

import static quest.yuzhou.portalsystem.Utilities.BlockTypeGetterAndConfigLoader.isPortalBlockTypeConfigValid;
import static quest.yuzhou.portalsystem.Utilities.PortalStorageUtil.*;

public final class PortalSystem extends JavaPlugin {

    private static PortalSystem plugin;
    private static Economy economy = null;

    public static String prefix;
    public static World mainWorld;
    public static World virtualWorld;

    @Override
    public void onEnable() {
        // Plugin startup logic

        plugin = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder() + "/config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        FileConfiguration config = getConfig();

        String mainWorldName = config.getString("mainWorld");
        String virtualWorldName = config.getString("virtualWorld");

        prefix = config.getString("prefix");
        mainWorld = getServer().getWorld(mainWorldName);
        virtualWorld = getServer().getWorld(virtualWorldName);

        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (mainWorld == null || virtualWorld ==  null) {
            getLogger().info(" Can't find the world '" + mainWorldName + "' and/or '" + virtualWorldName + "'.");
            getLogger().info(" Disabling plugin...");
            getPluginLoader().disablePlugin(this);
        }

        if (!isPortalBlockTypeConfigValid()) {
            getLogger().severe("Portal Block Type in config is invalid!");
            getPluginLoader().disablePlugin(this);
        }

        if (getServer().getPluginManager().getPlugin("ResourcePoint") == null) {
            getLogger().severe(" ResourcePoint plugin not found!");
            getPluginLoader().disablePlugin(this);
        }

        getCommand("porman").setExecutor(new CommandManager());
        getCommand("porman").setTabCompleter(new TabCompleter());

        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkInteractRestriction(), this);
        getServer().getPluginManager().registerEvents(new PortalBlockListener(), this);
        getServer().getPluginManager().registerEvents(new PortalMainBlockProtecter(), this);
        getServer().getPluginManager().registerEvents(new MenuHandler(), this);
        try {
            PortalStorageUtil.loadPortals();
        } catch (IOException e) {
            getLogger().severe("Error while loading portals: " + e.getMessage());
        }

        for (Portal portal : getPortals()) {
            portal.rescheduleClearing();
            if (!portal.isActive()) {
                removePortal(portal);
            }
        }

        getLogger().info("[PortalSystem] Loaded successfully");
        getLogger().info("__________              __         .__");
        getLogger().info("\\______   \\____________/  |______  |  |");
        getLogger().info(" |     ___/  _ \\_  __ \\   __\\__  \\ |  | ");
        getLogger().info(" |    |  (  <_> )  | \\/|  |  / __ \\|  |__");
        getLogger().info(" |____|   \\____/|__|   |__| (____  /____/ ");
        getLogger().info("                                 \\/");
        getLogger().info("");
        getLogger().info("  _________              __");
        getLogger().info(" /   _____/__.__. ______/  |_  ____   _____");
        getLogger().info(" \\_____  <   |  |/  ___|   __\\/ __ \\ /     \\");
        getLogger().info(" /        \\___  |\\___ \\ |  | \\  ___/|  Y Y  \\");
        getLogger().info("/_______  / ____/____  >|__|  \\___  >__|_|  /");
        getLogger().info("        \\/\\/         \\/           \\/      \\/");

        // Bukkit.getScheduler().scheduleSyncRepeatingTask(this, MobSpaner::spawnMobs, 20 * 60, 20 * 60 * 5);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            savePortals();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getLogger().info("[PortalSystem] Bye!");
    }

    public static PortalSystem getPlugin() {
        return plugin;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return economy;
    }

}
