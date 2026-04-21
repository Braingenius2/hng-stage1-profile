package com.hng.profile.seeder;

import java.io.InputStream;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import com.hng.profile.model.Profile;
import com.hng.profile.repository.ProfileRepository;

@Component
public class ProfileSeeder implements CommandLineRunner {

  private final ProfileRepository profileRepository;
  private final ObjectMapper objectMapper;

  public ProfileSeeder(ProfileRepository profileRepository, ObjectMapper objectMapper) {
    this.profileRepository = profileRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public void run(String... args) throws Exception {
    if (profileRepository.count() >= 2026) {
      System.out.println("Database is already seeded.");
      return;
    }

    System.out.println("Seeding database with 2026 profiles...");

    InputStream inputStream = getClass().getResourceAsStream("/seed_profiles.json");

    SeedWrapper wrapper = objectMapper.readValue(inputStream, SeedWrapper.class);

    List<Profile> profilesToSave = wrapper.profiles().stream().map(dto -> {
      Profile profile = new Profile();
      java.lang.reflect.Field idField;
      java.lang.reflect.Field createdAtField;
      try {
        idField = Profile.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(profile, UuidCreator.getTimeOrderedEpoch()); // UUID v7

        createdAtField = Profile.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(profile, java.time.Instant.now());
      } catch (Exception e) {
      }

      profile.setName(dto.name());
      profile.setGender(dto.gender());
      profile.setGenderProbability(dto.gender_probability());
      profile.setAge(dto.age());
      profile.setAgeGroup(dto.age_group());
      profile.setCountryId(dto.country_id());
      profile.setCountryName(dto.country_name());
      profile.setCountryProbability(dto.country_probability());

      return profile;
    }).toList();

    profileRepository.saveAll(profilesToSave);
    System.out.println("Database seeding complete!");
  }

  public record SeedWrapper(List<SeedDTO> profiles) {
  }

  public record SeedDTO(
      String name, String gender, Double gender_probability,
      Integer age, String age_group, String country_id,
      String country_name, Double country_probability) {
  }
}
