package kz.ruzgaliyev.Daras.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

//import jakarta.transaction.Transactional;
import kz.ruzgaliyev.Daras.dto.UserProfileRequest;
import kz.ruzgaliyev.Daras.entity.User;
import kz.ruzgaliyev.Daras.entity.UserProfile;
import kz.ruzgaliyev.Daras.repository.UserProfileRepository;
import kz.ruzgaliyev.Daras.repository.UserRepository;

@Service
public class UserProfileService {
    
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserRepository userRepository, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }


    public UserProfile getProfileByUserId(Long userId) {

        checkProfileAccess(userId);

        return userProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId));
    }

    
    @Transactional 
    public UserProfile saveOrUpdateProfile(Long userId, UserProfileRequest request) {

        checkProfileAccess(userId);

        User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId));

        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(new UserProfile());

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setBio(request.getBio());

        if(profile.getUser() == null) {
            profile.setUser(user);
        }


        return userProfileRepository.save(profile);


    }




    @Transactional
    public void deleteProfileByUserId(Long userId) {

        checkProfileAccess(userId);

        if(!userProfileRepository.existsByUserId(userId)) { 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Cannot delete: Profile does not exist for user ID: " + userId);
        }


        userProfileRepository.deleteByUserId(userId);
        
    }


    private void checkProfileAccess(Long userId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with this email not found"));
        
        if( !(currentUser.getId() == userId || currentUser.getRole().equals("ADMIN")) ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to access this profile!");
        }

    }


    


}
