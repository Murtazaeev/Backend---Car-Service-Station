package com.CarServieStation.backend.repository;

import com.CarServieStation.backend.entity.CustomerOrder;
import com.CarServieStation.backend.entity.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<CustomerOrder, Integer> {
    boolean existsByLicenceNumber(String licenceNumber);

    @Query("SELECT o FROM CustomerOrder o WHERE o.savedDate >= :startDate AND o.savedDate < :endDate")
    List<CustomerOrder> findAllBySavedDateBetween(@Param("startDate") Date start, @Param("endDate") Date end);


    @Query("SELECT SUM(o.cost) FROM CustomerOrder o WHERE o.savedDate >= :startDate AND o.savedDate <= :endDate")
    double calculateTotalCostBetweenDates(Date startDate, Date endDate);

    List<CustomerOrder> findAllByState(OrderState state);

    @Query("SELECT o FROM CustomerOrder o WHERE o.client.firstname LIKE %:name% OR o.client.lastname LIKE %:name%")
    List<CustomerOrder> findByClientNameOrSurname(@Param("name") String name);

    @Query("SELECT o.station.id, COUNT(o) FROM CustomerOrder o GROUP BY o.station.id")
    List<Object[]> countOrdersByStation();

    @Query("SELECT FUNCTION('DATE_FORMAT', o.savedDate, '%Y-%m') AS monthYear, COUNT(o) AS count FROM CustomerOrder o WHERE o.savedDate >= :startDate AND o.savedDate <= :endDate GROUP BY monthYear ORDER BY monthYear ASC")
    List<Object[]> findMonthlyOrderCounts(@Param("startDate") Date startDate, @Param("endDate") Date endDate);


}