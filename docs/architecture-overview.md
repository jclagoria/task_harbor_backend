# TaskHarbor - Architecture Overview

> **Version:** 1.0  
> **Date:** 2026-05-11  
> **Status:** Architecture Reference

---

## 1. Module Overview

| Module | Status | Design Document | Description |
|--------|--------|----------------|-------------|
| Foundation Security | ✅ Complete | `01-foundation-security-design.md` | Auth, ABAC, GDPR, Audit |
| Task Management | ✅ Complete | `2026-05-11-task-management-design.md` | Tasks, Kanban, Comments, Documents |
| Project Management | ✅ Complete | `2026-05-11-project-management-design.md` | Planning, Gantt, WBS, Budget, Risk/Issues |
| Collaboration | ✅ Complete | `2026-05-11-collaboration-design.md` | Workspaces, Forums, Notifications, Calendars |
| Reports & Analytics | ✅ Complete | `2026-05-11-reports-analytics-design.md` | Dashboards, Reports, Export, Alerts |
| Integrations | ✅ Complete | `2026-05-11-integrations-design.md` | Google Calendar, Drive, Git |
| Customization | ✅ Complete | `2026-05-11-customization-design.md` | Custom Fields, Workflows, Templates, Themes |

---

## 2. Module Architecture Pattern

Each module follows the same Clean Architecture pattern:

```
src/main/java/com/taskharbor/{module}/
├── domain/
│   ├── entity/          # Domain entities
│   ├── repository/      # Repository interfaces
│   └── service/         # Domain services
├── application/
│   ├── dto/            # Data transfer objects
│   └── usecase/        # Business use cases
├── infrastructure/
│   ├── persistence/    # Repository implementations
│   └── external/       # External service adapters
└── interface/
    └── controller/     # REST controllers
```

Frontend feature structure per module:

```
frontend/src/features/{module}/
├── components/         # Feature components
├── hooks/             # Custom React hooks
├── services/          # API services
└── types/             # TypeScript types
```

---

## 3. Module Dependencies

```
┌─────────────────────────────────────────────────────────────────┐
│                      FOUNDATION MODULE                          │
│              (Auth, Security, GDPR, Audit)                       │
└───────────────────────────────┬─────────────────────────────────┘
                                │
        ┌───────────┬───────────┼───────────┬───────────┐
        ▼           ▼           ▼           ▼           ▼
┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐
│   TASK    │ │  PROJECT  │ │COLLABORA- │ │  REPORTS  │ │   CUSTOM  │
│ MANAGEMENT│ │           │ │   TION    │ │           │ │           │
└─────┬─────┘ └─────┬─────┘ └─────┬─────┘ └─────┬─────┘ └─────┬─────┘
      │             │             │             │             │
      └──────┬──────┴──────┬──────┴──────┬──────┴──────┬──────┘
             │             │             │             │
             └─────────────┼─────────────┼─────────────┘
                           ▼
                   ┌───────────────┐
                   │ INTEGRATIONS  │
                   │  (External)   │
                   └───────────────┘
```

---

## 4. Module Specifications

### 4.1 Foundation Module (`foundation/`)

**Entities:** User, Session, AuditLog, Policy, Consent

**Services:**
- JWT Authentication
- ABAC Policy Engine
- TOTP 2FA
- AES Encryption
- GDPR Data Export/Deletion

**Database Tables:** users, sessions, audit_logs, policies, user_consents

### 4.2 Task Management Module (`task/`)

**Entities:** Task, TaskComment, TaskAttachment, TaskChecklist, KanbanBoard, KanbanColumn, TaskTemplate

**Services:**
- Task CRUD with assignment
- Kanban board management
- Comment threads with @mentions
- S3 file storage with versioning
- Checklist/subtask handling

**Database Tables:** tasks, task_comments, task_attachments, kanban_boards, kanban_columns, task_templates

### 4.3 Project Management Module (`project/`)

**Entities:** Project, WbsNode, GanttTask, GanttDependency, BudgetLineItem, Risk, Issue

**Services:**
- Project lifecycle management
- WBS hierarchy builder
- Custom Gantt chart engine (SVG)
- Budget tracking with variance
- Risk/Issue management with matrix

**Database Tables:** projects, wbs_nodes, gantt_tasks, gantt_dependencies, budget_line_items, risks, issues

### 4.4 Collaboration Module (`collaboration/`)

**Entities:** Workspace, Channel, ChannelMessage, ForumCategory, ForumThread, ThreadReply, CalendarEvent, Notification, NotificationPreference

**Services:**
- Workspace management
- Channel messaging with threads
- Forum categories and threads
- Calendar event management
- Notification dispatch (in-app + email digest)

**Database Tables:** workspaces, channels, channel_messages, forum_categories, forum_threads, thread_replies, calendar_events, notifications, notification_preferences

### 4.5 Reports & Analytics Module (`reports/`)

**Entities:** Report, Dashboard, DashboardWidget, ReportSchedule, AlertThreshold, ReportExport

**Services:**
- Standard report generation
- Dashboard builder with drag-drop
- Trend analysis
- Alert threshold monitoring
- PDF/Excel export generation
- Scheduled report delivery

**Database Tables:** reports, dashboards, dashboard_widgets, report_schedules, alert_thresholds, report_exports

### 4.6 Integrations Module (`integrations/`)

**Entities:** IntegrationConnection, Webhook, SyncLog, InvoiceExport

**Adapters:**
- GoogleCalendarAdapter
- GoogleDriveAdapter
- GitHubWebhookHandler
- GitLabWebhookHandler
- InvoiceExportAdapter

**Services:**
- OAuth flow management
- Two-way calendar sync
- Webhook processing
- Invoice CSV export

**Database Tables:** integration_connections, webhooks, sync_logs, invoice_exports

### 4.7 Customization Module (`customization/`)

**Entities:** CustomFieldDefinition, CustomFieldValue, Workflow, WorkflowStage, WorkflowTransition, TaskTemplate, ProjectTemplate, Theme

**Services:**
- Custom field definition and validation
- Workflow builder with visual editor
- Template library management
- Theme customization with CSS variables

**Database Tables:** custom_field_definitions, custom_field_values, workflows, workflow_stages, workflow_transitions, task_templates, project_templates, themes

---

## 5. API Organization

### 5.1 REST Endpoints by Module

| Module | Base Path | Key Endpoints |
|--------|-----------|---------------|
| Foundation | `/api/auth/*` | register, login, logout, refresh, 2fa, permissions, gdpr |
| Tasks | `/api/tasks/*`, `/api/projects/{id}/kanban` | CRUD, move, comments, attachments |
| Projects | `/api/projects/*`, `/api/projects/{id}/wbs`, `/api/projects/{id}/gantt` | CRUD, budget, risks, issues |
| Collaboration | `/api/workspaces/*`, `/api/channels/*`, `/api/forum/*` | workspace, channels, forum, calendar |
| Reports | `/api/reports/*`, `/api/dashboards/*` | progress, resources, budget, export |
| Integrations | `/api/integrations/*`, `/api/webhooks/*` | connect, sync, export |
| Customization | `/api/fields/*`, `/api/workflows/*`, `/api/templates/*`, `/api/themes/*` | fields, workflows, templates, themes |

### 5.2 SSE Event Organization

| Channel | Events | Module |
|---------|--------|--------|
| `/api/events/projects` | project.updated | Project |
| `/api/events/tasks` | task.created, task.updated, task.moved | Task |
| `/api/events/channels/{id}` | channel.message, channel.threadReply | Collaboration |
| `/api/events/notifications` | notification.new | Collaboration |
| `/api/events/reports` | report.generated, export.completed | Reports |
| `/api/events/alerts` | alert.triggered | Reports |

---

## 6. Shared Infrastructure

### 6.1 Cross-Module Services

| Service | Description | Used By |
|---------|-------------|---------|
| `EventBus` | SSE event distribution | All modules |
| `AuditService` | Centralized audit logging | All modules |
| `NotificationService` | Notification dispatch | Task, Collaboration, Reports |
| `FileStorageService` | S3 file operations | Task, Collaboration, Integrations |
| `EmailService` | Email sending | Collaboration, Reports |
| `EncryptionService` | AES-256 encryption | Foundation, Integrations |

### 6.2 Database Schema Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                      CORE TABLES                                │
├─────────────────────────────────────────────────────────────────┤
│ users, sessions, audit_logs, policies, user_consents            │
├─────────────────────────────────────────────────────────────────┤
│                      PROJECT TABLES                             │
├─────────────────────────────────────────────────────────────────┤
│ projects, wbs_nodes, gantt_tasks, gantt_dependencies            │
│ budget_line_items, risks, issues                                 │
├─────────────────────────────────────────────────────────────────┤
│                       TASK TABLES                               │
├─────────────────────────────────────────────────────────────────┤
│ tasks, task_comments, task_attachments, kanban_boards           │
│ kanban_columns                                                   │
├─────────────────────────────────────────────────────────────────┤
│                   COLLABORATION TABLES                          │
├─────────────────────────────────────────────────────────────────┤
│ workspaces, channels, channel_messages                          │
│ forum_categories, forum_threads, thread_replies                  │
│ calendar_events, notifications                                  │
├─────────────────────────────────────────────────────────────────┤
│                      REPORT TABLES                              │
├─────────────────────────────────────────────────────────────────┤
│ reports, dashboards, dashboard_widgets                           │
│ report_schedules, alert_thresholds                              │
├─────────────────────────────────────────────────────────────────┤
│                   INTEGRATION TABLES                            │
├─────────────────────────────────────────────────────────────────┤
│ integration_connections, webhooks, sync_logs                    │
├─────────────────────────────────────────────────────────────────┤
│                   CUSTOMIZATION TABLES                          │
├─────────────────────────────────────────────────────────────────┤
│ custom_field_definitions, custom_field_values                    │
│ workflows, workflow_stages, workflow_transitions                 │
│ task_templates, project_templates, themes                       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 7. Frontend Routes

```
frontend/src/app/
├── (auth)/
│   ├── login/
│   ├── register/
│   └── totp/
├── (dashboard)/
│   ├── layout.tsx              # Main dashboard layout
│   ├── page.tsx               # Dashboard home
│   │
│   ├── projects/
│   │   ├── page.tsx           # Project list
│   │   └── [projectId]/
│   │       ├── page.tsx       # Project overview
│   │       ├── kanban/        # Kanban board
│   │       ├── gantt/         # Gantt chart
│   │       ├── wbs/          # WBS tree
│   │       ├── budget/       # Budget tracking
│   │       ├── risks/        # Risk management
│   │       ├── issues/       # Issue management
│   │       ├── workspace/    # Collaboration workspace
│   │       │   ├── channels/
│   │       │   ├── forum/
│   │       │   └── calendar/
│   │       └── settings/     # Project settings
│   │
│   ├── tasks/
│   │   ├── page.tsx          # Task list
│   │   └── [taskId]/
│   │       └── page.tsx      # Task detail
│   │
│   ├── reports/
│   │   ├── page.tsx          # Reports dashboard
│   │   └── [reportId]/       # Report detail
│   │
│   ├── integrations/
│   │   └── page.tsx          # Integration settings
│   │
│   └── settings/
│       ├── profile/          # User profile
│       ├── customization/    # Custom fields, workflows
│       └── themes/          # Theme settings
```

---

## 8. Next Steps

- Implementation planning (see `docs/superpowers/skills/writing-plans/`)
- Module prioritization for development
- Team assignment by module
- CI/CD pipeline configuration per module