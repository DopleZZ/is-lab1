package vehicle.model;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "import_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean status; // true = success, false = failed

    @Column(name = "added_count")
    private Integer addedCount; // null if failed

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
