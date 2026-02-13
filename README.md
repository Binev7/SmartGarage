# SmartGarage

SmartGarage is a Spring Boot web application for managing a car service/repair shop. It provides both a user-facing UI (Thymeleaf) and a REST API for functions such as user authentication, managing vehicle catalog, booking visits, and administrative workflows (manage services, visits, invoices). The project uses MariaDB for persistence, JWT for authentication, and exposes OpenAPI/Swagger UI for API exploration.

---

## Project summary

SmartGarage is a demo/portfolio Spring Boot application for a car garage. It supports:
- User registration, login (JWT + cookie), password reset
- Catalog of car brands, models and vehicle years
- Customer flow: register vehicles, book visits (select date/time/services), view history
- Employee/Admin flow: manage services, visits, generate PDF invoices
- REST API for programmatic access and MVC controllers that serve Thymeleaf templates for the UI

## 🚀 Live Demo & Deployment

The application is successfully deployed and can be accessed live here:

* 👉 **[Smart Garage - Live Demo](https://smartgarage.onrender.com)**
* 📖 **[Interactive API Docs (Swagger UI)](https://smartgarage.onrender.com/swagger-ui.html)**

### 🌿 Deployment Strategy: `feat/deployment` branch
While the core development is maintained in the `main` branch, all deployment-specific configurations and optimizations for the cloud environment are isolated in the **`feat/deployment`** branch. 

Key cloud infrastructure & optimizations include:
* **Render Hosting & Uptime Optimization:** The application is hosted on Render. To bypass the free-tier container sleep cycle (cold starts), I implemented a custom, lightweight `/ping` endpoint integrated with UptimeRobot. This keeps the server awake 24/7, ensuring a lightning-fast user experience.
* **TiDB Cloud Database Integration:** The production database is powered by TiDB (a distributed, MySQL-compatible database). Spring Data JPA configurations and the Hibernate dialect (`MySQLDialect`) were specifically adjusted to ensure seamless schema generation and query execution tailored for the TiDB environment.
* **Secure Environment Variables:** Configured `application.properties` to securely consume Render's native Environment Variables for sensitive data (JWT Secrets, Database URL/Credentials) rather than hardcoding them into the repository.
* **Security Adjustments:** Customized the Spring `SecurityConfig` to explicitly permit health-check pings from external monitors, while keeping the core business logic strictly secured via JWT.
  
## Tech stack

- Java 17 (toolchain configured in `build.gradle`)
- Spring Boot 3.x (see `build.gradle`)
- Spring Data JPA (Hibernate)
- MariaDB (runtime driver included)
- Spring Security with JWT
- Thymeleaf for server-side HTML templates
- OpenPDF for PDF generation of invoices
- Springdoc OpenAPI (Swagger) for API docs
- JUnit / Spring Boot Test for automated tests

## 🏗️ Architecture & Project Structure

The application follows a standard, multi-tier Spring Boot architecture, strictly separating the web layer, business logic, and data access.

* `src/main/java/.../smartgarage/` — **Root Application Package**
    * 📁 `controller/rest/` — RESTful API controllers (returning JSON).
    * 📁 `controller/mvc/` — Spring MVC controllers serving Thymeleaf templates.
    * 📁 `service/` — Core business logic, external API integrations, and transaction management.
    * 📁 `repository/` — Spring Data JPA interfaces for database operations.
    * 📁 `model/` — JPA entity classes representing database tables.
    * 📁 `security/` — JWT utilities, authentication filters, and Spring Security configurations.
    * 📁 `dto/` — Data Transfer Objects used to decouple internal models from the external APIs and Views.
* `src/main/resources/` — **Static Assets & Configuration**
    * 📁 `templates/` — Server-side Thymeleaf HTML views (organized by `customer`, `employee`, and `auth`).
    * 📁 `static/` — CSS stylesheets, JavaScript files, and images.
* `db-create.sql` — SQL script containing initial DDL/DML to seed the database with demo data (brands, models, vehicles, services).
* `build.gradle` — Gradle build configuration defining project dependencies and plugins.

## Prerequisites

Install these on your machine (macOS):

- Java 17 (JDK)
- Gradle (optional; the project includes `gradlew` so you can use the wrapper)
- MariaDB or MySQL server (the project uses the MariaDB JDBC driver)

Recommended environment variables (see `application.properties` usage below):
- DB_USERNAME - database username
- DB_PASSWORD - database password
- JWT_SECRET_KEY - secret key for signing JWT tokens (must be at least 32 chars for HS256)
- MAIL_USERNAME - SMTP username (if email sending required)
- MAIL_PASSWORD - SMTP password
- CURRENCY_API_KEY - (optional) for currency conversion feature

You can export these for your shell (zsh) before running: 

```bash
export DB_USERNAME=mydbuser
export DB_PASSWORD=secret
export JWT_SECRET_KEY="your-very-long-secret-at-least-32-chars"
export MAIL_USERNAME=your@mail.com
export MAIL_PASSWORD=supersecret
export CURRENCY_API_KEY=your_api_key
```

## Quick start (development)

1. Ensure MariaDB is running and reachable at `localhost:3306` (or update `spring.datasource.url` in `application.properties`).
2. Create the database and seed demo data (see Database setup below) or let the app create schema automatically.
3. Run with Gradle wrapper (recommended):

```bash
./gradlew bootRun
```

Or build and run the JAR:

```bash
./gradlew clean bootJar
java -jar build/libs/SmartGarage-0.0.1-SNAPSHOT.jar
```

The app listens on port 8080 by default: http://localhost:8080

Swagger UI (API docs): http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs

## Production build & run

- Build optimized JAR:

```bash
./gradlew clean bootJar -x test
```

- Run the JAR (ensure env vars are set):

```bash
java -jar build/libs/SmartGarage-0.0.1-SNAPSHOT.jar
```

- For container or cloud deployment, consider creating environment-specific `application-*.properties` files and use Spring profiles.

## Database setup (db-create.sql)

The project expects a MariaDB/MySQL compatible database. The main JDBC URL in `application.properties` is:

```
spring.datasource.url=jdbc:mariadb://localhost:3306/smart_garage_db?createDatabaseIfNotExist=true
```

The repository includes `db-create.sql` at project root which seeds brands, models, vehicles and example services.

## Configuration (important keys in `application.properties`)

File: `src/main/resources/application.properties` — the most important properties:

- spring.application.name=SmartGarage
- server.port=8080

Datasource (change or supply env vars):
- spring.datasource.url=jdbc:mariadb://localhost:3306/smart_garage_db?createDatabaseIfNotExist=true
- spring.datasource.username=${DB_USERNAME}
- spring.datasource.password=${DB_PASSWORD}

JPA:
- spring.jpa.hibernate.ddl-auto=update (development convenience; consider `validate` or `none` in production)

JWT:
- smartgarage.jwt.secret=${JWT_SECRET_KEY}
- smartgarage.jwt.expiration=86400000  # default value in ms (1 day)

Mail (optional):
- spring.mail.username=${MAIL_USERNAME}
- spring.mail.password=${MAIL_PASSWORD}

Currency API (optional):
- smartgarage.currency.api.key=${CURRENCY_API_KEY}
- smartgarage.currency.api.url=https://v6.exchangerate-api.com/v6/

File upload limits:
- spring.servlet.multipart.max-file-size=2MB
- spring.servlet.multipart.max-request-size=2MB

Logging:
- logging.level.com.portfolio.smartgarage.security=DEBUG
- logging.level.org.springframework.security=DEBUG

Important env vars summary:
- DB_USERNAME, DB_PASSWORD, JWT_SECRET_KEY, MAIL_USERNAME, MAIL_PASSWORD, CURRENCY_API_KEY

Security note: Do NOT store `JWT_SECRET_KEY` or mail credentials in version control. Use environment variables or a secret manager.

Security and JWT details:
- The app accepts JWT via an Authorization: Bearer <token> header or a cookie named `jwt`.
- The `JwtAuthFilter` checks cookies and headers. Login (REST) sets `jwt` cookie automatically.

For complete API details and DTO shapes, open Swagger UI: http://localhost:8080/swagger-ui.html

## Frontend pages (MVC mappings)

Key Thymeleaf pages are located in `src/main/resources/templates` and tied to MVC controllers:
- `/` -> `index.html`
- `/auth/login` -> `templates/auth/login.html`
- `/auth/register` -> `templates/auth/register.html`
- `/auth/forgot-password` -> `templates/auth/forgot-password.html`
- Customer dashboard and booking pages under `/customer/**` (templates in `templates/customer/`)
- Employee admin pages under `/employee/**` (templates in `templates/employee/`)
