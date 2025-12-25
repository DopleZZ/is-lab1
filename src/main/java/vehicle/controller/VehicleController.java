package vehicle.controller;

import vehicle.model.Vehicle;
import vehicle.model.Coordinates;
import vehicle.model.VehicleType;
import vehicle.model.FuelType;
import vehicle.service.VehicleService;
import vehicle.service.CoordinatesService;
import vehicle.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/vehicles")
public class VehicleController {
    
    private final VehicleService vehicleService;
    private final CoordinatesService coordinatesService;
    private final NotificationService notificationService;

    public VehicleController(VehicleService vehicleService, CoordinatesService coordinatesService, NotificationService notificationService) {
        this.vehicleService = vehicleService;
        this.coordinatesService = coordinatesService;
        this.notificationService = notificationService;
    }
    
    @GetMapping
    public String listVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nameFilter,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "true") boolean sortAscending,
            Model model) {
        
        List<Vehicle> vehicles;
        long totalCount;
        
        if (nameFilter != null && !nameFilter.trim().isEmpty()) {
            vehicles = vehicleService.getFilteredVehicles(nameFilter, sortField, sortAscending);
            totalCount = vehicles.size();
        } else {
            vehicles = vehicleService.getVehiclesPaginated(page * size, size);
            totalCount = vehicleService.getTotalVehiclesCount();
        }
        
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalPages", (int) Math.ceil((double) totalCount / size));
        model.addAttribute("nameFilter", nameFilter);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortAscending", sortAscending);
        model.addAttribute("availableCoordinates", coordinatesService.getAllCoordinates());
        
        return "dashboard";
    }
    
    @GetMapping("/{id}")
    public String getVehicle(@PathVariable int id, Model model) {
        Optional<Vehicle> vehicleOpt = vehicleService.getVehicleById(id);
        if (vehicleOpt.isPresent()) {
            model.addAttribute("vehicle", vehicleOpt.get());
            return "vehicle-details";
        }
        return "redirect:/vehicles";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("availableCoordinates", coordinatesService.getAllCoordinates());
        return "vehicle-form";
    }
    
    @PostMapping("/create")
    public String createVehicle(@RequestParam(required = false) String name,
                               @RequestParam(required = false) Long coordinatesId,
                               @RequestParam(required = false) Boolean createNewCoordinates,
                               @RequestParam(required = false) Float newCoordX,
                               @RequestParam(required = false) Float newCoordY,
                               @RequestParam(required = false) String type,
                               @RequestParam(required = false) Integer enginePower,
                               @RequestParam(required = false) Long numberOfWheels,
                               @RequestParam(required = false) Long capacity,
                               @RequestParam(required = false) Float distanceTravelled,
                               @RequestParam(required = false) Float fuelConsumption,
                               @RequestParam(required = false) String fuelType,
                               Model model) {
        Vehicle vehicle = new Vehicle();
        String error = validateAndSetVehicleFields(vehicle, name, type, fuelType, enginePower, 
                numberOfWheels, capacity, distanceTravelled, fuelConsumption, model);
        if (error != null) return error;
        
        error = processCoordinates(vehicle, createNewCoordinates, coordinatesId, newCoordX, newCoordY, model);
        if (error != null) return error;
        
        try {
            vehicleService.createVehicle(vehicle);
            notificationService.notifyVehicleCreated(vehicle);
            return "redirect:/vehicles?created=true";
        } catch (Exception e) {
            return handleException(e, model, vehicle, "создании");
        }
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model) {
        Optional<Vehicle> vehicleOpt = vehicleService.getVehicleById(id);
        if (vehicleOpt.isPresent()) {
            model.addAttribute("vehicle", vehicleOpt.get());
            model.addAttribute("availableCoordinates", coordinatesService.getAllCoordinates());
            return "vehicle-form";
        }
        return "redirect:/vehicles";
    }
    
    @PostMapping("/{id}/update")
    public String updateVehicle(@PathVariable int id,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) Long coordinatesId,
                                @RequestParam(required = false) Boolean createNewCoordinates,
                                @RequestParam(required = false) Float newCoordX,
                                @RequestParam(required = false) Float newCoordY,
                                @RequestParam(required = false) String type,
                                @RequestParam(required = false) Integer enginePower,
                                @RequestParam(required = false) Long numberOfWheels,
                                @RequestParam(required = false) Long capacity,
                                @RequestParam(required = false) Float distanceTravelled,
                                @RequestParam(required = false) Float fuelConsumption,
                                @RequestParam(required = false) String fuelType,
                                Model model) {
        Optional<Vehicle> existingVehicleOpt = vehicleService.getVehicleById(id);
        if (!existingVehicleOpt.isPresent()) {
            return "redirect:/vehicles?error=Транспортное средство не найдено";
        }
        
        Vehicle vehicle = existingVehicleOpt.get();
        String error = validateAndSetVehicleFields(vehicle, name, type, fuelType, enginePower, 
                numberOfWheels, capacity, distanceTravelled, fuelConsumption, model);
        if (error != null) return error;
        
        error = processCoordinates(vehicle, createNewCoordinates, coordinatesId, newCoordX, newCoordY, model);
        if (error != null) return error;
        
        try {
            vehicleService.updateVehicle(vehicle);
            notificationService.notifyVehicleUpdated(vehicle);
            return "redirect:/vehicles?updated=true";
        } catch (Exception e) {
            return handleException(e, model, vehicle, "обновлении");
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteVehicle(@PathVariable int id) {
        try {
            vehicleService.deleteVehicle(id);
            notificationService.notifyVehicleDeleted(id);
            return "redirect:/vehicles?deleted=true";
        } catch (IllegalStateException e) {
            try {
                return "redirect:/vehicles?error=" + java.net.URLEncoder.encode("Невозможно удалить: есть связанные объекты", "UTF-8");
            } catch (java.io.UnsupportedEncodingException ex) {
                return "redirect:/vehicles?error=Невозможно удалить: есть связанные объекты";
            }
        } catch (Exception e) {
            try {
                return "redirect:/vehicles?error=" + java.net.URLEncoder.encode("Ошибка при удалении: " + e.getMessage(), "UTF-8");
            } catch (java.io.UnsupportedEncodingException ex) {
                return "redirect:/vehicles?error=Ошибка при удалении";
            }
        }
    }

    @PostMapping("/{id}/reset-distance")
    public String resetDistance(@PathVariable int id) {
        try {
            vehicleService.resetDistanceTravelled(id);
            Optional<Vehicle> v = vehicleService.getVehicleById(id);
            if(v.isPresent()) notificationService.notifyVehicleUpdated(v.get());
            return "redirect:/vehicles?updated=true";
        } catch (Exception e) {
            try {
                return "redirect:/vehicles?error=" + java.net.URLEncoder.encode("Ошибка при сбросе пробега: " + e.getMessage(), "UTF-8");
            } catch (java.io.UnsupportedEncodingException ex) {
                return "redirect:/vehicles?error=Error";
            }
        }
    }

    @PostMapping("/{id}/add-wheels")
    public String addWheels(@PathVariable int id, @RequestParam long wheelsToAdd) {
        try {
            vehicleService.addWheels(id, wheelsToAdd);
            Optional<Vehicle> v = vehicleService.getVehicleById(id);
            if(v.isPresent()) notificationService.notifyVehicleUpdated(v.get());
            return "redirect:/vehicles?updated=true";
        } catch (Exception e) {
            try {
                return "redirect:/vehicles?error=" + java.net.URLEncoder.encode("Ошибка при добавлении колес: " + e.getMessage(), "UTF-8");
            } catch (java.io.UnsupportedEncodingException ex) {
                return "redirect:/vehicles?error=Error";
            }
        }
    }
    
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<Vehicle>> getAllVehiclesApi() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }
    
    
    private String validateAndSetVehicleFields(Vehicle vehicle, String name, String type, String fuelType,
                                              Integer enginePower, Long numberOfWheels, Long capacity,
                                              Float distanceTravelled, Float fuelConsumption, Model model) {
        if (name == null || name.trim().isEmpty()) {
            return addErrorAndReturn("Название обязательно", vehicle, model);
        }
        vehicle.setName(name.trim());
        
        if (type == null || type.isEmpty()) {
            return addErrorAndReturn("Тип транспортного средства обязателен", vehicle, model);
        }
        try {
            vehicle.setType(VehicleType.valueOf(type));
        } catch (IllegalArgumentException e) {
            return addErrorAndReturn("Неверный тип транспортного средства", vehicle, model);
        }
        
        if (fuelType == null || fuelType.isEmpty()) {
            return addErrorAndReturn("Тип топлива обязателен", vehicle, model);
        }
        try {
            vehicle.setFuelType(FuelType.valueOf(fuelType));
        } catch (IllegalArgumentException e) {
            return addErrorAndReturn("Неверный тип топлива", vehicle, model);
        }
        
        if (enginePower != null && enginePower > 0) {
            vehicle.setEnginePower(enginePower);
        } else if (vehicle.getId() != 0) {
            vehicle.setEnginePower(null);
        }
        
        VehicleType vehicleType = vehicle.getType();
        if (vehicleType == VehicleType.SUBMARINE || vehicleType == VehicleType.BOAT) {
            vehicle.setNumberOfWheels(0);
        } else {
            if (numberOfWheels == null || numberOfWheels < 1) {
                return addErrorAndReturn("Количество колёс должно быть больше 0 для данного типа транспорта", vehicle, model);
            }
            vehicle.setNumberOfWheels(numberOfWheels);
        }
        
        if (capacity == null || capacity < 1) {
            return addErrorAndReturn("Вместимость должна быть больше 0", vehicle, model);
        }
        
        long maxCapacity;
        String typeName;
        switch (vehicleType) {
            case SUBMARINE:
                maxCapacity = 150;
                typeName = "подводной лодки";
                break;
            case BOAT:
                maxCapacity = 50;
                typeName = "лодки";
                break;
            case CHOPPER:
                maxCapacity = 12;
                typeName = "вертолёта";
                break;
            default:
                maxCapacity = Long.MAX_VALUE;
                typeName = "";
        }
        if (capacity > maxCapacity) {
            return addErrorAndReturn("Вместимость " + typeName + " не может превышать " + maxCapacity + " пассажиров", vehicle, model);
        }
        vehicle.setCapacity(capacity);
        
        if (distanceTravelled == null || distanceTravelled < 1) {
            return addErrorAndReturn("Пробег должен быть больше 0", vehicle, model);
        }
        vehicle.setDistanceTravelled(distanceTravelled);
        
        if (vehicle.getEnginePower() != null && vehicle.getEnginePower() > 0) {
            float expectedFuel = vehicle.getEnginePower() * 0.05f;
            float minAllowed = expectedFuel * 0.9f;
            float maxAllowed = expectedFuel * 1.1f;
            
            if (fuelConsumption != null && fuelConsumption > 0) {
                if (fuelConsumption < minAllowed || fuelConsumption > maxAllowed) {
                    return addErrorAndReturn(String.format(
                            "Расход топлива должен соответствовать мощности двигателя. " +
                            "При мощности %d л.с. допустимый расход: %.1f - %.1f л/100км",
                            vehicle.getEnginePower(), minAllowed, maxAllowed), vehicle, model);
                }
                vehicle.setFuelConsumption(fuelConsumption);
            } else {
                vehicle.setFuelConsumption(expectedFuel);
            }
        } else if (fuelConsumption != null && fuelConsumption >= 0.1f) {
            vehicle.setFuelConsumption(fuelConsumption);
        } else {
            return addErrorAndReturn("Необходимо указать мощность двигателя или расход топлива (минимум 0.1)", vehicle, model);
        }
        
        return null; 
    }
    
    private String processCoordinates(Vehicle vehicle, Boolean createNewCoordinates, Long coordinatesId,
                                     Float newCoordX, Float newCoordY, Model model) {
        if (createNewCoordinates != null && createNewCoordinates) {
            if (newCoordY == null) {
                return addErrorAndReturn("Координата Y обязательна и не может быть null", vehicle, model);
            }
            if (newCoordY > 820) {
                return addErrorAndReturn("Координата Y не может превышать 820", vehicle, model);
            }
            Coordinates newCoordinates = new Coordinates();
            newCoordinates.setX(newCoordX != null ? newCoordX : 0.0f);
            newCoordinates.setY(newCoordY);
            Coordinates savedCoordinates = coordinatesService.createCoordinates(newCoordinates);
            vehicle.setCoordinates(savedCoordinates);
        } else if (coordinatesId != null) {
            Optional<Coordinates> coordOpt = coordinatesService.getCoordinatesById(coordinatesId);
            if (coordOpt.isPresent()) {
                vehicle.setCoordinates(coordOpt.get());
            } else {
                return addErrorAndReturn("Выбранные координаты не найдены", vehicle, model);
            }
        } else {
            return addErrorAndReturn("Пожалуйста, выберите или создайте координаты", vehicle, model);
        }
        return null; 
    }
    
    private String addErrorAndReturn(String errorMessage, Vehicle vehicle, Model model) {
        model.addAttribute("error", errorMessage);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("availableCoordinates", coordinatesService.getAllCoordinates());
        return "vehicle-form";
    }
    
    private String handleException(Exception e, Model model, Vehicle vehicle, String action) {
        e.printStackTrace();
        String errorMsg = "Ошибка при " + action + " транспортного средства: " + e.getMessage();
        
        if (e instanceof org.hibernate.exception.SQLGrammarException) {
            org.hibernate.exception.SQLGrammarException sqlEx = (org.hibernate.exception.SQLGrammarException) e;
            errorMsg = "Ошибка SQL: " + sqlEx.getMessage();
            if (sqlEx.getSQLException() != null) {
                errorMsg += " (SQL State: " + sqlEx.getSQLException().getSQLState() + ")";
            }
        } else if (e instanceof javax.persistence.PersistenceException) {
            javax.persistence.PersistenceException persEx = (javax.persistence.PersistenceException) e;
            errorMsg = "Ошибка при работе с БД: " + persEx.getMessage();
            if (persEx.getCause() != null) {
                errorMsg += " (Причина: " + persEx.getCause().getMessage() + ")";
            }
        }
        
        model.addAttribute("error", errorMsg);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("availableCoordinates", coordinatesService.getAllCoordinates());
        return "vehicle-form";
    }
}

