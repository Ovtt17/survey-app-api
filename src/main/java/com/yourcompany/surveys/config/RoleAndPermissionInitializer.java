package com.yourcompany.surveys.config;

import com.yourcompany.surveys.entity.Permission;
import com.yourcompany.surveys.entity.Permissions;
import com.yourcompany.surveys.entity.Role;
import com.yourcompany.surveys.repository.PermissionRepository;
import com.yourcompany.surveys.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.stream.Collectors;

import static com.yourcompany.surveys.entity.Roles.*;


@Configuration
@RequiredArgsConstructor
public class RoleAndPermissionInitializer {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Bean
    public CommandLineRunner initializeRolesAndPermissions() {
        return args -> {
            for (Permissions permission : Permissions.values()) {
                permissionRepository.findByName(permission.getName())
                        .orElseGet(() -> {
                            Permission permissionEntity = new Permission();
                            permissionEntity.setName(permission.getName());
                            return permissionRepository.save(permissionEntity);
                        });
            }

            var allPermissions = permissionRepository.findAll();
            Set<Permission> adminPermissions = Set.copyOf(allPermissions);
            Set<Permission> managerPermissions = allPermissions.stream()
                    .filter(permission -> permission.getName().startsWith("manager:"))
                    .collect(Collectors.toSet());
            Set<Permission> userPermissions = Set.of();

            createRoleIfNotExists(ADMIN.name(), adminPermissions);
            createRoleIfNotExists(MANAGER.name(), managerPermissions);
            createRoleIfNotExists(USER.name(), userPermissions);
        };
    }

    private void createRoleIfNotExists(String roleName, Set<Permission> permissions) {
        roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setName(roleName);
            role.setPermissions(permissions);
            return roleRepository.save(role);
        });
    }
}
