package com.pluralsight.springdataoverview;

import com.pluralsight.springdataoverview.entity.Flight;
import com.pluralsight.springdataoverview.repository.FlightRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;

@DataMongoTest
class PagingAndSortingTests {
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
    void shouldSortFlightsByDestination(){
        final Flight flight1 = createFlight("Madrid");
        final Flight flight2 = createFlight("London");
        final Flight flight3 = createFlight("Saudi Arabia");

        flightRepository.save(flight1);
        flightRepository.save(flight2);
        flightRepository.save(flight3);

        final Iterable<Flight> flights = flightRepository.findAll(Sort.by("destination"));

        Assertions.assertThat(flights).hasSize(3);

        final Iterator<Flight> iterator = flights.iterator();

        Assertions.assertThat(iterator.next().getDestination()).isEqualTo("London");
        Assertions.assertThat(iterator.next().getDestination()).isEqualTo("Madrid");
        Assertions.assertThat(iterator.next().getDestination()).isEqualTo("Saudi Arabia");
    }

    @Test
    void shouldSortFlightsByScheduledAndThenName(){
        final LocalDateTime now = LocalDateTime.now();
        final Flight paris1 = createFlight("Paris",now);
        final Flight paris2 = createFlight("Paris",now.plusHours(2));
        final Flight paris3 = createFlight("Paris",now.minusHours(1));
        final Flight london1 = createFlight("London", now.plusHours(1));
        final Flight london2 = createFlight("London", now);

        flightRepository.save(paris1);
        flightRepository.save(paris2);
        flightRepository.save(paris3);
        flightRepository.save(london1);
        flightRepository.save(london2);

        final Iterable<Flight> flights = flightRepository.findAll(Sort.by("scheduledAt", "destination"));

        Assertions.assertThat(flights).hasSize(5);

        final Iterator<Flight> iterator = flights.iterator();

        Assertions.assertThat(iterator.next()).isEqualToComparingFieldByField(paris3);
        Assertions.assertThat(iterator.next()).isEqualToComparingFieldByField(london2);
        Assertions.assertThat(iterator.next()).isEqualToComparingFieldByField(paris1);
        Assertions.assertThat(iterator.next()).isEqualToComparingFieldByField(london1);
        Assertions.assertThat(iterator.next()).isEqualToComparingFieldByField(paris2);

    }

    @Test
    void shouldPageResults(){
        for(int i=0; i<50;i++){
            flightRepository.save(createFlight(String.valueOf(i)));
        }

        final Page<Flight> page = flightRepository.findAll(PageRequest.of(2, 5));

        Assertions.assertThat(page.getTotalElements()).isEqualTo(50);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(5);
        Assertions.assertThat(page.getTotalPages()).isEqualTo(10);
        Assertions.assertThat(page.getContent())
                .extracting(Flight::getDestination)
                .containsExactly("10","11","12","13","14");

    }

    @Test
    void shouldPageAndSortResults(){
        for(int i=0; i<50;i++){
            flightRepository.save(createFlight(String.valueOf(i)));
        }

        final Page<Flight> page = flightRepository.findAll(PageRequest.of(2, 5, Sort.by(Sort.Direction.DESC,"destination")));

        Assertions.assertThat(page.getTotalElements()).isEqualTo(50);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(5);
        Assertions.assertThat(page.getTotalPages()).isEqualTo(10);
        Assertions.assertThat(page.getContent())
                .extracting(Flight::getDestination)
                .containsExactly("44","43","42","41","40");

    }

    @Test
    void shouldPageAndSortADerivedQuery(){
        for(int i=0; i<10;i++){
            final Flight flight = createFlight(String.valueOf(i));
            flight.setOrigin("Paris");
            flightRepository.save(flight);
        }

        for(int i=0; i<10;i++){
            final Flight flight = createFlight(String.valueOf(i));
            flight.setOrigin("London");
            flightRepository.save(flight);
        }

        final Page<Flight> page = flightRepository.
                findByOrigin("London",
                        PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC,"destination")));

        Assertions.assertThat(page.getTotalElements()).isEqualTo(10);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(5);
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.getContent())
                .extracting(Flight::getDestination)
                .containsExactly("9","8","7","6","5");

    }


    private Flight createFlight(String destination, LocalDateTime scheduledAt) {
        Flight flight = new Flight();
        flight.setDestination(destination);
        flight.setOrigin("London");
        flight.setScheduledAt(scheduledAt.truncatedTo(ChronoUnit.MINUTES));
        return flight;
    }

    private Flight createFlight(String destination) {
        Flight flight = new Flight();
        flight.setDestination(destination);
        flight.setOrigin("London");
        flight.setScheduledAt(LocalDateTime.parse("2021-10-10T12:00:00"));
        return flight;
    }


}
