package vehicle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "coordinates")
@NamedQueries({
        @NamedQuery(name = "Coordinates.findAll", query = "SELECT c FROM Coordinates c"),
        @NamedQuery(name = "Coordinates.findByXY", query = "SELECT c FROM Coordinates c WHERE c.x = :x AND c.y = :y")
})
public class Coordinates implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "x", nullable = false)
    private float x;

    @NotNull(message = "Y coordinate is required")
    @Max(value = 820, message = "Y cannot exceed 820")
    @Column(name = "y", nullable = false)
    private Float y;

    @OneToMany(mappedBy = "coordinates", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Vehicle> vehicles;

    public Coordinates() {}

    public Coordinates(float x, Float y) {
        this.x = x;
        this.y = y;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public Float getY() { return y; }
    public void setY(Float y) { this.y = y; }

    public List<Vehicle> getVehicles() { return vehicles; }
    public void setVehicles(List<Vehicle> vehicles) { this.vehicles = vehicles; }

    @Override
    public String toString() {
        return "Coordinates{id=" + id + ", x=" + x + ", y=" + y + "}";
    }
}