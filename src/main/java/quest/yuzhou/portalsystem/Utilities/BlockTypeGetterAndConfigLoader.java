package quest.yuzhou.portalsystem.Utilities;

import org.bukkit.Material;

import java.util.List;

import static quest.yuzhou.portalsystem.PortalSystem.getPlugin;

public class BlockTypeGetterAndConfigLoader {

    public static boolean isPortalBlockTypeConfigValid() {
        List<String> portalBlockTypeList = getPlugin().getConfig().getStringList("portalBlockType");

        if (portalBlockTypeList.size() > 20)
            return false;

        for (String portalBlockType : portalBlockTypeList) {
            try {
                Material material = Material.valueOf(portalBlockType);
                if (!material.isBlock()) {
                    getPlugin().getLogger().info("The portalBlockType '" + portalBlockType + "' is invalid!");
                    return false;
                }
            } catch (IllegalArgumentException e) {
                getPlugin().getLogger().info("Error while loading portalBlockType");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static Material getPortalBlockType(int level) throws IndexOutOfBoundsException {
        List<String> portalBlockTypeList = getPlugin().getConfig().getStringList("portalBlockType");
        String materialString = portalBlockTypeList.get(level - 1).toUpperCase();
        return Material.getMaterial(materialString);
    }

}
