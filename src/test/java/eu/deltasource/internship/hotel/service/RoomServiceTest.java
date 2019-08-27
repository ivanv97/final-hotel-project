package eu.deltasource.internship.hotel.service;

import eu.deltasource.internship.hotel.domain.Room;
import eu.deltasource.internship.hotel.domain.commodity.*;
import eu.deltasource.internship.hotel.dto.AbstractCommodityDTO;
import eu.deltasource.internship.hotel.dto.BedDTO;
import eu.deltasource.internship.hotel.dto.RoomDTO;
import eu.deltasource.internship.hotel.exception.ArgumentNotValidException;
import eu.deltasource.internship.hotel.exception.FailedInitializationException;
import eu.deltasource.internship.hotel.exception.ItemNotFoundException;
import eu.deltasource.internship.hotel.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import eu.deltasource.internship.hotel.domain.commodity.Bed;
import eu.deltasource.internship.hotel.domain.commodity.BedType;

import java.util.*;

import static eu.deltasource.internship.hotel.domain.commodity.BedType.*;
import static org.junit.jupiter.api.Assertions.*;

public class RoomServiceTest {

	private RoomRepository roomRepository;
	private RoomService roomService;
	private Room singleRoom;
	private Room kingSizeRoom;

	@BeforeEach
	public void setUp() {
		roomRepository = new RoomRepository();
		roomService = new RoomService(roomRepository);

		Set<AbstractCommodity> singleSet = new HashSet<>
			(Arrays.asList(new Bed(SINGLE), new Toilet(), new Shower()));
		singleRoom = new Room(1, singleSet);


		Set<AbstractCommodity> kingSizeSet = new HashSet<>
			(Arrays.asList(new Bed(BedType.KING_SIZE), new Toilet(), new Shower()));
		kingSizeRoom = new Room(2, kingSizeSet);

		roomService.saveRooms(singleRoom, kingSizeRoom);
	}

	@Test
	public void getRoomByExistingIdShouldWorkIfExistingId() {
		//given


		//when
		Room searchedRoom = roomService.getRoomById(singleRoom.getRoomId());

		//then
		assertEquals(singleRoom, searchedRoom);
	}

	@Test
	public void getRoomByIdThatDoesNotExistShouldThrowException() {
		assertThrows(ItemNotFoundException.class, () -> roomService.getRoomById(roomService.findRooms().size() + 3));
	}

	@Test
	public void getAllExistingRoomsShouldReturnEverythingInList() {
		//given

		//when
		List<Room> rooms = roomService.findRooms();

		//then
		assertEquals(2, rooms.size());
		assertTrue(rooms.contains(singleRoom));
		assertTrue(rooms.contains(kingSizeRoom));
	}

	@Test
	public void saveRoomShouldWorkIfRoomIsValid() {
		//given
		Set<AbstractCommodity> commodities = new HashSet<>(Arrays.asList(new Bed(DOUBLE)));
		Room doubleBed = new Room(1, commodities);

		//when
		roomService.saveRoom(doubleBed);

		//then
		assertTrue(roomService.findRooms().contains((doubleBed)));
	}

	@Test
	public void saveRoomShouldFailIfNoOrNullCommoditiesOrRoomItselfNull() {
		assertThrows(FailedInitializationException.class,
			() -> roomService.saveRoom(new Room(3, new HashSet<>())));
		assertThrows(ArgumentNotValidException.class,
			() -> roomService.saveRoom((null)));
		assertThrows(FailedInitializationException.class,
			() -> roomService.saveRoom(new Room(3, null)));
	}

	@Test
	public void deleteRoomByIdShouldWorkIfIdExisting() {
		assertTrue(roomService.deleteRoomById(singleRoom.getRoomId()));
		assertFalse(roomService.findRooms().contains(singleRoom));
	}

	@Test
	public void deleteRoomByIdThatDoesNotExistShouldThrowException() {
		assertThrows(ItemNotFoundException.class, () -> roomService.deleteRoomById(roomService.findRooms().size() + 2));
	}

	@Test
	public void deleteRoomShouldThrowExceptionIfPassedNull() {
		assertThrows(ArgumentNotValidException.class, () -> roomService.deleteRoom(null));
	}

	@Test
	public void deleteRoomShouldWorkIfRoomExisting() {
		assertTrue(roomService.findRooms().contains(singleRoom));
		assertTrue(roomService.deleteRoom(singleRoom));
		assertFalse(roomService.findRooms().contains(singleRoom));
	}

	@Test
	public void deleteRoomShouldThrowExceptionIfRoomNonExisting() {
		//Given
		kingSizeRoom = new Room(3, new HashSet<>(Arrays.asList(new Bed(KING_SIZE), new Toilet())));

		//When


		//Then
		assertThrows(ItemNotFoundException.class, () -> roomService.deleteRoom(kingSizeRoom));
	}

	@Test
	public void deleteAllExistingRoomsShouldEmptyTheRepo() {
		//When
		roomService.deleteAll();

		//then
		assertTrue(roomService.findRooms().isEmpty());
	}

	@Test
	public void updateRoomShouldWorkIfProperRoomPassed() {
		Set<AbstractCommodity> updatedCommodities = new HashSet<>(Arrays.asList(new Bed(KING_SIZE), new Toilet()));
		Room updatedRoom = new Room(kingSizeRoom.getRoomId(), updatedCommodities);

		// when
		Room resultRoom = roomService.updateRoom(updatedRoom);

		//then
		assertEquals(updatedRoom.getCommodities(), resultRoom.getCommodities());
	}

	@Test
	public void updateRoomUnsuccessfully() {
		//Given
		Set<AbstractCommodity> validCommoditySet = new HashSet<>(Arrays.asList(new Bed(KING_SIZE)));
		Set<AbstractCommodity> emptyCommodities = new HashSet<>();
		Set<AbstractCommodity> nullCommoditySet = new HashSet<>(Arrays.asList(new Bed(SINGLE), null));

		//Then

		//id is invalid
		assertThrows(ItemNotFoundException.class, () -> roomService.updateRoom(
			new Room(roomService.findRooms().size() + 2, validCommoditySet)));
		//room is null
		assertThrows(ArgumentNotValidException.class, () -> roomService.updateRoom(null));
		//room with no commodities
		assertThrows(FailedInitializationException.class, () -> roomService.updateRoom(new Room(2, emptyCommodities)));
		//room with null commodity
		assertThrows(ArgumentNotValidException.class, () -> roomService.updateRoom(new Room(2, nullCommoditySet)));
	}

	@Test
	public void saveRoomsShouldWorkIfPassedProperVarargs() {
		//Given
		roomService.deleteAll();

		//When
		roomService.saveRooms(singleRoom, kingSizeRoom);

		//Then
		assertTrue(roomService.findRooms().contains(singleRoom));
		assertTrue(roomService.findRooms().contains(kingSizeRoom));
		assertEquals(2, roomService.findRooms().size());

	}

	@Test
	public void saveRoomsShouldFailIfVarargsNotProper() {
		//Given
		roomService.deleteAll();

		//When

		//Then
		assertThrows(FailedInitializationException.class, () -> roomService.saveRooms(new Room(2, null)));
		assertThrows(FailedInitializationException.class, () -> roomService.saveRooms(new Room(2, new HashSet<>())));
		assertThrows(ArgumentNotValidException.class, () -> roomService.saveRooms(kingSizeRoom, null));
	}

	@Test
	public void convertRoomDtoToRoomShouldWorkIfProperDtoIsPassed() {
		//Given
		Set<AbstractCommodityDTO> commodityDTOSet = new HashSet<>(Arrays.asList(new BedDTO(KING_SIZE)));
		RoomDTO roomDTO = new RoomDTO(1, commodityDTOSet);

		//When
		Room convertedRoom = roomService.convertRoomDtoToRoom(roomDTO);
		Room expectedRoom = new Room(1, new HashSet<>(Arrays.asList(new Bed(KING_SIZE))));

		//Then
		assertEquals(1, convertedRoom.getCommodities().size());
		assertEquals(expectedRoom.getRoomCapacity(), convertedRoom.getRoomCapacity());
	}

	@Test
	public void convertRoomDtoToRoomShouldThrowExceptionIfDtoNullOrInvalidCommodities() {
		assertThrows(ArgumentNotValidException.class, () -> roomService.convertRoomDtoToRoom(null));
	}

	@Test
	public void convertMultipleRoomDtoToRoomListShouldWorkWithAProperList() {
		//Given
		Set<AbstractCommodityDTO> commodityDTOSetFirstRoom = new HashSet<>(Arrays.asList(new BedDTO(SINGLE)));
		RoomDTO firstRoomDTO = new RoomDTO(1, commodityDTOSetFirstRoom);

		Set<AbstractCommodityDTO> commodityDTOSetSecondRoom = new HashSet<>(Arrays.asList(new BedDTO(DOUBLE)));
		RoomDTO secondRoomDTO = new RoomDTO(2, commodityDTOSetSecondRoom);

		List<RoomDTO> roomDTOS = new ArrayList<>(Arrays.asList(firstRoomDTO, secondRoomDTO));

		//When
		List<Room> convertedRooms = roomService.convertMultipleRoomDtoToRoomList(roomDTOS);
		Room firstExpectedRoom = new Room(1, new HashSet<>(Arrays.asList(new Bed(SINGLE))));
		Room secondExpectedRoom = new Room(2, new HashSet<>(Arrays.asList(new Bed(DOUBLE))));

		//Then
		assertEquals(2, convertedRooms.size());
		assertEquals(firstExpectedRoom.getRoomCapacity(), convertedRooms.get(0).getRoomCapacity());
		assertEquals(secondExpectedRoom.getRoomCapacity(), convertedRooms.get(1).getRoomCapacity());
	}

	@Test
	public void convertMultipleRoomDtoToRoomListShouldThrowExceptionIfArgIsNullOrEmpty() {
		assertThrows(ArgumentNotValidException.class, () -> roomService.convertMultipleRoomDtoToRoomList(null));
		assertThrows(ArgumentNotValidException.class, () -> roomService.convertMultipleRoomDtoToRoomList(new ArrayList<>()));
	}
}
