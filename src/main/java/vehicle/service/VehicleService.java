package vehicle.service;

import vehicle.dao.VehicleDAO;
import vehicle.model.Vehicle;
import vehicle.model.FuelType;
import vehicle.model.VehicleType;
import vehicle.validator.VehicleValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class VehicleService {
    
    private final VehicleDAO vehicleDAO;
    private final VehicleValidator vehicleValidator;

    public VehicleService(VehicleDAO vehicleDAO, VehicleValidator vehicleValidator) {
        this.vehicleDAO = vehicleDAO;
        this.vehicleValidator = vehicleValidator;
    }
    
    @Transactional(isolation = Isolation.SERIALIZABLE, noRollbackFor = IllegalArgumentException.class)
    public Vehicle createVehicle(@Valid Vehicle vehicle) {
        if (vehicleDAO.existsByName(vehicle.getName())) {
            throw new IllegalArgumentException("Транспортное средство с именем '" + vehicle.getName() + "' уже существует");
        }
        
        validateVehicleBusinessRules(vehicle);
        
        Vehicle savedVehicle = vehicleDAO.save(vehicle);
        return savedVehicle;
    }
    
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public Vehicle updateVehicle(@Valid Vehicle vehicle) {
        Optional<Vehicle> existingByName = vehicleDAO.findByName(vehicle.getName());
        if (existingByName.isPresent() && existingByName.get().getId() != vehicle.getId()) {
            throw new IllegalArgumentException("Транспортное средство с именем '" + vehicle.getName() + "' уже существует");
        }
        
        validateVehicleBusinessRules(vehicle);
        
        Vehicle savedVehicle = vehicleDAO.save(vehicle);
        return savedVehicle;
    }
    
    private void validateVehicleBusinessRules(Vehicle vehicle) {
        Errors errors = new BeanPropertyBindingResult(vehicle, "vehicle");
        vehicleValidator.validate(vehicle, errors);
        
        if (errors.hasErrors()) {
            String errorMessages = errors.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException(errorMessages);
        }
    }
    
    public void calculateAndSetFuelConsumption(Vehicle vehicle) {
        float calculatedFuel = VehicleValidator.calculateFuelConsumption(vehicle.getEnginePower());
        vehicle.setFuelConsumption(calculatedFuel);
    }
    
    public void applyWaterVehicleRestrictions(Vehicle vehicle) {
        if (vehicle.getType() == VehicleType.SUBMARINE || vehicle.getType() == VehicleType.BOAT) {
            vehicle.setNumberOfWheels(0);
        }
    }
    
    public void deleteVehicle(int id) {
        Optional<Vehicle> vehicleOpt = vehicleDAO.findById(id);
        if (vehicleOpt.isPresent()) {
            try {
                vehicleDAO.delete(id);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Cannot delete vehicle: it has related objects");
            }
        }
    }
    
    public Optional<Vehicle> getVehicleById(int id) {
        return vehicleDAO.findById(id);
    }
    
    public List<Vehicle> getAllVehicles() {
        return vehicleDAO.findAll();
    }
    
    public List<Vehicle> getVehiclesByNameStartingWith(String prefix) {
        return vehicleDAO.findByNameStartingWith(prefix);
    }
    
    public List<Vehicle> getVehiclesByFuelTypeLessThan(FuelType fuelType) {
        return vehicleDAO.findByFuelTypeLessThan(fuelType);
    }
    
    public List<Vehicle> getVehiclesPaginated(int first, int pageSize) {
        return vehicleDAO.findAllPaginated(first, pageSize);
    }
    
    public long getTotalVehiclesCount() {
        return vehicleDAO.getTotalCount();
    }
    
    public List<Vehicle> getFilteredVehicles(String nameFilter, String sortField, boolean sortAscending) {
        return vehicleDAO.findFilteredAndSorted(nameFilter, sortField, sortAscending);
    }
    
    public long countByFuelConsumption(float fuelConsumption) {
        return vehicleDAO.countByFuelConsumption(fuelConsumption);
    }
    
    public List<Vehicle> findByNameStartingWithFunction(String prefix) {
        return vehicleDAO.findByNameStartingWithFunction(prefix);
    }
    
    public List<Vehicle> findByFuelTypeLessThanFunction(FuelType fuelType) {
        return vehicleDAO.findByFuelTypeLessThanFunction(fuelType);
    }
    
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void resetDistanceTravelled(int id) {
        vehicleDAO.resetDistanceTravelled(id);
    }
    
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void addWheels(int id, long wheelsToAdd) {
        vehicleDAO.addWheels(id, wheelsToAdd);
    }
}

