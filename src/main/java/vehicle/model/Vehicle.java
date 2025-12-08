package vehicle.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private long numberOfWheels;

    @Min(value = 1, message = "Capacity must be greater than 0")
    @Column(nullable = false)
    private long capacity;

    @Min(value = 1, message = "Distance travelled must be greater than 0")
    @Column(name = "distance_travelled", nullable = false)
    private float distanceTravelled;

    @Min(value = 1, message = "Fuel consumption must be greater than 0")
    @Column(name = "fuel_consumption", nullable = false)
    private float fuelConsumption;

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

    public Vehicle() {
        this.numberOfWheels = 1L;
        this.capacity = 1L;
        this.distanceTravelled = 1.0f;
        this.fuelConsumption = 1.0f;
    }

    public Vehicle(String name, Coordinates coordinates, VehicleType type, 
                   Integer enginePower, long numberOfWheels, long capacity,
                   float distanceTravelled, float fuelConsumption, FuelType fuelType) {
        this();
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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public VehicleType getType() { return type; }
    public void setType(VehicleType type) { this.type = type; }

    public Integer getEnginePower() { return enginePower; }
    public void setEnginePower(Integer enginePower) { this.enginePower = enginePower; }

    public long getNumberOfWheels() { return numberOfWheels; }
    public void setNumberOfWheels(long numberOfWheels) { this.numberOfWheels = numberOfWheels; }

    public long getCapacity() { return capacity; }
    public void setCapacity(long capacity) { this.capacity = capacity; }

    public float getDistanceTravelled() { return distanceTravelled; }
    public void setDistanceTravelled(float distanceTravelled) { this.distanceTravelled = distanceTravelled; }

    public float getFuelConsumption() { return fuelConsumption; }
    public void setFuelConsumption(float fuelConsumption) { this.fuelConsumption = fuelConsumption; }

    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }

    @Override
    public String toString() {
        return "Vehicle{id=" + id + ", name='" + name + "', type=" + type + "}";
    }
}

