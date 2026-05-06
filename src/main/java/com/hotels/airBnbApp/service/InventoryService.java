package com.hotels.airBnbApp.service;

import com.hotels.airBnbApp.entity.Room;

public interface InventoryService {

    void initialRoomForAYear(Room room);

    void deleteFutureInventories(Room room);
}
