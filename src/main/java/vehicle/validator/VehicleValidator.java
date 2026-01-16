package vehicle.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vehicle.model.Vehicle;
import vehicle.model.VehicleTypeConfig;


@Component
public class VehicleValidator implements Validator {

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

        if (VehicleTypeConfig.isWaterVehicle(vehicle.getType())) {
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

        VehicleTypeConfig config = VehicleTypeConfig.forType(vehicle.getType());
        long capacity = vehicle.getCapacity();

        if (capacity > config.getMaxCapacity()) {
            errors.rejectValue("capacity", "vehicle.capacity.exceeded",
                    String.format("Вместимость %s не может превышать %d пассажиров",
                            config.getTypeName(), config.getMaxCapacity()));
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

    public static boolean isFuelConsumptionValid(Integer enginePower, float fuelConsumption) {
        if (enginePower == null || enginePower <= 0) {
            return fuelConsumption >= 0.1f;
        }
        double expected = enginePower * FUEL_COEFFICIENT;
        double min = expected * (1 - FUEL_TOLERANCE);
        double max = expected * (1 + FUEL_TOLERANCE);
        return fuelConsumption >= min && fuelConsumption <= max;
    }

    public static float[] getFuelConsumptionRange(Integer enginePower) {
        if (enginePower == null || enginePower <= 0) {
            return new float[]{0.1f, Float.MAX_VALUE};
        }
        double expected = enginePower * FUEL_COEFFICIENT;
        return new float[]{(float) (expected * (1 - FUEL_TOLERANCE)), (float) (expected * (1 + FUEL_TOLERANCE))};
    }
}
