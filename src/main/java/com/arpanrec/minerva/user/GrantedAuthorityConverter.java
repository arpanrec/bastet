package com.arpanrec.minerva.user;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.HashSet;
import java.util.Set;

@Converter
public class GrantedAuthorityConverter implements AttributeConverter<Set<UserRole>, String> {

    private final String ROLES_DELIMITER = ",";
    private final String PRIVILEGES_DELIMITER = ";";

    @Override
    public String convertToDatabaseColumn(Set<UserRole> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }

        StringBuilder rolesString = new StringBuilder();
        for (UserRole role : attribute) {

            rolesString.append(role.getName());
            Set<UserPrivilege> privileges = role.getPrivileges();
            if (privileges == null || privileges.isEmpty()) {
                rolesString.append(ROLES_DELIMITER);
                continue;
            }
            for (UserPrivilege privilege : privileges) {
                rolesString.append(PRIVILEGES_DELIMITER).append(privilege.getName());
            }

            rolesString.append(ROLES_DELIMITER);

        }

        return rolesString.toString();
    }

    @Override
    public Set<UserRole> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        Set<UserRole> roles = new HashSet<>();
        String[] rolesArray = dbData.split(ROLES_DELIMITER);
        for (String roleString : rolesArray) {

            if (roleString.strip().trim().isEmpty()) {
                continue;
            }

            String[] roleAndPrivileges = roleString.split(PRIVILEGES_DELIMITER);
            UserRole role = new UserRole();
            role.setName(RoleTypes.valueOf(roleAndPrivileges[0]));
            Set<UserPrivilege> privileges = new HashSet<>();

            for (int i = 1; i < roleAndPrivileges.length; i++) {
                UserPrivilege privilege = new UserPrivilege();
                privilege.setName(PrivilegeTypes.valueOf(roleAndPrivileges[i]));
                privileges.add(privilege);
            }

            role.setPrivileges(privileges);
            roles.add(role);
        }

        return roles;
    }
}
