package com.hng.profile.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hng.profile.model.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

  Optional<Profile> findByNameIgnoreCase(String name);
}
