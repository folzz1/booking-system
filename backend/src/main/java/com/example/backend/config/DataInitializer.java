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
    private final WingRepository wingRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final PasswordEncoder passwordEncoder;



    @Override
    @Transactional
    public void run(String... args) {
        // Проверяем, есть ли уже данные в системе
        if (roleRepository.count() == 0 && userRepository.count() == 0) {
            initializeData();
        }
    }
    private void initializeData() {
        // Инициализация ролей
        Role adminRole = new Role("ADMIN");
        roleRepository.save(adminRole);
        Role userRole = new Role("USER");
        roleRepository.save(userRole);

        // Инициализация пользователей
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123"));
        admin.setFirstName("Админ");
        admin.setLastName("Системы");
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);

        User regularUser = new User();
        regularUser.setUsername("user");
        regularUser.setPassword(passwordEncoder.encode("123"));
        regularUser.setFirstName("Обычный");
        regularUser.setLastName("Пользователь");
        regularUser.setRoles(Set.of(userRole));
        userRepository.save(regularUser);

        // Статусы бронирования
        bookingStatusRepository.save(createStatus("В рассмотрении"));
        bookingStatusRepository.save(createStatus("Одобрено"));
        bookingStatusRepository.save(createStatus("Отклонено"));
        bookingStatusRepository.save(createStatus("Отменено"));
        bookingStatusRepository.save(createStatus("Свободно"));

        // Создание адресов
        Address mainAddress = new Address();
        mainAddress.setCity("Москва");
        mainAddress.setStreet("Ленинский проспект");
        mainAddress.setBuildingNumber("32");
        mainAddress.setPostalCode("119991");
        addressRepository.save(mainAddress);

        Address secondaryAddress = new Address();
        secondaryAddress.setCity("Москва");
        secondaryAddress.setStreet("Тверская");
        secondaryAddress.setBuildingNumber("12");
        secondaryAddress.setPostalCode("101000");
        addressRepository.save(secondaryAddress);

        // Создание типов помещений
        RoomType conferenceRoomType = new RoomType();
        conferenceRoomType.setName("Конференц-зал");
        roomTypeRepository.save(conferenceRoomType);

        RoomType meetingRoomType = new RoomType();
        meetingRoomType.setName("Переговорная");
        roomTypeRepository.save(meetingRoomType);

        RoomType officeRoomType = new RoomType();
        officeRoomType.setName("Офис");
        roomTypeRepository.save(officeRoomType);

        // Создание центрального здания с 5 крыльями
        Building centralBuilding = new Building();
        centralBuilding.setName("Центральный офисный комплекс");
        centralBuilding.setAddress(mainAddress);
        buildingRepository.save(centralBuilding);

        // Крылья центрального здания
        Wing mainWing = createWing("Главное крыло", "Основное административное крыло", centralBuilding);
        Wing northWing = createWing("Северное крыло", "Департамент разработки", centralBuilding);
        Wing southWing = createWing("Южное крыло", "Клиентские переговоры", centralBuilding);
        Wing eastWing = createWing("Восточное крыло", "Финансовый департамент", centralBuilding);
        Wing westWing = createWing("Западное крыло", "Технические помещения", centralBuilding);

        // Создание помещений в центральном здании
        createConferenceRooms(centralBuilding, mainWing, conferenceRoomType, 5, "Главный зал");
        createMeetingRooms(centralBuilding, mainWing, meetingRoomType, 8);
        createOffices(centralBuilding, mainWing, officeRoomType, 15);

        createConferenceRooms(centralBuilding, northWing, conferenceRoomType, 3, "Технический зал");
        createMeetingRooms(centralBuilding, northWing, meetingRoomType, 6);
        createOffices(centralBuilding, northWing, officeRoomType, 20);

        createConferenceRooms(centralBuilding, southWing, conferenceRoomType, 4, "Клиентский зал");
        createMeetingRooms(centralBuilding, southWing, meetingRoomType, 10);
        createOffices(centralBuilding, southWing, officeRoomType, 12);

        createConferenceRooms(centralBuilding, eastWing, conferenceRoomType, 2, "Финансовый зал");
        createMeetingRooms(centralBuilding, eastWing, meetingRoomType, 4);
        createOffices(centralBuilding, eastWing, officeRoomType, 18);

        createConferenceRooms(centralBuilding, westWing, conferenceRoomType, 1, "Серверный зал");
        createMeetingRooms(centralBuilding, westWing, meetingRoomType, 2);
        createOffices(centralBuilding, westWing, officeRoomType, 8);

        // Второе здание на том же адресе
        Building businessCenter = new Building();
        businessCenter.setName("Бизнес-центр Премиум");
        businessCenter.setAddress(mainAddress);
        buildingRepository.save(businessCenter);

        Wing bcMainWing = createWing("Основное крыло", null, businessCenter);
        Wing bcConfWing = createWing("Конференц-крыло", "Зона переговоров", businessCenter);

        createConferenceRooms(businessCenter, bcMainWing, conferenceRoomType, 3, "Бизнес зал");
        createMeetingRooms(businessCenter, bcMainWing, meetingRoomType, 5);
        createOffices(businessCenter, bcMainWing, officeRoomType, 10);

        createConferenceRooms(businessCenter, bcConfWing, conferenceRoomType, 6, "Конференц-зал");
        createMeetingRooms(businessCenter, bcConfWing, meetingRoomType, 8);

        // Здание на втором адресе
        Building centralOffice = new Building();
        centralOffice.setName("Центральный офис");
        centralOffice.setAddress(secondaryAddress);
        buildingRepository.save(centralOffice);

        Wing coWing = createWing("Главное крыло", null, centralOffice);

        createConferenceRooms(centralOffice, coWing, conferenceRoomType, 2, "Главный зал");
        createMeetingRooms(centralOffice, coWing, meetingRoomType, 4);
        createOffices(centralOffice, coWing, officeRoomType, 8);
    }

    private Wing createWing(String name, String description, Building building) {
        Wing wing = new Wing();
        wing.setName(name);
        wing.setDescription(description);
        wing.setBuilding(building);
        return wingRepository.save(wing);
    }

    private void createConferenceRooms(Building building, Wing wing, RoomType type, int count, String namePrefix) {
        for (int i = 1; i <= count; i++) {
            Room room = new Room();
            room.setName(namePrefix + " " + i);
            room.setFloor(i % 5 + 1);
            room.setArea(50.0 + i * 10);
            room.setCapacity(10 + i * 5);
            room.setDescription("Конференц-зал для проведения совещаний");
            room.setType(type);
            room.setBuilding(building);
            room.setWing(wing);
            room.setIsActive(true);
            roomRepository.save(room);
        }
    }

    private void createMeetingRooms(Building building, Wing wing, RoomType type, int count) {
        for (int i = 1; i <= count; i++) {
            Room room = new Room();
            room.setName("Переговорная " + i);
            room.setFloor(i % 3 + 1);
            room.setArea(15.0 + i * 2);
            room.setCapacity(4 + i);
            room.setDescription("Комната для переговоров");
            room.setType(type);
            room.setBuilding(building);
            room.setWing(wing);
            room.setIsActive(true);
            roomRepository.save(room);
        }
    }

    private void createOffices(Building building, Wing wing, RoomType type, int count) {
        for (int i = 1; i <= count; i++) {
            Room room = new Room();
            room.setName("Офис " + i);
            room.setFloor(i % 4 + 1);
            room.setArea(20.0 + i * 3);
            room.setCapacity(2);
            room.setDescription("Рабочий кабинет");
            room.setType(type);
            room.setBuilding(building);
            room.setWing(wing);
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