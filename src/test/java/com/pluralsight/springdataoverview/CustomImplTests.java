package com.pluralsight.springdataoverview;
import com.pluralsight.springdataoverview.entity.Flight;
import com.pluralsight.springdataoverview.repository.FlightRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@DataMongoTest
public class CustomImplTests {
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
    void shouldSaveCustomImpl(){
        final Flight toDelete = createFlight("London");
        final Flight toKeep = createFlight("Paris");

        flightRepository.save(toDelete);
        flightRepository.save(toKeep);

        flightRepository.deleteByOrigin("London");

        Assertions.assertThat(flightRepository.findAll())
                .hasSize(1)
                .first()
                .isEqualToComparingFieldByField(toKeep);
    }

    private Flight createFlight(String origin) {
        final Flight flight = new Flight();
        flight.setOrigin(origin);
        flight.setDestination("Tokyo");
        flight.setScheduledAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        return flight;
    }

}