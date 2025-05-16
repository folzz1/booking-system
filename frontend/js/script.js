const PageLoader = {
    _startTime: null,
    _minShowTime: 300,

    show: () => {
        document.getElementById('initial-loader').style.display = 'flex';
        document.body.style.overflow = 'hidden';
        PageLoader._startTime = Date.now();
    },

    hide: () => {
        const elapsed = Date.now() - PageLoader._startTime;
        const remainingTime = Math.max(0, PageLoader._minShowTime - elapsed);

        setTimeout(() => {
            document.getElementById('initial-loader').style.display = 'none';
            document.body.style.overflow = '';
        }, remainingTime);
    }
};


document.addEventListener('DOMContentLoaded', async function() {
    PageLoader.show();
    await checkAuth();
    setupEventListeners();
    PageLoader.hide();
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
    console.log("Bookings data:", bookings);
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
        const canCancel = canBookingBeCancelled(booking);
        const room = booking.room;

        const buildingInfo = room.building ? `Здание: ${room.building.name}` : '';
        const wingInfo = room.wing ? `Крыло: ${room.wing.name}` : '';
        const locationInfo = [buildingInfo, wingInfo].filter(Boolean).join(', ');


        bookingElement.innerHTML = `
    <div class="booking-header">
        <div class="room-name">${room.name} (${room.type})</div>
        <div class="booking-time">${startTime} - ${endTime}</div>
    </div>
    <div class="booking-details">
        <div class="detail-row">
            <strong>Статус:</strong> ${booking.status}
        </div>
        ${locationInfo ? `
        <div class="detail-row">
            <strong>Здание:</strong> ${room.building?.name || 'Не указано'}
            ${room.wing ? `, <strong>Крыло:</strong> ${room.wing.name}` : ''}
        </div>` : ''}
        ${room.floor ? `<div class="detail-row"><strong>Этаж:</strong> ${room.floor}</div>` : ''}
        ${room.capacity ? `<div class="detail-row"><strong>Вместимость:</strong> ${room.capacity} чел.</div>` : ''}
        ${room.area ? `<div class="detail-row"><strong>Площадь:</strong> ${room.area} м²</div>` : ''}
    </div>
    <div class="booking-actions">
        ${canCancel ?
            `<button class="cancel-booking" data-id="${booking.id}">Отменить бронирование</button>` :
            `<button class="cancel-booking" disabled title="Нельзя отменить">Отменить бронирование</button>`
        }
    </div>
`;

        bookingsContainer.appendChild(bookingElement);
    });

    setupCancelButtonsHandlers();
}

function canBookingBeCancelled(booking) {
    console.log(`Checking booking:`, {
        id: booking.id,
        status: booking.status,
        startTime: booking.startTime,
        currentTime: new Date()
    });
    return true;
    // const now = new Date();
    // const startDate = new Date(booking.startTime);
    // return startDate > now && booking.status === 'Одобрено';
}

function setupCancelButtonsHandlers() {
    document.querySelectorAll('.cancel-booking:not(:disabled)').forEach(button => {
        button.addEventListener('click', () => handleCancelBooking(button.dataset.id));
    });
}

async function handleCancelBooking(bookingId) {
    const isConfirmed = confirm('Вы уверены, что хотите отменить это бронирование?');

    if (!isConfirmed) {
        return;
    }

    try {
        const response = await fetch(`/api/users/${bookingId}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (response.ok) {
            alert('Бронирование успешно отменено');
            const datePicker = document.getElementById('datePicker');
            loadBookings(datePicker.value);
        } else {
            const error = await response.text();
            alert(`Ошибка при отмене бронирования: ${error}`);
        }
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Произошла ошибка при отмене бронирования');
    }
}

function formatTime(dateTimeString) {
    const date = new Date(dateTimeString);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

document.getElementById('bookRoomBtn').addEventListener('click', async (e) => {
    e.preventDefault();
    try {
        const response = await fetch('/api/users/current', {
            credentials: 'include'
        });

        if (response.ok) {
            window.location.href = '/book-room';
        } else {
            window.location.href = '/login.html';
        }
    } catch (error) {
        window.location.href = '/login.html';
    }
});