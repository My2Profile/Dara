package kz.ruzgaliyev.Daras.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kz.ruzgaliyev.Daras.dto.UserProfileRequest;
import kz.ruzgaliyev.Daras.entity.UserProfile;
import kz.ruzgaliyev.Daras.service.UserProfileService;

@RestController
@RequestMapping("/users")
public class UserProfileController {
    
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }


    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfile> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getProfileByUserId(userId));
    }

    @PostMapping("{userId}/profile")
    public ResponseEntity<UserProfile> saveOrUpdateProfile(@PathVariable Long userId, @RequestBody @Valid UserProfileRequest request) {
        return ResponseEntity.ok(userProfileService.saveOrUpdateProfile(userId, request));
    }

    @DeleteMapping("{userId}/profile")
    public ResponseEntity<String> deleteProfileByUserId(@PathVariable Long userId) {
        userProfileService.deleteProfileByUserId(userId);

        return ResponseEntity.ok("User profile deleted successfully!");
    }

}
