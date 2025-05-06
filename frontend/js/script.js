document.addEventListener('DOMContentLoaded', async function() {
    await checkAuth();
    setupEventListeners();
});

async function checkAuth() {
    try {
        const response = await fetch('/api/users/current', {
            credentials: 'include'
        });

        if (!response.ok) {
            window.location.href = '/login.html';
        }
    } catch (error) {
        window.location.href = '/login.html';
    }
}

function setupEventListeners() {
    const datePicker = document.getElementById('datePicker');
    const today = new Date().toISOString().split('T')[0];
    datePicker.value = today;

    loadBookings(today);

    datePicker.addEventListener('change', function() {
        loadBookings(this.value);
    });

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

async function loadBookings(date) {
    const bookingsContainer = document.getElementById('bookings');
    bookingsContainer.innerHTML = '<div class="loading">Загрузка...</div>';

    try {
        const bookingsResponse = await fetch(`/api/bookings/user?date=${date}`, {
            credentials: 'include'
        });

        if (!bookingsResponse.ok) {
            throw new Error('Ошибка загрузки бронирований');
        }

        const bookings = await bookingsResponse.json();

        if (bookings.length === 0) {
            displayBookings([]);
            return;
        }

        const roomIds = bookings.map(b => b.roomId);
        const roomsResponse = await fetch(`/api/rooms?ids=${roomIds.join(',')}`, {
            credentials: 'include'
        });

        if (!roomsResponse.ok) {
            throw new Error('Ошибка загрузки информации о комнатах');
        }

        const rooms = await roomsResponse.json();

        const roomMap = new Map();
        rooms.forEach(room => {
            roomMap.set(room.id, room);
        });

        const enrichedBookings = bookings.map(booking => {
            return {
                ...booking,
                room: roomMap.get(booking.roomId)
            };
        });

        displayBookings(enrichedBookings);
    } catch (error) {
        console.error('Ошибка:', error);
        bookingsContainer.innerHTML = `
            <div class="error-message">
                Ошибка при загрузке бронирований: ${error.message}
            </div>
        `;
    }
}

function displayBookings(bookings) {
    const bookingsContainer = document.getElementById('bookings');

    if (bookings.length === 0) {
        bookingsContainer.innerHTML = `
            <div class="no-bookings">
                На выбранную дату бронирований не найдено
            </div>
        `;
        return;
    }

    bookingsContainer.innerHTML = '';

    bookings.forEach(booking => {
        const bookingElement = document.createElement('div');
        bookingElement.className = 'booking-item';

        const startTime = formatTime(booking.startTime);
        const endTime = formatTime(booking.endTime);

        bookingElement.innerHTML = `
            <div class="booking-header">
                <div class="room-name">${booking.room.name} (${booking.room.type})</div>
                <div class="booking-time">${startTime} - ${endTime}</div>
            </div>
            <div class="booking-details">
                <span>
                    <strong>Здание:</strong> ${booking.room.building.name}
                </span>
                ${booking.room.wing ? `
                    <span>
                        <strong>Крыло:</strong> ${booking.room.wing.name}
                    </span>
                ` : ''}
                <span>
                    <strong>Этаж:</strong> ${booking.room.floor}
                </span>
                <span>
                    <strong>Статус:</strong> ${booking.status}
                </span>
            </div>
        `;

        bookingsContainer.appendChild(bookingElement);
    });
}

function formatTime(dateTimeString) {
    const date = new Date(dateTimeString);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}