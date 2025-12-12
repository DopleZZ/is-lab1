package vehicle.dao;

import vehicle.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.NoResultException;
import java.util.Optional;

@Repository
@Transactional
public class UserDAO {

    @PersistenceContext
    private EntityManager em;

    public User save(User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        } else {
            return em.merge(user);
        }
    }

    public Optional<User> findByUsername(String username) {
        try {
            return Optional.of(em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }
}
