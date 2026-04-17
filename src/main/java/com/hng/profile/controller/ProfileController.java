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

  private final ProfileService profileService;

  public ProfileController(ProfileService profileService) {
    this.profileService = profileService;
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

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProfile(@PathVariable UUID id) {
    profileService.deleteProfile(id);
    return ResponseEntity.noContent().build();
  }
}
