package vehicle.validator;

import vehicle.model.Vehicle;
import vehicle.model.VehicleType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class VehicleValidator implements Validator {

    private static final long MAX_CAPACITY_SUBMARINE = 150;    
    private static final long MAX_CAPACITY_BOAT = 50;          
    private static final long MAX_CAPACITY_CHOPPER = 12;       


    private static final double FUEL_COEFFICIENT = 0.05;
    private static final double FUEL_TOLERANCE = 0.1; 

    @Override
    public boolean supports(Class<?> clazz) {
        return Vehicle.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Vehicle vehicle = (Vehicle) target;

        validateWheelsForWaterVehicles(vehicle, errors);

        validateCapacityByType(vehicle, errors);

        validateEngineFuelRatio(vehicle, errors);
    }

    private void validateWheelsForWaterVehicles(Vehicle vehicle, Errors errors) {
        if (vehicle.getType() == null) {
            return;
        }

        if (vehicle.getType() == VehicleType.SUBMARINE || vehicle.getType() == VehicleType.BOAT) {
            if (vehicle.getNumberOfWheels() != 0) {
                errors.rejectValue("numberOfWheels", "vehicle.wheels.water",
                        "Подводные лодки и лодки не могут иметь колёс (должно быть 0)");
            }
        }
    }

    private void validateCapacityByType(Vehicle vehicle, Errors errors) {
        if (vehicle.getType() == null) {
            return;
        }

        long capacity = vehicle.getCapacity();
        long maxCapacity;
        String vehicleTypeName;

        switch (vehicle.getType()) {
            case SUBMARINE:
                maxCapacity = MAX_CAPACITY_SUBMARINE;
                vehicleTypeName = "подводной лодки";
                break;
            case BOAT:
                maxCapacity = MAX_CAPACITY_BOAT;
                vehicleTypeName = "лодки";
                break;
            case CHOPPER:
                maxCapacity = MAX_CAPACITY_CHOPPER;
                vehicleTypeName = "вертолёта";
                break;
            default:
                return;
        }

        if (capacity > maxCapacity) {
            errors.rejectValue("capacity", "vehicle.capacity.exceeded",
                    String.format("Вместимость %s не может превышать %d пассажиров", vehicleTypeName, maxCapacity));
        }
    }

    private void validateEngineFuelRatio(Vehicle vehicle, Errors errors) {
        if (vehicle.getEnginePower() == null || vehicle.getEnginePower() <= 0) {
            return;
        }

        double expectedFuelConsumption = vehicle.getEnginePower() * FUEL_COEFFICIENT;
        double actualFuelConsumption = vehicle.getFuelConsumption();

        double minAllowed = expectedFuelConsumption * (1 - FUEL_TOLERANCE);
        double maxAllowed = expectedFuelConsumption * (1 + FUEL_TOLERANCE);

        if (actualFuelConsumption < minAllowed || actualFuelConsumption > maxAllowed) {
            errors.rejectValue("fuelConsumption", "vehicle.fuel.ratio",
                    String.format("Расход топлива должен соответствовать мощности двигателя. " +
                            "При мощности %d л.с. ожидаемый расход: %.1f-%.1f л/100км",
                            vehicle.getEnginePower(), minAllowed, maxAllowed));
        }
    }

    public static float calculateFuelConsumption(Integer enginePower) {
        if (enginePower == null || enginePower <= 0) {
            return 1.0f;
        }
        return (float) (enginePower * FUEL_COEFFICIENT);
    }
}
