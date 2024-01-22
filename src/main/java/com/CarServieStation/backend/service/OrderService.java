package com.CarServieStation.backend.service;


import com.CarServieStation.backend.dto.*;
import com.CarServieStation.backend.entity.*;
import com.CarServieStation.backend.exception.NotFoundException;
import com.CarServieStation.backend.exception.WrongInputException;
import com.CarServieStation.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final Random random = new Random();
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        Client client = clientRepository.findById(orderRequestDto.getClientId())
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + orderRequestDto.getClientId()));

        Station station = stationRepository.findById(orderRequestDto.getStationId())
                .orElseThrow(() -> new NotFoundException("Station not found with id: " + orderRequestDto.getStationId()));

        if (station.getUser() == null) {
            throw new WrongInputException("Station with id: " + station.getId() + " does not have a manager assigned.");
        }

        Car car = carRepository.findByLicenceNumber(orderRequestDto.getLicenceNumber())
                .orElseThrow(() -> new WrongInputException("No car found with licence number: " + orderRequestDto.getLicenceNumber() + " for " + client.getFirstname()));

        // Additional validation for order state
        if (orderRequestDto.getState() == OrderState.COMPLETED) {
            throw new WrongInputException("New order cannot be in a 'Completed' state.");
        }

        if (!car.getClient().getId().equals(client.getId())) {
            throw new WrongInputException("The car with licence number " + orderRequestDto.getLicenceNumber() + " does not belong to the client with id: " + client.getId());
        }

        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setClient(client);
        customerOrder.setStation(station);
        customerOrder.setLicenceNumber(car.getLicenceNumber());
        customerOrder.setServiceType(orderRequestDto.getServiceType());
        customerOrder.setState(orderRequestDto.getState());
        customerOrder.setCost(orderRequestDto.getCost());
        customerOrder.setSavedDate(new Date()); // Set current date
        customerOrder.setRegisterNumber(generateRegisterNumber()); // Generate unique register number

        CustomerOrder newCustomerOrder = orderRepository.save(customerOrder);

        // Increment totalOrders for the manager
        User manager = station.getUser();
        manager.setTotalOrders(manager.getTotalOrders() + 1);
        userRepository.save(manager);

        return convertToOrderResponseDto(newCustomerOrder);
    }


    @Transactional
    public OrderResponseDto updateOrder(Integer orderId, OrderRequestDto orderRequestDto) {
        CustomerOrder customerOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

        // Check if client ID is provided and valid
        if (orderRequestDto.getClientId() != null) {
            Client client = clientRepository.findById(orderRequestDto.getClientId())
                    .orElseThrow(() -> new NotFoundException("Client not found with id: " + orderRequestDto.getClientId()));
            customerOrder.setClient(client);
        }

        // Check if station ID is provided and valid
        if (orderRequestDto.getStationId() != null) {
            Station station = stationRepository.findById(orderRequestDto.getStationId())
                    .orElseThrow(() -> new NotFoundException("Station not found with id: " + orderRequestDto.getStationId()));

            // Check if the station has a manager assigned
            if (station.getUser() == null) {
                throw new WrongInputException("Station with id: " + station.getId() + " does not have a manager assigned.");
            }

            customerOrder.setStation(station);
        }

        // Check if licence number is provided, valid, and belongs to the client
        if (orderRequestDto.getLicenceNumber() != null && !orderRequestDto.getLicenceNumber().isEmpty()) {
            Car car = carRepository.findByLicenceNumber(orderRequestDto.getLicenceNumber())
                    .orElseThrow(() -> new WrongInputException("No car found with licence number: " + orderRequestDto.getLicenceNumber() + " for " + customerOrder.getClient().getFirstname()));

            if (!car.getClient().getId().equals(orderRequestDto.getClientId())) {
                throw new WrongInputException("The car with licence number " + orderRequestDto.getLicenceNumber() + " does not belong to the client with id: " + orderRequestDto.getClientId());
            }

            customerOrder.setLicenceNumber(car.getLicenceNumber());
        }

        // Check state transition validity
        if (!isValidTransition(customerOrder.getState(), orderRequestDto.getState())) {
            throw new WrongInputException("Invalid state transition from " + customerOrder.getState() + " to " + orderRequestDto.getState());
        }

        // Update other order fields
        customerOrder.setState(orderRequestDto.getState());
        customerOrder.setServiceType(orderRequestDto.getServiceType());
        customerOrder.setCost(orderRequestDto.getCost());

        CustomerOrder updatedCustomerOrder = orderRepository.save(customerOrder);
        return convertToOrderResponseDto(updatedCustomerOrder);
    }

    public OrderResponseDto getOrder(Integer orderId) {
        CustomerOrder customerOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));
        return convertToOrderResponseDto(customerOrder);
    }

    public List<OrderResponseDto> getAllOrders() {
        List<CustomerOrder> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToOrderResponseDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public void deleteOrder(Integer orderId) {
        CustomerOrder customerOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

        // Disconnect the order from the client and station
        customerOrder.setClient(null);
        customerOrder.setStation(null);

        orderRepository.delete(customerOrder);
    }

    private boolean isValidTransition(OrderState currentState, OrderState newState) {
        if (currentState == OrderState.NOT_STARTED && newState == OrderState.MAINTAINING) {
            return true;
        } else if (currentState == OrderState.MAINTAINING && newState == OrderState.COMPLETED) {
            return true;
        }
        return false; // Invalid transition
    }


    private String generateRegisterNumber() {
        return "RD-" + random.ints(10, 0, 10)
                .mapToObj(Integer::toString)
                .reduce("", String::concat);
    }


    private OrderResponseDto convertToOrderResponseDto(CustomerOrder customerOrder) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(customerOrder.getId());
        dto.setRegisterNumber(customerOrder.getRegisterNumber());
        dto.setSavedDate(customerOrder.getSavedDate());
        dto.setServiceType(customerOrder.getServiceType());
        dto.setCost(customerOrder.getCost());
        dto.setLicenceNumber(customerOrder.getLicenceNumber());
        dto.setState(customerOrder.getState());

        // Setting the client details
        Client client = customerOrder.getClient();
        if (client != null) {
            OrderClientResponseDto clientDto = new OrderClientResponseDto(
                    client.getId(),
                    client.getFirstname(),
                    client.getLastname(),
                    client.getEmail(),
                    client.getPhoneNumber()
            );
            dto.setClient(clientDto);
        }

        // Setting the station details
        Station station = customerOrder.getStation();
        if (station != null) {
            OrderStationResponseDto stationDto = new OrderStationResponseDto(
                    station.getId(),
                    station.getStationName(),
                    station.getColorType()
            );
            dto.setStation(stationDto);

            // Setting the manager details
            User manager = station.getUser();
            if (manager != null) {
                OrderManagerResponseDto managerDto = new OrderManagerResponseDto(
                        manager.getId(),
                        manager.getFirstname(),
                        manager.getLastname(),
                        manager.getEmail(),
                        manager.getRole().toString(),
                        manager.getPhoneNumber(),
                        manager.getBirthDate()
                );
                dto.setManager(managerDto);
            }
        }

        return dto;
    }
}