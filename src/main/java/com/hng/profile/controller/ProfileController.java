package com.hng.profile.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hng.profile.dto.ProfileListResponse;
import com.hng.profile.dto.ProfileResponse;
import com.hng.profile.model.Profile;
import com.hng.profile.service.ProfileService;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

  private final NaturalLanguageParser languageParser;

  private final ProfileService profileService;

  public ProfileController(ProfileService profileService, NaturalLanguageParser languageParser) {
    this.profileService = profileService;
    this.languageParser = languageParser;
  }

  @PostMapping
  public ResponseEntity<ProfileResponse> createProfile(@RequestBody Map<String, String> body) {
    String name = body.get("name");
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Missing or empty name");
    }

    ProfileService.ProfileCreationResult result = profileService.createOrGetProfile(name);

    if (result.alreadyExisted()) {
      return ResponseEntity.status(HttpStatus.OK)
          .body(new ProfileResponse("success", "Profile already exists", result.profile()));
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ProfileResponse("success", null, result.profile()));
  }

  @GetMapping("/search")
  public org.springframework.http.ResponseEntity<?> searchProfiles(
      @RequestParam String q,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit) {

    try {
      // 1. Parse the natural language query into concrete filters
      NaturalLanguageParser.ParsedQuery filters = languageParser.parse(q);

      // 2. Pass the extracted filters directly to the Service you updated earlier!
      Page<Profile> profilePage = profileService.getProfiles(
          filters.gender, filters.ageGroup, filters.countryId,
          filters.minAge, filters.maxAge, null, null,
          null, null, page, limit);

      // 3. Return the exact same pagination structure
      PaginatedResponse response = new PaginatedResponse(
          "success",
          page,
          profilePage.getSize(),
          profilePage.getTotalElements(),
          profilePage.getContent());

      return org.springframework.http.ResponseEntity.ok(response);

    } catch (IllegalArgumentException ex) {
      // "Unable to interpret query" mapping (handled globally or right here)
      return org.springframework.http.ResponseEntity.badRequest().body(
          java.util.Map.of("status", "error", "message", ex.getMessage()));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProfileResponse> getProfileById(@PathVariable UUID id) {
    Profile profile = profileService.getProfileById(id);
    return ResponseEntity.ok(new ProfileResponse("success", null, profile));
  }

  @GetMapping
  public ResponseEntity<ProfileListResponse> getProfiles(
      @RequestParam(required = false) String gender,
      @RequestParam(required = false) String country_id,
      @RequestParam(required = false) String age_group) {

    List<Profile> profiles = profileService.getProfiles(gender, country_id, age_group);
    return ResponseEntity.ok(new ProfileListResponse("success", profiles.size(), profiles));
  }

  @GetMapping
  public org.springframework.http.ResponseEntity<?> getProfiles(@RequestParam(required = false) String gender,
      @RequestParam(required = false, name = "age_group") String ageGroup,
      @RequestParam(required = false, name = "country_id") String countryId,
      @RequestParam(required = false, name = "min_age") Integer minAge,
      @RequestParam(required = false, name = "max_age") Integer maxAge,
      @RequestParam(required = false, name = "min_gender_probability") Double minGenderProb,
      @RequestParam(required = false, name = "min_country_probability") Double minCountryProb,
      @RequestParam(required = false, name = "sort_by_name") String sortBy,
      @RequestParam(required = false) String order, @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit) {

    Page<Profile> profilePage = profileService.getProfiles(gender, ageGroup, countryId, minAge, maxAge, minGenderProb,
        minCountryProb, sortBy, order, page, limit);

    paginatedresponse response = new PaginatedResponse("success", page, profilePage.getSize(),
        profilePage.getTotalElements(), profilePage.getContent());

    return org.springframework.http.ResponseEntity.ok(response);

  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProfile(@PathVariable UUID id) {
    profileService.deleteProfile(id);
    return ResponseEntity.noContent().build();
  }
}

public record PaginatedResponse(String status, int page, int limit, long total,
    java.util.List<com.hng.profile.model.Profile> data) {

}
