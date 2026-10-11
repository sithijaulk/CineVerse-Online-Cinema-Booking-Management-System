create table users (
    id bigint auto_increment primary key,
    full_name varchar(200) not null,
    email varchar(120) not null unique,
    password varchar(255) not null,
    phone varchar(30),
    role varchar(30) not null,
    status varchar(20) not null,
    created_at datetime not null,
    updated_at datetime not null,
    index idx_users_email (email)
) engine=InnoDB;

create table movies (
    id bigint auto_increment primary key,
    title varchar(150) not null unique,
    description varchar(2000) not null,
    genre varchar(100) not null,
    language varchar(50) not null,
    duration_minutes int not null,
    release_date date not null,
    age_rating varchar(20) not null,
    cast_members varchar(1000),
    director varchar(150),
    trailer_url varchar(500),
    poster_url varchar(500),
    status varchar(30) not null,
    created_at datetime not null,
    updated_at datetime not null,
    index idx_movies_title (title),
    index idx_movies_status (status)
) engine=InnoDB;

create table cinema_branches (
    id bigint auto_increment primary key,
    branch_name varchar(150) not null unique,
    location varchar(255) not null,
    active boolean not null default true,
    created_at datetime not null,
    updated_at datetime not null
) engine=InnoDB;

create table screens (
    id bigint auto_increment primary key,
    branch_id bigint not null,
    screen_name varchar(100) not null,
    capacity int not null,
    active boolean not null default true,
    created_at datetime not null,
    updated_at datetime not null,
    unique key uk_screens_branch_name (branch_id, screen_name),
    constraint fk_screens_branch foreign key (branch_id) references cinema_branches(id)
) engine=InnoDB;

create table seats (
    id bigint auto_increment primary key,
    screen_id bigint not null,
    row_name varchar(10) not null,
    seat_number int not null,
    seat_type varchar(20) not null,
    active boolean not null default true,
    created_at datetime not null,
    updated_at datetime not null,
    unique key uk_seats_position (screen_id, row_name, seat_number),
    constraint fk_seats_screen foreign key (screen_id) references screens(id)
) engine=InnoDB;

create table showtimes (
    id bigint auto_increment primary key,
    movie_id bigint not null,
    screen_id bigint not null,
    show_date date not null,
    start_time time not null,
    ticket_price decimal(10,2) not null,
    status varchar(20) not null,
    created_at datetime not null,
    updated_at datetime not null,
    unique key uk_showtimes_start (screen_id, show_date, start_time),
    index idx_showtimes_date (show_date),
    constraint fk_showtimes_movie foreign key (movie_id) references movies(id),
    constraint fk_showtimes_screen foreign key (screen_id) references screens(id)
) engine=InnoDB;

create table reservations (
    id bigint auto_increment primary key,
    booking_reference varchar(30) not null unique,
    user_id bigint not null,
    showtime_id bigint not null,
    booking_date datetime not null,
    total_amount decimal(10,2) not null,
    reservation_status varchar(20) not null,
    seat_summary varchar(255) not null,
    created_at datetime not null,
    updated_at datetime not null,
    index idx_reservations_reference (booking_reference),
    index idx_reservations_status (reservation_status),
    constraint fk_reservations_user foreign key (user_id) references users(id),
    constraint fk_reservations_showtime foreign key (showtime_id) references showtimes(id)
) engine=InnoDB;

create table reservation_seats (
    id bigint auto_increment primary key,
    reservation_id bigint not null,
    showtime_id bigint not null,
    seat_id bigint not null,
    created_at datetime not null,
    updated_at datetime not null,
    unique key uk_reservation_seats (showtime_id, seat_id),
    constraint fk_reservation_seats_reservation foreign key (reservation_id) references reservations(id),
    constraint fk_reservation_seats_showtime foreign key (showtime_id) references showtimes(id),
    constraint fk_reservation_seats_seat foreign key (seat_id) references seats(id)
) engine=InnoDB;

create table seat_holds (
    id bigint auto_increment primary key,
    showtime_id bigint not null,
    seat_id bigint not null,
    user_id bigint not null,
    expires_at datetime not null,
    created_at datetime not null,
    unique key uk_seat_holds (showtime_id, seat_id),
    index idx_seat_holds_expiry (expires_at),
    constraint fk_seat_holds_showtime foreign key (showtime_id) references showtimes(id),
    constraint fk_seat_holds_seat foreign key (seat_id) references seats(id),
    constraint fk_seat_holds_user foreign key (user_id) references users(id)
) engine=InnoDB;

create table payments (
    id bigint auto_increment primary key,
    reservation_id bigint not null unique,
    amount decimal(10,2) not null,
    payment_method varchar(50) not null,
    payment_status varchar(20) not null,
    payment_date datetime null,
    transaction_ref varchar(60),
    created_at datetime not null,
    updated_at datetime not null,
    constraint fk_payments_reservation foreign key (reservation_id) references reservations(id)
) engine=InnoDB;
