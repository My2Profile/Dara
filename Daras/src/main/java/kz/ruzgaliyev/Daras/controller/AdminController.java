package kz.ruzgaliyev.Daras.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import kz.ruzgaliyev.Daras.dto.UpdateUserStatusRequest;
import kz.ruzgaliyev.Daras.dto.UpdateUserRoleRequest;
import kz.ruzgaliyev.Daras.dto.UserResponse;
import kz.ruzgaliyev.Daras.service.AdminService;
import kz.ruzgaliyev.Daras.dto.BlockUserRequest;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    @GetMapping("/test")
    public String testAdmin() {
        return "Access Granted! The security config and jwtconfig is working!";
    }

    @GetMapping("/getAll")
    public List<UserResponse> getAll() {
        return adminService.getAllUsers();
    }

    @PatchMapping("/users/{id}/role")
    public String changeUserRole(@PathVariable Long id, @RequestBody UpdateUserRoleRequest request) {
        adminService.changeUserRole(id, request.getRole());

        return "Success";
    }

    @PatchMapping("/users/{id}/active")
    public String changeUserState(@PathVariable Long id, @RequestBody UpdateUserStatusRequest userState) {

        adminService.changeUserStatus(id, userState.getState());

        return "Success";

    }

    @GetMapping("/users/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return adminService.getUserById(id);
    }

    @DeleteMapping("/users/{id}/delete")
    public String softDeleteUser(@PathVariable Long id, HttpServletRequest request) {
        String adminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        adminService.softDeleteUser(id, adminEmail, request);
        return "User soft deleted successfully";
    }

    @PatchMapping("/users/{id}/restore")
    public String restoreUser(@PathVariable Long id, HttpServletRequest request) {
        String adminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        adminService.restoreUser(id, adminEmail, request);
        return "User restored Successfully";
    }

    @PatchMapping("/users/{id}/block")
    public String blockUser(@PathVariable Long id, @RequestBody BlockUserRequest body, HttpServletRequest request) {
        String adminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        adminService.blockUser(id, body.getReason(), adminEmail, request);
        return "User blocked successfully";
    }

    @PatchMapping("/users/{id}/unblock")
    public String unblockUser(@PathVariable Long id, HttpServletRequest request) {
        String adminEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        adminService.unblockUser(id, adminEmail, request);
        return "User unblocked successfully";
    }

}
