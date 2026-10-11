# MySQL setup

The application uses MySQL by default. H2 is available only for automated tests.

## 1. Create or start MySQL

Install MySQL Server and make sure it is running on port `3306`. phpMyAdmin is optional; it is only a browser UI for MySQL.

## 2. Configure the application

PowerShell example:

```powershell
$env:DATABASE_URL = "jdbc:mysql://localhost:3306/cinema_reservation?createDatabaseIfNotExist=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true"
$env:DATABASE_USERNAME = "root"
$env:DATABASE_PASSWORD = "your_mysql_password"
$env:JPA_HIBERNATE_DDL_AUTO = "update"
$env:FLYWAY_ENABLED = "false"
$env:Path = "C:\Tools\apache-maven-3.9.16\bin;$env:Path"
mvn spring-boot:run
```

The application creates and updates the required tables automatically. `update` preserves existing rows when the app restarts.

## 3. View the data

Open phpMyAdmin at `http://localhost/phpmyadmin`, select `cinema_reservation`, and inspect tables such as:

- `users`
- `movies`
- `cinema_branches`
- `screens`
- `seats`
- `showtimes`
- `reservations`
- `payments`

The default local seed creates `admin@cinema.com` with password `password` when the database is empty.

## Notes

- Do not use `create-drop` for MySQL because it removes tables when the application stops.
- The current default is `update`, so data remains after restart.
- If MySQL uses a non-default port or username, change the environment variables before starting the app.
