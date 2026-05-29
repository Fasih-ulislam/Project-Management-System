# Project Management System — Spring Boot REST API
 
A backend REST API for a project management platform built with Java Spring Boot 3, Spring Security, and JWT authentication. Supports multi-role access control across admins and team members, with full management of projects, tasks, teams, members, expenses, and task progress tracking.
 
---
 
## Table of Contents
 
- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [API Overview](#api-overview)
- [Project Structure](#project-structure)
---
 
## Overview
 
This system provides a structured backend for managing software projects within an organization. Project managers can create projects, define tasks, assemble teams, track expenses, and monitor task progress. Team members can log work hours, update completion percentages, and add comments on tasks assigned to them. All access is controlled through JWT-based authentication and role-based authorization enforced at both the route and method level.
 
---
 
## Features
 
### Authentication & Security
- User registration with BCrypt password hashing
- JWT-based stateless authentication via `Authorization: Bearer <token>`
- Spring Security filter chain with a custom `JwtFilter`
- Role-based route restrictions: `ADMIN`, `MEMBER`, `USER`
- Method-level authorization via `@PreAuthorize` annotations
- CORS configured for frontend origin
### User & Member Management
- Users register and log in to receive a JWT
- Users can create a **Member profile** linked to their account (name, position, salary)
- Admins can list all members; managers can view members on their projects
- Member profiles can be updated or deleted by the owner
### Project Management
- Admins and managers can create, update, and delete projects
- Projects track: name, description, start/end dates, budget, and status
- Status auto-updates to `Completed` when all tasks are finished, `In Progress` otherwise
- Managers can retrieve only their own projects via `/projects/my-projects`
### Task Management
- Managers create tasks under a project with title, description, and date range
- Tasks can be assigned to a team
- Members can view tasks assigned to them via their team membership
- Tasks can be updated or deleted by the project manager
### Team Management
- Managers can create teams if they manage at least one project
- Members can be added to or removed from a team
- Teams can be assigned to or removed from specific tasks
- Managers can list all teams they created
### Expense Tracking
- Managers can add, update, and delete expense entries per task
- Each expense has a description and amount
- Expenses for a task are retrievable by authorized users
### Task Progress & Detail Logging
- Team members assigned to a task can log progress entries
- Each entry tracks: status, hours worked, percentage completed, comment, and timestamp
- Members can update or delete their own entries
- All detail records for a task are retrievable by the team
---
 
## Tech Stack
 
| Layer          | Technology                          |
|----------------|-------------------------------------|
| Language       | Java 17                             |
| Framework      | Spring Boot 3.3                     |
| Security       | Spring Security + JWT (jjwt)        |
| Persistence    | Spring Data JPA + Hibernate         |
| Database       | MySQL 8                             |
| Validation     | Jakarta Validation                  |
| Mapping        | ModelMapper                         |
| Build Tool     | Maven                               |
| Utilities      | Lombok, Spring DevTools             |
 
---
 
## Architecture
 
```
┌─────────────────────────────────────────┐
│              Client / Frontend          │
│         http://127.0.0.1:8081           │
└──────────────────┬──────────────────────┘
                   │ HTTP  Authorization: Bearer <token>
┌──────────────────▼──────────────────────┐
│           Spring Boot API :8080         │
│                                         │
│  ┌──────────┐   ┌──────────────────┐   │
│  │JwtFilter │──►│ Security Config  │   │
│  └──────────┘   │ (Filter Chain)   │   │
│                 └────────┬─────────┘   │
│                          │             │
│  ┌───────────────────────▼──────────┐  │
│  │          Controllers             │  │
│  │  User │ Project │ Task │ Team    │  │
│  │  Member │ Expense │ TaskDetails  │  │
│  └───────────────────────┬──────────┘  │
│                          │             │
│  ┌───────────────────────▼──────────┐  │
│  │           Services               │  │
│  │  Business logic + validation     │  │
│  └───────────────────────┬──────────┘  │
│                          │             │
│  ┌───────────────────────▼──────────┐  │
│  │       JPA Repositories           │  │
│  └───────────────────────┬──────────┘  │
└──────────────────────────┼─────────────┘
                           │
               ┌───────────▼────────────┐
               │      MySQL 8           │
               │  project_management    │
               │       _system          │
               └────────────────────────┘
```
 
### Request Flow
1. Client sends request with `Authorization: Bearer <token>`
2. `JwtFilter` intercepts, validates the token, and loads the user into the security context
3. Spring Security checks route-level role restrictions
4. Controller receives the request and applies `@PreAuthorize` method-level checks
5. Service layer handles business logic (e.g. auto-updating project status on task change)
6. JPA repository persists or retrieves data from MySQL
7. ModelMapper converts entities to DTOs before returning the response
---
 
## Getting Started
 
### Prerequisites
- Java 17+
- MySQL 8
- Maven
### Setup
 
```bash
# Clone the repository
git clone https://github.com/Fasih-ulislam/Project-Management-System
cd Project-Management-System
 
# Create the MySQL database
mysql -u root -p
CREATE DATABASE project_management_system;
exit;
 
# Build and run
mvn spring-boot:run
```
 
The API will be available at `http://localhost:8080`.
 
---
 
## Environment Variables
 
Configuration is managed in `src/main/resources/application.properties`. Update the following before running:
 
```properties
# Server
server.port=8080
 
# MySQL
spring.datasource.url=jdbc:mysql://localhost/project_management_system
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
 
# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
 
# CORS (Frontend origin)
cors.allowed-origin=http://127.0.0.1:8081
```
 
> **Note:** The JWT secret key is generated at runtime inside `JWTService` and is not stored in config. Restart will invalidate existing tokens.
 
---
 
## API Overview
 
All protected endpoints require `Authorization: Bearer <token>` in the request header.
 
### Auth & Users
 
| Method | Endpoint               | Role       | Description                  |
|--------|------------------------|------------|------------------------------|
| POST   | `/users`               | Public     | Register a new user          |
| POST   | `/users/login`         | Public     | Login and receive JWT        |
| GET    | `/users`               | ADMIN      | List all users               |
| DELETE | `/users`               | ADMIN      | Delete user by credentials   |
 
### Member Profiles
 
| Method | Endpoint               | Role        | Description                  |
|--------|------------------------|-------------|------------------------------|
| POST   | `/members`             | USER        | Create member profile        |
| GET    | `/members`             | ADMIN/MGR   | List all members             |
| PUT    | `/members/{id}`        | MEMBER      | Update own member profile    |
| DELETE | `/members/{id}`        | MEMBER      | Delete own member profile    |
 
### Projects
 
| Method | Endpoint                    | Role    | Description                        |
|--------|-----------------------------|---------|----------------------------------- |
| POST   | `/projects`                 | ADMIN   | Create a project                   |
| GET    | `/projects`                 | ADMIN   | List all projects                  |
| GET    | `/projects/{id}`            | ADMIN   | Get project by ID                  |
| GET    | `/projects/my-projects`     | MEMBER  | Get projects managed by current user |
| PUT    | `/projects/{id}`            | ADMIN   | Update project                     |
| DELETE | `/projects/{id}`            | ADMIN   | Delete project                     |
 
### Tasks
 
| Method | Endpoint                        | Role    | Description                      |
|--------|---------------------------------|---------|----------------------------------|
| POST   | `/tasks`                        | MEMBER  | Create task under a project      |
| GET    | `/tasks/project/{projectId}`    | MEMBER  | List tasks for a project         |
| GET    | `/tasks/my-tasks`               | MEMBER  | List tasks assigned to current user |
| PUT    | `/tasks/{id}`                   | MEMBER  | Update task                      |
| DELETE | `/tasks/{id}`                   | MEMBER  | Delete task                      |
 
### Teams
 
| Method | Endpoint                            | Role   | Description                     |
|--------|-------------------------------------|--------|---------------------------------|
| POST   | `/teams`                            | MEMBER | Create a team                   |
| GET    | `/teams/my-teams`                   | MEMBER | List teams created by manager   |
| PUT    | `/teams/{id}`                       | MEMBER | Update team                     |
| DELETE | `/teams/{id}`                       | MEMBER | Delete team                     |
| POST   | `/teams/{id}/members/{memberId}`    | MEMBER | Add member to team              |
| DELETE | `/teams/{id}/members/{memberId}`    | MEMBER | Remove member from team         |
| POST   | `/teams/tasks/{taskId}`             | MEMBER | Assign team to task             |
| DELETE | `/teams/tasks/{taskId}`             | MEMBER | Remove team from task           |
| GET    | `/teams/tasks/{taskId}`             | MEMBER | Get team assigned to a task     |
 
### Expenses
 
| Method | Endpoint                        | Role   | Description               |
|--------|---------------------------------|--------|---------------------------|
| POST   | `/expenses`                     | MEMBER | Add expense to a task     |
| GET    | `/expenses/task/{taskId}`       | MEMBER | List expenses for a task  |
| PUT    | `/expenses/{id}`                | MEMBER | Update expense            |
| DELETE | `/expenses/{id}`                | MEMBER | Delete expense            |
 
### Task Details (Progress Log)
 
| Method | Endpoint                           | Role   | Description                      |
|--------|------------------------------------|--------|----------------------------------|
| POST   | `/task-details`                    | MEMBER | Log a progress entry for a task  |
| GET    | `/task-details/task/{taskId}`      | MEMBER | Get all entries for a task       |
| PUT    | `/task-details/{id}`               | MEMBER | Update own progress entry        |
| DELETE | `/task-details/{id}`               | MEMBER | Delete own progress entry        |
 
---
 
## Project Structure
 
```
project-management-system/
└── src/main/java/com/softManager/project_management_system/
    ├── config/
    │   ├── SecurityConfig.java       # Filter chain, route restrictions, CORS
    │   ├── JwtFilter.java            # Token extraction, validation, auth context
    │   └── AppConfig.java            # ModelMapper bean
    ├── controllers/
    │   ├── UserController.java
    │   ├── ProjectController.java
    │   ├── TaskController.java
    │   ├── TeamController.java
    │   ├── MemberController.java
    │   ├── ExpenseController.java
    │   └── TaskDetailsController.java
    ├── services/
    │   ├── UserService.java
    │   ├── JWTService.java           # Token generation & validation
    │   ├── MyUserDetailsService.java # Spring Security user loading
    │   ├── ProjectServices.java      # Includes auto status update logic
    │   ├── TaskServices.java
    │   ├── TeamService.java
    │   ├── MemberService.java
    │   ├── ExpenseServices.java
    │   └── TaskDetailsService.java
    ├── model/
    │   ├── User.java
    │   ├── Member.java
    │   ├── Project.java
    │   ├── Task.java
    │   ├── Team.java
    │   ├── Expense.java
    │   ├── TaskDetails.java
    │   ├── Role.java
    │   └── BaseEntity.java           # Shared audit fields
    ├── repository/                   # Spring Data JPA interfaces
    ├── dto/                          # Request/response DTOs
    ├── constraints/                  # Custom validation (unique username)
    └── exception/                    # Global exception handling
```
 
---
 
## License
 
This project is for educational purposes. All rights reserved.
 
