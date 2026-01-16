package vehicle.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vehicle.dao.ImportHistoryDAO;
import vehicle.dao.VehicleDAO;
import vehicle.model.*;
import vehicle.validator.CsvImportValidator;

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
    private final CsvImportValidator csvImportValidator;

    public ImportService(VehicleService vehicleService, VehicleDAO vehicleDAO,
                         CoordinatesService coordinatesService, ImportHistoryDAO importHistoryDAO,
                         CsvImportValidator csvImportValidator) {
        this.vehicleService = vehicleService;
        this.vehicleDAO = vehicleDAO;
        this.coordinatesService = coordinatesService;
        this.importHistoryDAO = importHistoryDAO;
        this.csvImportValidator = csvImportValidator;
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
                    Vehicle vehicle = csvImportValidator.parseAndValidate(record, rowNumber);
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
        ImportHistory history = importHistoryDAO.findAll().stream()
                .filter(h -> h.getId().equals(historyId))
                .findFirst()
                .orElse(null);
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
