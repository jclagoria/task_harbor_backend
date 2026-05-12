# TaskHarbor - Technical Specification

> **Version:** 1.0  
> **Date:** 2026-05-11  
> **Status:** Technical Specification

---

## 1. Technology Stack

### 1.1 Backend

| Category | Technology | Version |
|----------|------------|---------|
| Language | Java | 21 LTS |
| Framework | Spring WebFlux | 3.3+ |
| Security | Spring Security + Spring Authorization Server | - |
| Database | PostgreSQL | 16+ |
| ORM | Spring Data R2DBC (Reactive) | - |
| JWT | jjwt | 0.12.5 |
| 2FA | dev.samstevens.totp | 1.7.1 |
| Build | Maven | 3.9+ |
| Validation | Spring Validation | - |

### 1.2 Frontend

| Category | Technology | Version |
|----------|------------|---------|
| Framework | Next.js | 16+ |
| Language | TypeScript | 5.5+ |
| UI | Tailwind CSS | 3.4+ |
| UI Components | shadcn/ui | - |
| Auth | NextAuth.js | 5.0.0-beta |
| State/Data | TanStack Query | 5.50+ |
| Validation | Zod | 3.23+ |
| Icons | Lucide React | 0.400+ |

### 1.3 Infrastructure

| Category | Technology |
|----------|------------|
| Database | PostgreSQL 16+ |
| API | REST + Server-Sent Events (SSE) |
| Container | Docker |
| Orchestration | Kubernetes |
| Real-time | SSE for event streaming |

---

## 2. Architecture

### 2.1 Backend Architecture - Clean Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │   REST      │  │    SSE      │  │   Filters   │             │
│  │ Controllers │  │  Handler    │  │             │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     APPLICATION LAYER                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │  Use Cases  │  │    DTOs     │  │   Mappers   │             │
│  │             │  │             │  │             │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │  Entities   │  │ Repository  │  │   Domain    │             │
│  │             │  │  Interfaces │  │   Services  │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   INFRASTRUCTURE LAYER                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │   R2DBC     │  │    JWT      │  │  Encryption │             │
│  │ Repositories│  │   Service   │  │   Service   │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │   Password  │  │    TOTP     │  │  ABAC       │             │
│  │   Service   │  │   Service   │  │   Engine    │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 Frontend Architecture - Feature-Based

```
┌─────────────────────────────────────────────────────────────────┐
│                        APP LAYER                                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    App Router                            │   │
│  │   (auth)/login    (auth)/register    (dashboard)/...     │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     FEATURES LAYER                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │    auth     │  │  projects   │  │   tasks    │             │
│  │  components │  │  components │  │  components│             │
│  │    hooks    │  │    hooks    │  │    hooks   │             │
│  │   services  │  │   services  │  │   services │             │
│  │    types    │  │    types    │  │    types   │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        LIB LAYER                                │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │   auth.ts   │  │   api.ts    │  │  utils.ts  │             │
│  │ (NextAuth)  │  │ (Fetch)     │  │             │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    COMPONENTS LAYER                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │    ui/      │  │   layout/   │  │   common/   │             │
│  │ (shadcn/ui) │  │             │  │             │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. System Components

### 3.1 Backend Modules

```
src/main/java/com/taskharbor/
├── domain/
│   ├── entity/
│   │   ├── User.java          # User entity
│   │   ├── Session.java       # Session entity
│   │   ├── AuditLog.java      # Audit log entity
│   │   ├── Policy.java        # ABAC policy entity
│   │   └── Consent.java       # GDPR consent entity
│   └── repository/
│       ├── UserRepository.java
│       ├── SessionRepository.java
│       ├── AuditLogRepository.java
│       ├── PolicyRepository.java
│       └── ConsentRepository.java
│
├── application/
│   ├── dto/
│   │   ├── auth/
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── TokenResponse.java
│   │   │   ├── TotpSetupResponse.java
│   │   │   ├── RefreshRequest.java
│   │   │   └── TotpVerifyRequest.java
│   │   ├── authorization/
│   │   │   └── PolicyRequest.java
│   │   └── gdpr/
│   │       └── UserDataExport.java
│   │
│   └── usecase/
│       ├── auth/
│       │   ├── RegisterUseCase.java
│       │   ├── LoginUseCase.java
│       │   ├── LogoutUseCase.java
│       │   ├── RefreshTokenUseCase.java
│       │   └── TotpUseCase.java
│       ├── authorization/
│       │   ├── CheckPermissionUseCase.java
│       │   └── ManagePolicyUseCase.java
│       ├── audit/
│       │   └── QueryAuditUseCase.java
│       └── gdpr/
│           ├── ExportDataUseCase.java
│           └── DeleteDataUseCase.java
│
├── infrastructure/
│   ├── config/
│   │   └── SecurityConfig.java
│   │
│   ├── persistence/
│   │   ├── repository/
│   │   │   └── (R2DBC implementations)
│   │   └── encryption/
│   │       └── AESEncryptionService.java
│   │
│   └── security/
│       ├── jwt/
│       │   ├── JwtService.java
│       │   ├── JwtTokenProvider.java
│       │   └── JwtAuthenticationFilter.java
│       ├── password/
│       │   └── BCryptPasswordService.java
│       ├── totp/
│       │   └── TotpService.java
│       └── authorization/
│           └── PolicyEvaluator.java
│
└── interface/
    └── controller/
        ├── AuthController.java
        ├── AuthorizationController.java
        ├── AuditController.java
        └── GdprController.java
```

### 3.2 Frontend Structure

```
frontend/src/
├── app/
│   ├── (auth)/
│   │   ├── login/
│   │   │   └── page.tsx
│   │   ├── register/
│   │   │   └── page.tsx
│   │   └── layout.tsx
│   ├── (dashboard)/
│   │   └── ...
│   ├── api/
│   │   └── auth/
│   │       └── [...nextauth]/
│   │           └── route.ts
│   ├── layout.tsx
│   ├── page.tsx
│   └── globals.css
│
├── features/
│   └── auth/
│       ├── components/
│       │   ├── LoginForm.tsx
│       │   ├── RegisterForm.tsx
│       │   ├── TotpSetup.tsx
│       │   └── TotpVerify.tsx
│       ├── hooks/
│       │   ├── useAuth.ts
│       │   ├── useLogin.ts
│       │   └── useRegister.ts
│       ├── services/
│       │   └── authApi.ts
│       └── types/
│           └── auth.ts
│
├── lib/
│   ├── auth.ts        # NextAuth configuration
│   ├── api.ts         # API client
│   └── utils.ts       # Utility functions
│
├── components/
│   └── ui/            # shadcn/ui components
│
└── types/
    └── index.ts
```

---

## 4. API Design

### 4.1 REST Endpoints

#### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login with credentials |
| POST | /api/auth/logout | Logout (invalidate session) |
| POST | /api/auth/refresh | Refresh access token |
| POST | /api/auth/2fa/setup | Setup 2FA |
| POST | /api/auth/2fa/verify | Verify 2FA code |

#### Authorization
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/auth/permissions | Check user permissions |
| POST | /api/auth/policies | Create policy |

#### Audit
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/audit/logs | Query audit logs |

#### GDPR
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/gdpr/export | Export user data |
| DELETE | /api/gdpr/delete | Delete user data |

### 4.2 Server-Sent Events (SSE)

```
/api/events/{resource}
```

SSE endpoints for real-time updates:
- `/api/events/projects` - Project updates
- `/api/events/tasks` - Task updates
- `/api/events/notifications` - User notifications

**SSE Implementation:**
```java
@RestController
@RequestMapping("/api/events")
public class SseController {
    
    @GetMapping(value = "/{resource}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<?>> streamEvents(@PathVariable String resource) {
        return eventBus.subscribe(resource)
                .map(event -> ServerSentEvent.builder()
                        .data(event)
                        .event(resource)
                        .build());
    }
}
```

---

## 5. Security Implementation

### 5.1 Authentication Flow

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│  User    │────▶│  Login   │────▶│   JWT    │────▶│  Store  │
│          │     │  Form    │     │ Service  │     │ Session │
└──────────┘     └──────────┘     └──────────┘     └──────────┘
                                              │
                                              ▼
                                    ┌──────────────────┐
                                    │  Access + Refresh │
                                    │      Tokens       │
                                    └──────────────────┘
```

### 5.2 JWT Structure

**Access Token (15 min):**
```json
{
  "sub": "user@example.com",
  "role": "USER",
  "tenantId": "uuid",
  "iat": 1234567890,
  "exp": 1234568790
}
```

**Refresh Token (7 days):**
```json
{
  "sub": "user@example.com",
  "iat": 1234567890,
  "exp": 1235177890
}
```

### 5.3 ABAC Policy Engine

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐
│   Request   │────▶│   Policy      │────▶│   Decision  │
│   Context   │     │   Evaluator   │     │   (Allow/   │
│             │     │              │     │    Deny)    │
└─────────────┘     └──────────────┘     └─────────────┘
                            │
                            ▼
                   ┌──────────────────┐
                   │   Policies      │
                   │   (from DB)     │
                   └──────────────────┘
```

### 5.4 Encryption

- **Algorithm:** AES-256-GCM
- **Key Derivation:** PBKDF2
- **IV:** Random 12 bytes per encryption
- **Storage:** Sensitive fields encrypted at rest

---

## 6. Database Schema

### 6.1 Tables

```sql
-- Users
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    totp_secret VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    attributes JSONB,
    tenant_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Sessions
CREATE TABLE sessions (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    refresh_token_hash VARCHAR(255),
    created_at TIMESTAMP,
    expires_at TIMESTAMP
);

-- Audit Logs
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    action VARCHAR(100),
    resource_type VARCHAR(100),
    resource_id UUID,
    details JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp TIMESTAMP
);

-- Policies
CREATE TABLE policies (
    id UUID PRIMARY KEY,
    name VARCHAR(100) UNIQUE,
    effect VARCHAR(10),
    conditions JSONB,
    priority INTEGER,
    tenant_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- User Consents
CREATE TABLE user_consents (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    consent_type VARCHAR(100),
    granted BOOLEAN,
    granted_at TIMESTAMP,
    revoked_at TIMESTAMP
);
```

---

## 7. Frontend Integration

### 7.1 NextAuth.js Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Login     │────▶│  NextAuth   │────▶│   Backend   │
│   Page      │     │   Provider  │     │    API      │
└─────────────┘     └─────────────┘     └─────────────┘
                                              │
                                              ▼
                                    ┌─────────────────┐
                                    │  JWT Tokens     │
                                    │  (HTTP-only     │
                                    │   cookies)      │
                                    └─────────────────┘
```

### 7.2 State Management

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   React         │────▶│  TanStack      │────▶│   API          │
│   Context       │     │  Query         │     │   Server       │
│   (Auth State)  │     │  (Server State)│     │                │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

---

## 8. Real-Time Communication (SSE)

### 8.1 SSE Architecture

```
┌──────────────┐        ┌──────────────┐        ┌──────────────┐
│   Backend    │        │   SSE        │        │   Frontend   │
│   Domain     │───────▶│   Event       │───────▶│   Event       │
│   Events     │        │   Bridge      │        │   Listener   │
└──────────────┘        └──────────────┘        └──────────────┘
```

### 8.2 SSE Implementation

**Backend (Java):**
```java
@GetMapping(value = "/events/{resource}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<?>> stream(@PathVariable String resource) {
    return eventService.subscribe(resource)
        .map(event -> ServerSentEvent.builder()
            .data(event)
            .event(resource)
            .build());
}
```

**Frontend (React):**
```typescript
useEffect(() => {
  const eventSource = new EventSource('/api/events/tasks');
  
  eventSource.onmessage = (event) => {
    const data = JSON.parse(event.data);
    // Update state
  };
  
  return () => eventSource.close();
}, []);
```

---

## 9. Deployment

### 9.1 Docker

**Backend:**
```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Frontend:**
```dockerfile
FROM node:20-alpine
WORKDIR /app
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
```

### 9.2 Kubernetes

Services:
- `taskharbor-backend` - Spring WebFlux API
- `taskharbor-frontend` - Next.js SPA
- `taskharbor-db` - PostgreSQL

---

## 10. Summary

| Layer | Backend | Frontend |
|-------|---------|----------|
| Architecture | Clean Architecture | Feature-Based |
| Framework | Spring WebFlux | Next.js 16 |
| Auth | Custom JWT | NextAuth.js |
| Database | PostgreSQL + R2DBC | - |
| Real-time | SSE | EventSource API |
| Security | JWT + TOTP + ABAC | NextAuth Providers |

This technical specification provides a complete overview of the TaskHarbor system architecture, technology choices, and integration patterns.