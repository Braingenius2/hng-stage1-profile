# Profile Intelligence Service (HNG Stage 1)

This is a RESTful API service that accepts a name, enriches it by querying multiple external intelligence APIs (Genderize, Agify, Nationalize), and persists the aggregated data. It is built as part of the HNG Backend Engineering Stage 1 Task.

## Features
- **External API Integration**: Aggregates demographic data (Gender, Age, Nationality).
- **Data Persistence**: Uses PostgreSQL for robust data storage.
- **Idempotency**: Prevents duplicate profile creation for the same name.
- **Filtering**: Retrieve profiles by gender, age group, or country.
- **UUID v7**: Uses time-ordered UUIDs for primary keys.
- **Global Error Handling**: Standardized JSON error responses.

## Technologies Used
- Java 21
- Spring Boot 4.05
- Spring Data JPA
- PostgreSQL (Production) & H2 (Local Development)
- Maven
- Docker

## API Endpoints

### 1. Create a Profile
`POST /api/profiles`
```json
{
  "name": "ella"
}
```

### 2. Get a Profile by ID
`GET /api/profiles/{id}`

### 3. Get All Profiles (with optional filters)
`GET /api/profiles?gender=female&country_id=NG&age_group=adult`

### 4. Delete a Profile
`DELETE /api/profiles/{id}`

## Local Setup
1. Clone the repository.
2. Run `.\mvnw spring-boot:run` to start the application (uses H2 in-memory DB locally).
3. The server will be available at `http://localhost:8080`.

## Author
Fortune C. (HNG Backend Engineering Trainee)
