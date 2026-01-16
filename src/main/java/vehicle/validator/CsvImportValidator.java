package vehicle.validator;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import vehicle.model.*;


@Component
public class CsvImportValidator {

    private static final float MAX_COORD_Y = 820f;


    public Vehicle parseAndValidate(CSVRecord record, int rowNumber) {
        Vehicle vehicle = new Vehicle();

        validateAndSetName(record, vehicle);
        validateAndSetCoordinates(record, vehicle);
        validateAndSetVehicleType(record, vehicle);
        validateAndSetEnginePower(record, vehicle);
        validateAndSetWheels(record, vehicle);
        validateAndSetCapacity(record, vehicle);
        validateAndSetDistanceTravelled(record, vehicle);
        validateAndSetFuelConsumption(record, vehicle);
        validateAndSetFuelType(record, vehicle);

        return vehicle;
    }

    private void validateAndSetName(CSVRecord record, Vehicle vehicle) {
        String name = record.get("name");
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название обязательно");
        }
        vehicle.setName(name.trim());
    }

    private void validateAndSetCoordinates(CSVRecord record, Vehicle vehicle) {
        float x = parseFloat(record, "x", "Координата X");
        Float y = parseFloat(record, "y", "Координата Y");

        if (y > MAX_COORD_Y) {
            throw new IllegalArgumentException("Координата Y не может превышать " + (int) MAX_COORD_Y + " (текущее: " + y + ")");
        }

        vehicle.setCoordinates(new Coordinates(x, y));
    }

    private void validateAndSetVehicleType(CSVRecord record, Vehicle vehicle) {
        String typeStr = record.get("type");
        try {
            VehicleType type = VehicleType.valueOf(typeStr);
            vehicle.setType(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверный тип транспорта: " + typeStr);
        }
    }

    private void validateAndSetEnginePower(CSVRecord record, Vehicle vehicle) {
        Integer enginePower = parseInt(record, "enginePower", "Мощность двигателя");
        if (enginePower < 1) {
            throw new IllegalArgumentException("Мощность должна быть больше 0");
        }
        vehicle.setEnginePower(enginePower);
    }

    private void validateAndSetWheels(CSVRecord record, Vehicle vehicle) {
        long numberOfWheels = parseLong(record, "numberOfWheels", "Количество колёс");
        VehicleTypeConfig config = VehicleTypeConfig.forType(vehicle.getType());

        if (!config.hasWheels()) {
            if (numberOfWheels != 0) {
                throw new IllegalArgumentException("Лодки и подводные лодки не могут иметь колёс (должно быть 0)");
            }
        } else if (numberOfWheels < 1) {
            throw new IllegalArgumentException("Количество колёс должно быть больше 0 для данного типа");
        }

        vehicle.setNumberOfWheels(numberOfWheels);
    }

    private void validateAndSetCapacity(CSVRecord record, Vehicle vehicle) {
        long capacity = parseLong(record, "capacity", "Вместимость");

        if (capacity < 1) {
            throw new IllegalArgumentException("Вместимость должна быть больше 0");
        }

        VehicleTypeConfig config = VehicleTypeConfig.forType(vehicle.getType());
        if (capacity > config.getMaxCapacity()) {
            throw new IllegalArgumentException("Вместимость " + config.getTypeName() +
                    " не может превышать " + config.getMaxCapacity() + " (текущее: " + capacity + ")");
        }

        vehicle.setCapacity(capacity);
    }

    private void validateAndSetDistanceTravelled(CSVRecord record, Vehicle vehicle) {
        float distanceTravelled = parseFloat(record, "distanceTravelled", "Пробег");

        if (distanceTravelled < 1) {
            throw new IllegalArgumentException("Пробег должен быть больше 0");
        }

        vehicle.setDistanceTravelled(distanceTravelled);
    }

    private void validateAndSetFuelConsumption(CSVRecord record, Vehicle vehicle) {
        float fuelConsumption = parseFloat(record, "fuelConsumption", "Расход топлива");

        if (!VehicleValidator.isFuelConsumptionValid(vehicle.getEnginePower(), fuelConsumption)) {
            float[] range = VehicleValidator.getFuelConsumptionRange(vehicle.getEnginePower());
            throw new IllegalArgumentException(String.format(
                    "Расход топлива не соответствует мощности. При мощности %d л.с. допустимо: %.1f-%.1f (текущее: %.1f)",
                    vehicle.getEnginePower(), range[0], range[1], fuelConsumption));
        }

        vehicle.setFuelConsumption(fuelConsumption);
    }

    private void validateAndSetFuelType(CSVRecord record, Vehicle vehicle) {
        String fuelTypeStr = record.get("fuelType");
        try {
            vehicle.setFuelType(FuelType.valueOf(fuelTypeStr));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверный тип топлива: " + fuelTypeStr);
        }
    }

    

    private float parseFloat(CSVRecord record, String field, String fieldName) {
        try {
            return Float.parseFloat(record.get(field));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " должно быть числом");
        }
    }

    private int parseInt(CSVRecord record, String field, String fieldName) {
        try {
            return Integer.parseInt(record.get(field));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " должно быть целым числом");
        }
    }

    private long parseLong(CSVRecord record, String field, String fieldName) {
        try {
            return Long.parseLong(record.get(field));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " должно быть целым числом");
        }
    }
}
