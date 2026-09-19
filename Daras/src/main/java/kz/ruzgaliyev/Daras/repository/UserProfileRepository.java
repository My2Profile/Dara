package kz.ruzgaliyev.Daras.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;
import kz.ruzgaliyev.Daras.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUserId(Long userId);

    boolean existsByUserId(Long id);

    @Transactional
    void deleteByUserId(Long userId);
    
}
