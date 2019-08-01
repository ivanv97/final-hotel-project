package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.*;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.BookingRepository;
import eu.deltasource.internship.hotel.repository.GuestRepository;
import eu.deltasource.internship.hotel.repository.RoomRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static eu.deltasource.internship.hotel.domain.commodity.BedType.DOUBLE;
import static org.junit.jupiter.api.Assertions.*;
import static eu.deltasource.internship.hotel.domain.commodity.BedType.SINGLE;

public class RoomServiceTest {

    BookingRepository bookingRepository = new BookingRepository();
    GuestRepository guestRepository = new GuestRepository();
    RoomRepository roomRepository = new RoomRepository();
    RoomService roomService = new RoomService(roomRepository);
    GuestService guestService = new GuestService(guestRepository);
    BookingService bookingService = new BookingService(bookingRepository, roomService, guestService);

    @Before
    public void setUp() {
        // Commodities for a double room
        Set<AbstractCommodity> doubleSet = new HashSet<>(Arrays.asList(new Bed(DOUBLE), new Toilet(), new Shower()));

        // commodities for a single room
        Set<AbstractCommodity> singleSet = new HashSet<>(Arrays.asList(new Bed(SINGLE), new Toilet(), new Shower()));

        // commodities for a double room with king size bed
        Set<AbstractCommodity> kingSizeSet = new HashSet<>(Arrays.asList(new Bed(BedType.KING_SIZE), new Toilet(), new Shower()));

        // commodities for a 3 person room with a king size and a single
        Set<AbstractCommodity> threePeopleKingSizeSet = new HashSet<>(Arrays.asList(new Bed(BedType.KING_SIZE), new Bed(SINGLE), new Toilet(), new Shower()));

        // create some rooms
        Room doubleRoom = new Room(1, doubleSet);
        Room singleRoom = new Room(2, singleSet);
        Room kingSizeRoom = new Room(3, kingSizeSet);

        // adds the rooms to the repository, which then can be accessed from RoomService
        roomService.saveRooms(doubleRoom, singleRoom, kingSizeRoom);
    }

    @Test
    public void getRoomByIdSuccessfully() {
        //given
        int roomID = 3;

        //when
        Room searchedRoom = roomService.getRoomById(roomID);

        //then
        assertEquals(roomID, searchedRoom.getRoomId());
    }

    @Test
    public void getRoomByIdUnsuccessfully() {
        //given
        Set<AbstractCommodity> kingSizeSet = new HashSet<>(Arrays.asList(new Bed(BedType.KING_SIZE), new Toilet(), new Shower()));
        int roomID = 7;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> roomService.getRoomById(roomID));
    }

    @Test
    public void findRooms() {
        //given
        int numberOfRooms = 3;

        //when
        List<Room> rooms = roomService.findRooms();

        //then
        assertEquals(numberOfRooms, rooms.size());
    }

    @Test
    public void saveRoom() {
        //given
        Bed bed = new Bed(BedType.DOUBLE);
        Set<AbstractCommodity> commodities = new HashSet<>();
        commodities.add(bed);
        int roomID = 4;
        Room room = new Room(roomID, commodities);

        //when
        Room newRoom = roomService.saveRoom(room);

        //then
        assertEquals(newRoom.getRoomId(), roomService.getRoomById(roomID).getRoomId());
    }

    @Test
    public void deleteRoomByExistingId() {
        // given
        int roomID = 1;
        int size = 2;

        // when
        boolean result = roomService.deleteRoomById(roomID);

        //then
        assertEquals(true, result);

        //then 2
        assertEquals(size, roomService.findRooms().size());
    }

    @Test
    public void saveRoomsNullCheck() {
        //given
        Room[] rooms = null;

        // when and then
        assertThrows(FailedInitializationException.class, () -> roomService.saveRooms(rooms));
    }

    @Test
    public void saveRoomNullCheck() {
        //given
        Room room = null;

        // when and then
        assertThrows(FailedInitializationException.class, () -> roomService.saveRoom(room));
    }

    @Test
    public void deleteRoomByIdThatDoesNotExist() {
        //given
        int roomID = 12;
        boolean expectedResult = false;

        //then
        assertThrows(ItemNotFoundException.class,
                () -> roomService.deleteRoomById(roomID));
    }

    @Test
    public void updateRoomSuccessfully() {
        // given
        Set<AbstractCommodity> commodities = new HashSet<>();
        commodities.add(new Bed(BedType.KING_SIZE));
        commodities.add(new Toilet());
        int roomID = 2;
        Room room = new Room(roomID, commodities);

        // when
        Room checkRoom = roomService.updateRoom(room);

        //then
        assertEquals(checkRoom.getCommodities().size(), room.getCommodities().size());
    }

    @Test
    public void updateRoomUnsuccessfully() {
        // given
        Set<AbstractCommodity> commodities = new HashSet<>();
        commodities.add(new Bed(BedType.KING_SIZE));
        commodities.add(new Toilet());
        int roomID = 8;
        Room room = new Room(roomID, commodities);
        Room newRoom = null;

        // when and then 1
        assertThrows(ItemNotFoundException.class, () -> roomService.updateRoom(room));

        // when and then 2 (null check)
        assertThrows(FailedInitializationException.class, () -> roomService.updateRoom(newRoom));
    }

    @Test
    public void deleteRoomNullCheck() {
        // given
        Room room = null;

        //when and then
        assertThrows(FailedInitializationException.class, () -> roomService.deleteRoom(room));
    }

    @Test
    public void deleteRoomSuccessfully() {
        //given
        Set<AbstractCommodity> commodities = new HashSet<>();
        commodities.add(new Bed(BedType.KING_SIZE));
        int roomID = 3;
        Room threePeopleKingSizeRoom = new Room(roomID, commodities);

        //when
        boolean result = roomService.deleteRoom(threePeopleKingSizeRoom);

        //then
        assertEquals(true, result);
    }
}