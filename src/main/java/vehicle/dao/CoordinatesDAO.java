package vehicle.dao;

import vehicle.model.Coordinates;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public class CoordinatesDAO {
    

    @PersistenceContext
    private EntityManager em;
    
    public Coordinates save(Coordinates coordinates) {
        if (coordinates.getId() == null) {
            em.persist(coordinates);
            return coordinates;
        } else {
            return em.merge(coordinates);
        }
    }
    
    public void delete(Long id) {
        Coordinates coordinates = em.find(Coordinates.class, id);
        if (coordinates != null) {
            long relatedCount = em.createQuery(
                "SELECT COUNT(v) FROM Vehicle v WHERE v.coordinates.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
            
            if (relatedCount > 0) {
                throw new IllegalStateException("Cannot delete coordinates: it has related vehicles");
            }
            em.remove(coordinates);
        }
    }
    
    public Optional<Coordinates> findById(Long id) {
        return Optional.ofNullable(em.find(Coordinates.class, id));
    }
    
    public List<Coordinates> findAll() {
        return em.createNamedQuery("Coordinates.findAll", Coordinates.class).getResultList();
    }
    
    public Optional<Coordinates> findByXY(float x, Float y) {
        TypedQuery<Coordinates> query = em.createNamedQuery("Coordinates.findByXY", Coordinates.class);
        query.setParameter("x", x);
        query.setParameter("y", y);
        List<Coordinates> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}



