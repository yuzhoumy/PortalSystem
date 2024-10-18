package quest.yuzhou.portalsystem.Utilities;

import com.google.gson.Gson;
import quest.yuzhou.portalsystem.Modal.Portal;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.UUID;

import static quest.yuzhou.portalsystem.PortalSystem.getPlugin;
import static quest.yuzhou.portalsystem.PortalSystem.prefix;

public class PortalStorageUtil {

    private static ArrayList<Portal> portals = new ArrayList<>();

    public static void addPortal(Portal portal) throws IllegalArgumentException {
        for (Portal loopPortal : portals) {
            if (loopPortal.getName().equalsIgnoreCase(portal.getName())) {
                throw new IllegalArgumentException("There is already a portal with same name");
            }
        }
        portals.add(portal);
    }

    public static void removePortal(Portal portal) {
        portals.remove(portal);
    }

    public static ArrayList<Portal> findPortalsByAdmin(UUID adminUUID) {
        ArrayList<Portal> portalArrayList = new ArrayList<>();
        for (Portal portal : portals) {
            for (String loopUUID : portal.getAllAdmin()) {
                if (adminUUID.toString().equals(loopUUID)) {
                    portalArrayList.add(portal);
                }
            }
        }
        return portalArrayList;
    }

    public static ArrayList<Portal> findPortalsByMember(UUID memberUUID) {
        ArrayList<Portal> portalArrayList = new ArrayList<>();
        for (Portal portal : portals) {
            for (String loopUUID : portal.getAllMembers()) {
                if (memberUUID.toString().equals(loopUUID)) {
                    portalArrayList.add(portal);
                }
            }
        }
        return portalArrayList;
    }

    public static Portal findPortalByName(String name) {
        for (Portal portal : portals) {
            if (portal.getName().equalsIgnoreCase(name)) {
                return portal;
            }
        }
        return null;
    }
    public static void updatePortal(Portal newPortal) throws NoSuchElementException {
        for (Portal portal : portals) {
            if (portal.getName().equalsIgnoreCase(newPortal.getName())) {
                portals.remove(portal);
                portals.add(newPortal);
                return;
            }
        }
        throw new NoSuchElementException("No such portal found in portals array list");
    }

    public static void savePortals() throws IOException {
        Gson gson = new Gson();
        File file = new File(getPlugin().getDataFolder().getAbsolutePath() + "/portals.json");
        file.getParentFile().mkdir();
        file.createNewFile();
        Writer writer = new FileWriter(file, false);
        gson.toJson(portals, writer);
        writer.flush();
        writer.close();
        getPlugin().getLogger().info(prefix + " Portals saved");
    }

    public static void recordPortalAfterDelete(Portal portal) throws IOException {
        Gson gson = new Gson();
        File file = new File(getPlugin().getDataFolder().getAbsolutePath() + "/deletedPortals.json");
        Writer writer = new FileWriter(file, true);
        gson.toJson(portal, writer);
        writer.flush();
        writer.close();
        getPlugin().getLogger().info(portal.getName() + " has been recorded into deletedPortals.json");
    }

    public static void loadPortals() throws IOException {

        Gson gson = new Gson();
        File file = new File(getPlugin().getDataFolder().getAbsolutePath() + "/portals.json");
        if (file.exists()) {
            Reader reader = new FileReader(file);
            Portal[] p = gson.fromJson(reader, Portal[].class);

            if (p != null) {
                portals = new ArrayList<>(Arrays.asList(p));
                getPlugin().getLogger().info("[PortalSystem] Portals loaded from portals.json");
            } else {
                portals = new ArrayList<>();
                getPlugin().getLogger().info("[PortalSystem] Can't load portals from portals.json as the file is empty.");
            }

        } else {
            portals = new ArrayList<>();
            getPlugin().getLogger().info("[PortalSystem] portals.json doesn't exists.");
        }

    }

    public static ArrayList<Portal> getPortals() {
        return portals;
    }


}
