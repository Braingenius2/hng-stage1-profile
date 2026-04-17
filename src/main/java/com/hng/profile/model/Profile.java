package com.hng.profile.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.github.f4b6a3.uuid.UuidCreator;

@Entity
@Table(name = "profiles")
public class Profile {

  @Id
  private UUID id;

  @Column(unique = true, nullable = false)
  private String name;

  private String gender;

  @Column(name = "gender_probability")
  private Double genderProbability;

  @Column(name = "sample_size")
  private Integer sampleSize;

  private Integer age;

  @Column(name = "age_group")
  private String ageGroup;

  @Column(name = "country_id")
  private String countryId;

  @Column(name = "country_probability")
  private Double countryProbability;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public Profile() {

  }

  public Profile(String name, String gender, Double genderProbability, Integer sampleSize,
      Integer age, String ageGroup, String countryId, Double countryProbability) {
    this.id = UuidCreator.getTimeOrderedEpoch(); // Generates UUID v7!
    this.name = name;
    this.gender = gender;
    this.genderProbability = genderProbability;
    this.sampleSize = sampleSize;
    this.age = age;
    this.ageGroup = ageGroup;
    this.countryId = countryId;
    this.countryProbability = countryProbability;
    this.createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getGender() {
    return gender;
  }

  public Double getGenderProbability() {
    return genderProbability;
  }

  public Integer getSampleSize() {
    return sampleSize;
  }

  public Integer getAge() {
    return age;
  }

  public String getAgeGroup() {
    return ageGroup;
  }

  public String getCountryId() {
    return countryId;
  }

  public Double getCountryProbability() {
    return countryProbability;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public void setCountryId(String countryId) {
    this.countryId = countryId;
  }

  public void setAgeGroup(String ageGroup) {
    this.ageGroup = ageGroup;
  }

}
