package com.se2030.cinema.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.se2030.cinema.entity.CinemaBranch;
import com.se2030.cinema.entity.Movie;
import com.se2030.cinema.entity.MovieStatus;
import com.se2030.cinema.entity.Role;
import com.se2030.cinema.entity.Screen;
import com.se2030.cinema.entity.Seat;
import com.se2030.cinema.entity.SeatType;
import com.se2030.cinema.entity.Showtime;
import com.se2030.cinema.entity.ShowtimeStatus;
import com.se2030.cinema.entity.User;
import com.se2030.cinema.entity.UserStatus;
import com.se2030.cinema.repository.CinemaBranchRepository;
import com.se2030.cinema.repository.MovieRepository;
import com.se2030.cinema.repository.ScreenRepository;
import com.se2030.cinema.repository.SeatRepository;
import com.se2030.cinema.repository.ShowtimeRepository;
import com.se2030.cinema.repository.UserRepository;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin@cinema.com";
    private static final String ADMIN_PASSWORD = "password";

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final CinemaBranchRepository branchRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      MovieRepository movieRepository,
                      CinemaBranchRepository branchRepository,
                      ScreenRepository screenRepository,
                      SeatRepository seatRepository,
                      ShowtimeRepository showtimeRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.branchRepository = branchRepository;
        this.screenRepository = screenRepository;
        this.seatRepository = seatRepository;
        this.showtimeRepository = showtimeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedAdminUser();
        seedMovies();
        seedCinemaData();
    }

    private void seedAdminUser() {
        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }

        User admin = new User();
        admin.setFullName("System Administrator");
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setPhone("0770000000");
        admin.setRole(Role.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);

        userRepository.save(admin);
    }

    private void seedMovies() {
        if (movieRepository.count() > 0) {
            return;
        }

        List<Movie> movies = List.of(
                buildMovie("Dune: Part Two", "Paul Atreides must unite with the Fremen to protect Arrakis while the empire closes in.",
                        "Sci-Fi / Action", "English", 166, LocalDate.of(2024, 3, 1), "PG-13",
                        "Timothée Chalamet, Zendaya, Rebecca Ferguson", "Denis Villeneuve",
                        "https://example.com/trailer/dune-part-two.mp4",
                        "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=800&q=80",
                        MovieStatus.NOW_SHOWING),
                buildMovie("Inside Out 2", "Riley's emotions return for a bigger, more chaotic adventure in adolescence.",
                        "Animation / Family", "English", 96, LocalDate.of(2024, 6, 14), "G",
                        "Amy Poehler, Maya Hawke, Lewis Black", "Kelsey Mann",
                        "https://example.com/trailer/inside-out-2.mp4",
                        "https://images.unsplash.com/photo-1513106580091-1d82408b8cd6?auto=format&fit=crop&w=800&q=80",
                        MovieStatus.NOW_SHOWING),
                buildMovie("A Quiet Place: Day One", "A deadly alien invasion hits Manhattan as a woman tries to survive the chaos.",
                        "Horror / Thriller", "English", 100, LocalDate.of(2024, 6, 28), "PG-13",
                        "Lupita Nyong'o, Joseph Quinn, Alex Wolff", "Michael Sarnoski",
                        "https://example.com/trailer/quiet-place-day-one.mp4",
                        "https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=800&q=80",
                        MovieStatus.NOW_SHOWING),
                buildMovie("The Batman", "Batman follows a brutal trail through Gotham while a killer's reign of terror unfolds.",
                        "Action / Crime", "English", 176, LocalDate.of(2022, 3, 4), "PG-13",
                        "Robert Pattinson, Zoë Kravitz, Paul Dano", "Matt Reeves",
                        "https://example.com/trailer/the-batman.mp4",
                        "https://images.unsplash.com/photo-1535016120720-40c646be5580?auto=format&fit=crop&w=800&q=80",
                        MovieStatus.NOW_SHOWING),
                buildMovie("Kung Fu Panda 4", "Po returns for a new adventure where he must embrace a new role as a dragon warrior.",
                        "Animation / Adventure", "English", 94, LocalDate.of(2024, 7, 13), "PG",
                        "Jack Black, Awkwafina, Viola Davis", "Mike Mitchell",
                        "https://example.com/trailer/kung-fu-panda-4.mp4",
                        "https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&w=800&q=80",
                        MovieStatus.COMING_SOON)
        );

        movieRepository.saveAll(movies);
    }

    private Movie buildMovie(String title, String description, String genre, String language, int durationMinutes,
                            LocalDate releaseDate, String ageRating, String castMembers, String director,
                            String trailerUrl, String posterUrl, MovieStatus status) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setGenre(genre);
        movie.setLanguage(language);
        movie.setDurationMinutes(durationMinutes);
        movie.setReleaseDate(releaseDate);
        movie.setAgeRating(ageRating);
        movie.setCastMembers(castMembers);
        movie.setDirector(director);
        movie.setTrailerUrl(trailerUrl);
        movie.setPosterUrl(posterUrl);
        movie.setStatus(status);
        return movie;
    }

    private void seedCinemaData() {
        if (branchRepository.count() > 0) {
            return;
        }

        List<CinemaBranch> branches = List.of(
                createBranch("Majestic Cinema", "Colombo 03"),
                createBranch("CineLux Downtown", "Kandy"),
                createBranch("Skyline Multiplex", "Galle")
        );

        List<CinemaBranch> savedBranches = branchRepository.saveAll(branches);
        List<Screen> screens = new ArrayList<>();

        for (int i = 0; i < savedBranches.size(); i++) {
            CinemaBranch branch = savedBranches.get(i);
            for (int s = 1; s <= 2; s++) {
                Screen screen = new Screen();
                screen.setBranch(branch);
                screen.setScreenName("Screen " + s);
                screen.setCapacity(48);
                screen.setActive(true);
                screens.add(screen);
            }
        }

        List<Screen> savedScreens = screenRepository.saveAll(screens);

        for (Screen screen : savedScreens) {
            List<Seat> seats = new ArrayList<>();
            String[] rows = {"A", "B", "C", "D", "E", "F"};
            for (String row : rows) {
                for (int number = 1; number <= 8; number++) {
                    Seat seat = new Seat();
                    seat.setScreen(screen);
                    seat.setRowName(row);
                    seat.setSeatNumber(number);
                    seat.setSeatType(number % 5 == 0 ? SeatType.PREMIUM : SeatType.STANDARD);
                    seat.setActive(true);
                    seats.add(seat);
                }
            }
            seatRepository.saveAll(seats);
        }

        seedShowtimes(savedScreens);
    }

    private CinemaBranch createBranch(String branchName, String location) {
        CinemaBranch branch = new CinemaBranch();
        branch.setBranchName(branchName);
        branch.setLocation(location);
        branch.setActive(true);
        return branch;
    }

    private void seedShowtimes(List<Screen> screens) {
        List<Movie> movies = movieRepository.findAll();
        if (movies.isEmpty()) {
            return;
        }

        List<Showtime> showtimes = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < Math.min(screens.size(), movies.size()); i++) {
            Screen screen = screens.get(i);
            Movie movie = movies.get(i % movies.size());
            showtimes.add(createShowtime(movie, screen, today.plusDays(i % 3), LocalTime.of(12 + (i % 4), 30), new BigDecimal("1200.00")));
            showtimes.add(createShowtime(movie, screen, today.plusDays((i % 3) + 1), LocalTime.of(15 + (i % 3), 0), new BigDecimal("1500.00")));
        }

        if (!showtimes.isEmpty()) {
            showtimeRepository.saveAll(showtimes);
        }
    }

    private Showtime createShowtime(Movie movie, Screen screen, LocalDate showDate, LocalTime startTime, BigDecimal price) {
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setScreen(screen);
        showtime.setShowDate(showDate);
        showtime.setStartTime(startTime);
        showtime.setTicketPrice(price);
        showtime.setStatus(ShowtimeStatus.SCHEDULED);
        return showtime;
    }
}
