package com.alejandro.meetingbooking.service.impl;

import com.alejandro.meetingbooking.dto.response.RoomResponse;
import com.alejandro.meetingbooking.entity.Room;
import com.alejandro.meetingbooking.mapper.RoomMapper;
import com.alejandro.meetingbooking.repository.RoomRepository;
import com.alejandro.meetingbooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepo;

    @Override
    public List<RoomResponse> findAll() {
        List<Room> rooms = roomRepo.findAll();
        return rooms.stream().map(RoomMapper::toResponse).toList();
    }
}
