package vehicle.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleFormDTO {
    private String name;
    private Long coordinatesId;
    private Boolean createNewCoordinates;
    private Float newCoordX;
    private Float newCoordY;
    private String type;
    private Integer enginePower;
    private Long numberOfWheels;
    private Long capacity;
    private Float distanceTravelled;
    private Float fuelConsumption;
    private String fuelType;
}
