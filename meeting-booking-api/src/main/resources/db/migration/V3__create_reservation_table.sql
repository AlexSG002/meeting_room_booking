CREATE TABLE reservation(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(120) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    employee_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,

    CONSTRAINT fk_reservation_employee
                        FOREIGN KEY (employee_id)
                        REFERENCES employee(id),

    CONSTRAINT fk_reservation_room
                        FOREIGN KEY (room_id)
                        REFERENCES room(id)
);

CREATE INDEX idx_reservation_room_start
    ON reservation(room_id, start_time);