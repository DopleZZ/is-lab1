package vehicle.dao;

import vehicle.model.ImportHistory;
import vehicle.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class ImportHistoryDAO {

    @PersistenceContext
    private EntityManager em;

    public ImportHistory save(ImportHistory history) {
        if (history.getId() == null) {
            em.persist(history);
            return history;
        } else {
            return em.merge(history);
        }
    }

    public List<ImportHistory> findAll() {
        return em.createQuery("SELECT h FROM ImportHistory h ORDER BY h.timestamp DESC", ImportHistory.class).getResultList();
    }

    public List<ImportHistory> findByUser(User user) {
        return em.createQuery("SELECT h FROM ImportHistory h WHERE h.user = :user ORDER BY h.timestamp DESC", ImportHistory.class)
                .setParameter("user", user)
                .getResultList();
    }
}
