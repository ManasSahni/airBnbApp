package com.hotels.airBnbApp.service;

import com.hotels.airBnbApp.dto.HotelDto;
import com.hotels.airBnbApp.entity.Hotel;
import com.hotels.airBnbApp.entity.Room;
import com.hotels.airBnbApp.exceptions.ResourceNotFoundException;
import com.hotels.airBnbApp.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new hotel with name : {}", hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        hotel = hotelRepository.save(hotel);
        log.info("Created a new hotel with id : {}", hotelDto.getId());
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(("Hotel with id: {} not found!" + id)));
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(("Hotel with id: {} not found!" + id)));
        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public Boolean deleteHotelById(Long id) {
        Boolean exists = hotelRepository.existsById(id);
        if(!exists) throw new ResourceNotFoundException("Hotel not found with ID: " + id);

        hotelRepository.deleteById(id);
        return true;
    }

    @Override
    public void activateHotel(Long id) {
        log.info("Activating the hotel with ID : {}", id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(("Hotel with id: {} not found!" + id)));

        hotel.setActive(true);

        for(Room room: hotel.getRooms()) {
            inventoryService.initialRoomForAYear(room);
        }
    }
}
