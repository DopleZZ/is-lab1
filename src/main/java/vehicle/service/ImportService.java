package vehicle.service;

import vehicle.dao.ImportHistoryDAO;
import vehicle.dao.UserDAO;
import vehicle.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private ImportHistoryDAO importHistoryDAO;

    @Autowired
    private UserDAO userDAO;

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public int executeImport(MultipartFile file) throws Exception {
        List<Vehicle> vehiclesToSave = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord record : csvParser) {
                Vehicle vehicle = new Vehicle();
                vehicle.setName(record.get("name"));
                
                // Coordinates
                float x = Float.parseFloat(record.get("x"));
                Float y = Float.parseFloat(record.get("y"));
                Coordinates coordinates = new Coordinates(x, y);
                vehicle.setCoordinates(coordinates);

                vehicle.setType(VehicleType.valueOf(record.get("type")));
                vehicle.setEnginePower(Integer.parseInt(record.get("enginePower")));
                vehicle.setNumberOfWheels(Long.parseLong(record.get("numberOfWheels")));
                vehicle.setCapacity(Long.parseLong(record.get("capacity")));
                vehicle.setDistanceTravelled(Float.parseFloat(record.get("distanceTravelled")));
                vehicle.setFuelConsumption(Float.parseFloat(record.get("fuelConsumption")));
                vehicle.setFuelType(FuelType.valueOf(record.get("fuelType")));

                vehiclesToSave.add(vehicle);
            }

            for (Vehicle v : vehiclesToSave) {
                vehicleService.createVehicle(v);
            }
            
            return vehiclesToSave.size();
        }
    }

    @Transactional
    public ImportHistory logStart(User user) {
        ImportHistory history = new ImportHistory();
        history.setUser(user);
        history.setTimestamp(LocalDateTime.now());
        history.setStatus(false); // Initially false
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
        // Status is already false, but maybe update timestamp or error message if we had a field
    }
    
    public List<ImportHistory> getHistory(User user) {
        if (user.getRole() == Role.ADMIN) {
            return importHistoryDAO.findAll();
        } else {
            return importHistoryDAO.findByUser(user);
        }
    }
}
