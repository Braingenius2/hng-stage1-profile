package com.hng.profile.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class NaturalLanguageParser {

  // Simple dictionary for ISO country codes
  private static final Map<String, String> COUNTRY_MAP = new HashMap<>();
  static {
    COUNTRY_MAP.put("algeria", "DZ");
    COUNTRY_MAP.put("angola", "AO");
    COUNTRY_MAP.put("benin", "BJ");
    COUNTRY_MAP.put("burkina faso", "BF");
    COUNTRY_MAP.put("burundi", "BI");
    COUNTRY_MAP.put("cameroon", "CM");
    COUNTRY_MAP.put("cape verde", "CV");
    COUNTRY_MAP.put("chad", "TD");
    COUNTRY_MAP.put("comoros", "KM");
    COUNTRY_MAP.put("congo", "CG");
    COUNTRY_MAP.put("republic of the congo", "CG");
    COUNTRY_MAP.put("djibouti", "DJ");
    COUNTRY_MAP.put("egypt", "EG");
    COUNTRY_MAP.put("eritrea", "ER");
    COUNTRY_MAP.put("eswatini", "SZ");
    COUNTRY_MAP.put("gambia", "GM");
    COUNTRY_MAP.put("ghana", "GH");
    COUNTRY_MAP.put("guinea", "GN");
    COUNTRY_MAP.put("india", "IN");
    COUNTRY_MAP.put("ivory coast", "CI");
    COUNTRY_MAP.put("kenya", "KE");
    COUNTRY_MAP.put("lesotho", "LS");
    COUNTRY_MAP.put("liberia", "LR");
    COUNTRY_MAP.put("libya", "LY");
    COUNTRY_MAP.put("madagascar", "MG");
    COUNTRY_MAP.put("malawi", "MW");
    COUNTRY_MAP.put("mauritius", "MU");
    COUNTRY_MAP.put("mozambique", "MZ");
    COUNTRY_MAP.put("niger", "NE");
    COUNTRY_MAP.put("nigeria", "NG");
    COUNTRY_MAP.put("rwanda", "RW");
    COUNTRY_MAP.put("senegal", "SN");
    COUNTRY_MAP.put("seychelles", "SC");
    COUNTRY_MAP.put("sierra leone", "SL");
    COUNTRY_MAP.put("somalia", "SO");
    COUNTRY_MAP.put("south africa", "ZA");
    COUNTRY_MAP.put("south sudan", "SS");
    COUNTRY_MAP.put("sudan", "SD");
    COUNTRY_MAP.put("tanzania", "TZ");
    COUNTRY_MAP.put("togo", "TG");
    COUNTRY_MAP.put("tunisia", "TN");
    COUNTRY_MAP.put("uganda", "UG");
    COUNTRY_MAP.put("united kingdom", "GB");
    COUNTRY_MAP.put("united states", "US");
    COUNTRY_MAP.put("zambia", "ZM");
    COUNTRY_MAP.put("australia", "AU");
  }

  public ParsedQuery parse(String query) {
    if (query == null || query.isBlank()) {
      throw new IllegalArgumentException("Query cannot be empty");
    }

    String lowerQuery = query.toLowerCase().trim();
    ParsedQuery result = new ParsedQuery();
    boolean matchedSomething = false;

    // 1. Parse Gender
    if (lowerQuery.contains("female") || lowerQuery.contains("women")) {
      result.gender = "female";
      matchedSomething = true;
    } else if (lowerQuery.matches(".*\\b(male|males|men|man)\\b.*")) {
      result.gender = "male";
      matchedSomething = true;
    }

    // 2. Parse Age Groups & Custom "Young" logic
    if (lowerQuery.contains("young")) {
      result.minAge = 16;
      result.maxAge = 24;
      matchedSomething = true;
    } else if (lowerQuery.contains("teenager")) {
      result.ageGroup = "teenager";
      matchedSomething = true;
    } else if (lowerQuery.contains("adult")) {
      result.ageGroup = "adult";
      matchedSomething = true;
    } else if (lowerQuery.contains("senior")) {
      result.ageGroup = "senior";
      matchedSomething = true;
    } else if (lowerQuery.contains("child")) {
      result.ageGroup = "child";
      matchedSomething = true;
    }

    // 3. Parse Exact Age Comparisons ("above 30", "older than 17", "under 25")
    Matcher aboveMatcher = Pattern.compile("(?:above|older than|over)\\s+(\\d+)").matcher(lowerQuery);
    if (aboveMatcher.find()) {
      result.minAge = Integer.parseInt(aboveMatcher.group(1));
      matchedSomething = true;
    }

    Matcher belowMatcher = Pattern.compile("(?:below|under|younger than)\\s+(\\d+)").matcher(lowerQuery);
    if (belowMatcher.find()) {
      result.maxAge = Integer.parseInt(belowMatcher.group(1));
      matchedSomething = true;
    }

    // 4. Parse Location ("from nigeria", "in kenya")
    Matcher countryMatcher = Pattern.compile("(?:from|in)\\s+([a-z\\s]+)").matcher(lowerQuery);
    if (countryMatcher.find()) {
      String extractedCountry = countryMatcher.group(1).trim();
      for (String knownCountry : COUNTRY_MAP.keySet()) {
        if (extractedCountry.contains(knownCountry)) {
          result.countryId = COUNTRY_MAP.get(knownCountry);
          matchedSomething = true;
          break;
        }
      }
    }

    if (!matchedSomething) {
      throw new IllegalArgumentException("Unable to interpret query");
    }

    return result;
  }

  // DTO to hold the extracted parameters
  public static class ParsedQuery {
    public String gender;
    public String ageGroup;
    public String countryId;
    public Integer minAge;
    public Integer maxAge;
  }
}
