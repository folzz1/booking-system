package com.example.backend.config;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final BuildingRepository buildingRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Роли
        Role adminRole = new Role("ADMIN");
        roleRepository.save(adminRole);
        Role userRole = new Role("USER");
        roleRepository.save(userRole);

        // Пользователь
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123")); // шифруем пароль "123"
        admin.setFirstName("Админ");
        admin.setLastName("Системы");
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);

        // Статусы бронирования
        bookingStatusRepository.save(createStatus("В рассмотрении"));
        bookingStatusRepository.save(createStatus("Одобрено"));
        bookingStatusRepository.save(createStatus("Отклонено"));
        bookingStatusRepository.save(createStatus("Отменено"));
        bookingStatusRepository.save(createStatus("Свободно"));

        // Адрес
        Address address = new Address();
        address.setCity("Москва");
        address.setStreet("Тверская");
        address.setBuildingNumber("12");
        address.setPostalCode("101000");
        addressRepository.save(address);

        // Здание
        Building building = new Building();
        building.setName("Центральное здание");
        building.setAddress(address);
        buildingRepository.save(building);

        // Тип комнаты
        RoomType conference = new RoomType();
        conference.setName("Конференц-зал");
        roomTypeRepository.save(conference);

        RoomType meeting = new RoomType();
        meeting.setName("Переговорная");
        roomTypeRepository.save(meeting);

        RoomType office = new RoomType();
        office.setName("Офис");
        roomTypeRepository.save(office);

        // Комнаты
        for (int i = 1; i <= 5; i++) {
            Room room = new Room();
            room.setName("Конф. Зал " + i);
            room.setFloor(i);
            room.setArea(50.0 + i * 10);
            room.setCapacity(10 + i * 5);
            room.setDescription("Конференц-зал #" + i);
            room.setType(conference);
            room.setBuilding(building);
            room.setIsActive(true);
            roomRepository.save(room);
        }

        for (int i = 1; i <= 3; i++) {
            Room room = new Room();
            room.setName("Переговорная " + i);
            room.setFloor(1);
            room.setArea(30.0);
            room.setCapacity(6 + i);
            room.setDescription("Маленькая переговорка #" + i);
            room.setType(meeting);
            room.setBuilding(building);
            room.setIsActive(true);
            roomRepository.save(room);
        }

        for (int i = 1; i <= 2; i++) {
            Room room = new Room();
            room.setName("Офис " + i);
            room.setFloor(2);
            room.setArea(70.0);
            room.setCapacity(15);
            room.setDescription("Отдельный офис #" + i);
            room.setType(office);
            room.setBuilding(building);
            room.setIsActive(true);
            roomRepository.save(room);
        }
    }

    private BookingStatus createStatus(String name) {
        BookingStatus status = new BookingStatus();
        status.setName(name);
        return status;
    }
}
