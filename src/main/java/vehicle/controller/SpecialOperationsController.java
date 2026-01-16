package vehicle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vehicle.dto.OperationResult;
import vehicle.model.FuelType;
import vehicle.model.Vehicle;
import vehicle.service.VehicleOperationService;
import vehicle.service.VehicleService;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/special")
public class SpecialOperationsController {

    private static final String VIEW_SPECIAL = "special";

    private static final Map<String, String> FUEL_TYPE_NAMES = Map.of(
            "GASOLINE", "Бензин",
            "KEROSENE", "Керосин",
            "ELECTRICITY", "Электричество",
            "ANTIMATTER", "Антиматерия"
    );

    private final VehicleService vehicleService;
    private final VehicleOperationService vehicleOperationService;

    public SpecialOperationsController(VehicleService vehicleService,
                                        VehicleOperationService vehicleOperationService) {
        this.vehicleService = vehicleService;
        this.vehicleOperationService = vehicleOperationService;
    }

    @GetMapping
    public String showSpecialOperations(Model model) {
        return VIEW_SPECIAL;
    }

    @PostMapping("/count-by-fuel-consumption")
    public String countByFuelConsumption(@RequestParam float fuelConsumption, Model model) {
        long count = vehicleService.countByFuelConsumption(fuelConsumption);
        model.addAttribute("result", "Найдено " + count + " транспортных средств с расходом топлива = " + fuelConsumption);
        return VIEW_SPECIAL;
    }

    @PostMapping("/find-by-name-prefix")
    public String findByNamePrefix(@RequestParam String prefix, Model model) {
        List<Vehicle> vehicles = vehicleService.findByNameStartingWithFunction(prefix);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("result", "Найдено " + vehicles.size() + " транспортных средств с названием, начинающимся с '" + prefix + "'");
        return VIEW_SPECIAL;
    }

    @PostMapping("/find-by-fuel-type-less")
    public String findByFuelTypeLess(@RequestParam String fuelType, Model model) {
        try {
            FuelType fuelTypeEnum = FuelType.valueOf(fuelType);
            List<Vehicle> vehicles = vehicleService.findByFuelTypeLessThanFunction(fuelTypeEnum);
            model.addAttribute("vehicles", vehicles);
            String fuelTypeName = FUEL_TYPE_NAMES.getOrDefault(fuelType, fuelType);
            model.addAttribute("result", "Найдено " + vehicles.size() + " транспортных средств с типом топлива меньше " + fuelTypeName);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Неверный тип топлива: " + fuelType);
        }
        return VIEW_SPECIAL;
    }

    @PostMapping("/reset-distance")
    public String resetDistanceTravelled(@RequestParam int id, Model model) {
        OperationResult result = vehicleOperationService.resetDistance(id);
        if (result.isSuccess()) {
            model.addAttribute("result", "Пробег сброшен до 0 для транспортного средства с ID: " + id);
        } else {
            model.addAttribute("error", result.getErrorMessage());
        }
        return VIEW_SPECIAL;
    }

    @PostMapping("/add-wheels")
    public String addWheels(@RequestParam int id, @RequestParam long wheelsToAdd, Model model) {
        OperationResult result = vehicleOperationService.addWheels(id, wheelsToAdd);
        if (result.isSuccess()) {
            model.addAttribute("result", "Добавлено " + wheelsToAdd + " колёс к транспортному средству с ID: " + id);
        } else {
            model.addAttribute("error", result.getErrorMessage());
        }
        return VIEW_SPECIAL;
    }
}

