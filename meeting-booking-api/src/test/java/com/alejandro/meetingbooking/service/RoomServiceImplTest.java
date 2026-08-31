package com.alejandro.meetingbooking.service;

import com.alejandro.meetingbooking.dto.response.RoomResponse;
import com.alejandro.meetingbooking.entity.Room;
import com.alejandro.meetingbooking.repository.RoomRepository;
import com.alejandro.meetingbooking.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepo;

    @InjectMocks
    private RoomServiceImpl roomService;

    @Test
    void shouldReturnAllRooms() {

        Room room = Room.builder()
                .id(1L)
                .name("Sala Prueba")
                .capacity(10)
                .build();

        when(roomRepo.findAll())
                .thenReturn(List.of(room));

        List<RoomResponse> response =
                roomService.findAll();

        assertEquals(1, response.size());
        assertEquals("Sala Prueba", response.getFirst().getName());

        verify(roomRepo).findAll();
    }
}
