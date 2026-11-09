package com.alejandro.meetingbooking.outbox;

import com.alejandro.meetingbooking.event.ReservationCreatedEvent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ReservationEventSender {

    void send(String topic, String messageKey, ReservationCreatedEvent event)
            throws InterruptedException, ExecutionException, TimeoutException;
}
