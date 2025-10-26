package de.ait.training.controller;
import de.ait.training.model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RestApiCarControllerIT {


    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    @DisplayName("price between 10000 and 30000, 3 cars were found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testReturnCarsWithPriceBetween10000And30000() throws Exception {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/between/10000/30000"),
                Car[].class);
        //assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(3);
        assertThat(cars.get(0).getModel()).isEqualTo("BMW x5");

    }

    @Test
    @DisplayName("price under 16000, 1 car was found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testPriceUnder16000Success() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/under/16000"),
                Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(1);
        assertThat(cars.get(0).getModel()).isEqualTo("Audi A4");
    }

    @Test
    @DisplayName("wrong min and max price, 0 cars ware found, status BadRequest")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testMinMaxPricesWrongFail() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/between/30000/10000"),
                Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Car[] result = response.getBody();
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isEqualTo(true);

    }


    //------------------------------------------HW_06-------------------------------------------

    @Test
    @DisplayName("Return  list with 4 cars")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testReturnListWith4Cars() {
        ResponseEntity<Car[]> response= restTemplate.getForEntity(url("/api/cars"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(4);

    }

    @Test
    @DisplayName("Return  empty list with  zero cars")
    @Sql(scripts = {"classpath:sql/clear.sql"})
    void testReturnEmptyListCars() {
        ResponseEntity<Car[]> response= restTemplate.getForEntity(url("/api/cars"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Car[] cars = response.getBody();
        assertThat(cars.length).isZero();

    }

    @Test
    @DisplayName("color red, 1 car was found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testReturnWithRedColorCars() {
        ResponseEntity<Car[]> response= restTemplate.getForEntity(url("/api/cars/color/red"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(1);
        assertThat(cars.get(0).getModel()).isEqualTo("Ferrari");

    }

    @Test
    @DisplayName("color reD!!!, 1 car was found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testReturnWithReDColorCars() {
        ResponseEntity<Car[]> response= restTemplate.getForEntity(url("/api/cars/color/reD"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(1);
        assertThat(cars.get(0).getModel()).isEqualTo("Ferrari");

    }



    @Test
    @DisplayName("color red, zero car was found, status 404 Not Found")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testRenurnByRedColorCarsFail() {
        ResponseEntity<Car[]> response= restTemplate.getForEntity(url("/api/cars/color/re"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isEqualTo(true);

    }

    @Test
    @DisplayName("price between 100 and 500, zero cars were found, status Not Found")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testReturnCarsWithPriceBetween100And500Fail() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/between/100/500"),
                Car[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isEqualTo(true);
    }

        @Test
        @DisplayName("Return cars with prices between  min  and  max and equels min and max prises, 4 cars were found, status OK")
        @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
        void testReturnCarsWithPriceBetweenMinEquelsAndMaxEquels()  {
            ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/between/15000/250000"),
                    Car[].class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            List<Car> cars = Arrays.asList(response.getBody());
            assertThat(cars.size()).isEqualTo(4);


        }

    @Test
    @DisplayName("Return cars with prices under 20000, 2 cars ware found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testReturnCarsWithPriceUnder20000Success() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/under/20000"),
                Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(2);
        assertThat(cars.get(0).getModel()).isEqualTo("Audi A4");
        assertThat(cars.get(1).getModel()).isEqualTo("MB A220");
    }

    @Test
    @DisplayName("Return zero cars with prices under 10000, 0 cars ware found, status Not Found")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testReturnZeroCarsWithPriceUnder10000Success() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/under/10000"),
                Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("Return cars with prices over and equels 25000, 2 cars ware found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testReturnCarsWithPriceOverAndEquels25000Success() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/over/25000"),
                Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(2);
        assertThat(cars.get(0).getModel()).isEqualTo("BMW x5");
        assertThat(cars.get(1).getModel()).isEqualTo("Ferrari");
    }

    @Test
    @DisplayName("Return zero cars with prices over and equels 1000000, 0 cars ware found, status Not Found")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testReturnZeroCarsWithPriceOverAndEquels1000000Fail() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/over/1000000"),
                Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isEqualTo(true);
    }


}