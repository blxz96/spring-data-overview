package com.pluralsight.springdataoverview;

import com.pluralsight.springdataoverview.entity.Flight;
import com.pluralsight.springdataoverview.repository.FlightRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DerivedQueryTests {
    @Autowired
    private FlightRepository flightRepository;

    @Before
    public void setUp(){
        flightRepository.deleteAll();
    }

    @Test
    public void shouldFindFlightsFromLondon(){
        final Flight flight1 = createFlight("London");
        final Flight flight2 = createFlight("London");
        final Flight flight3 = createFlight("New York");

        flightRepository.save(flight1);
        flightRepository.save(flight2);
        flightRepository.save(flight3);

        List<Flight> flightsFromLondon = flightRepository.findByOrigin("London");

        Assertions.assertThat(flightsFromLondon).hasSize(2);
        Assertions.assertThat(flightsFromLondon.get(0)).isEqualTo(flight1);
        Assertions.assertThat(flightsFromLondon.get(1)).isEqualTo(flight2);
    }

    @Test
    public void shouldFindFlightsToLondon(){
        final Flight flight1 = createFlight("USA", "London");
        final Flight flight2 = createFlight("Singapore", "New York");
        final Flight flight3 = createFlight("New York", "London");

        flightRepository.save(flight1);
        flightRepository.save(flight2);
        flightRepository.save(flight3);

        List<Flight> flightsToLondon = flightRepository.findByDestination("London");

        Assertions.assertThat(flightsToLondon).hasSize(2);
        Assertions.assertThat(flightsToLondon.get(0)).isEqualTo(flight1);
        Assertions.assertThat(flightsToLondon.get(1)).isEqualTo(flight3);
    }

    @Test
    public void shouldFindFlightsFromLondonToParis(){
        final Flight flight1 = createFlight("London", "New York");
        final Flight flight2 = createFlight("London", "Paris");
        final Flight flight3 = createFlight("Madrid", "New York");

        flightRepository.save(flight1);
        flightRepository.save(flight2);
        flightRepository.save(flight3);

        final List<Flight> londonToParis = flightRepository.findByOriginAndDestination("London", "Paris");

        Assertions.assertThat(londonToParis).hasSize(1);
        Assertions.assertThat(londonToParis.get(0)).isEqualTo(flight2);

    }

    @Test
    public void shouldFindFlightsFromLondonOrParis(){
        final Flight flight1 = createFlight("London", "New York");
        final Flight flight2 = createFlight("Tokyo", "London");
        final Flight flight3 = createFlight("Madrid", "New York");

        flightRepository.save(flight1);
        flightRepository.save(flight2);
        flightRepository.save(flight3);

        final List<Flight> londonOrMadrid = flightRepository.findByOriginIn("London", "Madrid");

        Assertions.assertThat(londonOrMadrid).hasSize(2);
        Assertions.assertThat(londonOrMadrid.get(0)).isEqualTo(flight1);
        Assertions.assertThat(londonOrMadrid.get(1)).isEqualTo(flight3);

    }

    @Test
    public void shouldFindFlightsFromLondonIgnoreCase(){
        final Flight flight1 = createFlight("London");
        final Flight flight2 = createFlight("LONDON");
        final Flight flight3 = createFlight("New York");

        flightRepository.save(flight1);
        flightRepository.save(flight2);
        flightRepository.save(flight3);

        List<Flight> flightsFromLondon = flightRepository.findByOriginIgnoreCase("London");

        Assertions.assertThat(flightsFromLondon).hasSize(2);
        Assertions.assertThat(flightsFromLondon.get(0)).isEqualTo(flight1);
        Assertions.assertThat(flightsFromLondon.get(1)).isEqualTo(flight2);
    }



    private Flight createFlight(String origin, String destination) {
        final Flight flight = new Flight();
        flight.setOrigin(origin);
        flight.setDestination(destination);
        flight.setScheduledAt(LocalDateTime.parse("2021-10-10T12:00:00"));
        System.out.println(flight);
        return flight;
    }

    private Flight createFlight(String origin) {
        final Flight flight = new Flight();
        flight.setOrigin(origin);
        flight.setDestination("Madrid");
        flight.setScheduledAt(LocalDateTime.parse("2021-10-10T12:00:00"));
        System.out.println(flight);
        return flight;
    }

}
