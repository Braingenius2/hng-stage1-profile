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

  public page<Profile> getProfiles(String gender, String ageGroup, String countryId, Integer minAge,
      Integer maxAge, Double minGenderProb, Double minCountryProb, String sortBy, String order, int page, int limit) {
    Sort sort = Sort.unsorted();
    if (sortBy != null && !sortBy.isBlank()) {
      Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
      String sortProperty = switch (sortBy.toLowerCase()) {
        case "created_at" -> "createdAt";
        case "gender_probability" -> "genderProbability";
        case "country_probability" -> "countryProbability";
        default -> "age";
      };
      sort = Sort.by(direction, sortProperty);
    }

    int springPage = page > 0 ? page - 1 : 0;
    int safeLimit = Math.min(Math.max(limit, 1), 50);
    Pageable pageable = PageRequest.of(springPage, safeLimit, sort);

    Specification<Profile> spec = com.hng.profile.ProfileSpecifications.buildFilter(gender, ageGroup, countryId, minAge,
        maxAge, minGenderProb, minCountryProb);

    return profileRepository.findAll(spec, pageable);
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
