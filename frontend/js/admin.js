document.addEventListener('DOMContentLoaded', async function() {
    try {
        await checkAdminAuth();
        setupEventListeners();
        loadInitialData();
    } catch (error) {
        console.error('Initialization error:', error);
        window.location.href = '/login.html';
    }
});

async function checkAdminAuth() {
    try {
        const [authResponse, roleResponse] = await Promise.all([
            fetch('/api/users/current', { credentials: 'include' }),
            fetch('/api/users/current/role', { credentials: 'include' })
        ]);

        if (!authResponse.ok || !roleResponse.ok) {
            throw new Error('Authentication check failed');
        }

        const { role } = await roleResponse.json();

        if (role !== 'ADMIN') {
            window.location.href = '/';
            return;
        }
    } catch (error) {
        console.error('Auth check error:', error);
        window.location.href = '/login.html';
        throw error;
    }
}

function setupEventListeners() {
    // Навигация между разделами
    document.querySelectorAll('.admin-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            document.querySelectorAll('.admin-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.admin-section').forEach(s => s.classList.remove('active'));

            this.classList.add('active');
            document.getElementById(`${this.dataset.section}-section`).classList.add('active');

            // Загружаем данные при переключении раздела
            switch(this.dataset.section) {
                case 'bookings':
                    loadBookings(document.getElementById('adminDatePicker').value);
                    break;
                case 'buildings':
                    loadBuildings();
                    break;
                case 'wings':
                    loadWings();
                    break;
                case 'addresses':
                    loadAddresses();
                    break;
                case 'users':
                    loadUsers();
                    break;
            }
        });
    });

    // Дата для бронирований
    const datePicker = document.getElementById('adminDatePicker');
    const today = new Date().toISOString().split('T')[0];
    datePicker.value = today;
    datePicker.addEventListener('change', function() {
        loadBookings(this.value);
    });

    // Кнопка выхода
    document.getElementById('logoutButton').addEventListener('click', async function() {
        try {
            const response = await fetch('/logout', {
                method: 'POST',
                credentials: 'include'
            });

            if (response.ok) {
                window.location.href = '/login.html';
            }
        } catch (error) {
            console.error('Ошибка выхода:', error);
        }
    });
}

function loadInitialData() {
    loadBookings(document.getElementById('adminDatePicker').value);
}

async function loadBookings(date) {
    const bookingsContainer = document.getElementById('all-bookings');
    bookingsContainer.innerHTML = '<div class="loading">Загрузка бронирований...</div>';

    try {
        const response = await fetch(`/admin/api/bookings?date=${date}`, {
            credentials: 'include'
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Ошибка загрузки бронирований');
        }

        const bookings = await response.json();
        displayAllBookings(bookings);
    } catch (error) {
        console.error('Ошибка:', error);
        bookingsContainer.innerHTML = `
            <div class="error-message">
                Ошибка при загрузке бронирований: ${error.message}
                <button onclick="loadBookings(document.getElementById('adminDatePicker').value)">Попробовать снова</button>
            </div>
        `;
    }
}

function displayAllBookings(bookings) {
    const container = document.getElementById('all-bookings');

    if (!bookings || bookings.length === 0) {
        container.innerHTML = '<div class="no-data">Нет бронирований на выбранную дату</div>';
        return;
    }

    container.innerHTML = '';

    bookings.forEach(booking => {
        const bookingElement = document.createElement('div');
        bookingElement.className = 'booking-item';

        const startTime = formatTime(booking.startTime);
        const endTime = formatTime(booking.endTime);

        bookingElement.innerHTML = `
            <div class="booking-header">
                <div class="room-name">${booking.roomName || 'Неизвестно'} (${booking.roomType || 'Неизвестно'})</div>
                <div class="booking-time">${startTime} - ${endTime}</div>
            </div>
            <div class="booking-details">
                <span><strong>Пользователь:</strong> ${booking.userFullName || 'Неизвестно'}</span>
                <span><strong>Здание:</strong> ${booking.buildingName || 'Неизвестно'}</span>
                ${booking.wingName ? `<span><strong>Крыло:</strong> ${booking.wingName}</span>` : ''}
                ${booking.floor !== null ? `<span><strong>Этаж:</strong> ${booking.floor}</span>` : ''}
                <span><strong>Статус:</strong> ${getStatusText(booking.status)}</span>
            </div>
        `;

        container.appendChild(bookingElement);
    });
}

function getStatusText(status) {
    const statusMap = {
        'PENDING': 'В рассмотрении',
        'APPROVED': 'Одобрено',
        'REJECTED': 'Отклонено'
    };
    return statusMap[status] || status;
}

async function loadBuildings() {
    const container = document.getElementById('buildings-list');
    container.innerHTML = '<div class="loading">Загрузка зданий...</div>';

    try {
        const response = await fetch('/admin/api/buildings', {
            credentials: 'include'
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Ошибка загрузки зданий');
        }

        const buildings = await response.json();
        displayBuildings(buildings);
    } catch (error) {
        console.error('Ошибка:', error);
        container.innerHTML = `
            <div class="error-message">
                Ошибка при загрузке зданий: ${error.message}
                <button onclick="loadBuildings()">Попробовать снова</button>
            </div>
        `;
    }
}

function displayBuildings(buildings) {
    const container = document.getElementById('buildings-list');

    if (!buildings || buildings.length === 0) {
        container.innerHTML = '<div class="no-data">Нет данных о зданиях</div>';
        return;
    }

    container.innerHTML = '';

    buildings.forEach(building => {
        const element = document.createElement('div');
        element.className = 'data-item';
        element.innerHTML = `
            <h3>${building.name || 'Неизвестно'}</h3>
            <p><strong>Адрес:</strong> ${building.city || ''}, ${building.street || ''}, ${building.buildingNumber || ''}</p>
            <p><strong>Количество крыльев:</strong> ${building.wingsCount || 0}</p>
            <p><strong>Количество аудиторий:</strong> ${building.roomsCount || 0}</p>
        `;
        container.appendChild(element);
    });
}



async function loadUsers() {
    const container = document.getElementById('users-list');
    container.innerHTML = '<div class="loading">Загрузка пользователей...</div>';

    try {
        const response = await fetch('/admin/api/users', {
            credentials: 'include'
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Ошибка загрузки пользователей');
        }

        const users = await response.json();

        if (!users || users.length === 0) {
            container.innerHTML = '<div class="no-data">Нет пользователей</div>';
            return;
        }

        container.innerHTML = '';
        users.forEach(user => {
            if (!user) {
                console.warn('Invalid user data');
                return;
            }

            const el = document.createElement('div');
            el.className = 'data-item';
            el.innerHTML = `
                <p><strong>${user.firstName || ''} ${user.lastName || ''}</strong></p>
                <p>Логин: ${user.username || 'Неизвестно'}</p>
                <p>Роли: ${user.roles ? user.roles.join(', ') : 'Нет ролей'}</p>
            `;
            container.appendChild(el);
        });
    } catch (e) {
        console.error('Ошибка:', e);
        container.innerHTML = `
            <div class="error-message">
                ${e.message}
                <button onclick="loadUsers()">Попробовать снова</button>
            </div>
        `;
    }
}

async function loadWings() {
    const container = document.getElementById('wings-list');
    container.innerHTML = '<div class="loading">Загрузка крыльев...</div>';

    try {
        const response = await fetch('/admin/api/wings', {
            credentials: 'include'
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Ошибка загрузки крыльев');
        }

        const wings = await response.json();
        displayWings(wings);
    } catch (e) {
        console.error('Ошибка:', e);
        container.innerHTML = `
            <div class="error-message">
                ${e.message}
                <button onclick="loadWings()">Попробовать снова</button>
            </div>
        `;
    }
}

function displayWings(wings) {
    const container = document.getElementById('wings-list');

    if (!wings || wings.length === 0) {
        container.innerHTML = '<div class="no-data">Нет крыльев</div>';
        return;
    }

    container.innerHTML = '';

    wings.forEach(wing => {
        const el = document.createElement('div');
        el.className = 'data-item';
        el.innerHTML = `
            <p><strong>${wing.name || 'Неизвестно'}</strong></p>
            <p>Описание: ${wing.description || '—'}</p>
            <p>Здание: ${wing.buildingName || 'Неизвестно'}</p>
            <p>Количество аудиторий: ${wing.roomsCount || 0}</p>
        `;
        container.appendChild(el);
    });
}

async function loadAddresses() {
    const container = document.getElementById('addresses-list');
    container.innerHTML = '<div class="loading">Загрузка адресов...</div>';

    try {
        const response = await fetch('/admin/api/addresses', {
            credentials: 'include'
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Ошибка загрузки адресов');
        }

        const addresses = await response.json();
        displayAddresses(addresses);
    } catch (e) {
        console.error('Ошибка:', e);
        container.innerHTML = `
            <div class="error-message">
                ${e.message}
                <button onclick="loadAddresses()">Попробовать снова</button>
            </div>
        `;
    }
}

function displayAddresses(addresses) {
    const container = document.getElementById('addresses-list');

    if (!addresses || addresses.length === 0) {
        container.innerHTML = '<div class="no-data">Нет адресов</div>';
        return;
    }

    container.innerHTML = '';

    addresses.forEach(addr => {
        const el = document.createElement('div');
        el.className = 'data-item';
        el.innerHTML = `
            <p>${addr.city || ''}, ${addr.street || ''}, ${addr.buildingNumber || ''}</p>
            <p>Почтовый индекс: ${addr.postalCode || '—'}</p>
            <p>Количество зданий: ${addr.buildingsCount || 0}</p>
        `;
        container.appendChild(el);
    });
}

function formatTime(dateTimeString) {
    try {
        const date = new Date(dateTimeString);
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } catch (e) {
        console.error('Invalid date format:', dateTimeString);
        return '--:--';
    }
}