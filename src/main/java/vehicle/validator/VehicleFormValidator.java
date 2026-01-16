package vehicle.validator;

import org.springframework.stereotype.Component;
import vehicle.dto.OperationResult;
import vehicle.dto.VehicleFormDTO;
import vehicle.model.FuelType;
import vehicle.model.Vehicle;
import vehicle.model.VehicleType;
import vehicle.model.VehicleTypeConfig;

@Component
public class VehicleFormValidator {

    public OperationResult validateAndPopulate(VehicleFormDTO dto, Vehicle vehicle) {
        OperationResult result;

        result = validateName(dto, vehicle);
        if (!result.isSuccess()) return result;

        result = validateVehicleType(dto, vehicle);
        if (!result.isSuccess()) return result;

        result = validateFuelType(dto, vehicle);
        if (!result.isSuccess()) return result;

        setEnginePower(dto, vehicle);

        result = validateWheels(dto, vehicle);
        if (!result.isSuccess()) return result;

        result = validateCapacity(dto, vehicle);
        if (!result.isSuccess()) return result;

        result = validateDistanceTravelled(dto, vehicle);
        if (!result.isSuccess()) return result;

        result = validateFuelConsumption(dto, vehicle);
        if (!result.isSuccess()) return result;

        return OperationResult.success(vehicle);
    }


    public OperationResult validateName(VehicleFormDTO dto, Vehicle vehicle) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return OperationResult.error("Название обязательно", vehicle);
        }
        vehicle.setName(dto.getName().trim());
        return OperationResult.success(vehicle);
    }

    public OperationResult validateVehicleType(VehicleFormDTO dto, Vehicle vehicle) {
        if (dto.getType() == null || dto.getType().isEmpty()) {
            return OperationResult.error("Тип транспортного средства обязателен", vehicle);
        }
        try {
            VehicleType vehicleType = VehicleType.valueOf(dto.getType());
            vehicle.setType(vehicleType);
            return OperationResult.success(vehicle);
        } catch (IllegalArgumentException e) {
            return OperationResult.error("Неверный тип транспортного средства", vehicle);
        }
    }


    public OperationResult validateFuelType(VehicleFormDTO dto, Vehicle vehicle) {
        if (dto.getFuelType() == null || dto.getFuelType().isEmpty()) {
            return OperationResult.error("Тип топлива обязателен", vehicle);
        }
        try {
            vehicle.setFuelType(FuelType.valueOf(dto.getFuelType()));
            return OperationResult.success(vehicle);
        } catch (IllegalArgumentException e) {
            return OperationResult.error("Неверный тип топлива", vehicle);
        }
    }


    public void setEnginePower(VehicleFormDTO dto, Vehicle vehicle) {
        if (dto.getEnginePower() != null && dto.getEnginePower() > 0) {
            vehicle.setEnginePower(dto.getEnginePower());
        } else if (vehicle.getId() != 0) {
            vehicle.setEnginePower(null);
        }
    }

    public OperationResult validateWheels(VehicleFormDTO dto, Vehicle vehicle) {
        if (vehicle.getType() == null) {
            return OperationResult.error("Сначала укажите тип транспортного средства", vehicle);
        }

        VehicleTypeConfig config = VehicleTypeConfig.forType(vehicle.getType());
        if (!config.hasWheels()) {
            vehicle.setNumberOfWheels(0);
        } else {
            if (dto.getNumberOfWheels() == null || dto.getNumberOfWheels() < 1) {
                return OperationResult.error("Количество колёс должно быть больше 0 для данного типа транспорта", vehicle);
            }
            vehicle.setNumberOfWheels(dto.getNumberOfWheels());
        }
        return OperationResult.success(vehicle);
    }

    public OperationResult validateCapacity(VehicleFormDTO dto, Vehicle vehicle) {
        if (dto.getCapacity() == null || dto.getCapacity() < 1) {
            return OperationResult.error("Вместимость должна быть больше 0", vehicle);
        }

        if (vehicle.getType() != null) {
            VehicleTypeConfig config = VehicleTypeConfig.forType(vehicle.getType());
            if (dto.getCapacity() > config.getMaxCapacity()) {
                return OperationResult.error(
                        "Вместимость " + config.getTypeName() + " не может превышать " + config.getMaxCapacity() + " пассажиров",
                        vehicle
                );
            }
        }

        vehicle.setCapacity(dto.getCapacity());
        return OperationResult.success(vehicle);
    }


    public OperationResult validateDistanceTravelled(VehicleFormDTO dto, Vehicle vehicle) {
        if (dto.getDistanceTravelled() == null || dto.getDistanceTravelled() < 1) {
            return OperationResult.error("Пробег должен быть больше 0", vehicle);
        }
        vehicle.setDistanceTravelled(dto.getDistanceTravelled());
        return OperationResult.success(vehicle);
    }


    public OperationResult validateFuelConsumption(VehicleFormDTO dto, Vehicle vehicle) {
        if (vehicle.getEnginePower() != null && vehicle.getEnginePower() > 0) {
            float[] range = VehicleValidator.getFuelConsumptionRange(vehicle.getEnginePower());
            float minAllowed = range[0];
            float maxAllowed = range[1];

            if (dto.getFuelConsumption() != null && dto.getFuelConsumption() > 0) {
                if (!VehicleValidator.isFuelConsumptionValid(vehicle.getEnginePower(), dto.getFuelConsumption())) {
                    return OperationResult.error(String.format(
                            "Расход топлива должен соответствовать мощности двигателя. " +
                                    "При мощности %d л.с. допустимый расход: %.1f - %.1f л/100км",
                            vehicle.getEnginePower(), minAllowed, maxAllowed), vehicle);
                }
                vehicle.setFuelConsumption(dto.getFuelConsumption());
            } else {
                vehicle.setFuelConsumption(VehicleValidator.calculateFuelConsumption(vehicle.getEnginePower()));
            }
        } else if (dto.getFuelConsumption() != null && dto.getFuelConsumption() >= 0.1f) {
            vehicle.setFuelConsumption(dto.getFuelConsumption());
        } else {
            return OperationResult.error("Необходимо указать мощность двигателя или расход топлива (минимум 0.1)", vehicle);
        }
        return OperationResult.success(vehicle);
    }
}
