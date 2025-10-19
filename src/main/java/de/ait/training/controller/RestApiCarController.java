package de.ait.training.controller;

import de.ait.training.model.Car;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Cars", description = "Operation on cars")
@RestController
@RequestMapping("/api/cars")
public class RestApiCarController {

    Car carOne = new Car(1, "black", "BMW x5", 25000);
    Car carTwo = new Car(2, "green", "Audi A4", 15000);
    Car carThree = new Car(3, "white", "MB A220", 18000);
    Car carFour = new Car(4, "red", "Ferrari", 250000);

    List<Car> cars = new ArrayList<>();

    public RestApiCarController() {
        cars.add(carOne);
        cars.add(carTwo);
        cars.add(carThree);
        cars.add(carFour);
    }

    /**
     * GET /api/cars
     *
     * @return возвращает список всех автомобилей
     */

    @Operation(
            summary = "Show cars",
            description = "Show list of the cars "

    )
    @GetMapping
    Iterable<Car> getCars() {
        return cars;
    }

    /**
     * создает новый авто и добавляет его в лист
     *
     * @param car
     * @return созданный авто
     */

    @Operation(
            summary = "Create car",
            description = "Create a new car ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Created")
            }

    )


    @PostMapping
    Car postCar(@RequestBody Car car) {
        if (car.getId() < 0) {
            log.error("Car id must be greater than zero");
            Car errorCar = new Car(9999, "000", "000", 9999);
            return errorCar;
        }
        cars.add(car);
        return car;
    }

    /**
     * Замена существующего автомобиля, если id не найден то создаем новый
     *
     * @param id
     * @param car
     * @return созданный или найденный автомобиль
     */

    @Operation(
            summary = "Change car",
            description = "Change existing car by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "car with id found and changed")

            }

    )
    @PutMapping("/{id}")
    ResponseEntity<Car> putCar(@PathVariable long id, @RequestBody Car car) {
        int carIndex = -1;
        for (Car carInList : cars) {
            if (carInList.getId() == id) {
                carIndex = cars.indexOf(carInList);
                cars.set(carIndex, car);
                log.info("Car id " + carInList.getId() + " has been updated");
            }
        }

        return (carIndex == -1)
                ? new ResponseEntity<>(postCar(car), HttpStatus.CREATED)
                : new ResponseEntity<>(car, HttpStatus.OK);
    }

    /**
     * удаляем автомобиль по id
     *
     * @param id
     */
    @Operation(
            summary = "Delete car",
            description = "Delete existing car by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "car with id found and deleted")

            }
    )

    @DeleteMapping("/{id}")
    void deleteCar(@PathVariable long id) {
        log.info("Delete car with id {}", id);
        cars.removeIf(car -> car.getId() == id);
    }

    /**
     * GET /api/cars/color/{color}
     * Возвращает список всех автомобилей заданного цвета
     *
     */
    @Operation(
            summary = "Show list of cars by color",
            description = "Returns a list of cars filtered by color",
            responses = {
        @ApiResponse(responseCode = "200", description = "cars found ")

    }
    )


    @GetMapping("/color/{color}")
    ResponseEntity<List<Car>> getCarsByColor(@PathVariable String color){
        List<Car> listCarsByColor = cars.stream().
                filter(car -> car.getColor().equalsIgnoreCase(color))
                .collect(Collectors.toList());
        if (listCarsByColor.isEmpty()) {
            log.warn("No cars found with color {}", color);
        }

        return (!listCarsByColor.isEmpty())
                ? new ResponseEntity<>(listCarsByColor, HttpStatus.OK)
                : new ResponseEntity<>(listCarsByColor, HttpStatus.NOT_FOUND);

}

}



