package com.hng.profile.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.hng.profile.model.Profile;
import com.hng.profile.repository.ProfileRepository;

@Service
public class ProfileService {

  private final ProfileRepository profileRepository;
  private final EnrichmentService enrichmentService;

  public ProfileService(ProfileRepository profileRepository, EnrichmentService enrichmentService) {
    this.profileRepository = profileRepository;
    this.enrichmentService = enrichmentService;
  }

  public ProfileCreationResult createOrGetProfile(String name) {
    String cleanName = name.trim().toLowerCase();
    Optional<Profile> existing = profileRepository.findByNameIgnoreCase(cleanName);
    if (existing.isPresent()) {
      return new ProfileCreationResult(existing.get(), true);
    }

    Profile newProfile = enrichmentService.enrichName(cleanName);
    Profile savedProfile = profileRepository.save(newProfile);

    return new ProfileCreationResult(savedProfile, false);
  }

  public Profile getProfileById(UUID id) {
    return profileRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Profile not found"));
  }

  public List<Profile> getProfiles(String gender, String countryId, String ageGroup) {
    Profile filterProbe = new Profile();
    if (gender != null)
      filterProbe.setGender(gender);
    if (countryId != null)
      filterProbe.setCountryId(countryId);
    if (ageGroup != null)
      filterProbe.setAgeGroup(ageGroup);

    ExampleMatcher matcher = ExampleMatcher.matching()
        .withIgnoreNullValues()
        .withIgnoreCase();

    Example<Profile> example = Example.of(filterProbe, matcher);
    return profileRepository.findAll(example);
  }

  public void deleteProfile(UUID id) {
    if (!profileRepository.existsById(id)) {
      throw new RuntimeException("Profile not found");
    }
    profileRepository.deleteById(id);
  }

  public record ProfileCreationResult(Profile profile, boolean alreadyExisted) {
  }
}
