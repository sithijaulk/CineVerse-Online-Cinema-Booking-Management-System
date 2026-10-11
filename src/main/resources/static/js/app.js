const API = '';

/* ===== THEME MANAGEMENT ===== */
(function applyThemeEarly() {
  const saved = localStorage.getItem('cineverse-theme');
  if (saved === 'light') {
    document.body.classList.add('light-mode');
  }
})();

function initThemeToggle() {
  let btn = document.getElementById('themeToggleBtn');
  if (!btn) {
    const authArea = document.getElementById('navAuthArea');
    if (authArea) {
      authArea.insertAdjacentHTML('afterbegin', '<button id="themeToggleBtn" class="theme-toggle-btn" type="button" aria-label="Toggle theme"><i class="bi bi-moon-stars-fill" id="themeIcon"></i></button>');
      btn = document.getElementById('themeToggleBtn');
    }
  }
  const icon = document.getElementById('themeIcon');
  if (!btn) return;
  if (btn.dataset.themeReady === 'true') return;
  btn.dataset.themeReady = 'true';

  function updateIcon() {
    const isLight = document.body.classList.contains('light-mode');
    if (icon) {
      icon.className = isLight ? 'bi bi-sun-fill' : 'bi bi-moon-stars-fill';
    }
    btn.title = isLight ? 'Switch to Dark Mode' : 'Switch to Light Mode';
  }

  updateIcon();

  btn.addEventListener('click', () => {
    document.body.classList.toggle('light-mode');
    const isLight = document.body.classList.contains('light-mode');
    localStorage.setItem('cineverse-theme', isLight ? 'light' : 'dark');
    updateIcon();
  });
}

async function apiFetch(path, options = {}) {
  const response = await fetch(`${API}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    },
    credentials: 'include',
    ...options
  });
  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try {
      const payload = await response.json();
      message = payload.message || payload.error || JSON.stringify(payload);
      if (payload.fieldErrors) {
        message = Object.entries(payload.fieldErrors).map(([key, value]) => `${key}: ${value}`).join('\n');
      }
    } catch (error) {
      const text = await response.text();
      if (text) message = text;
    }
    throw new Error(message);
  }
  if (response.status === 204) return null;
  return response.json();
}

function query(name) {
  return new URLSearchParams(window.location.search).get(name);
}

function money(value) {
  return new Intl.NumberFormat('en-LK', { style: 'currency', currency: 'LKR', maximumFractionDigits: 2 }).format(value || 0);
}

function setMessage(target, message, type = 'info') {
  const el = document.querySelector(target);
  if (!el) return;
  el.innerHTML = `<div class="alert alert-${type} mb-0">${message}</div>`;
}

function showToast(message, type = 'success') {
  let container = document.querySelector('#toastContainer');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toast-container position-fixed top-0 end-0 p-3';
    container.style.zIndex = '1080';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast align-items-center text-bg-${type} border-0`;
  toast.setAttribute('role', 'status');
  toast.setAttribute('aria-live', 'polite');
  toast.innerHTML = `<div class="d-flex"><div class="toast-body"></div><button type="button" class="btn-close btn-close-white me-2 m-auto" aria-label="Close"></button></div>`;
  toast.querySelector('.toast-body').textContent = message;
  toast.querySelector('button').addEventListener('click', () => toast.remove());
  container.appendChild(toast);
  requestAnimationFrame(() => toast.classList.add('show'));
  setTimeout(() => toast.remove(), 4000);
}

function confirmDelete(message) {
  return window.confirm(message || 'Are you sure you want to delete this record?');
}

window.addEventListener('unhandledrejection', (event) => {
  event.preventDefault();
  alert(event.reason?.message || 'The request could not be completed.');
});

function getTrailerEmbedUrl(url) {
  if (!url) return '';
  const match = url.match(/(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))([\w-]{11})/);
  return match ? `https://www.youtube-nocookie.com/embed/${match[1]}?autoplay=1` : url;
}

window.openTrailer = function(url, title) {
  const modalEl = document.getElementById('trailerModal');
  if (!modalEl) return;
  const iframe = document.getElementById('trailerIframe');
  const titleEl = document.getElementById('trailerModalTitle');
  if (titleEl) titleEl.textContent = `${title} - Official Trailer`;
  if (iframe) iframe.src = getTrailerEmbedUrl(url);
  const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
  modal.show();
};

document.addEventListener('DOMContentLoaded', () => {
  const modalEl = document.getElementById('trailerModal');
  if (modalEl) {
    modalEl.addEventListener('hidden.bs.modal', () => {
      const iframe = document.getElementById('trailerIframe');
      if (iframe) iframe.src = '';
    });
  }
});

function movieCard(movie, index = 0) {
  const isNowShowing = movie.status === 'NOW_SHOWING';
  const delayClass = `delay-${(index % 4) + 1}`;
  const formats = ['IMAX 3D', 'DOLBY ATMOS', '4DX MOTION', 'VIP LUXE'];
  const formatBadge = formats[index % formats.length];

  return `
    <div class="col-sm-6 col-lg-4 col-xl-3 reveal ${delayClass}">
      <div class="movie-card">
        <div class="poster-wrap">
          <img class="movie-poster" src="${movie.posterUrl || 'https://via.placeholder.com/600x900?text=Movie'}" alt="${movie.title}" loading="lazy">
          <div class="poster-gradient"></div>
          <div class="poster-badge-top-left">
            <span class="rating-badge">${movie.ageRating || 'PG-13'}</span>
          </div>
          <div class="poster-badge-top-right">
            <span class="format-badge">${formatBadge}</span>
          </div>
          <div class="poster-overlay-actions">
            ${movie.trailerUrl ? `
              <button type="button" class="play-trailer-btn" title="Watch Trailer" onclick="openTrailer('${movie.trailerUrl}', '${movie.title.replace(/'/g, "\\'")}')">
                <i class="bi bi-play-fill"></i>
              </button>
              <span class="small text-white fw-bold">WATCH TRAILER</span>
            ` : ''}
          </div>
        </div>
        <div class="movie-card-body">
          <div class="movie-genre-text">${movie.genre || 'Action / Adventure'}</div>
          <h5 class="movie-card-title">${movie.title}</h5>
          <div class="movie-meta-details">
            <span><i class="bi bi-clock me-1 text-warning"></i>${movie.durationMinutes || 120} min</span>
            <span>&bull;</span>
            <span>${movie.language || 'English'}</span>
          </div>
          <div class="movie-card-footer">
            ${isNowShowing ? `
              <a class="btn btn-accent w-100 btn-sm" href="/movie-details.html?id=${movie.id}">
                <i class="bi bi-ticket-perforated"></i> Book Tickets
              </a>
            ` : `
              <a class="btn btn-outline-gold w-100 btn-sm" href="/movie-details.html?id=${movie.id}">
                <i class="bi bi-info-circle"></i> View Details
              </a>
            `}
          </div>
        </div>
      </div>
    </div>`;
}

let allNowShowingMovies = [];

function initScrollAnimations() {
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('revealed');
      }
    });
  }, {
    threshold: 0.08,
    rootMargin: '0px 0px -40px 0px'
  });

  document.querySelectorAll('.reveal').forEach(el => observer.observe(el));
}

function initNavbarScroll() {
  const navbar = document.querySelector('#mainNavbar');
  if (!navbar) return;
  window.addEventListener('scroll', () => {
    if (window.scrollY > 40) {
      navbar.classList.add('scrolled');
    } else {
      navbar.classList.remove('scrolled');
    }
  });
}

async function initAuthNav() {
  const authArea = document.querySelector('#navAuthArea');
  if (!authArea) return;
  try {
    const user = await apiFetch('/api/auth/me');
    if (user && user.fullName) {
      const isStaffOrAdmin = user.role === 'ADMIN' || user.role === 'STAFF';
      authArea.innerHTML = `
        <div class="dropdown">
          <button class="btn btn-glass btn-sm dropdown-toggle d-flex align-items-center gap-2" type="button" data-bs-toggle="dropdown">
            <i class="bi bi-person-circle text-warning"></i>
            <span>${user.fullName.split(' ')[0]}</span>
            ${isStaffOrAdmin ? `<span class="badge bg-warning text-dark small">${user.role}</span>` : ''}
          </button>
          <ul class="dropdown-menu dropdown-menu-dark dropdown-menu-end shadow">
            <li><h6 class="dropdown-header">${user.fullName} (${user.role})</h6></li>
            <li><a class="dropdown-item" href="/profile.html"><i class="bi bi-person-vcard me-2"></i>Profile</a></li>
            <li><a class="dropdown-item" href="/my-bookings.html"><i class="bi bi-ticket-perforated me-2"></i>My Bookings</a></li>
            ${user.role === 'ADMIN' ? '<li><a class="dropdown-item text-warning" href="/admin/dashboard.html"><i class="bi bi-speedometer2 me-2"></i>Admin Dashboard</a></li>' : ''}
            ${user.role === 'STAFF' ? '<li><a class="dropdown-item text-warning" href="/staff/dashboard.html"><i class="bi bi-upc-scan me-2"></i>Staff Portal</a></li>' : ''}
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item text-danger" href="/logout"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
          </ul>
        </div>
      `;
      initThemeToggle();
    }
  } catch (e) {
    // Guest
  }
}

function validateMovieReleaseDate(payload) {
  if (!payload.releaseDate) {
    throw new Error('Release date is required.');
  }
  const releaseDate = new Date(`${payload.releaseDate}T00:00:00`);
  const earliestDate = new Date('1900-01-01T00:00:00');
  if (Number.isNaN(releaseDate.getTime()) || releaseDate < earliestDate) {
    throw new Error('Enter a valid release date after 1900.');
  }
}

function setupHeroSpotlight(movies) {
  if (!movies || movies.length === 0) return;
  const heroList = movies.slice(0, 5);
  const thumbList = document.querySelector('#heroThumbList');
  
  if (thumbList) {
    thumbList.innerHTML = heroList.map((m, idx) => `
      <div class="hero-thumb-item ${idx === 0 ? 'active' : ''}" data-movie-id="${m.id}" title="${m.title}">
        <img src="${m.posterUrl}" alt="${m.title}">
      </div>
    `).join('');

    thumbList.querySelectorAll('.hero-thumb-item').forEach(item => {
      item.addEventListener('click', () => {
        thumbList.querySelectorAll('.hero-thumb-item').forEach(t => t.classList.remove('active'));
        item.classList.add('active');
        const m = movies.find(x => x.id === Number(item.dataset.movieId));
        if (m) updateHeroDisplay(m);
      });
    });
  }

  updateHeroDisplay(heroList[0]);
}

function updateHeroDisplay(m) {
  const titleEl = document.querySelector('#heroTitle');
  const ratingEl = document.querySelector('#heroRating');
  const genreEl = document.querySelector('#heroGenre');
  const durEl = document.querySelector('#heroDuration');
  const synEl = document.querySelector('#heroSynopsis');
  const bookBtn = document.querySelector('#heroBookBtn');
  const trailerBtn = document.querySelector('#heroTrailerBtn');
  const backdrop = document.querySelector('#heroBackdrop');

  if (titleEl) titleEl.textContent = m.title;
  if (ratingEl) ratingEl.textContent = m.ageRating || 'PG-13';
  if (genreEl) genreEl.textContent = m.genre || 'Action / Adventure';
  if (durEl) durEl.innerHTML = `<i class="bi bi-clock me-1"></i> ${m.durationMinutes} MIN`;
  if (synEl) synEl.textContent = m.description || '';
  if (bookBtn) bookBtn.href = `/movie-details.html?id=${m.id}`;
  if (trailerBtn) {
    trailerBtn.onclick = () => openTrailer(m.trailerUrl, m.title);
  }
  if (backdrop && m.posterUrl) {
    backdrop.style.backgroundImage = `url('${m.posterUrl}')`;
  }
}

function setupQuickBookingBar(movies, branches) {
  const movieSelect = document.querySelector('#quickMovieSelect');
  const branchSelect = document.querySelector('#quickBranchSelect');
  const dateSelect = document.querySelector('#quickDateSelect');
  const form = document.querySelector('#quickBookForm');

  if (movieSelect) {
    movieSelect.innerHTML = '<option value="" disabled selected>Choose a movie...</option>' + 
      movies.map(m => `<option value="${m.id}">${m.title} (${m.ageRating || 'PG-13'})</option>`).join('');
  }

  if (branchSelect && branches) {
    branchSelect.innerHTML = '<option value="">All Cinema Branches</option>' + 
      branches.map(b => `<option value="${b.id}">${b.branchName}</option>`).join('');
  }

  if (dateSelect) {
    const dates = [];
    const today = new Date();
    for (let i = 0; i < 4; i++) {
      const d = new Date(today);
      d.setDate(today.getDate() + i);
      const str = d.toISOString().split('T')[0];
      const label = i === 0 ? `Today (${d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })})` 
                  : i === 1 ? `Tomorrow (${d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })})`
                  : d.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' });
      dates.push(`<option value="${str}">${label}</option>`);
    }
    dateSelect.innerHTML = dates.join('');
  }

  if (form) {
    form.addEventListener('submit', (e) => {
      e.preventDefault();
      const movieId = movieSelect.value;
      if (!movieId) return;
      window.location.href = `/movie-details.html?id=${movieId}`;
    });
  }
}

function setupGenreFilters() {
  const pills = document.querySelectorAll('.filter-pill');
  pills.forEach(pill => {
    pill.addEventListener('click', () => {
      pills.forEach(p => p.classList.remove('active'));
      pill.classList.add('active');
      const genre = pill.dataset.genre;
      if (genre === 'all') {
        renderNowShowing(allNowShowingMovies);
      } else {
        const filtered = allNowShowingMovies.filter(m => (m.genre || '').toLowerCase().includes(genre.toLowerCase()));
        renderNowShowing(filtered);
      }
    });
  });
}

function renderNowShowing(movies) {
  const container = document.querySelector('#nowShowing');
  if (!container) return;
  if (!movies || movies.length === 0) {
    container.innerHTML = '<div class="col-12 text-center py-5 text-muted"><i class="bi bi-film fs-1 d-block mb-2"></i>No movies currently showing in this category.</div>';
    return;
  }
  container.innerHTML = movies.map((m, i) => movieCard(m, i)).join('');
  initScrollAnimations();
}

async function loadMovies(target, params = {}) {
  const search = new URLSearchParams(params);
  const data = await apiFetch(`/api/public/movies${search.toString() ? `?${search}` : ''}`);
  document.querySelector(target).innerHTML = data.map((m, i) => movieCard(m, i)).join('');
  initScrollAnimations();
}

async function initHomePage() {
  initNavbarScroll();
  initAuthNav();
  initThemeToggle();

  const [nowShowing, upcoming, branches] = await Promise.all([
    apiFetch('/api/public/movies?status=NOW_SHOWING').catch(() => []),
    apiFetch('/api/public/movies?status=COMING_SOON').catch(() => []),
    apiFetch('/api/public/branches').catch(() => [])
  ]);

  allNowShowingMovies = nowShowing || [];

  setupHeroSpotlight(allNowShowingMovies);
  setupQuickBookingBar(allNowShowingMovies, branches);
  renderNowShowing(allNowShowingMovies);
  setupGenreFilters();

  const upcomingContainer = document.querySelector('#upcoming');
  if (upcomingContainer) {
    upcomingContainer.innerHTML = (upcoming || []).map((m, i) => movieCard(m, i)).join('') || '<p class="muted">No upcoming movies announced.</p>';
  }

  initScrollAnimations();
}

async function initMoviesPage() {
  const form = document.querySelector('#movieFilterForm');
  let requestNumber = 0;
  let searchTimer;
  const render = async () => {
    const currentRequest = ++requestNumber;
    const params = Object.fromEntries(new FormData(form).entries());
    const filtered = Object.fromEntries(Object.entries(params).filter(([, value]) => value));
    const data = await apiFetch(`/api/public/movies${new URLSearchParams(filtered).toString() ? `?${new URLSearchParams(filtered)}` : ''}`);
    if (currentRequest !== requestNumber) return;
    document.querySelector('#movieList').innerHTML = data.map(movieCard).join('') || '<p class="muted">No matches found.</p>';
  };
  form.addEventListener('submit', (event) => { event.preventDefault(); render(); });
  const nameInput = document.querySelector('#movieNameSearch');
  nameInput?.addEventListener('input', () => {
    clearTimeout(searchTimer);
    searchTimer = setTimeout(render, 250);
  });
  document.querySelector('#clearFilters').addEventListener('click', () => { form.reset(); render(); });
  await render();
}

async function initMovieDetailsPage() {
  initNavbarScroll();
  initAuthNav();
  initThemeToggle();

  const id = query('id');
  const movie = await apiFetch(`/api/public/movies/${id}`);
  document.querySelector('#movieTitle').textContent = movie.title;

  const metaContainer = document.querySelector('#movieMeta');
  if (metaContainer) {
    metaContainer.innerHTML = `
      <span class="rating-badge">${movie.ageRating || 'PG-13'}</span>
      <span class="tag-badge tag-gold">${movie.genre || 'Action'}</span>
      <span class="tag-badge"><i class="bi bi-clock me-1 text-warning"></i>${movie.durationMinutes || 120} Mins</span>
      <span class="tag-badge"><i class="bi bi-translate me-1"></i>${movie.language || 'English'}</span>
      <span class="format-badge">IMAX 3D</span>
      <span class="format-badge">DOLBY ATMOS</span>
    `;
  }

  document.querySelector('#movieDescription').textContent = movie.description;
  const poster = document.querySelector('#moviePoster');
  if (poster) poster.src = movie.posterUrl || 'https://via.placeholder.com/600x900?text=Movie';

  const showtimes = await apiFetch(`/api/public/showtimes/movie/${id}`);
  const showtimeListEl = document.querySelector('#showtimeList');
  if (showtimeListEl) {
    showtimeListEl.innerHTML = showtimes.map((showtime) => `
      <div class="col-md-6 col-xl-4">
        <div class="glass-panel p-3 rounded-4 h-100 d-flex flex-column justify-content-between">
          <div>
            <div class="d-flex justify-content-between align-items-center mb-2">
              <span class="format-badge">${showtime.screenName.includes('IMAX') ? 'IMAX LASER' : showtime.screenName.includes('VIP') ? 'VIP LUXE' : 'DIGITAL 4K'}</span>
              <strong class="text-warning">${money(showtime.ticketPrice)}</strong>
            </div>
            <h6 class="text-white mb-1 fw-bold">${showtime.branchName}</h6>
            <div class="small text-muted mb-3"><i class="bi bi-display me-1"></i>${showtime.screenName}</div>
            <div class="d-flex align-items-center gap-2 mb-3">
              <span class="tag-badge"><i class="bi bi-calendar3 me-1"></i>${showtime.showDate}</span>
              <span class="tag-badge tag-gold"><i class="bi bi-clock me-1"></i>${showtime.startTime}</span>
            </div>
          </div>
          <a class="btn btn-accent w-100" href="/booking.html?showtimeId=${showtime.id}&movieTitle=${encodeURIComponent(showtime.movieTitle)}&branchName=${encodeURIComponent(showtime.branchName)}&screenName=${encodeURIComponent(showtime.screenName)}&showDate=${showtime.showDate}&startTime=${showtime.startTime}&ticketPrice=${showtime.ticketPrice}">
            <i class="bi bi-ticket-perforated"></i> Select Seats
          </a>
        </div>
      </div>`).join('') || '<div class="col-12 text-center py-4 text-muted"><i class="bi bi-calendar-x fs-2 d-block mb-2"></i>No showtimes currently scheduled for this title.</div>';
  }
}

async function initBookingPage() {
  const showtimeId = query('showtimeId');
  const movieTitle = query('movieTitle') || '';
  const branchName = query('branchName') || '';
  const screenName = query('screenName') || '';
  const showDate = query('showDate') || '';
  const startTime = query('startTime') || '';
  const ticketPrice = Number(query('ticketPrice') || 0);
  const seats = await apiFetch(`/api/public/showtimes/${showtimeId}/seats`);
  const selected = new Set();

  document.querySelector('#bookingSummary').innerHTML = `
    <div class="glass-panel p-4 rounded-4 mb-3">
      <div class="section-tag mb-1"><i class="bi bi-film"></i> SHOW SELECTION</div>
      <h4 class="text-white fw-bold mb-3">${movieTitle}</h4>
      <div class="d-flex justify-content-between mb-2 small"><span class="text-muted"><i class="bi bi-geo-alt me-1"></i>Cinema:</span><strong class="text-white">${branchName}</strong></div>
      <div class="d-flex justify-content-between mb-2 small"><span class="text-muted"><i class="bi bi-display me-1"></i>Auditorium:</span><strong class="text-warning">${screenName}</strong></div>
      <div class="d-flex justify-content-between mb-2 small"><span class="text-muted"><i class="bi bi-calendar3 me-1"></i>Date:</span><span class="text-white">${showDate}</span></div>
      <div class="d-flex justify-content-between mb-2 small"><span class="text-muted"><i class="bi bi-clock me-1"></i>Time:</span><span class="text-white">${startTime}</span></div>
      <div class="d-flex justify-content-between pt-2 border-top border-secondary small"><span class="text-muted">Ticket Price:</span><strong class="text-warning">${money(ticketPrice)}</strong></div>
    </div>`;

  const seatGrid = document.querySelector('#seatGrid');
  const seatLabels = new Map(seats.map((seat) => [seat.seatId, `${seat.rowName}${seat.seatNumber}`]));
  function redraw() {
    seatGrid.innerHTML = seats.map((seat) => {
      const status = seat.status === 'BOOKED' ? 'booked' : seat.status === 'TEMPORARILY_HELD' ? 'held' : selected.has(seat.seatId) ? 'selected' : 'available';
      const disabled = status === 'booked' || status === 'held';
      return `<button type="button" class="seat ${status}" ${disabled ? 'disabled' : ''} data-seat-id="${seat.seatId}">${seat.rowName}${seat.seatNumber}</button>`;
    }).join('');

    seatGrid.querySelectorAll('[data-seat-id]').forEach((button) => {
      button.addEventListener('click', () => {
        const seatId = Number(button.dataset.seatId);
        if (selected.has(seatId)) selected.delete(seatId); else selected.add(seatId);
        redraw();
      });
    });

    const count = selected.size;
    document.querySelector('#ticketCount').textContent = String(count);
    document.querySelector('#seatSummary').textContent = count
      ? [...selected].map((seatId) => seatLabels.get(seatId)).join(', ')
      : 'No seats selected';
    document.querySelector('#totalAmount').textContent = money(count * ticketPrice);
  }

  redraw();

  document.querySelector('#bookingForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    if (selected.size === 0) {
      setMessage('#bookingMessage', 'Please select at least one available seat before booking.', 'warning');
      return;
    }
    try {
      const simulateSuccess = document.querySelector('#simulateSuccess').checked;
      await apiFetch('/api/customer/reservations/holds', {
        method: 'POST',
        body: JSON.stringify({ showtimeId: Number(showtimeId), seatIds: [...selected] })
      });
      const result = await apiFetch('/api/customer/reservations', {
        method: 'POST',
        body: JSON.stringify({
          showtimeId: Number(showtimeId),
          seatIds: [...selected],
          paymentMethod: document.querySelector('#paymentMethod').value,
          simulateSuccess
        })
      });
      document.querySelector('#ticketPanel').classList.remove('d-none');
      document.querySelector('#ticketPanel').innerHTML = `
        <div class="card border-success">
          <div class="card-body">
            <span class="badge bg-success mb-3">ETicket</span>
            <h4>${result.bookingReference}</h4>
            <div class="muted">${result.customerName} | ${result.movieTitle}</div>
            <hr>
            <div class="row g-2">
              <div class="col-md-6">Branch: ${result.branchName}</div>
              <div class="col-md-6">Screen: ${result.screenName}</div>
              <div class="col-md-6">Show Date: ${result.showDate}</div>
              <div class="col-md-6">Start Time: ${result.startTime}</div>
              <div class="col-md-6">Seats: ${result.seatSummary}</div>
              <div class="col-md-6">Amount: ${money(result.totalAmount)}</div>
              <div class="col-md-6">Payment: ${result.paymentStatus}</div>
              <div class="col-md-6">Reservation: ${result.reservationStatus}</div>
            </div>
          </div>
        </div>`;
    } catch (error) {
      setMessage('#bookingMessage', error.message, 'danger');
    }
  });
}

async function initMyBookingsPage() {
  initNavbarScroll();
  initThemeToggle();
  initAuthNav();
  try {
    const bookings = await apiFetch('/api/customer/reservations/me');
    document.querySelector('#bookingList').innerHTML = bookings.map((booking) => `
      <div class="card mb-3">
        <div class="card-body d-flex justify-content-between align-items-center flex-wrap gap-3">
          <div>
            <h5 class="mb-1">${booking.bookingReference}</h5>
            <div class="muted">${booking.movieTitle} | ${booking.showDate} ${booking.startTime}</div>
            <div class="muted">Seats: ${booking.seatSummary}</div>
          </div>
          <div class="text-end">
            <div class="fw-bold">${money(booking.totalAmount)}</div>
            <div class="badge bg-${booking.reservationStatus === 'CONFIRMED' ? 'success' : booking.reservationStatus === 'PENDING' ? 'warning text-dark' : 'secondary'}">${booking.reservationStatus}</div>
          </div>
        </div>
      </div>`).join('') || '<p class="muted">No bookings found.</p>';
  } catch (error) {
    if (error.message.includes('(401)')) {
      window.location.href = '/login.html?returnUrl=/my-bookings.html';
      return;
    }
    setMessage('#bookingMessage', error.message, 'danger');
  }
}

async function initProfilePage() {
  initNavbarScroll();
  initAuthNav();
  const form = document.querySelector('#profileForm');
  const message = document.querySelector('#profileMessage');
  try {
    const user = await apiFetch('/api/customer/profile');
    form.fullName.value = user.fullName || '';
    form.email.value = user.email || '';
    form.phone.value = user.phone || '';
  } catch (error) {
    window.location.href = '/login.html?returnUrl=/profile.html';
    return;
  }

  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    if (!form.reportValidity()) return;
    try {
      const updated = await apiFetch('/api/customer/profile', {
        method: 'PUT',
        body: JSON.stringify({ fullName: form.fullName.value, phone: form.phone.value })
      });
      form.fullName.value = updated.fullName || '';
      form.phone.value = updated.phone || '';
      message.innerHTML = '<div class="alert alert-success">Profile updated successfully.</div>';
      await initAuthNav();
    } catch (error) {
      message.innerHTML = `<div class="alert alert-danger">${error.message}</div>`;
    }
  });

  document.querySelector('#deactivateProfileBtn')?.addEventListener('click', async () => {
    if (!confirm('Deactivate your account? You will not be able to sign in again until an administrator reactivates it.')) return;
    try {
      await apiFetch('/api/customer/profile', { method: 'DELETE' });
      window.location.href = '/logout';
    } catch (error) {
      message.innerHTML = `<div class="alert alert-danger">${error.message}</div>`;
    }
  });
}

async function initAdminUsersPage() {
  const body = document.querySelector('#userTableBody');
  document.querySelector('#createUserForm')?.addEventListener('submit', async (event) => {
    event.preventDefault();
    if (!event.target.reportValidity()) return;
    try {
      await apiFetch('/api/admin/users', {
        method: 'POST',
        body: JSON.stringify(Object.fromEntries(new FormData(event.target).entries()))
      });
      event.target.reset();
      showToast('Managed account created successfully.');
      await loadUsers();
    } catch (error) {
      setMessage('#userMessage', error.message, 'danger');
    }
  });
  const loadUsers = async () => {
    const users = await apiFetch('/api/admin/users');
    body.innerHTML = users.map(user => `
      <tr>
        <td><strong class="text-white">${user.fullName}</strong><div class="small text-muted">#${user.id}</div></td>
        <td><span class="text-muted">${user.email}</span><div class="small text-muted">${user.phone || 'No phone number'}</div></td>
        <td><select class="form-select form-select-sm" data-role="${user.id}" ${user.email === 'admin@cinema.com' ? 'disabled' : ''}>
          ${['CUSTOMER', 'STAFF', 'ADMIN'].map(role => `<option value="${role}" ${role === user.role ? 'selected' : ''}>${role}</option>`).join('')}
        </select></td>
        <td><select class="form-select form-select-sm" data-status="${user.id}">
          ${['ACTIVE', 'INACTIVE', 'LOCKED'].map(status => `<option value="${status}" ${status === user.status ? 'selected' : ''}>${status}</option>`).join('')}
        </select></td>
        <td class="text-end text-nowrap"><button class="btn btn-sm btn-accent" data-save-user="${user.id}"><i class="bi bi-check2 me-1"></i>Save</button> <button class="btn btn-sm btn-outline-danger" data-delete-user="${user.id}" ${user.email === 'admin@cinema.com' ? 'disabled' : ''} title="Delete user"><i class="bi bi-trash"></i></button></td>
      </tr>`).join('') || '<tr><td colspan="5" class="text-center py-4 text-muted">No users found.</td></tr>';

    body.querySelectorAll('[data-save-user]').forEach(button => {
      button.addEventListener('click', async () => {
        const id = button.dataset.saveUser;
        button.disabled = true;
        try {
          await apiFetch(`/api/admin/users/${id}`, {
            method: 'PUT',
            body: JSON.stringify({
              role: body.querySelector(`[data-role="${id}"]`).value,
              status: body.querySelector(`[data-status="${id}"]`).value
            })
          });
          showToast('User access updated successfully.');
        } catch (error) {
          setMessage('#userMessage', error.message, 'danger');
        } finally {
          button.disabled = false;
        }
      });
    });

    body.querySelectorAll('[data-delete-user]').forEach(button => {
      button.addEventListener('click', async () => {
        if (!confirm('Delete this user? Users with booking history can only be deactivated.')) return;
        button.disabled = true;
        try {
          await apiFetch(`/api/admin/users/${button.dataset.deleteUser}`, { method: 'DELETE' });
          showToast('User deleted successfully.');
          await loadUsers();
        } catch (error) {
          setMessage('#userMessage', error.message, 'danger');
          button.disabled = false;
        }
      });
    });
  };
  await loadUsers();
}

async function initDashboardPage() {
  const stats = await apiFetch('/api/admin/dashboard/stats');
  renderDashboardStats(stats);

  // Load all movies into dashboard table
  const reloadMovies = async () => {
    const list = await apiFetch('/api/admin/movies').catch(() => []);
    const tbody = document.querySelector('#dashboardMovieTableBody');
    if (!tbody) return;
    if (!list || list.length === 0) {
      tbody.innerHTML = '<tr><td colspan="5" class="text-center py-4 text-muted"><i class="bi bi-film me-2"></i>No movies in the system yet. <a href="/admin/movies.html" class="text-warning">Add one now</a></td></tr>';
      return;
    }
    tbody.innerHTML = list.map(movie => {
      const statusBadge = movie.status === 'NOW_SHOWING' ? 'badge-now-showing'
                        : movie.status === 'COMING_SOON' ? 'badge-coming-soon' : 'badge-archived';
      const poster = movie.posterUrl || 'https://via.placeholder.com/100x150?text=No+Poster';
      return `<tr>
        <td style="width: 70px;">
          <img src="${poster}" alt="${movie.title}" style="width: 50px; height: 75px; object-fit: cover; border-radius: 6px;">
        </td>
        <td>
          <div class="fw-bold text-white fs-6">${movie.title}</div>
          <div class="small text-warning">${movie.genre || 'N/A'} &bull; ${movie.language || 'English'}</div>
          <div class="small text-muted">Rated: ${movie.ageRating || 'N/A'}</div>
        </td>
        <td><span class="text-white">${movie.durationMinutes || 0}</span> <small class="text-muted">mins</small></td>
        <td><span class="badge ${statusBadge}">${movie.status}</span></td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-warning me-2" onclick='editMovieDash(${JSON.stringify(movie).replace(/'/g, "&#39;")})'>
            <i class="bi bi-pencil-square me-1"></i>Edit
          </button>
          <button class="btn btn-sm btn-outline-danger" onclick="deleteMovieDash(${movie.id})">
            <i class="bi bi-slash-circle me-1"></i>Deactivate
          </button>
        </td>
      </tr>`;
    }).join('');
  };

  await reloadMovies();

  // Wire up edit for dashboard movie modal (reuse same modal structure)
  window.editMovieDash = (movie) => {
    document.getElementById('editMovieId').value = movie.id;
    document.getElementById('editMovieTitle').value = movie.title || '';
    document.getElementById('editMovieDescription').value = movie.description || '';
    document.getElementById('editMovieGenre').value = movie.genre || '';
    document.getElementById('editMovieLanguage').value = movie.language || '';
    document.getElementById('editMovieDuration').value = movie.durationMinutes || 0;
    document.getElementById('editMovieReleaseDate').value = movie.releaseDate || '';
    document.getElementById('editMovieAgeRating').value = movie.ageRating || '';
    document.getElementById('editMovieStatus').value = movie.status || 'NOW_SHOWING';
    document.getElementById('editMoviePosterUrl').value = movie.posterUrl || '';
    document.getElementById('editMovieTrailerUrl').value = movie.trailerUrl || '';
    const preview = document.getElementById('editPosterPreviewImg');
    if (preview) preview.src = movie.posterUrl || 'https://via.placeholder.com/100x150?text=No+Poster';
    const modal = new bootstrap.Modal(document.getElementById('editMovieModal'));
    modal.show();
  };

  window.deleteMovieDash = async (id) => {
    if (!confirmDelete('Delete this movie? This cannot be undone.')) return;
    await runCrudAction(async () => {
      await apiFetch(`/api/admin/movies/${id}`, { method: 'DELETE' });
      await reloadMovies();
      showToast('Movie deactivated successfully.');
    });
  };

  // Handle upload button in dashboard edit modal
  const editUploadBtn = document.getElementById('editUploadPosterBtn');
  if (editUploadBtn) {
    editUploadBtn.addEventListener('click', () => {
      uploadMovieFile('editMovieFileInput', 'editMoviePosterUrl', 'editPosterPreviewImg', 'editPosterPreviewWrap');
    });
  }

  // Handle edit form submit in dashboard
  const editForm = document.querySelector('#editMovieForm');
  if (editForm) {
    editForm.addEventListener('submit', async (event) => {
      event.preventDefault();
      const payload = Object.fromEntries(new FormData(event.target).entries());
      const id = payload.id;
      delete payload.id;
      payload.durationMinutes = Number(payload.durationMinutes);
      await runCrudAction(async () => {
        await apiFetch(`/api/admin/movies/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
        bootstrap.Modal.getInstance(document.getElementById('editMovieModal')).hide();
        await reloadMovies();
        showToast('Movie updated successfully.');
      });
    });
  }
}

async function initStaffDashboardPage() {
  const stats = await apiFetch('/api/staff/dashboard/stats');
  renderDashboardStats(stats);
}

function renderDashboardStats(stats) {
  const cards = [
    ['Total Movies', stats.totalMovies, 'bi-film'],
    ['Now Showing', stats.nowShowingMovies, 'bi-play-circle'],
    ['Upcoming', stats.upcomingMovies, 'bi-calendar-event'],
    ['Branches', stats.totalBranches, 'bi-buildings'],
    ['Screens', stats.totalScreens, 'bi-display'],
    ['Showtimes', stats.totalShowtimes, 'bi-clock-history'],
    ['Reservations', stats.totalReservations, 'bi-receipt'],
    ['Confirmed', stats.confirmedReservations, 'bi-check-circle'],
  ];
  document.querySelector('#statsGrid').innerHTML = cards.map(([label, value, icon]) => `
    <div class="col-md-6 col-xl-3"><div class="card stat-card h-100"><div class="card-body"><div class="stat-card-label"><i class="bi ${icon}"></i><span>${label}</span></div><h2 class="stat-card-value mb-0">${value}</h2></div></div></div>`).join('');
}

async function crudLoad(endpoint, target, mapper) {
  const data = await apiFetch(endpoint);
  document.querySelector(target).innerHTML = data.map(mapper).join('') || '<tr><td colspan="6" class="text-center muted">No data found</td></tr>';
}

async function runCrudAction(action) {
  try {
    await action();
  } catch (error) {
    alert(error.message);
  }
}

async function uploadMovieFile(fileInputId, targetUrlInputId, previewImgId, previewWrapId) {
  const fileInput = document.getElementById(fileInputId);
  if (!fileInput || !fileInput.files || fileInput.files.length === 0) {
    alert('Please select an image file first.');
    return;
  }
  const file = fileInput.files[0];
  const formData = new FormData();
  formData.append('file', file);

  try {
    const res = await fetch('/api/admin/uploads/movie-image', {
      method: 'POST',
      body: formData,
      credentials: 'include'
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Image upload failed');
    }
    const data = await res.json();
    const urlInput = document.getElementById(targetUrlInputId);
    if (urlInput) urlInput.value = data.url;
    const previewImg = document.getElementById(previewImgId);
    if (previewImg) previewImg.src = data.url;
    const previewWrap = document.getElementById(previewWrapId);
    if (previewWrap) previewWrap.classList.remove('d-none');
    showToast('Image uploaded successfully from your computer!');
  } catch (error) {
    alert('Upload error: ' + error.message);
  }
}

function movieRow(movie) {
  const statusBadge = movie.status === 'NOW_SHOWING' ? 'badge-now-showing' 
                    : movie.status === 'COMING_SOON' ? 'badge-coming-soon' : 'badge-archived';
  const poster = movie.posterUrl || 'https://via.placeholder.com/100x150?text=No+Poster';
  return `<tr>
    <td style="width: 70px;">
      <img src="${poster}" alt="${movie.title}" style="width: 50px; height: 75px; object-fit: cover; border-radius: 6px;">
    </td>
    <td>
      <div class="fw-bold text-white fs-6">${movie.title}</div>
      <div class="small text-warning">${movie.genre || 'N/A'} &bull; ${movie.language || 'English'}</div>
      <div class="small text-muted">Rated: ${movie.ageRating || 'N/A'}</div>
    </td>
    <td><span class="text-white">${movie.durationMinutes || 0}</span> <small class="text-muted">mins</small></td>
    <td><span class="badge ${statusBadge}">${movie.status}</span></td>
    <td class="text-end">
      <button class="btn btn-sm btn-outline-warning me-2" onclick='editMovie(${JSON.stringify(movie).replace(/'/g, "&#39;")})'><i class="bi bi-pencil-square me-1"></i>Edit</button>
      <button class="btn btn-sm btn-outline-danger" onclick="deleteMovie(${movie.id})"><i class="bi bi-trash me-1"></i>Delete</button>
    </td>
  </tr>`;
}

async function initAdminMoviesPage() {
  const reload = async () => {
    const list = await apiFetch('/api/admin/movies');
    document.querySelector('#movieTableBody').innerHTML = list.map(movieRow).join('') || '<tr><td colspan="5" class="text-center py-4 text-muted">No movies in database</td></tr>';
    const badge = document.querySelector('#movieCountBadge');
    if (badge) badge.textContent = `${list.length} movies`;
  };

  await reload();

  // Handle local PC file upload in create form
  const uploadBtn = document.getElementById('uploadPosterBtn');
  if (uploadBtn) {
    uploadBtn.addEventListener('click', () => {
      uploadMovieFile('movieFileInput', 'moviePosterUrl', 'posterPreviewImg', 'posterPreviewWrap');
    });
  }

  // Handle local PC file upload in edit modal
  const editUploadBtn = document.getElementById('editUploadPosterBtn');
  if (editUploadBtn) {
    editUploadBtn.addEventListener('click', () => {
      uploadMovieFile('editMovieFileInput', 'editMoviePosterUrl', 'editPosterPreviewImg', 'editPosterPreviewWrap');
    });
  }

  // Create Movie Form Submit
  document.querySelector('#movieForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(event.target).entries());
    payload.durationMinutes = Number(payload.durationMinutes);
    await runCrudAction(async () => {
      validateMovieReleaseDate(payload);
      await apiFetch('/api/admin/movies', { method: 'POST', body: JSON.stringify(payload) });
      event.target.reset();
      const preview = document.getElementById('posterPreviewWrap');
      if (preview) preview.classList.add('d-none');
      await reload();
      showToast('Movie created successfully.');
    });
  });

  // Edit Movie Button Trigger -> Opens Modal with Full Form Data
  window.editMovie = (movie) => {
    document.getElementById('editMovieId').value = movie.id;
    document.getElementById('editMovieTitle').value = movie.title || '';
    document.getElementById('editMovieDescription').value = movie.description || '';
    document.getElementById('editMovieGenre').value = movie.genre || '';
    document.getElementById('editMovieLanguage').value = movie.language || '';
    document.getElementById('editMovieDuration').value = movie.durationMinutes || 0;
    document.getElementById('editMovieReleaseDate').value = movie.releaseDate || '';
    document.getElementById('editMovieAgeRating').value = movie.ageRating || '';
    document.getElementById('editMovieStatus').value = movie.status || 'NOW_SHOWING';
    document.getElementById('editMoviePosterUrl').value = movie.posterUrl || '';
    document.getElementById('editMovieTrailerUrl').value = movie.trailerUrl || '';

    const preview = document.getElementById('editPosterPreviewImg');
    if (preview) preview.src = movie.posterUrl || 'https://via.placeholder.com/100x150?text=No+Poster';

    const modal = new bootstrap.Modal(document.getElementById('editMovieModal'));
    modal.show();
  };

  // Submit Edit Movie Form
  document.querySelector('#editMovieForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(event.target).entries());
    const id = payload.id;
    delete payload.id;
    payload.durationMinutes = Number(payload.durationMinutes);

    await runCrudAction(async () => {
      validateMovieReleaseDate(payload);
      await apiFetch(`/api/admin/movies/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
      bootstrap.Modal.getInstance(document.getElementById('editMovieModal')).hide();
      await reload();
      showToast('Movie updated successfully.');
    });
  });

  window.deleteMovie = async (id) => {
    if (!confirmDelete('Deactivate this movie? It will no longer show in ticket bookings.')) return;
    await runCrudAction(async () => {
      await apiFetch(`/api/admin/movies/${id}`, { method: 'DELETE' });
      await reload();
      showToast('Movie deleted successfully.');
    });
  };
}

function branchRow(branch) {
  return `<tr>
    <td><span class="badge bg-secondary">#${branch.id}</span></td>
    <td><strong class="text-white">${branch.branchName}</strong></td>
    <td class="text-muted">${branch.location}</td>
    <td><span class="badge ${branch.active ? 'bg-success' : 'bg-danger'}">${branch.active ? 'Active' : 'Inactive'}</span></td>
    <td class="text-end">
      <button class="btn btn-sm btn-outline-warning me-2" onclick='editBranch(${JSON.stringify(branch).replace(/'/g, "&#39;")})'><i class="bi bi-pencil-square me-1"></i>Edit</button>
      <button class="btn btn-sm btn-outline-danger" onclick="deleteBranch(${branch.id})"><i class="bi bi-trash me-1"></i>Delete</button>
    </td>
  </tr>`;
}

async function initAdminBranchesPage() {
  const reload = async () => {
    await crudLoad('/api/admin/branches', '#branchTableBody', branchRow);
  };
  await reload();

  document.querySelector('#branchForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(event.target).entries());
    await runCrudAction(async () => {
      await apiFetch('/api/admin/branches', { method: 'POST', body: JSON.stringify(payload) });
      event.target.reset();
      await reload();
      showToast('Branch created successfully.');
    });
  });

  window.editBranch = (branch) => {
    document.getElementById('editBranchId').value = branch.id;
    document.getElementById('editBranchName').value = branch.branchName || '';
    document.getElementById('editBranchLocation').value = branch.location || '';
    const modal = new bootstrap.Modal(document.getElementById('editBranchModal'));
    modal.show();
  };

  document.querySelector('#editBranchForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(event.target).entries());
    const id = payload.id;
    delete payload.id;

    await runCrudAction(async () => {
      await apiFetch(`/api/admin/branches/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
      bootstrap.Modal.getInstance(document.getElementById('editBranchModal')).hide();
      await reload();
      showToast('Branch updated successfully in database.');
    });
  });

  window.deleteBranch = async (id) => {
    if (!confirmDelete('Deactivate this cinema branch?')) return;
    await runCrudAction(async () => {
      await apiFetch(`/api/admin/branches/${id}`, { method: 'DELETE' });
      await reload();
      showToast('Branch deactivated successfully.');
    });
  };
}

function screenRow(screen) {
  return `<tr>
    <td><span class="badge bg-secondary">#${screen.id}</span></td>
    <td><strong class="text-warning">${screen.branchName || 'Branch ' + screen.branchId}</strong></td>
    <td><strong class="text-white">${screen.screenName}</strong></td>
    <td><span class="text-info">${screen.capacity}</span> seats</td>
    <td><span class="badge ${screen.active ? 'bg-success' : 'bg-danger'}">${screen.active ? 'Active' : 'Inactive'}</span></td>
    <td class="text-end">
      <button class="btn btn-sm btn-outline-warning me-2" onclick='editScreen(${JSON.stringify(screen).replace(/'/g, "&#39;")})'><i class="bi bi-pencil-square me-1"></i>Edit</button>
      <button class="btn btn-sm btn-outline-danger" onclick="deleteScreen(${screen.id})"><i class="bi bi-trash me-1"></i>Delete</button>
    </td>
  </tr>`;
}

async function initAdminScreensPage() {
  const reload = async () => {
    await crudLoad('/api/admin/screens', '#screenTableBody', screenRow);
  };

  // Populate branch select dropdown
  const branches = await apiFetch('/api/admin/branches').catch(() => []);
  const select = document.getElementById('screenBranchSelect');
  if (select) {
    select.innerHTML = '<option value="" disabled selected>Choose Cinema Branch...</option>' + 
      branches.map(b => `<option value="${b.id}">${b.branchName}</option>`).join('');
  }

  await reload();

  document.querySelector('#screenForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(event.target).entries());
    payload.branchId = Number(payload.branchId);
    payload.capacity = Number(payload.capacity);
    await runCrudAction(async () => {
      await apiFetch('/api/admin/screens', { method: 'POST', body: JSON.stringify(payload) });
      event.target.reset();
      await reload();
      showToast('Screen created successfully.');
    });
  });

  window.editScreen = (screen) => {
    document.getElementById('editScreenId').value = screen.id;
    document.getElementById('editScreenName').value = screen.screenName || '';
    const modal = new bootstrap.Modal(document.getElementById('editScreenModal'));
    modal.show();
  };

  document.querySelector('#editScreenForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(event.target).entries());
    const id = payload.id;
    delete payload.id;

    await runCrudAction(async () => {
      await apiFetch(`/api/admin/screens/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
      bootstrap.Modal.getInstance(document.getElementById('editScreenModal')).hide();
      await reload();
      showToast('Screen updated successfully.');
    });
  });

  window.deleteScreen = async (id) => {
    if (!confirmDelete('Deactivate this screen auditorium?')) return;
    await runCrudAction(async () => {
      await apiFetch(`/api/admin/screens/${id}`, { method: 'DELETE' });
      await reload();
      showToast('Screen deleted successfully.');
    });
  };
}

function seatAdminRow(seat) {
  const typeBadge = seat.seatType === 'VIP' ? 'bg-danger' 
                  : seat.seatType === 'PREMIUM' ? 'bg-warning text-dark' 
                  : 'bg-primary';
  return `<tr>
    <td><span class="badge bg-secondary">#${seat.id}</span></td>
    <td><strong class="text-white">${seat.screenName || 'Screen ' + seat.screenId}</strong></td>
    <td><span class="badge bg-dark border border-secondary text-white fs-6">${seat.rowName}${seat.seatNumber}</span></td>
    <td><span class="badge ${typeBadge}">${seat.seatType}</span></td>
    <td><span class="badge ${seat.active ? 'bg-success' : 'bg-danger'}">${seat.active ? 'Active' : 'Inactive'}</span></td>
    <td class="text-end">
      <button class="btn btn-sm btn-outline-warning me-2" onclick='editSeat(${JSON.stringify(seat).replace(/'/g, "&#39;")})'><i class="bi bi-pencil-square me-1"></i>Edit</button>
      <button class="btn btn-sm btn-outline-danger" onclick="deleteSeat(${seat.id})"><i class="bi bi-trash me-1"></i>Deactivate</button>
    </td>
  </tr>`;
}

async function initAdminSeatsPage() {
  const reload = async () => {
    const seats = await apiFetch('/api/admin/seats');
    document.querySelector('#seatTableBody').innerHTML = seats.map(seatAdminRow).join('') || '<tr><td colspan="6" class="text-center py-4 text-muted">No seats configured</td></tr>';
    const badge = document.querySelector('#seatCountBadge');
    if (badge) badge.textContent = `${seats.length} seats`;
  };

  // Populate screen dropdown
  const screens = await apiFetch('/api/admin/screens').catch(() => []);
  const select = document.getElementById('seatScreenSelect');
  if (select) {
    select.innerHTML = '<option value="" disabled selected>Select Screen...</option>' + 
      screens.map(s => `<option value="${s.id}">${s.branchName} - ${s.screenName}</option>`).join('');
  }

  await reload();

  document.querySelector('#seatForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(event.target).entries());
    payload.screenId = Number(payload.screenId);
    payload.seatNumber = Number(payload.seatNumber);
    await runCrudAction(async () => {
      await apiFetch('/api/admin/seats', { method: 'POST', body: JSON.stringify(payload) });
      event.target.reset();
      await reload();
      showToast('Seat created successfully.');
    });
  });

  window.editSeat = (seat) => {
    document.getElementById('editSeatId').value = seat.id;
    document.getElementById('editSeatScreenId').value = seat.screenId;
    document.getElementById('editSeatRowName').value = seat.rowName || '';
    document.getElementById('editSeatNumber').value = seat.seatNumber || 1;
    document.getElementById('editSeatType').value = seat.seatType || 'STANDARD';
    const modal = new bootstrap.Modal(document.getElementById('editSeatModal'));
    modal.show();
  };

  document.querySelector('#editSeatForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(event.target).entries());
    const id = payload.id;
    const body = {
      screenId: Number(payload.screenId),
      rowName: payload.rowName.toUpperCase().trim(),
      seatNumber: Number(payload.seatNumber),
      seatType: payload.seatType,
      active: true
    };

    await runCrudAction(async () => {
      await apiFetch(`/api/admin/seats/${id}`, { method: 'PUT', body: JSON.stringify(body) });
      bootstrap.Modal.getInstance(document.getElementById('editSeatModal')).hide();
      await reload();
      showToast('Seat updated successfully in database.');
    });
  });

  window.deleteSeat = async (id) => {
    if (!confirmDelete('Deactivate this seat?')) return;
    await runCrudAction(async () => {
      await apiFetch(`/api/admin/seats/${id}`, { method: 'DELETE' });
      await reload();
      showToast('Seat deactivated successfully.');
    });
  };
}

function showtimeRow(showtime) {
  const statusBadge = showtime.status === 'SCHEDULED' ? 'bg-success' 
                    : showtime.status === 'RESCHEDULED' ? 'bg-warning text-dark' 
                    : 'bg-secondary';
  return `<tr>
    <td><span class="badge bg-secondary">#${showtime.id}</span></td>
    <td><strong class="text-white">${showtime.movieTitle}</strong></td>
    <td><span class="text-muted">${showtime.screenName}</span></td>
    <td><span class="text-white">${showtime.showDate}</span> <span class="badge bg-dark border border-secondary text-warning">${showtime.startTime}</span></td>
    <td><strong class="text-warning">${money(showtime.ticketPrice)}</strong></td>
    <td><span class="badge ${statusBadge}">${showtime.status}</span></td>
    <td class="text-end">
      <button class="btn btn-sm btn-outline-warning me-2" onclick='editShowtime(${JSON.stringify(showtime).replace(/'/g, "&#39;")})'><i class="bi bi-pencil-square me-1"></i>Edit</button>
      <button class="btn btn-sm btn-outline-danger" onclick="cancelShowtime(${showtime.id})"><i class="bi bi-x-circle me-1"></i>Cancel</button>
    </td>
  </tr>`;
}

async function initAdminShowtimesPage() {
  const reload = async () => {
    const list = await apiFetch('/api/admin/showtimes');
    document.querySelector('#showtimeTableBody').innerHTML = list.map(showtimeRow).join('') || '<tr><td colspan="7" class="text-center py-4 text-muted">No showtimes found</td></tr>';
    const badge = document.querySelector('#showtimeCountBadge');
    if (badge) badge.textContent = `${list.length} showtimes`;
  };

  // Populate Movie and Screen Selects
  const [movies, screens] = await Promise.all([
    apiFetch('/api/admin/movies').catch(() => []),
    apiFetch('/api/admin/screens').catch(() => [])
  ]);

  const movieSelect = document.getElementById('showtimeMovieSelect');
  if (movieSelect) {
    movieSelect.innerHTML = '<option value="" disabled selected>Select Movie...</option>' + 
      movies.map(m => `<option value="${m.id}">${m.title} (${m.ageRating || 'PG'})</option>`).join('');
  }

  const screenSelect = document.getElementById('showtimeScreenSelect');
  if (screenSelect) {
    screenSelect.innerHTML = '<option value="" disabled selected>Select Screen / Hall...</option>' + 
      screens.map(s => `<option value="${s.id}">${s.branchName} - ${s.screenName}</option>`).join('');
  }

  await reload();

  document.querySelector('#showtimeForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(event.target).entries());
    payload.movieId = Number(payload.movieId);
    payload.screenId = Number(payload.screenId);
    payload.ticketPrice = Number(payload.ticketPrice);
    await runCrudAction(async () => {
      await apiFetch('/api/admin/showtimes', { method: 'POST', body: JSON.stringify(payload) });
      event.target.reset();
      await reload();
      showToast('Showtime created successfully.');
    });
  });

  window.editShowtime = (showtime) => {
    document.getElementById('editShowtimeId').value = showtime.id;
    document.getElementById('editShowtimeMovieId').value = showtime.movieId;
    document.getElementById('editShowtimeScreenId').value = showtime.screenId;
    document.getElementById('editShowtimeDate').value = showtime.showDate || '';
    document.getElementById('editShowtimeTime').value = showtime.startTime || '';
    document.getElementById('editShowtimePrice').value = showtime.ticketPrice || 0;
    document.getElementById('editShowtimeStatus').value = showtime.status || 'SCHEDULED';
    const modal = new bootstrap.Modal(document.getElementById('editShowtimeModal'));
    modal.show();
  };

  document.querySelector('#editShowtimeForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    const payload = Object.fromEntries(new FormData(event.target).entries());
    const id = payload.id;
    const body = {
      movieId: Number(payload.movieId),
      screenId: Number(payload.screenId),
      showDate: payload.showDate,
      startTime: payload.startTime,
      ticketPrice: Number(payload.ticketPrice),
      status: payload.status
    };

    await runCrudAction(async () => {
      await apiFetch(`/api/admin/showtimes/${id}`, { method: 'PUT', body: JSON.stringify(body) });
      bootstrap.Modal.getInstance(document.getElementById('editShowtimeModal')).hide();
      await reload();
      showToast('Showtime updated successfully in database.');
    });
  });

  window.cancelShowtime = async (id) => {
    if (!confirmDelete('Cancel this scheduled showtime?')) return;
    await runCrudAction(async () => {
      await apiFetch(`/api/admin/showtimes/${id}`, { method: 'DELETE' });
      await reload();
      showToast('Showtime cancelled successfully.');
    });
  };
}

function reservationRow(reservation) {
  const statusBadge = reservation.reservationStatus === 'CONFIRMED' ? 'bg-success' 
                    : reservation.reservationStatus === 'PENDING' ? 'bg-warning text-dark' 
                    : 'bg-danger';
  const payBadge = reservation.paymentStatus === 'PAID' ? 'bg-success' : 'bg-warning text-dark';
  return `<tr>
    <td>
      <span class="badge bg-dark border border-warning text-warning fs-6">${reservation.bookingReference}</span>
      <div class="small text-muted">${reservation.bookingDate ? reservation.bookingDate.split('T')[0] : ''}</div>
    </td>
    <td>
      <div class="fw-bold text-white">${reservation.customerName}</div>
      <div class="small text-muted">${reservation.customerEmail || ''}</div>
    </td>
    <td>
      <div class="text-white">${reservation.movieTitle}</div>
      <div class="small text-muted">${reservation.branchName} - ${reservation.screenName}</div>
      <div class="small text-warning">${reservation.showDate} at ${reservation.startTime}</div>
    </td>
    <td><span class="badge bg-secondary">${reservation.seatSummary}</span></td>
    <td><strong class="text-warning">${money(reservation.totalAmount)}</strong></td>
    <td><span class="badge ${statusBadge}">${reservation.reservationStatus}</span></td>
    <td><span class="badge ${payBadge}">${reservation.paymentStatus}</span></td>
    <td class="text-end">
      ${reservation.reservationStatus !== 'CANCELLED' ? `
        <button class="btn btn-sm btn-outline-danger" onclick="cancelReservation(${reservation.id})">
          <i class="bi bi-x-circle me-1"></i>Cancel
        </button>
      ` : '<span class="text-muted small">Cancelled</span>'}
    </td>
  </tr>`;
}

async function initAdminReservationsPage() {
  const loadData = async (term = '') => {
    const endpoint = `/api/admin/reservations${term ? `?term=${encodeURIComponent(term)}` : ''}`;
    const data = await apiFetch(endpoint);
    document.querySelector('#reservationTableBody').innerHTML = data.map(reservationRow).join('') || '<tr><td colspan="8" class="text-center py-4 text-muted">No reservations found</td></tr>';
    const badge = document.querySelector('#reservationCountBadge');
    if (badge) badge.textContent = `${data.length} records`;
  };

  await loadData();

  document.querySelector('#reservationSearchForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    const term = new FormData(event.target).get('term');
    await loadData(term);
  });

  window.cancelReservation = async (id) => {
    if (!confirmDelete('Are you sure you want to cancel this customer reservation? Reserved seats will be released.')) return;
    await runCrudAction(async () => {
      await apiFetch(`/api/admin/reservations/${id}/cancel`, { method: 'POST' });
      await loadData();
      showToast('Reservation cancelled and seats released successfully.');
    });
  };
}

async function initStaffReservationsPage() {
  const loadData = async (term = '') => {
    const data = await apiFetch(`/api/staff/reservations?term=${encodeURIComponent(term)}`);
    document.querySelector('#staffReservationTableBody').innerHTML = data.map((reservation) => `
      <tr>
        <td><strong class="text-warning">${reservation.bookingReference}</strong></td>
        <td><span class="text-white">${reservation.customerName}</span></td>
        <td><span class="text-white">${reservation.movieTitle}</span></td>
        <td><span class="badge bg-secondary">${reservation.seatSummary}</span></td>
        <td><span class="badge ${reservation.reservationStatus === 'CONFIRMED' ? 'bg-success' : 'bg-warning text-dark'}">${reservation.reservationStatus}</span></td>
        <td>${reservation.verified ? '<span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>Verified</span>' : `<button class="btn btn-sm btn-accent" onclick="verifyReservation('${reservation.bookingReference}')"><i class="bi bi-qr-code-scan me-1"></i>Verify Ticket</button>`}</td>
      </tr>`).join('') || '<tr><td colspan="6" class="text-center py-4 text-muted">No reservations found</td></tr>';
  };

  await loadData();

  document.querySelector('#staffReservationSearchForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    const term = new FormData(event.target).get('term');
    await loadData(term);
  });

  window.verifyReservation = async (reference) => {
    await runCrudAction(async () => {
      await apiFetch(`/api/staff/reservations/${encodeURIComponent(reference)}/verify`, { method: 'POST' });
      const term = new FormData(document.querySelector('#staffReservationSearchForm')).get('term');
      await loadData(term);
      showToast('Reservation ticket verified successfully.');
    });
  };
}

document.addEventListener('DOMContentLoaded', () => {
  // Apply saved theme before anything renders
  initThemeToggle();

  const page = document.body.dataset.page;
  const initializers = {
    home: initHomePage,
    movies: initMoviesPage,
    details: initMovieDetailsPage,
    booking: initBookingPage,
    bookings: initMyBookingsPage,
    profile: initProfilePage,
    dashboard: initDashboardPage,
    adminUsers: initAdminUsersPage,
    staffDashboard: initStaffDashboardPage,
    adminMovies: initAdminMoviesPage,
    adminBranches: initAdminBranchesPage,
    adminScreens: initAdminScreensPage,
    adminSeats: initAdminSeatsPage,
    adminShowtimes: initAdminShowtimesPage,
    adminReservations: initAdminReservationsPage,
    staffReservations: initStaffReservationsPage
  };
  initializers[page]?.().catch((error) => console.error(error));
});