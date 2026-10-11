# CineVerse Online Cinema Booking Management System

CineVerse is a full-stack cinema reservation and theatre management system built with Spring Boot, Spring Security, JPA/Hibernate, MySQL, and a responsive HTML/CSS/JavaScript frontend.

The platform supports public movie discovery, seat selection, reservations, payments, e-tickets, customer profiles, staff verification, and administrator management of the cinema catalogue and users.

## Features

### Customer experience

- Browse the complete movie catalogue, including now-showing and coming-soon movies.
- Search movies by name, genre, language, and age rating.
- View movie details, posters, trailers, showtimes, branches, and ticket prices.
- Select seats from a live seat layout.
- Temporarily hold seats before completing a reservation.
- Complete simulated payments and receive booking references/e-tickets.
- View booking history and reservation status.
- Edit profile name and mobile number.
- Deactivate a customer account.
- Switch between dark and light themes.

### Administration and staff

- Admin dashboard with cinema statistics.
- Movie create, update, poster upload, and deactivation.
- Branch, screen, seat, and showtime management.
- Reservation search, cancellation, and ticket verification.
- User management with customer/staff/admin roles.
- Activate, deactivate, lock, or delete managed users.
- Create staff accounts from the admin portal.
- Staff reservation verification workflow.

### Validation and reliability

- Server-side Jakarta Bean Validation for authentication, profiles, movies, bookings, seats, and showtimes.
- Browser-side form constraints and mobile-number filtering.
- Mobile numbers accept exactly 10 digits.
- Release dates are validated in both the browser and service layer.
- Booking references fit the database schema limit.
- Seat holds are flushed before replacement holds are created.
- Reservation and seat conflicts return user-friendly messages.
- Success toast notifications are shown after create, update, delete, upload, cancellation, and verification actions.

## Technology Stack

- Java 17
- Spring Boot 3.3.2
- Spring Web
- Spring Data JPA / Hibernate
- Spring Security
- Jakarta Bean Validation
- MySQL 8+ or XAMPP MySQL
- Maven
- Bootstrap 5.3
- Bootstrap Icons
- Vanilla JavaScript
- H2 only for automated tests

## Project Structure

```text
src/main/java/com/se2030/cinema
  config/          Application configuration and demo data seeding
  controller/      REST controllers
  dto/             Request and response objects
  entity/          JPA entities and enums
  exception/       API exception handling
  repository/      Spring Data repositories
  security/        Authentication and user details
  service/         Service contracts and implementations

src/main/resources
  application.properties
  db/migration/    Optional Flyway migration scripts
  static/          Customer, staff, and admin web pages
    css/style.css
    js/app.js

database/          SQL reference and sample scripts
src/test/           Automated Spring Boot tests
MYSQL_SETUP.md      MySQL and phpMyAdmin setup guide
```

## Requirements

Install the following before running the application:

- JDK 17 or newer
- Maven 3.9+
- MySQL Server 8+ or XAMPP with MySQL enabled

The current project is configured for MySQL on `localhost:3306`.

## Database Setup

Start MySQL from XAMPP or from the MySQL service. The application uses these defaults:

```text
Database: cinema_reservation
Username: root
Password: empty by default for XAMPP
Host: localhost
Port: 3306
```

The JDBC URL creates the database if it does not already exist. Hibernate uses `update`, so existing data is preserved when the application restarts.

For a non-default MySQL password, set the environment variable before starting:

```powershell
$env:DATABASE_USERNAME = "root"
$env:DATABASE_PASSWORD = "your_mysql_password"
```

For the complete setup and phpMyAdmin instructions, see [MYSQL_SETUP.md](MYSQL_SETUP.md).

## Run Locally

From the project directory:

```powershell
$env:Path = "C:\Tools\apache-maven-3.9.16\bin;$env:Path"
$env:DATABASE_USERNAME = "root"
$env:DATABASE_PASSWORD = ""
$env:JPA_HIBERNATE_DDL_AUTO = "update"
$env:FLYWAY_ENABLED = "false"
mvn spring-boot:run
```

Open the application at:

```text
http://localhost:8080
```

On first startup, the application creates demo data when the database is empty.

## Demo Credentials

The local demo seed creates the following administrator account:

```text
Email: admin@cinema.com
Password: password
```

Change demo credentials before using the application in a real environment.

## Main Routes

### Public pages

```text
/index.html
/movies.html
/movie-details.html?id={movieId}
/booking.html?showtimeId={showtimeId}
/my-bookings.html
/profile.html
/login.html
/register.html
```

### Admin pages

```text
/admin/dashboard.html
/admin/movies.html
/admin/branches.html
/admin/screens.html
/admin/seats.html
/admin/showtimes.html
/admin/reservations.html
/admin/users.html
```

### REST API groups

```text
/api/auth/**
/api/public/**
/api/customer/**
/api/admin/**
/api/staff/**
```

Admin and staff API routes are protected by role-based Spring Security rules. Customer reservation and profile routes require an authenticated user.

## Testing

Run the focused application data test:

```powershell
mvn test -Dtest=AppDataBootstrapTest
```

Run the complete test suite:

```powershell
mvn test
```

H2 is used only by the test profile in `src/test/resources/application.properties`; the running application uses MySQL.

## Data and Security Notes

- Never commit production passwords or private credentials.
- Replace the demo admin password for any shared deployment.
- Use HTTPS and secure cookie settings in production.
- Use a restricted MySQL user instead of `root` in production.
- Do not use `create-drop` with a persistent database.
- Booking history is preserved when a user is deactivated.
- Users with booking history cannot be hard-deleted because reservations depend on their user record.

## License

This project is provided for educational and demonstration purposes.
