document.getElementById('datePicker').addEventListener('change', function() {
    const date = this.value;
    const bookingsContainer = document.getElementById('bookings');
    bookingsContainer.innerHTML = '';

    fetch(`/api/bookings/user?date=${date}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка сети');
            }
            return response.json();
        })
        .then(bookings => {
            if (bookings.length === 0) {
                const noBookingsMessage = document.createElement('div');
                noBookingsMessage.className = 'no-bookings';
                noBookingsMessage.textContent = 'Похоже, на сегодня у вас нет броней';
                bookingsContainer.appendChild(noBookingsMessage);
            } else {
                bookings.forEach(booking => {
                    const bookingElement = document.createElement('div');
                    bookingElement.className = 'booking';

                    const startTime = new Date(booking.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
                    const endTime = new Date(booking.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

                    bookingElement.textContent = `Бронь: ${booking.room.name} с ${startTime} до ${endTime}`;
                    bookingsContainer.appendChild(bookingElement);
                });
            }
        })
        .catch(error => {
            console.error('Ошибка при получении бронирований:', error);
            const errorMessage = document.createElement('div');
            errorMessage.className = 'no-bookings';
            errorMessage.textContent = 'Ошибка при загрузке данных';
            bookingsContainer.appendChild(errorMessage);
        });
});

document.querySelector('.logout').addEventListener('click', function() {
    fetch('/logout', { method: 'POST' })
        .then(() => {
            window.location.href = '/login';
        })
        .catch(error => {
            console.error('Ошибка при выходе:', error);
        });
});
