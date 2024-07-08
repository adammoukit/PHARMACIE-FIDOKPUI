package com.fido.pharmacie.controller.Authorization;

import com.fido.pharmacie.controller.Permission.Permission;
import com.fido.pharmacie.controller.RoleType.RoleType;
import com.fido.pharmacie.model.User;

import java.util.EnumMap;
import java.util.EnumSet;

public class Authorization {
    private static final EnumMap<RoleType, EnumSet<Permission>> rolePermissions = new EnumMap<>(RoleType.class);

    static {
        rolePermissions.put(RoleType.ADMINISTRATEUR, EnumSet.of(Permission.ADD_MEDICINE, Permission.MODIFY_MEDICINE, Permission.DELETE_MEDICINE, Permission.VIEW_REPORTS));
        rolePermissions.put(RoleType.PHARMACIEN, EnumSet.of(Permission.ADD_MEDICINE, Permission.MODIFY_MEDICINE, Permission.VIEW_REPORTS));
        rolePermissions.put(RoleType.CAISSIER, EnumSet.of(Permission.VIEW_REPORTS));
    }

    public static boolean hasPermission(User user, Permission permission) {
        RoleType roleType = RoleType.valueOf(user.getRole().getName().toUpperCase());
        return rolePermissions.get(roleType).contains(permission);
    }
}
