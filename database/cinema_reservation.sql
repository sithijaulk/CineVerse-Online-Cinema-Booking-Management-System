CREATE DATABASE IF NOT EXISTS cinema_reservation CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cinema_reservation;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(200) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(30),
    role VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_users_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL UNIQUE,
    description VARCHAR(2000) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    language VARCHAR(50) NOT NULL,
    duration_minutes INT NOT NULL,
    release_date DATE NOT NULL,
    age_rating VARCHAR(20) NOT NULL,
    cast_members VARCHAR(1000),
    director VARCHAR(150),
    trailer_url VARCHAR(500),
    poster_url VARCHAR(500),
    status VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_movies_title (title),
    INDEX idx_movies_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cinema_branches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_name VARCHAR(150) NOT NULL UNIQUE,
    location VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS screens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_id BIGINT NOT NULL,
    screen_name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_screens_branch_name (branch_id, screen_name),
    CONSTRAINT fk_screens_branch FOREIGN KEY (branch_id) REFERENCES cinema_branches(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    screen_id BIGINT NOT NULL,
    row_name VARCHAR(10) NOT NULL,
    seat_number INT NOT NULL,
    seat_type VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_seats_position (screen_id, row_name, seat_number),
    CONSTRAINT fk_seats_screen FOREIGN KEY (screen_id) REFERENCES screens(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS showtimes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    screen_id BIGINT NOT NULL,
    show_date DATE NOT NULL,
    start_time TIME NOT NULL,
    ticket_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_showtimes_start (screen_id, show_date, start_time),
    INDEX idx_showtimes_date (show_date),
    CONSTRAINT fk_showtimes_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_showtimes_screen FOREIGN KEY (screen_id) REFERENCES screens(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_reference VARCHAR(30) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    showtime_id BIGINT NOT NULL,
    booking_date DATETIME NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    reservation_status VARCHAR(20) NOT NULL,
    seat_summary VARCHAR(255) NOT NULL,
    verified_at DATETIME NULL,
    verified_by BIGINT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_reservations_reference (booking_reference),
    INDEX idx_reservations_status (reservation_status),
    CONSTRAINT fk_reservations_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reservations_showtime FOREIGN KEY (showtime_id) REFERENCES showtimes(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reservation_seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    showtime_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_reservation_seats (showtime_id, seat_id),
    CONSTRAINT fk_reservation_seats_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    CONSTRAINT fk_reservation_seats_showtime FOREIGN KEY (showtime_id) REFERENCES showtimes(id),
    CONSTRAINT fk_reservation_seats_seat FOREIGN KEY (seat_id) REFERENCES seats(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS seat_holds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    showtime_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    UNIQUE KEY uk_seat_holds (showtime_id, seat_id),
    INDEX idx_seat_holds_expiry (expires_at),
    CONSTRAINT fk_seat_holds_showtime FOREIGN KEY (showtime_id) REFERENCES showtimes(id),
    CONSTRAINT fk_seat_holds_seat FOREIGN KEY (seat_id) REFERENCES seats(id),
    CONSTRAINT fk_seat_holds_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    payment_date DATETIME NULL,
    transaction_ref VARCHAR(60),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_payments_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id)
) ENGINE=InnoDB;
