package com.alejandro.meetingbooking.mapper;

import com.alejandro.meetingbooking.dto.response.RoomResponse;
import com.alejandro.meetingbooking.entity.Room;

public final class RoomMapper {
    private RoomMapper(){

    }

    public static RoomResponse toResponse(Room room){
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .capacity(room.getCapacity())
                .location(room.getLocation())
                .build();
    }
}
