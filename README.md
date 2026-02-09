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

## Tech stack

- Java 17 (toolchain configured in `build.gradle`)
- Spring Boot 4.x (see `build.gradle`)
- Spring Data JPA (Hibernate)
- MariaDB (runtime driver included)
- Spring Security with JWT
- Thymeleaf for server-side HTML templates
- OpenPDF for PDF generation of invoices
- Springdoc OpenAPI (Swagger) for API docs
- JUnit / Spring Boot Test for automated tests

## Architecture & important folders

- `src/main/java/com/portfolio/smartgarage` - application package
  - `controller/rest` - REST controllers (API)
  - `controller/mvc` - MVC controllers serving templates
  - `service` - business logic
  - `repository` - JPA repositories
  - `model` - JPA entities
  - `security` - JWT utilities and filters
  - `dto` - data transfer objects
- `src/main/resources/templates` - Thymeleaf pages (customer, employee, auth pages)
- `src/main/resources/static` - CSS/images
- `db-create.sql` - SQL script to create sample data (brands, models, vehicles, services)
- `build.gradle` - Gradle build file
- `HELP.md` - extra reference links

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

To apply the script manually (using the mysql CLI):

```bash
# login to your MariaDB server
mysql -u root -p < db-create.sql
# or if you need to specify database/user
mysql -u youruser -p -h localhost < db-create.sql
```

Notes:
- The script uses `TRUNCATE` and manipulates foreign key checks. Use with caution in non-development environments.
- The application is also configured with `spring.jpa.hibernate.ddl-auto=update`, so it will create/update schema automatically when you run it.

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

## API / Endpoints (high-level overview)

This section lists key API endpoints inferred from the controller classes. The app also exposes Swagger UI at `/swagger-ui.html`.

Authentication (public):
- POST /api/auth/login — JSON login, returns AuthResponseDto and sets `jwt` cookie
- POST /api/auth/register — Register a new user
- POST /api/auth/forgot-password — Initiate password reset (email)
- POST /api/auth/reset-password — Reset using token

Vehicles & catalog (authenticated; customer & employee):
- GET /api/vehicles/brands — list brands
- GET /api/vehicles/brands/{brandId}/models — list models for brand
- GET /api/vehicles/models/{modelId}/years — list years for a model
- GET /api/vehicles/my — get vehicles registered to the current user
- POST /api/vehicles/my/register — register a vehicle to current user
- GET /api/vehicles/{id} — get client-vehicle details (owner or employee)
- DELETE /api/vehicles/{id} — delete a client vehicle (owner or employee)

Services (customer & employee):
- GET /api/services — list all services
- GET /api/services/search?name={name} — search services by name
- GET /api/services/{id} — get service by id

Visits (booking & management):
- POST /api/visits — book a visit (authenticated customer)
- GET /api/visits/availability?startDate=YYYY-MM-DD — check calendar availability
- GET /api/visits/{id}?currency=BGN|EUR|USD — get visit details
- GET /api/visits/my-history — get current user's visit history (requires CUSTOMER role)
- DELETE /api/visits/{id} — cancel a visit (owner or employee)

Admin (EMPLOYEE role required):
- POST /api/admin/services — create service
- PUT /api/admin/services/{id} — update service
- DELETE /api/admin/services/{id} — remove service
- POST /api/admin/visits — create a visit (for existing customers)
- POST /api/admin/visits/new-customer — register user + vehicle + visit in a single request
- GET /api/admin/visits/report/{id} — detailed admin report for a visit
- PATCH /api/admin/visits/{id}/status?status=IN_PROGRESS — update visit status
- DELETE /api/admin/visits/{id} — delete visit

Invoices (EMPLOYEE):
- GET /api/invoices/download/{orderId} — generate and download invoice PDF

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
