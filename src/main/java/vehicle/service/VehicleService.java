package vehicle.service;

import vehicle.dao.VehicleDAO;
import vehicle.model.Vehicle;
import vehicle.model.FuelType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class VehicleService {
    
    @Autowired
    private VehicleDAO vehicleDAO;
    
    @Autowired
    private NotificationService notificationService;
    
    public Vehicle createVehicle(@Valid Vehicle vehicle) {
        if (vehicleDAO.existsByName(vehicle.getName())) {
            throw new IllegalArgumentException("Vehicle with name " + vehicle.getName() + " already exists");
        }
        Vehicle savedVehicle = vehicleDAO.save(vehicle);
        return savedVehicle;
    }
    
    public Vehicle updateVehicle(@Valid Vehicle vehicle) {
        Vehicle savedVehicle = vehicleDAO.save(vehicle);
        return savedVehicle;
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
    
    public void resetDistanceTravelled(int id) {
        vehicleDAO.resetDistanceTravelled(id);
    }
    
    public void addWheels(int id, long wheelsToAdd) {
        vehicleDAO.addWheels(id, wheelsToAdd);
    }
}

