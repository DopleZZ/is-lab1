package vehicle.validator;

import org.springframework.stereotype.Component;
import vehicle.dto.OperationResult;
import vehicle.dto.VehicleFormDTO;
import vehicle.model.Coordinates;
import vehicle.model.Vehicle;
import vehicle.service.CoordinatesService;
import java.util.Optional;

@Component
public class CoordinatesFormValidator {

    private static final float MAX_COORD_Y = 820f;

    private final CoordinatesService coordinatesService;

    public CoordinatesFormValidator(CoordinatesService coordinatesService) {
        this.coordinatesService = coordinatesService;
    }

    public OperationResult validateAndSetCoordinates(VehicleFormDTO dto, Vehicle vehicle) {
        if (isCreatingNewCoordinates(dto)) {
            return validateAndCreateNewCoordinates(dto, vehicle);
        } else if (dto.getCoordinatesId() != null) {
            return validateAndSetExistingCoordinates(dto, vehicle);
        } else {
            return OperationResult.error("Пожалуйста, выберите или создайте координаты", vehicle);
        }
    }


    private boolean isCreatingNewCoordinates(VehicleFormDTO dto) {
        return dto.getCreateNewCoordinates() != null && dto.getCreateNewCoordinates();
    }


    private OperationResult validateAndCreateNewCoordinates(VehicleFormDTO dto, Vehicle vehicle) {
        OperationResult validationResult = validateNewCoordinatesValues(dto, vehicle);
        if (!validationResult.isSuccess()) {
            return validationResult;
        }

        Coordinates newCoordinates = new Coordinates();
        newCoordinates.setX(dto.getNewCoordX() != null ? dto.getNewCoordX() : 0.0f);
        newCoordinates.setY(dto.getNewCoordY());

        Coordinates savedCoordinates = coordinatesService.createCoordinates(newCoordinates);
        vehicle.setCoordinates(savedCoordinates);

        return OperationResult.success(vehicle);
    }


    private OperationResult validateNewCoordinatesValues(VehicleFormDTO dto, Vehicle vehicle) {
        if (dto.getNewCoordY() == null) {
            return OperationResult.error("Координата Y обязательна и не может быть null", vehicle);
        }
        if (dto.getNewCoordY() > MAX_COORD_Y) {
            return OperationResult.error("Координата Y не может превышать " + (int) MAX_COORD_Y, vehicle);
        }
        return OperationResult.success(vehicle);
    }


    private OperationResult validateAndSetExistingCoordinates(VehicleFormDTO dto, Vehicle vehicle) {
        Optional<Coordinates> coordOpt = coordinatesService.getCoordinatesById(dto.getCoordinatesId());
        if (coordOpt.isPresent()) {
            vehicle.setCoordinates(coordOpt.get());
            return OperationResult.success(vehicle);
        } else {
            return OperationResult.error("Выбранные координаты не найдены", vehicle);
        }
    }

    public OperationResult validateCoordinates(Float x, Float y) {
        if (y == null) {
            return OperationResult.error("Координата Y обязательна");
        }
        if (y > MAX_COORD_Y) {
            return OperationResult.error("Координата Y не может превышать " + (int) MAX_COORD_Y);
        }
        return OperationResult.success(null);
    }
}
