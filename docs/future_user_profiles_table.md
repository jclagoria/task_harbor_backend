# Future: user_profiles Table

## Overview

When adding extended user information (fullName, publicName, pronouns, location, timezone, bio, avatar, etc.), create a separate `user_profiles` table.

## Why Separate Table?

| Reason | Explanation |
|--------|-------------|
| **Optional data** | Not all users complete their profile |
| **Selective loading** | Load profile data only when needed |
| **Independent updates** | Profile can be updated without touching auth data |
| **Scalability** | Avoids bloating the users table with rarely-used fields |
| **Cleaner schema** | Users table stays focused on authentication/authorization |

## Proposed Schema

```sql
CREATE TABLE user_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    
    -- Display name fields
    full_name VARCHAR(255),
    public_name VARCHAR(100),
    pronouns VARCHAR(50),
    
    -- Location & time
    location VARCHAR(255),
    time_zone VARCHAR(100) DEFAULT 'UTC',
    
    -- Personal info
    bio TEXT,
    avatar_url VARCHAR(500),
    birth_date DATE,
    
    -- Preferences (JSON for flexibility)
    preferences JSONB DEFAULT '{}',
    
    -- Metadata
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);
```

## Implementation Steps

1. **Create Flyway migration** (`V6__create_user_profiles_table.sql`)
2. **Create entity** `UserProfile.java` in domain/entity
3. **Create repository** `UserProfileRepository.java` in domain/repository
4. **Create DTOs** for profile request/response
5. **Create use case** `UpdateProfileUseCase.java`
6. **Add endpoint** in `UserController.java` (new controller)
7. **Add service** if needed for profile logic

## API Design Example

```
PUT /api/users/profile
Authorization: Bearer <token>

Request:
{
  "fullName": "Juan Perez",
  "publicName": "juanp",
  "pronouns": "he/him",
  "location": "Mexico City, Mexico",
  "timeZone": "America/Mexico_City",
  "bio": "Software developer"
}

Response:
{
  "success": true,
  "data": { ... }
}
```

## Notes

- Use `fullName` for derived/computed fields (firstName + lastName)
- Use `publicName` for display names (nicknames, usernames)
- Store `timeZone` as IANA timezone string (e.g., "America/New_York")
- Use JSONB for `preferences` to allow flexible key-value options