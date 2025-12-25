package vehicle.controller;

import vehicle.model.Vehicle;
import vehicle.model.FuelType;
import vehicle.service.VehicleService;
import vehicle.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/special")
public class SpecialOperationsController {
    
    private final VehicleService vehicleService;
    private final NotificationService notificationService;

    public SpecialOperationsController(VehicleService vehicleService, NotificationService notificationService) {
        this.vehicleService = vehicleService;
        this.notificationService = notificationService;
    }
    
    @GetMapping
    public String showSpecialOperations(Model model) {
        return "special";
    }
    
    @PostMapping("/count-by-fuel-consumption")
    public String countByFuelConsumption(@RequestParam float fuelConsumption, Model model) {
        long count = vehicleService.countByFuelConsumption(fuelConsumption);
        model.addAttribute("result", "Найдено " + count + " транспортных средств с расходом топлива = " + fuelConsumption);
        return "special";
    }
    
    @PostMapping("/find-by-name-prefix")
    public String findByNamePrefix(@RequestParam String prefix, Model model) {
        List<Vehicle> vehicles = vehicleService.findByNameStartingWithFunction(prefix);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("result", "Найдено " + vehicles.size() + " транспортных средств с названием, начинающимся с '" + prefix + "'");
        return "special";
    }
    
    @PostMapping("/find-by-fuel-type-less")
    public String findByFuelTypeLess(@RequestParam String fuelType, Model model) {
        try {
            FuelType fuelTypeEnum = FuelType.valueOf(fuelType);
            List<Vehicle> vehicles = vehicleService.findByFuelTypeLessThanFunction(fuelTypeEnum);
            model.addAttribute("vehicles", vehicles);
            String fuelTypeName = getFuelTypeName(fuelType);
            model.addAttribute("result", "Найдено " + vehicles.size() + " транспортных средств с типом топлива меньше " + fuelTypeName);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Неверный тип топлива: " + fuelType);
        }
        return "special";
    }
    
    @PostMapping("/reset-distance")
    public String resetDistanceTravelled(@RequestParam int id, Model model) {
        try {
            vehicleService.resetDistanceTravelled(id);
            vehicleService.getVehicleById(id).ifPresent(vehicle -> {
                notificationService.notifyVehicleUpdated(vehicle);
            });
            model.addAttribute("result", "Пробег сброшен до 0 для транспортного средства с ID: " + id);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при сбросе пробега: " + e.getMessage());
        }
        return "special";
    }
    
    @PostMapping("/add-wheels")
    public String addWheels(@RequestParam int id, @RequestParam long wheelsToAdd, Model model) {
        try {
            vehicleService.addWheels(id, wheelsToAdd);
            vehicleService.getVehicleById(id).ifPresent(vehicle -> {
                notificationService.notifyVehicleUpdated(vehicle);
            });
            model.addAttribute("result", "Добавлено " + wheelsToAdd + " колёс к транспортному средству с ID: " + id);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при добавлении колёс: " + e.getMessage());
        }
        return "special";
    }
    
    private String getFuelTypeName(String fuelType) {
        switch (fuelType) {
            case "GASOLINE": return "Бензин";
            case "KEROSENE": return "Керосин";
            case "ELECTRICITY": return "Электричество";
            case "ANTIMATTER": return "Антиматерия";
            default: return fuelType;
        }
    }
}

