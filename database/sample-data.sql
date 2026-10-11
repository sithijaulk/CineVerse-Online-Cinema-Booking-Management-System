USE cinema_reservation;

-- 1. Insert Cinema Branches
INSERT INTO cinema_branches (id, branch_name, location, active, created_at, updated_at) VALUES
(1, 'Scope Cinemas - Colombo City Centre', 'Mall Level 3, 137 Sir James Pieris Mawatha, Colombo 02', TRUE, NOW(), NOW()),
(2, 'PVR Cinemas - One Galle Face Mall', 'Level 4, 1A Centre Road, Galle Face, Colombo 01', TRUE, NOW(), NOW()),
(3, 'Liberty Cinema by Scope', '266 R. A. De Mel Mawatha, Colombo 03', TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE branch_name=VALUES(branch_name), location=VALUES(location), updated_at=NOW();

-- 2. Insert Screens
INSERT INTO screens (id, branch_id, screen_name, capacity, active, created_at, updated_at) VALUES
(1, 1, 'Screen 1 - IMAX Laser 3D', 40, TRUE, NOW(), NOW()),
(2, 1, 'Screen 2 - Dolby Atmos VIP', 40, TRUE, NOW(), NOW()),
(3, 2, 'Screen 1 - PVR LUXE Recliner', 40, TRUE, NOW(), NOW()),
(4, 2, 'Screen 2 - PVR 4DX Motion', 40, TRUE, NOW(), NOW()),
(5, 3, 'Auditorium A - Scope Premier', 40, TRUE, NOW(), NOW())
ON DUPLICATE KEY UPDATE branch_id=VALUES(branch_id), screen_name=VALUES(screen_name), capacity=VALUES(capacity), updated_at=NOW();

-- 3. Insert Seats for Screens 1 to 5 (4 rows: A, B, C, D with 10 seats each)
-- Row A & B: STANDARD, Row C: PREMIUM, Row D: VIP
INSERT IGNORE INTO seats (screen_id, row_name, seat_number, seat_type, active, created_at, updated_at)
SELECT s.id, r.row_name, n.seat_num, 
       CASE WHEN r.row_name IN ('A','B') THEN 'STANDARD' 
            WHEN r.row_name = 'C' THEN 'PREMIUM' 
            ELSE 'VIP' END,
       TRUE, NOW(), NOW()
FROM screens s
CROSS JOIN (SELECT 'A' AS row_name UNION SELECT 'B' UNION SELECT 'C' UNION SELECT 'D') r
CROSS JOIN (SELECT 1 AS seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 
            UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) n;

-- 4. Insert Real-World Blockbuster Movies
INSERT INTO movies (id, title, description, genre, language, duration_minutes, release_date, age_rating, cast_members, director, trailer_url, poster_url, status, created_at, updated_at) VALUES
(1, 'Dune: Part Two', 
 'Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family. Facing a choice between the love of his life and the fate of the known universe, he endeavors to prevent a terrible future only he can foresee.',
 'Sci-Fi / Adventure / Action', 'English', 166, '2024-03-01', 'PG-13',
 'Timothée Chalamet, Zendaya, Rebecca Ferguson, Javier Bardem, Austin Butler, Florence Pugh',
 'Denis Villeneuve',
 'https://www.youtube.com/watch?v=Way9Dexny3w',
 'https://image.tmdb.org/t/p/w500/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg',
 'NOW_SHOWING', NOW(), NOW()),

(2, 'Deadpool & Wolverine',
 'A listless Wade Wilson toils in civilian life with his days as the morally flexible mercenary Deadpool behind him. But when his homeworld faces an existential threat, Wade must reluctantly suit up again with an even more reluctant Wolverine.',
 'Action / Comedy / Sci-Fi', 'English', 128, '2024-07-26', 'R',
 'Ryan Reynolds, Hugh Jackman, Emma Corrin, Matthew Macfadyen, Morena Baccarin',
 'Shawn Levy',
 'https://www.youtube.com/watch?v=73_1biulkYk',
 'https://image.tmdb.org/t/p/w500/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg',
 'NOW_SHOWING', NOW(), NOW()),

(3, 'Gladiator II',
 'Years after witnessing the death of the revered hero Maximus at the hands of his uncle, Lucius must enter the Colosseum after his home is conquered by the tyrannical Emperors who now lead Rome with an iron fist. With rage in his heart, he looks to his past to return glory to Rome.',
 'Action / Drama / Adventure', 'English', 148, '2024-11-22', 'R',
 'Paul Mescal, Pedro Pascal, Denzel Washington, Connie Nielsen, Joseph Quinn',
 'Ridley Scott',
 'https://www.youtube.com/watch?v=4rgYUipGJNo',
 'https://image.tmdb.org/t/p/w500/2cxhvwyEwRlysAmRH4iodkvo0z5.jpg',
 'NOW_SHOWING', NOW(), NOW()),

(4, 'Oppenheimer',
 'The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb during World War II, exploring the moral and geopolitical ramifications of the Manhattan Project.',
 'Biography / Drama / History', 'English', 180, '2023-07-21', 'R',
 'Cillian Murphy, Emily Blunt, Matt Damon, Robert Downey Jr., Florence Pugh',
 'Christopher Nolan',
 'https://www.youtube.com/watch?v=uYPbbksJxIg',
 'https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg',
 'NOW_SHOWING', NOW(), NOW()),

(5, 'Inside Out 2',
 'Disney and Pixar’s Inside Out 2 returns to the mind of newly minted teenager Riley just as headquarters is undergoing a sudden demolition to make room for something entirely unexpected: new Emotions! Joy, Sadness, Anger, Fear and Disgust face Anxiety, Envy, Ennui and Embarrassment.',
 'Animation / Family / Comedy', 'English', 96, '2024-06-14', 'PG',
 'Amy Poehler, Maya Hawke, Kensington Tallman, Liza Lapira, Tony Hale',
 'Kelsey Mann',
 'https://www.youtube.com/watch?v=LEjhY15eCx0',
 'https://image.tmdb.org/t/p/w500/vpnVM9B6NMmQpWeZvzLvDESb2QY.jpg',
 'NOW_SHOWING', NOW(), NOW()),

(6, 'Spider-Man: Across the Spider-Verse',
 'Miles Morales catapults across the Multiverse, where he encounters a team of Spider-People charged with protecting its very existence. When the heroes clash on how to handle a new threat, Miles must redefine what it means to be a hero.',
 'Animation / Action / Sci-Fi', 'English', 140, '2023-06-02', 'PG',
 'Shameik Moore, Hailee Steinfeld, Oscar Isaac, Daniel Kaluuya, Issa Rae',
 'Joaquim Dos Santos, Kemp Powers',
 'https://www.youtube.com/watch?v=cqGjhVJWtEg',
 'https://image.tmdb.org/t/p/w500/8Vt6mWEReuy4Of61Lnj5Xj704m8.jpg',
 'NOW_SHOWING', NOW(), NOW()),

(7, 'Interstellar: 10th Anniversary IMAX',
 'When Earth becomes uninhabitable in the future, a farmer and ex-NASA pilot, Joseph Cooper, is tasked to pilot a spacecraft, along with a team of researchers, to find a new planet for humans.',
 'Sci-Fi / Adventure / Drama', 'English', 169, '2026-11-01', 'PG-13',
 'Matthew McConaughey, Anne Hathaway, Jessica Chastain, Michael Caine',
 'Christopher Nolan',
 'https://www.youtube.com/watch?v=zSWdZVtXT7E',
 'https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg',
 'COMING_SOON', NOW(), NOW()),

(8, 'Avatar: The Way of Water',
 'Jake Sully lives with his newfound family formed on the extrasolar moon Pandora. Once a familiar threat returns to finish what was previously started, Jake must work with Neytiri and the army of the Na\'vi race to protect their home.',
 'Sci-Fi / Action / Adventure', 'English', 192, '2026-11-15', 'PG-13',
 'Sam Worthington, Zoe Saldana, Sigourney Weaver, Stephen Lang, Kate Winslet',
 'James Cameron',
 'https://www.youtube.com/watch?v=d9MyW72ELq0',
 'https://image.tmdb.org/t/p/w500/t6HIqrRAclMCA60NsSmeqe9RmNV.jpg',
 'COMING_SOON', NOW(), NOW()),

(9, 'The Batman: Part II',
 'The Dark Knight ventures into Gotham City\'s underworld when a sadistic killer leaves behind a trail of cryptic clues. As the evidence begins to lead closer to home and the scale of the perpetrator\'s plans becomes clear, he must forge new relationships.',
 'Action / Crime / Drama', 'English', 175, '2026-12-05', 'PG-13',
 'Robert Pattinson, Colin Farrell, Andy Serkis, Jeffrey Wright',
 'Matt Reeves',
 'https://www.youtube.com/watch?v=mqqft2x_Aa4',
 'https://image.tmdb.org/t/p/w500/74xTEgt7R36Fpooo50r9T25onhq.jpg',
 'COMING_SOON', NOW(), NOW()),

(10, 'Inception',
 'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project and his team to disaster.',
 'Sci-Fi / Action / Mystery', 'English', 148, '2026-12-25', 'PG-13',
 'Leonardo DiCaprio, Joseph Gordon-Levitt, Elliot Page, Tom Hardy, Ken Watanabe',
 'Christopher Nolan',
 'https://www.youtube.com/watch?v=YoHD9XEInc0',
 'https://image.tmdb.org/t/p/w500/edv5CZvWj09upOsy2Y6IwDhK8bt.jpg',
 'COMING_SOON', NOW(), NOW())
ON DUPLICATE KEY UPDATE 
 title=VALUES(title), description=VALUES(description), genre=VALUES(genre), 
 duration_minutes=VALUES(duration_minutes), poster_url=VALUES(poster_url), 
 status=VALUES(status), trailer_url=VALUES(trailer_url), updated_at=NOW();

-- 5. Insert Showtimes for Today and Next 3 Days
INSERT IGNORE INTO showtimes (movie_id, screen_id, show_date, start_time, ticket_price, status, created_at, updated_at) VALUES
-- Today Dune 2
(1, 1, CURDATE(), '13:30:00', 2500.00, 'SCHEDULED', NOW(), NOW()),
(1, 1, CURDATE(), '17:15:00', 2800.00, 'SCHEDULED', NOW(), NOW()),
(1, 1, CURDATE(), '21:00:00', 2800.00, 'SCHEDULED', NOW(), NOW()),
(1, 2, CURDATE(), '14:45:00', 2200.00, 'SCHEDULED', NOW(), NOW()),

-- Today Deadpool & Wolverine
(2, 2, CURDATE(), '18:00:00', 2200.00, 'SCHEDULED', NOW(), NOW()),
(2, 3, CURDATE(), '14:00:00', 2400.00, 'SCHEDULED', NOW(), NOW()),
(2, 4, CURDATE(), '19:30:00', 3000.00, 'SCHEDULED', NOW(), NOW()),

-- Today Gladiator II
(3, 3, CURDATE(), '17:30:00', 2400.00, 'SCHEDULED', NOW(), NOW()),
(3, 5, CURDATE(), '20:15:00', 1800.00, 'SCHEDULED', NOW(), NOW()),

-- Today Oppenheimer
(4, 5, CURDATE(), '13:00:00', 1800.00, 'SCHEDULED', NOW(), NOW()),
(4, 5, CURDATE(), '16:45:00', 1800.00, 'SCHEDULED', NOW(), NOW()),

-- Today Inside Out 2
(5, 2, CURDATE(), '11:00:00', 1800.00, 'SCHEDULED', NOW(), NOW()),
(5, 3, CURDATE(), '11:30:00', 2000.00, 'SCHEDULED', NOW(), NOW()),

-- Today Spider-Man
(6, 4, CURDATE(), '15:30:00', 2500.00, 'SCHEDULED', NOW(), NOW()),

-- Tomorrow Showtimes
(1, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00:00', 2800.00, 'SCHEDULED', NOW(), NOW()),
(1, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '18:30:00', 2800.00, 'SCHEDULED', NOW(), NOW()),
(2, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '16:00:00', 2400.00, 'SCHEDULED', NOW(), NOW()),
(2, 4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '20:00:00', 3000.00, 'SCHEDULED', NOW(), NOW()),
(3, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '19:00:00', 2200.00, 'SCHEDULED', NOW(), NOW()),
(4, 5, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '17:00:00', 1800.00, 'SCHEDULED', NOW(), NOW()),
(5, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:45:00', 2000.00, 'SCHEDULED', NOW(), NOW()),

-- Day After Tomorrow Showtimes
(1, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '15:00:00', 2800.00, 'SCHEDULED', NOW(), NOW()),
(2, 3, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '18:15:00', 2400.00, 'SCHEDULED', NOW(), NOW()),
(3, 4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '20:30:00', 3000.00, 'SCHEDULED', NOW(), NOW());
