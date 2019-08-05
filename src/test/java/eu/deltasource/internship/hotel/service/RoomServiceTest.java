package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.*;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static eu.deltasource.internship.hotel.domain.commodity.BedType.*;
import static org.junit.jupiter.api.Assertions.*;

public class RoomServiceTest {

    RoomRepository roomRepository = new RoomRepository();
    RoomService roomService = new RoomService(roomRepository);
    Room singleRoom;
    Room kingSizeRoom;

    @BeforeEach
    public void setUp() {

        // commodities for a single room
        Set<AbstractCommodity> singleSet = new HashSet<>
                (Arrays.asList(new Bed(SINGLE), new Toilet(), new Shower()));

        // commodities for a double room with king size bed
        Set<AbstractCommodity> kingSizeSet = new HashSet<>
                (Arrays.asList(new Bed(BedType.KING_SIZE), new Toilet(), new Shower()));

        // commodities for a 3 person room with a king size and a single
        Set<AbstractCommodity> threePeopleKingSizeSet = new HashSet<>
                (Arrays.asList(new Bed(BedType.KING_SIZE), new Bed(SINGLE), new Toilet(), new Shower()));

        // create some rooms
        singleRoom = new Room(1, singleSet);
        kingSizeRoom = new Room(2, kingSizeSet);

        // adds the rooms to the repository, which then can be accessed from RoomService
        roomService.saveRooms(singleRoom, kingSizeRoom);
    }

    @Test
    public void getRoomByExistingId() {
        //given
        // 2 rooms already exist
        int roomId = 1;

        //when
        Room searchedRoom = roomService.getRoomById(roomId);

        //then
        assertTrue(searchedRoom.equals(singleRoom));
    }

    @Test
    public void getRoomByIdThatDoesNotExist() {
        //given
        // 2 rooms already exist
        int roomID = 7;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> roomService.getRoomById(roomID));
    }

    @Test
    public void getAllExistingRooms() {
        //given
        // 2 rooms already exist
        int numberOfRooms = 2;

        //when
        List<Room> rooms = roomService.findRooms();

        //then
        assertEquals(numberOfRooms, rooms.size());
    }

    @Test
    public void getAllRoomsThatDoNotExist() {
        //given
        RoomRepository repositoryRoom = new RoomRepository();
        RoomService serviceRoom = new RoomService(repositoryRoom);

        // when and then
        assertThrows(ItemNotFoundException.class, () -> serviceRoom.findRooms());
    }

    @Test
    public void createRoom() {
        //given
        // 2 rooms already exist
        Set<AbstractCommodity> commodities = new HashSet<>(Arrays.asList(new Bed(DOUBLE)));
        int roomID = 3;
        Room room = new Room(roomID, commodities);

        //when
        roomService.saveRoom(room);

        //then
        assertTrue(roomService.findRooms().contains((room)));
    }

    @Test
    public void createInvalidRoom() {
        //given
        // 2 rooms already exist
        Set<AbstractCommodity> invalidSet = null;
        Set<AbstractCommodity> doubleSet = new HashSet<>();
        int roomID = 3;
        Room kingSizeRoom = null;

        //when and then
        assertThrows(FailedInitializationException.class,
                () -> roomService.saveRoom(new Room(roomID, doubleSet)));
        assertThrows(FailedInitializationException.class,
                () -> roomService.saveRoom((kingSizeRoom)));
        assertThrows(FailedInitializationException.class,
                () -> roomService.saveRoom(new Room(roomID, invalidSet)));
    }

    @Test
    public void deleteRoomByExistingId() {
        // given
        // 2 rooms already exist
        int roomID = 1;

        // when
        boolean result = roomService.deleteRoomById(roomID);

        //then
        assertEquals(true, result);
        assertThrows(ItemNotFoundException.class, () -> roomService.getRoomById(roomID));
    }

    @Test
    public void deleteRoomByIdThatDoesNotExist() {
        //given
        // 2 rooms already exist
        int roomID = 12;

        //when and then
        assertThrows(ItemNotFoundException.class, () -> roomService.deleteRoomById(roomID));
    }

    @Test
    public void deleteRoomNullCheck() {
        // given
        Room room = null;

        //when and then
        assertThrows(FailedInitializationException.class, () -> roomService.deleteRoom(room));
    }

    @Test
    public void deleteExistingRoom() {
        //given
        // two rooms already exist
        int roomId = 2;

        //when
        boolean result = roomService.deleteRoom(kingSizeRoom);

        //then
        assertEquals(true, result);
        assertThrows(ItemNotFoundException.class, () -> roomService.getRoomById(roomId));
    }

    @Test
    public void deleteAllExistingRooms() {
        //given

        // when
        roomService.deleteAll();

        //then
        assertThrows(ItemNotFoundException.class, () -> roomService.findRooms());
    }

    @Test
    public void deleteAllRoomsThatDoNotExist() {
        //given
        RoomRepository roomRepository = new RoomRepository();
        RoomService roomService = new RoomService(roomRepository);

        //when and then
        assertThrows(ItemNotFoundException.class, () -> roomService.deleteAll());
    }

    @Test
    public void updateValidRoom() {
        // given
        // 2 rooms already exist
        Set<AbstractCommodity> commodities = new HashSet<>(Arrays.asList(new Bed(KING_SIZE), new Toilet()));
        int roomID = 2;
        Room room = new Room(roomID, commodities);

        // when
        Room expectedRoom = roomService.updateRoom(room);

        //then
        assertTrue(roomService.getRoomById(roomID).getCommodities().equals(expectedRoom.getCommodities()));
    }

    @Test
    public void updateInvalidRoom() {
        // given
        // 2 rooms already exist
        Set<AbstractCommodity> commodities = new HashSet<>
                (Arrays.asList(new Bed(DOUBLE), new Shower()));
        int roomID = 8;
        Room room = new Room(roomID, commodities);
        Room newRoom = null;

        // when and then
        assertThrows(ItemNotFoundException.class, () -> roomService.updateRoom(room));
        assertThrows(FailedInitializationException.class, () -> roomService.updateRoom(newRoom));
    }

    @Test
    public void saveInvalidRooms() {
        //given
        // 2 rooms already exist
        Room[] rooms = null;
        Set<AbstractCommodity> commodities = new HashSet<>
                (Arrays.asList(new Bed(KING_SIZE), new Shower()));
        int roomID = 3;
        Room kingSizeRoom = new Room(roomID, commodities);
        Room invalidRoom = null;

        // when and then
        assertThrows(FailedInitializationException.class, () -> roomService.saveRooms(rooms));
        assertThrows(FailedInitializationException.class, () -> roomService.saveRooms(kingSizeRoom, invalidRoom));
    }

    @Test
    public void saveValidRooms() {
        //given
        // 2 rooms already exist
        Set<AbstractCommodity> thirdRoomCommodities = new HashSet<>
                (Arrays.asList(new Bed(DOUBLE), new Toilet()));
        Set<AbstractCommodity> fourthRoomCommodities = new HashSet<>
                (Arrays.asList(new Bed(SINGLE), new Bed(KING_SIZE)));
        int roomID = 3, roomId = 4, expectedSize = 4;
        Room kingSizeRoom = new Room(roomID, thirdRoomCommodities);
        Room singleKingSizeRoom = new Room(roomId, fourthRoomCommodities);

        //when
        roomService.saveRooms(kingSizeRoom, singleKingSizeRoom);

        //then
        assertEquals(expectedSize, roomService.findRooms().size());
        assertTrue(roomService.findRooms().contains(kingSizeRoom));
        assertTrue(roomService.findRooms().contains(singleKingSizeRoom));
    }

    @AfterEach
    void tearDown() {
        singleRoom = null;
        kingSizeRoom = null;
    }
}