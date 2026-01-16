package vehicle.dto;

import lombok.Getter;
import vehicle.model.Vehicle;

@Getter
public class OperationResult {
    private final boolean success;
    private final String errorMessage;
    private final Vehicle vehicle;

    private OperationResult(boolean success, String errorMessage, Vehicle vehicle) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.vehicle = vehicle;
    }

    public static OperationResult success(Vehicle vehicle) {
        return new OperationResult(true, null, vehicle);
    }

    public static OperationResult error(String message) {
        return new OperationResult(false, message, null);
    }

    public static OperationResult error(String message, Vehicle vehicle) {
        return new OperationResult(false, message, vehicle);
    }
}