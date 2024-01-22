package com.CarServieStation.backend.service;

import com.CarServieStation.backend.dto.CarRequestDto;
import com.CarServieStation.backend.dto.CarResponseDto;
import com.CarServieStation.backend.dto.ClientRequestDto;
import com.CarServieStation.backend.dto.ClientResponseDto;
import com.CarServieStation.backend.entity.Car;
import com.CarServieStation.backend.entity.Client;
import com.CarServieStation.backend.exception.AlreadyExistsException;
import com.CarServieStation.backend.exception.NotFoundException;
import com.CarServieStation.backend.repository.CarRepository;
import com.CarServieStation.backend.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CarRepository carRepository;

    @Transactional
    public ClientResponseDto createClientWithCars(ClientRequestDto request) {
        Client client = new Client();
        client.setFirstname(request.getFirstname());
        client.setLastname(request.getLastname());
        client.setEmail(request.getEmail());
        client.setPhoneNumber(request.getPhoneNumber());

        // Check if the request contains cars
        if (request.getCars() != null && !request.getCars().isEmpty()) {
            List<Car> cars = new ArrayList<>();

            for (CarRequestDto carDto : request.getCars()) {
                // Check if a car with the given licence number already exists
                Optional<Car> existingCar = carRepository.findByLicenceNumber(carDto.getLicenceNumber());
                if (existingCar.isPresent()) {
                    throw new AlreadyExistsException("A car with licence number " + carDto.getLicenceNumber() + " already exists.");
                }

                Car newCar = new Car();
                newCar.setModel(carDto.getModel());
                newCar.setMake(carDto.getMake());
                newCar.setLicenceNumber(carDto.getLicenceNumber());
                newCar.setColor(carDto.getColorType());
                newCar.setClient(client);
                cars.add(newCar);
            }

            client.setCars(cars);
        }

        Client savedClient = clientRepository.save(client);
        return convertToDto(savedClient);
    }

    public ClientResponseDto getClient(Integer id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
        return convertToDto(client);
    }

    public List<ClientResponseDto> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Car> getCarsByClientId(Integer clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + clientId));

        return client.getCars();
    }

    @Transactional
    public void deleteClient(Integer id) {
        clientRepository.deleteById(id);
    }

    private ClientResponseDto convertToDto(Client client) {
        List<CarResponseDto> carDtos = client.getCars().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new ClientResponseDto(
                client.getId(),
                client.getFirstname(),
                client.getLastname(),
                client.getEmail(),
                client.getPhoneNumber(),
                carDtos
        );
    }

    private CarResponseDto convertToDto(Car car) {
        return new CarResponseDto(
                car.getId(),
                car.getModel(),
                car.getMake(),
                car.getLicenceNumber(),
                car.getColor()
        );
    }
}