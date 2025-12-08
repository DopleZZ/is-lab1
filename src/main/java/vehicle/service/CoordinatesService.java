package vehicle.service;

import vehicle.dao.CoordinatesDAO;
import vehicle.model.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CoordinatesService {
    
    @Autowired
    private CoordinatesDAO coordinatesDAO;
    
    public Coordinates createCoordinates(@Valid Coordinates coordinates) {
        return coordinatesDAO.save(coordinates);
    }
    
    public Coordinates updateCoordinates(@Valid Coordinates coordinates) {
        return coordinatesDAO.save(coordinates);
    }
    
    public void deleteCoordinates(Long id) {
        try {
        coordinatesDAO.delete(id);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Cannot delete coordinates: it has related vehicles");
        }
    }
    
    public Optional<Coordinates> getCoordinatesById(Long id) {
        return coordinatesDAO.findById(id);
    }
    
    public List<Coordinates> getAllCoordinates() {
        return coordinatesDAO.findAll();
    }
    
    public Optional<Coordinates> getCoordinatesByXY(float x, Float y) {
        return coordinatesDAO.findByXY(x, y);
    }
}



