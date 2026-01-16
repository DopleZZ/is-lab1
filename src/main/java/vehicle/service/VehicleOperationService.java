package vehicle.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vehicle.dto.OperationResult;
import vehicle.dto.VehicleFormDTO;
import vehicle.model.Vehicle;
import vehicle.validator.CoordinatesFormValidator;
import vehicle.validator.VehicleFormValidator;

import java.util.Optional;


@Service
@Transactional
public class VehicleOperationService {

    private final VehicleService vehicleService;
    private final NotificationService notificationService;
    private final VehicleFormValidator vehicleFormValidator;
    private final CoordinatesFormValidator coordinatesFormValidator;

    public VehicleOperationService(VehicleService vehicleService,
                                   NotificationService notificationService,
                                   VehicleFormValidator vehicleFormValidator,
                                   CoordinatesFormValidator coordinatesFormValidator) {
        this.vehicleService = vehicleService;
        this.notificationService = notificationService;
        this.vehicleFormValidator = vehicleFormValidator;
        this.coordinatesFormValidator = coordinatesFormValidator;
    }

    public OperationResult createVehicle(VehicleFormDTO dto) {
        Vehicle vehicle = new Vehicle();
        return processVehicleForm(vehicle, dto, false);
    }


    public OperationResult updateVehicle(int id, VehicleFormDTO dto) {
        Optional<Vehicle> existingVehicleOpt = vehicleService.getVehicleById(id);
        if (!existingVehicleOpt.isPresent()) {
            return OperationResult.error("Транспортное средство не найдено");
        }
        return processVehicleForm(existingVehicleOpt.get(), dto, true);
    }


    public OperationResult deleteVehicle(int id) {
        try {
            vehicleService.deleteVehicle(id);
            notificationService.notifyVehicleDeleted(id);
            return OperationResult.success(null);
        } catch (IllegalStateException e) {
            return OperationResult.error("Невозможно удалить: есть связанные объекты");
        } catch (Exception e) {
            return OperationResult.error("Ошибка при удалении: " + e.getMessage());
        }
    }


    public OperationResult resetDistance(int id) {
        try {
            vehicleService.resetDistanceTravelled(id);
            notifyIfPresent(id);
            return OperationResult.success(null);
        } catch (IllegalArgumentException e) {
            return OperationResult.error(e.getMessage());
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof IllegalArgumentException) {
                return OperationResult.error(cause.getMessage());
            }
            return OperationResult.error("Ошибка при сбросе пробега");
        }
    }


    public OperationResult addWheels(int id, long wheelsToAdd) {
        try {
            vehicleService.addWheels(id, wheelsToAdd);
            notifyIfPresent(id);
            return OperationResult.success(null);
        } catch (IllegalArgumentException e) {
            return OperationResult.error(e.getMessage());
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof IllegalArgumentException) {
                return OperationResult.error(cause.getMessage());
            }
            return OperationResult.error("Ошибка при добавлении колёс");
        }
    }

    private void notifyIfPresent(int id) {
        vehicleService.getVehicleById(id).ifPresent(notificationService::notifyVehicleUpdated);
    }

    private OperationResult processVehicleForm(Vehicle vehicle, VehicleFormDTO dto, boolean isUpdate) {
        OperationResult validationResult = vehicleFormValidator.validateAndPopulate(dto, vehicle);
        if (!validationResult.isSuccess()) {
            return validationResult;
        }

        OperationResult coordsResult = coordinatesFormValidator.validateAndSetCoordinates(dto, vehicle);
        if (!coordsResult.isSuccess()) {
            return coordsResult;
        }

    
        try {
            if (isUpdate) {
                vehicleService.updateVehicle(vehicle);
                notificationService.notifyVehicleUpdated(vehicle);
            } else {
                vehicleService.createVehicle(vehicle);
                notificationService.notifyVehicleCreated(vehicle);
            }
            return OperationResult.success(vehicle);
        } catch (Exception e) {
            return handleException(e, vehicle, isUpdate ? "обновлении" : "создании");
        }
    }

    private OperationResult handleException(Exception e, Vehicle vehicle, String action) {
        String errorMsg;
        
        if (e instanceof IllegalArgumentException) {
            errorMsg = e.getMessage();
            return OperationResult.error(errorMsg, vehicle);
        }
        
        Throwable cause = e.getCause();
        if (cause instanceof IllegalArgumentException) {
            errorMsg = cause.getMessage();
            return OperationResult.error(errorMsg, vehicle);
        }
        
        if (e instanceof org.hibernate.exception.ConstraintViolationException ||
            (cause != null && cause instanceof org.hibernate.exception.ConstraintViolationException)) {
            errorMsg = "Нарушение ограничения целостности данных";
            return OperationResult.error(errorMsg, vehicle);
        }
        
        if (e.getMessage() != null && e.getMessage().contains("could not serialize access")) {
            errorMsg = "Конфликт при параллельном доступе. Пожалуйста, повторите операцию.";
            return OperationResult.error(errorMsg, vehicle);
        }
        
        errorMsg = "Ошибка при " + action + " транспортного средства";
        
        if (e instanceof org.hibernate.exception.SQLGrammarException) {
            org.hibernate.exception.SQLGrammarException sqlEx = (org.hibernate.exception.SQLGrammarException) e;
            errorMsg = "Ошибка SQL: " + sqlEx.getMessage();
        } else if (e instanceof javax.persistence.PersistenceException) {
            javax.persistence.PersistenceException persEx = (javax.persistence.PersistenceException) e;
            if (persEx.getCause() != null && persEx.getCause().getMessage() != null) {
                String causeMsg = persEx.getCause().getMessage();
                if (causeMsg.contains("duplicate") || causeMsg.contains("unique")) {
                    errorMsg = "Транспортное средство с таким именем уже существует";
                }
            }
        }

        return OperationResult.error(errorMsg, vehicle);
    }
}
