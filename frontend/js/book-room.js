
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


document.addEventListener('DOMContentLoaded', async function () {
    await checkAuth();

    const startDate = document.getElementById('startDate');
    const startTime = document.getElementById('startTime');
    const endTime = document.getElementById('endTime');
    const checkAvailabilityButton = document.getElementById('checkAvailability');
    const applyFiltersButton = document.getElementById('applyFilters');
    const resetFiltersButton = document.getElementById('resetFilters');
    const availableRoomsContainer = document.getElementById('availableRooms');
    const buildingFilter = document.getElementById('buildingFilter');
    const wingFilter = document.getElementById('wingFilter');
    const floorFilter = document.getElementById('floorFilter');
    const capacityFilter = document.getElementById('capacityFilter');

    let allAvailableRooms = [];
    let filteredRooms = [];

    const today = new Date();
    startDate.value = today.toISOString().substr(0, 10);
    startTime.value = '08:00';
    endTime.value = '09:00';

    loadFilters();

    async function loadFilters() {
        try {
            const buildingsResponse = await fetch('/api/rooms/buildings', {
                credentials: 'include'
            });
            if (!buildingsResponse.ok) throw new Error('Ошибка загрузки зданий');
            const buildings = await buildingsResponse.json();

            buildings.forEach(building => {
                const option = document.createElement('option');
                option.value = building.id;
                option.textContent = building.name;
                buildingFilter.appendChild(option);
            });

            const wingsResponse = await fetch('/api/rooms/wings', {
                credentials: 'include'
            });
            if (!wingsResponse.ok) throw new Error('Ошибка загрузки крыльев');
            const wings = await wingsResponse.json();

            wings.forEach(wing => {
                const option = document.createElement('option');
                option.value = wing.id;
                option.textContent = wing.name;
                wingFilter.appendChild(option);
            });

            for (let i = 1; i <= 10; i++) {
                const option = document.createElement('option');
                option.value = i;
                option.textContent = i;
                floorFilter.appendChild(option);
            }
        } catch (error) {
            console.error('Ошибка загрузки фильтров:', error);
            showError('Ошибка загрузки фильтров. Пожалуйста, обновите страницу.');
        }
    }


    checkAvailabilityButton.addEventListener('click', async function () {
        if (!startDate.value || !startTime.value || !endTime.value) {
            showError('Пожалуйста, заполните все поля даты и времени.');
            return;
        }

        const start = new Date(`${startDate.value}T${startTime.value}`);
        const end = new Date(`${startDate.value}T${endTime.value}`);

        if (end <= start) {
            showError('Время окончания должно быть позже времени начала');
            return;
        }

        try {
            checkAvailabilityButton.disabled = true;
            checkAvailabilityButton.textContent = 'Поиск...';
            availableRoomsContainer.innerHTML = '<div class="loading">Идет поиск доступных аудиторий...</div>';

            const startDateTime = encodeURIComponent(`${startDate.value}T${startTime.value}`);
            const endDateTime = encodeURIComponent(`${startDate.value}T${endTime.value}`);

            const response = await fetch(`/api/bookings/available?start=${startDateTime}&end=${endDateTime}`, {
                credentials: 'include',
                headers: {
                    'Accept': 'application/json'
                }
            });

            if (!response.ok) {
                if (response.status === 403) {
                    throw new Error('Доступ запрещен. Пожалуйста, авторизуйтесь.');
                }
                const error = await response.text();
                throw new Error(error || 'Ошибка загрузки доступных аудиторий');
            }

            allAvailableRooms = await response.json();
            filteredRooms = [...allAvailableRooms];

            displayRooms(filteredRooms);
        } catch (error) {
            console.error('Ошибка:', error);
            showError(error.message);
        } finally {
            checkAvailabilityButton.disabled = false;
            checkAvailabilityButton.textContent = 'Найти доступные аудитории';
        }
    });

    applyFiltersButton.addEventListener('click', function () {
        if (allAvailableRooms.length === 0) {
            showError('Сначала найдите доступные аудитории, указав дату и время');
            return;
        }

        filteredRooms = allAvailableRooms.filter(room => {
            if (buildingFilter.value && room.building.id != buildingFilter.value) return false;
            if (wingFilter.value && (!room.wing || room.wing.id != wingFilter.value)) return false;
            if (floorFilter.value && room.floor != floorFilter.value) return false;
            if (capacityFilter.value && room.capacity < parseInt(capacityFilter.value)) return false;
            return true;
        });

        displayRooms(filteredRooms);
    });

    resetFiltersButton.addEventListener('click', function () {
        buildingFilter.value = '';
        wingFilter.value = '';
        floorFilter.value = '';
        capacityFilter.value = '';

        if (allAvailableRooms.length > 0) {
            filteredRooms = [...allAvailableRooms];
            displayRooms(filteredRooms);
        }
    });

    function displayRooms(rooms) {
        availableRoomsContainer.innerHTML = '';

        if (rooms.length === 0) {
            availableRoomsContainer.innerHTML = '<div class="no-rooms">Нет аудиторий, соответствующих критериям поиска.</div>';
        } else {
            rooms.forEach(room => {
                const roomElement = document.createElement('div');
                roomElement.className = 'room-item';
                roomElement.innerHTML = `
                    <div>
                        <div class="room-name">${room.name} (${room.type})</div>
                        <div class="room-details">
                            <span>Здание: ${room.building.name}</span>
                            ${room.wing ? `<span>Крыло: ${room.wing.name}</span>` : ''}
                            <span>Этаж: ${room.floor}</span>
                            <span>Вместимость: ${room.capacity} чел.</span>
                            <span>Площадь: ${room.area} м²</span>
                        </div>
                    </div>
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
    }

    function bookRoom(roomId, start, end) {
        fetch(`/api/bookings`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                roomId: roomId,
                startTime: start,
                endTime: end
            }),
            credentials: 'include'
        })
            .then(async response => {
                if (!response.ok) {
                    const error = await response.text();
                    throw new Error(error || 'Ошибка при создании брони');
                }
                return response.json();
            })
            .then(() => {
                alert('Бронь успешно создана!');
                window.location.reload();
            })
            .catch(error => {
                showError(error.message);
            });
    }

    function showError(message) {
        availableRoomsContainer.innerHTML = `<div class="error">${message}</div>`;
    }
});