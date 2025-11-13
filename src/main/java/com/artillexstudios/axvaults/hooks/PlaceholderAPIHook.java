package com.artillexstudios.axvaults.hooks;

import com.artillexstudios.axvaults.AxVaults;
import com.artillexstudios.axvaults.utils.PermissionUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    @NotNull
    public String getIdentifier() {
        return "axvaults";
    }

    @NotNull
    public String getAuthor() {
        return "ArtillexStudios";
    }

    @NotNull
    public String getVersion() {
        return AxVaults.getInstance().getDescription().getVersion();
    }

    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }
        if (identifier.equalsIgnoreCase("vault_count")) {
            return String.valueOf(getVaultCount(player));
        }
        return null;
    }

    private int getVaultCount(@NotNull Player player) {
        if (AxVaults.CONFIG.getInt("permission-mode", 0) == 0) {
            int count = 0;
            for (int i = 1; i <= 100 && PermissionUtils.hasPermission(player, i); ++i) {
                ++count;
            }
            return count;
        }
        if (player.isOp() || player.hasPermission("*")) {
            return AxVaults.CONFIG.getInt("max-vaults", 100);
        }
        int max = 0;
        if (PermissionUtils.hasPermission(player, 1)) {
            max = 1;
        }
        for (PermissionAttachmentInfo effectivePermission : player.getEffectivePermissions()) {
            String permission;
            if (!effectivePermission.getValue() || !(permission = effectivePermission.getPermission()).startsWith("axvaults.vault."))
                continue;
            try {
                int value = Integer.parseInt(permission.substring(permission.lastIndexOf(46) + 1));
                if (value <= max) continue;
                max = value;
            } catch (NumberFormatException ignored) {
            }
        }
        return max;
    }
}
