package vehicle.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "vehicles")
@JsonIgnoreProperties({"coordinates.vehicles"})
@NamedQueries({
        @NamedQuery(name = "Vehicle.findAll", query = "SELECT v FROM Vehicle v"),
        @NamedQuery(name = "Vehicle.findById", query = "SELECT v FROM Vehicle v WHERE v.id = :id"),
        @NamedQuery(name = "Vehicle.findByNameStartingWith", query = "SELECT v FROM Vehicle v WHERE v.name LIKE :prefix"),
        @NamedQuery(name = "Vehicle.findByFuelTypeLessThan", query = "SELECT v FROM Vehicle v WHERE v.fuelType < :fuelType")
})
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name cannot be null or empty")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Coordinates cannot be null")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @NotNull(message = "Vehicle type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, columnDefinition = "VARCHAR(50)")
    private VehicleType type;

    @Min(value = 1, message = "Engine power must be greater than 0")
    @Column(name = "engine_power")
    private Integer enginePower;

    @Min(value = 1, message = "Number of wheels must be greater than 0")
    @Column(name = "number_of_wheels", nullable = false)
    private long numberOfWheels = 1L;

    @Min(value = 1, message = "Capacity must be greater than 0")
    @Column(nullable = false)
    private long capacity = 1L;

    @Min(value = 1, message = "Distance travelled must be greater than 0")
    @Column(name = "distance_travelled", nullable = false)
    private float distanceTravelled = 1.0f;

    @Min(value = 1, message = "Fuel consumption must be greater than 0")
    @Column(name = "fuel_consumption", nullable = false)
    private float fuelConsumption = 1.0f;

    @NotNull(message = "Fuel type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false, columnDefinition = "VARCHAR(50)")
    private FuelType fuelType;

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = new Date();
        }
    }

    // Custom constructor for convenience (excluding id and creationDate)
    public Vehicle(String name, Coordinates coordinates, VehicleType type, 
                   Integer enginePower, long numberOfWheels, long capacity,
                   float distanceTravelled, float fuelConsumption, FuelType fuelType) {
        this.name = name;
        this.coordinates = coordinates;
        this.type = type;
        this.enginePower = enginePower;
        this.numberOfWheels = numberOfWheels;
        this.capacity = capacity;
        this.distanceTravelled = distanceTravelled;
        this.fuelConsumption = fuelConsumption;
        this.fuelType = fuelType;
    }
}

