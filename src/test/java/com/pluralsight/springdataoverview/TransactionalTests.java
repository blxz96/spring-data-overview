package com.pluralsight.springdataoverview;

import com.pluralsight.springdataoverview.entity.Flight;
import com.pluralsight.springdataoverview.repository.FlightRepository;
import com.pluralsight.springdataoverview.service.FlightsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TransactionalTests {
    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightsService flightsService;

    @BeforeEach
    public void setUp(){
        flightRepository.deleteAll();
    }

    @AfterEach
    public void tearDown(){
        flightRepository.deleteAll();
    }

    @Test
    public void shouldNotRollBackWhenThereIsNoTransaction(){
        try {
            flightsService.saveFlight(new Flight());
        } catch (Exception e){

        }
        finally {
            Assertions.assertThat(flightRepository.findAll())
                    .isNotEmpty();
        }
    }

    @Test
    public void shouldNotRollBackWhenThereIsATransaction(){
        try {
            flightsService.saveFlightTransaction(new Flight());
        } catch (Exception e){

        }
        finally {
            Assertions.assertThat(flightRepository.findAll())
                    .isNotEmpty();
        }

    }
}
