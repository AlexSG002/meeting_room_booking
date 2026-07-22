package com.alejandro.meetingbooking.service;

import com.alejandro.meetingbooking.dto.response.RoomResponse;

import java.util.List;

public interface RoomService {
    List<RoomResponse> findAll();
}
