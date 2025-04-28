
const bookButton = document.createElement('button');
bookButton.textContent = 'Забронировать';
bookButton.className = 'book-room';
bookButton.onclick = () => bookRoom(room.id, startDateTime, endDateTime);

document.addEventListener('DOMContentLoaded', function() {
    const startDate = document.getElementById('startDate');
    const startTime = document.getElementById('startTime');
    const endTime = document.getElementById('endTime');
    const checkAvailabilityButton = document.getElementById('checkAvailability');
    const availableRoomsContainer = document.getElementById('availableRooms');

    const today = new Date();
    startDate.value = today.toISOString().substr(0, 10);
    startTime.value = '08:00';
    endTime.value = '09:00';

    checkAvailabilityButton.addEventListener('click', function() {
        if (!startDate.value || !startTime.value || !endTime.value) {
            alert('Пожалуйста, заполните все поля.');
            return;
        }

        const start = new Date(`${startDate.value}T${startTime.value}`);
        const end = new Date(`${startDate.value}T${endTime.value}`);

        if (end <= start) {
            alert('Время окончания должно быть позже времени начала');
            return;
        }

        const startDateTime = encodeURIComponent(`${startDate.value}T${startTime.value}`);
        const endDateTime = encodeURIComponent(`${startDate.value}T${endTime.value}`);

        checkAvailabilityButton.disabled = true;
        checkAvailabilityButton.textContent = 'Поиск...';
        availableRoomsContainer.innerHTML = '<div class="loading">Идет поиск...</div>';

        fetch(`/api/bookings/available?start=${startDateTime}&end=${endDateTime}`)
            .then(response => {
                if (!response.ok) throw new Error('Ошибка сервера');
                return response.json();
            })
            .then(rooms => {
                availableRoomsContainer.innerHTML = '';

                if (rooms.length === 0) {
                    availableRoomsContainer.innerHTML = '<div class="no-rooms">Нет свободных аудиторий на выбранное время.</div>';
                } else {
                    rooms.forEach(room => {
                        const roomElement = document.createElement('div');
                        roomElement.className = 'room-item';
                        roomElement.innerHTML = `
                            <div class="room-name">${room.name}${room.type ? ` (${room.type})` : ''}</div>
                            <button class="book-btn">Забронировать</button>
                        `;

                        roomElement.querySelector('.book-btn').addEventListener('click', () => {
                            bookRoom(room.id,
                                `${startDate.value}T${startTime.value}`,
                                `${startDate.value}T${endTime.value}`);
                        });

                        availableRoomsContainer.appendChild(roomElement);
                    });
                }
            })
            .catch(error => {
                availableRoomsContainer.innerHTML = `<div class="error">${error.message}</div>`;
            })
            .finally(() => {
                checkAvailabilityButton.disabled = false;
                checkAvailabilityButton.textContent = 'Проверить доступность';
            });
    });

    function bookRoom(roomId, start, end) {
        fetch(`/api/bookings`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            },
            body: JSON.stringify({
                room: { id: roomId },
                startTime: start,
                endTime: end
            })
        })
            .then(response => {
                if (!response.ok) throw new Error('Ошибка при создании брони');
                alert('Бронь успешно создана!');
                checkAvailabilityButton.click();
            })
            .catch(error => {
                alert(error.message);
            });
    }
});