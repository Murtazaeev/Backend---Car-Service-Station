package com.CarServieStation.backend.repository;

import com.CarServieStation.backend.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<CustomerOrder, Integer> {
    boolean existsByLicenceNumber(String licenceNumber);

    @Query("SELECT o FROM CustomerOrder o JOIN FETCH o.client c JOIN FETCH o.station s WHERE o.id = :orderId")
    Optional<CustomerOrder> findByIdWithDetails(@Param("orderId") Integer orderId);


    @Query("SELECT o FROM CustomerOrder o WHERE o.savedDate >= :startDate AND o.savedDate < :endDate")
    List<CustomerOrder> findAllBySavedDateBetween(@Param("startDate") Date start, @Param("endDate") Date end);


    @Query("SELECT SUM(o.cost) FROM CustomerOrder o WHERE o.savedDate >= :startDate AND o.savedDate <= :endDate")
    double calculateTotalCostBetweenDates(Date startDate, Date endDate);

}