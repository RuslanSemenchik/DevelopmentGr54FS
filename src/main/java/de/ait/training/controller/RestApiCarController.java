package de.ait.training.controller;

import de.ait.training.model.Car;
import de.ait.training.repository.CarRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "Cars", description = "Operation on cars")
@RestController
@RequestMapping("/api/cars")
public class RestApiCarController {

    CarRepository carRepository;

    /**
     * Car carOne = new Car(1, "black", "BMW x5", 25000);
     * Car carTwo = new Car(2, "green", "Audi A4", 15000);
     * Car carThree = new Car(3, "white", "MB A220", 18000);
     * Car carFour = new Car(4, "red", "Ferrari", 250000);
     * <p>
     * List<Car> cars = new ArrayList<>();
     * <p>
     * public RestApiCarController() {
     * cars.add(carOne);
     * cars.add(carTwo);
     * cars.add(carThree);
     * cars.add(carFour);
     * }
     * <p>
     * /**
     * GET /api/cars
     *
     * @return возвращает список всех автомобилей
     */


    RestApiCarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Operation(
            summary = "Show cars",
            description = "Show list of the cars "

    )
    @GetMapping
    Iterable<Car> getCars() {
        return carRepository.findAll();
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

@ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    Car postCar(@RequestBody Car car) {
     //   if (car.getId() < 0) {
      //      log.error("Car id must be greater than zero");
       //     Car errorCar = new Car("000", "000", 9999);
       //     return errorCar;
       // }
        carRepository.save(car);
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
    ResponseEntity<Car> putCar(@PathVariable Long id, @RequestBody Car car) {

        Car foundCar = carRepository.findById(id).orElse(null);
        if (foundCar == null) {
            log.info("Car with id {} not found", id);
            return new ResponseEntity<>(postCar(car), HttpStatus.CREATED);
        } else {
            log.info("Car with id {} found and changed", id);
            carRepository.save(car);
           return new ResponseEntity<>(car, HttpStatus.OK);
        }

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
    void deleteCar(@PathVariable Long id) {
        log.info("Delete car with id {}", id);
        carRepository.deleteById(id);
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
                    @ApiResponse(responseCode = "200", description = "cars found "),
                    @ApiResponse(responseCode = "404", description = "cars not found ")

            }
    )

    @GetMapping("/color/{color}")
    public ResponseEntity<List<Car>> getCarsByColor(@PathVariable String color) {
        List<Car> listCarsByColor = carRepository.findCarByColorIgnoreCase(color);

        if (listCarsByColor.isEmpty()) {
            log.info("No cars found with color {}", color);
        }

        return (!listCarsByColor.isEmpty())
                ? new ResponseEntity<>(listCarsByColor, HttpStatus.OK)
                : new ResponseEntity<>(listCarsByColor, HttpStatus.NOT_FOUND);

    }

    @Operation(
            summary = "Show list of cars by  between prices",
            description = "Returns a list of cars filtered by  between prices",
            responses = {
                    @ApiResponse(responseCode = "200", description = "cars found between prices "),
                    @ApiResponse(responseCode = "404", description = "cars not found between prices ")
            }
    )

    @GetMapping("/price/between/{min}/{max}")
    public ResponseEntity<List<Car>> getCarsByPriceBetween(
            @Parameter(description = "min price", example = "10000.0")
            @PathVariable Double min,

            @Parameter(description = "max price", example = "30000.0")
            @PathVariable Double max) {

        if(max < min) {
            log.error("Max price must be greater than min");
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }

        List<Car> listCarsByPriceBetween = carRepository.findByPriceBetween(min, max);
        if (listCarsByPriceBetween.isEmpty() ) {
            log.info("No cars found with price between {} and {}", min, max);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        log.info("Found {} cars with price between {} and {}", listCarsByPriceBetween.size(), min, max);
        return new ResponseEntity<>(listCarsByPriceBetween, HttpStatus.OK);

    }


    @Operation(
            summary = "Show list of cars by  under  or equals max price",
            description = "Returns a list of cars filtered by  under or equels max price",
            responses = {
                    @ApiResponse(responseCode = "200", description = "cars found under or equels max price "),
                    @ApiResponse(responseCode = "404", description = "cars not found under or equels max price ")
            }
    )


    @GetMapping("/price/under/{max}")
    public ResponseEntity<List<Car>> getCarsByPriceLessThanMaxPrice(
            @Parameter(description = "max price", example = "30000.0")
            @PathVariable Double max) {
        List<Car> listCarsByPriceUnderMaxOrEquels = carRepository.findByPriceLessThanEqual(max);
        if (listCarsByPriceUnderMaxOrEquels.isEmpty()) {
            log.info("No cars found with price under or equels {}", max);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        log.info("Found {} cars with price less than {}", listCarsByPriceUnderMaxOrEquels.size(), max);
        return new ResponseEntity<>(listCarsByPriceUnderMaxOrEquels, HttpStatus.OK);

    }





    @Operation(
            summary = "Show list of cars by  over  or equals min price",
            description = "Returns a list of cars filtered by  over or equels min price",
            responses = {
                    @ApiResponse(responseCode = "200", description = "cars found over or equels min price "),
                    @ApiResponse(responseCode = "404", description = "cars not found over or equels min price ")
            }
    )
    @GetMapping("/price/over/{min}")
    public ResponseEntity<List<Car>> getCarsByPriceGreaterThanMinPrice(
            @Parameter(description = "min price", example = "5000")
            @PathVariable Double min) {
        List<Car> listCarsByPriceGreaterMinOrEquels = carRepository.findByPriceGreaterThanEqual(min);
        if (listCarsByPriceGreaterMinOrEquels.isEmpty()) {
            log.warn("No cars found with price over or equels {}", min);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        log.info("Found {} cars with price greater than {}", listCarsByPriceGreaterMinOrEquels.size(), min);
        return new ResponseEntity<>(listCarsByPriceGreaterMinOrEquels, HttpStatus.OK);


}

public void attachImage(){

}









}



