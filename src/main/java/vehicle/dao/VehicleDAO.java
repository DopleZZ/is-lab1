package vehicle.dao;

import vehicle.model.Vehicle;
import vehicle.model.FuelType;
import vehicle.model.VehicleType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
@Transactional
public class VehicleDAO {
    

    @PersistenceContext
    private EntityManager em;
    
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == 0) {
            em.persist(vehicle);
            return vehicle;
        } else {
            return em.merge(vehicle);
        }
    }
    
    public void delete(int id) {
        Vehicle vehicle = em.find(Vehicle.class, id);
        if (vehicle != null) {
            em.remove(vehicle);
        }
    }
    
    public Optional<Vehicle> findById(int id) {
        return Optional.ofNullable(em.find(Vehicle.class, id));
    }
    
    public List<Vehicle> findAll() {
        return em.createNamedQuery("Vehicle.findAll", Vehicle.class).getResultList();
    }
    
    public List<Vehicle> findByNameStartingWith(String prefix) {
        TypedQuery<Vehicle> query = em.createNamedQuery("Vehicle.findByNameStartingWith", Vehicle.class);
        query.setParameter("prefix", prefix + "%");
        return query.getResultList();
    }
    
    public List<Vehicle> findByFuelTypeLessThan(FuelType fuelType) {
        TypedQuery<Vehicle> query = em.createNamedQuery("Vehicle.findByFuelTypeLessThan", Vehicle.class);
        query.setParameter("fuelType", fuelType);
        return query.getResultList();
    }
    
    public List<Vehicle> findAllPaginated(int first, int pageSize) {
        TypedQuery<Vehicle> query = em.createNamedQuery("Vehicle.findAll", Vehicle.class);
        query.setFirstResult(first);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }
    
    public long getTotalCount() {
        return em.createQuery("SELECT COUNT(v) FROM Vehicle v", Long.class).getSingleResult();
    }
    
    public List<Vehicle> findFilteredAndSorted(String nameFilter, String sortField, boolean sortAscending) {
        StringBuilder queryBuilder = new StringBuilder("SELECT v FROM Vehicle v WHERE 1=1");
        
        if (nameFilter != null && !nameFilter.trim().isEmpty()) {
            queryBuilder.append(" AND LOWER(v.name) LIKE LOWER(:nameFilter)");
        }
        
        String orderDirection = sortAscending ? "ASC" : "DESC";
        queryBuilder.append(" ORDER BY v.").append(sortField).append(" ").append(orderDirection);
        
        TypedQuery<Vehicle> query = em.createQuery(queryBuilder.toString(), Vehicle.class);
        
        if (nameFilter != null && !nameFilter.trim().isEmpty()) {
            query.setParameter("nameFilter", "%" + nameFilter.trim() + "%");
        }
        
        return query.getResultList();
    }
    

    public long countByFuelConsumption(float fuelConsumption) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(v) FROM Vehicle v WHERE v.fuelConsumption = :fuelConsumption", Long.class);
        query.setParameter("fuelConsumption", fuelConsumption);
        return query.getSingleResult();
    }
    
    public List<Vehicle> findByNameStartingWithFunction(String prefix) {
        return findByNameStartingWith(prefix);
    }
    
    public List<Vehicle> findByFuelTypeLessThanFunction(FuelType fuelType) {
        int targetOrdinal = fuelType.ordinal();
        List<Vehicle> allVehicles = em.createNamedQuery("Vehicle.findAll", Vehicle.class).getResultList();
        
        return allVehicles.stream()
            .filter(v -> v.getFuelType().ordinal() < targetOrdinal)
            .sorted((v1, v2) -> Integer.compare(v1.getFuelType().ordinal(), v2.getFuelType().ordinal()))
            .collect(Collectors.toList());
    }
    
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void resetDistanceTravelled(int id) {
        try {
            em.createNativeQuery("ALTER TABLE vehicles DROP CONSTRAINT IF EXISTS vehicles_distance_travelled_check").executeUpdate();
            
            int updated = em.createNativeQuery(
                "UPDATE vehicles SET distance_travelled = 0.0 WHERE id = :id")
                .setParameter("id", id)
                .executeUpdate();
            
            if (updated == 0) {
                throw new IllegalArgumentException("Vehicle with id " + id + " not found");
            }
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            int updated = em.createNativeQuery(
                "UPDATE vehicles SET distance_travelled = 0.0001 WHERE id = :id")
                .setParameter("id", id)
                .executeUpdate();
            
            if (updated == 0) {
                throw new IllegalArgumentException("Vehicle with id " + id + " not found");
            }
        }
    }
    
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void addWheels(int id, long wheelsToAdd) {
        Vehicle vehicle = em.find(Vehicle.class, id);
        if (vehicle != null) {
            if (vehicle.getType() == VehicleType.SUBMARINE || 
                vehicle.getType() == VehicleType.BOAT) {
                throw new IllegalArgumentException("Подводные лодки и лодки не могут иметь колёс");
            }
            
            if (wheelsToAdd < 1) {
                throw new IllegalArgumentException("Количество добавляемых колёс должно быть больше 0");
            }
            
            long newWheels = vehicle.getNumberOfWheels() + wheelsToAdd;
            
            if (vehicle.getType() == VehicleType.CHOPPER && newWheels > 6) {
                throw new IllegalArgumentException("Вертолёт не может иметь более 6 колёс (текущее: " + 
                        vehicle.getNumberOfWheels() + ", после добавления: " + newWheels + ")");
            }
            
            vehicle.setNumberOfWheels(newWheels);
            em.merge(vehicle);
        } else {
            throw new IllegalArgumentException("Транспортное средство с id " + id + " не найдено");
        }
    }

    public boolean existsByName(String name) {
        Long count = em.createQuery("SELECT COUNT(v) FROM Vehicle v WHERE v.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }
    
    public Optional<Vehicle> findByName(String name) {
        List<Vehicle> results = em.createQuery("SELECT v FROM Vehicle v WHERE v.name = :name", Vehicle.class)
                .setParameter("name", name)
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}

