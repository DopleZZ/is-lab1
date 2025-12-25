package vehicle.service;

import vehicle.dao.ImportHistoryDAO;
import vehicle.dao.UserDAO;
import vehicle.dao.VehicleDAO;
import vehicle.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportService {

    private final VehicleService vehicleService;
    private final VehicleDAO vehicleDAO;
    private final CoordinatesService coordinatesService;
    private final ImportHistoryDAO importHistoryDAO;
    private final UserDAO userDAO;

    public ImportService(VehicleService vehicleService, VehicleDAO vehicleDAO, 
                         CoordinatesService coordinatesService, ImportHistoryDAO importHistoryDAO, 
                         UserDAO userDAO) {
        this.vehicleService = vehicleService;
        this.vehicleDAO = vehicleDAO;
        this.coordinatesService = coordinatesService;
        this.importHistoryDAO = importHistoryDAO;
        this.userDAO = userDAO;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public int executeImport(MultipartFile file) throws Exception {
        List<Vehicle> vehiclesToSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int rowNumber = 1;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord record : csvParser) {
                rowNumber++;
                try {
                    Vehicle vehicle = parseAndValidateVehicle(record, rowNumber);
                    vehiclesToSave.add(vehicle);
                } catch (IllegalArgumentException e) {
                    errors.add("Строка " + rowNumber + ": " + e.getMessage());
                }
            }

            if (!errors.isEmpty()) {
                throw new IllegalArgumentException("Ошибки валидации:\n" + String.join("\n", errors));
            }

            for (Vehicle v : vehiclesToSave) {
                if (vehicleDAO.existsByName(v.getName())) {
                    throw new IllegalArgumentException("Транспорт с именем '" + v.getName() + "' уже существует в базе");
                }
            }

            for (Vehicle v : vehiclesToSave) {
                Coordinates savedCoords = coordinatesService.createCoordinates(v.getCoordinates());
                v.setCoordinates(savedCoords);
                vehicleService.createVehicle(v);
            }
            
            return vehiclesToSave.size();
        }
    }

    private Vehicle parseAndValidateVehicle(CSVRecord record, int rowNumber) {
        Vehicle vehicle = new Vehicle();
        
        String name = record.get("name");
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название обязательно");
        }
        vehicle.setName(name.trim());
        
        float x = Float.parseFloat(record.get("x"));
        Float y = Float.parseFloat(record.get("y"));
        if (y > 820) {
            throw new IllegalArgumentException("Координата Y не может превышать 820 (текущее: " + y + ")");
        }
        Coordinates coordinates = new Coordinates(x, y);
        vehicle.setCoordinates(coordinates);

        VehicleType type;
        try {
            type = VehicleType.valueOf(record.get("type"));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверный тип транспорта: " + record.get("type"));
        }
        vehicle.setType(type);
        
        Integer enginePower = Integer.parseInt(record.get("enginePower"));
        if (enginePower < 1) {
            throw new IllegalArgumentException("Мощность должна быть больше 0");
        }
        vehicle.setEnginePower(enginePower);
        
        long numberOfWheels = Long.parseLong(record.get("numberOfWheels"));
        if (type == VehicleType.SUBMARINE || type == VehicleType.BOAT) {
            if (numberOfWheels != 0) {
                throw new IllegalArgumentException("Лодки и подводные лодки не могут иметь колёс (должно быть 0)");
            }
        } else if (numberOfWheels < 1) {
            throw new IllegalArgumentException("Количество колёс должно быть больше 0 для данного типа");
        }
        vehicle.setNumberOfWheels(numberOfWheels);

        long capacity = Long.parseLong(record.get("capacity"));
        if (capacity < 1) {
            throw new IllegalArgumentException("Вместимость должна быть больше 0");
        }
        long maxCapacity = getMaxCapacity(type);
        if (capacity > maxCapacity) {
            throw new IllegalArgumentException("Вместимость " + getTypeName(type) + " не может превышать " + maxCapacity + " (текущее: " + capacity + ")");
        }
        vehicle.setCapacity(capacity);
        
        float distanceTravelled = Float.parseFloat(record.get("distanceTravelled"));
        if (distanceTravelled < 1) {
            throw new IllegalArgumentException("Пробег должен быть больше 0");
        }
        vehicle.setDistanceTravelled(distanceTravelled);

        float fuelConsumption = Float.parseFloat(record.get("fuelConsumption"));
        float expectedFuel = enginePower * 0.05f;
        float minAllowed = expectedFuel * 0.9f;
        float maxAllowed = expectedFuel * 1.1f;
        
        if (fuelConsumption < minAllowed || fuelConsumption > maxAllowed) {
            throw new IllegalArgumentException(String.format(
                    "Расход топлива не соответствует мощности. При мощности %d л.с. допустимо: %.1f-%.1f (текущее: %.1f)",
                    enginePower, minAllowed, maxAllowed, fuelConsumption));
        }
        vehicle.setFuelConsumption(fuelConsumption);
        
        try {
            vehicle.setFuelType(FuelType.valueOf(record.get("fuelType")));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверный тип топлива: " + record.get("fuelType"));
        }

        return vehicle;
    }
    
    private long getMaxCapacity(VehicleType type) {
        switch (type) {
            case SUBMARINE: return 150;
            case BOAT: return 50;
            case CHOPPER: return 12;
            default: return Long.MAX_VALUE;
        }
    }
    
    private String getTypeName(VehicleType type) {
        switch (type) {
            case SUBMARINE: return "подводной лодки";
            case BOAT: return "лодки";
            case CHOPPER: return "вертолёта";
            default: return "транспорта";
        }
    }

    @Transactional
    public ImportHistory logStart(User user) {
        ImportHistory history = new ImportHistory();
        history.setUser(user);
        history.setTimestamp(LocalDateTime.now());
        history.setStatus(false); 
        return importHistoryDAO.save(history);
    }

    @Transactional
    public void logSuccess(Long historyId, int count) {
        ImportHistory history = importHistoryDAO.findAll().stream().filter(h -> h.getId().equals(historyId)).findFirst().orElse(null);
        if (history != null) {
            history.setStatus(true);
            history.setAddedCount(count);
            importHistoryDAO.save(history);
        }
    }
    
    @Transactional
    public void logFailure(Long historyId) {
    }
    
    public List<ImportHistory> getHistory(User user) {
        if (user.getRole() == Role.ADMIN) {
            return importHistoryDAO.findAll();
        } else {
            return importHistoryDAO.findByUser(user);
        }
    }
}
