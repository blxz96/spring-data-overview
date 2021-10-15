package com.pluralsight.springdataoverview;

import com.pluralsight.springdataoverview.entity.Flight;
import com.pluralsight.springdataoverview.repository.FlightRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@DataMongoTest
class CrudTests {

    @Autowired
    private FlightRepository flightRepository;

    @BeforeEach
    public void setUp(){
        flightRepository.deleteAll();
    }

    @AfterEach
    public void tearDown(){
        flightRepository.deleteAll();
    }

    @Test
    void shouldPerformCRUDOperations(){
        final Flight flight = new Flight();
        flight.setOrigin("London");
        flight.setDestination("New York");
        flight.setScheduledAt(LocalDateTime.parse("2021-10-10T12:00:00"));

        flightRepository.save(flight);

        Assertions.assertThat(flightRepository.findAll())
                .hasSize(1)
                .first()
                .isEqualToComparingFieldByField(flight);

        flightRepository.deleteById(flight.getId());

        Assertions.assertThat(flightRepository.count()).isZero();
    }

}
