package vehicle.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vehicle.model.Vehicle;
import java.util.HashMap;
import java.util.Map;


@Service
public class NotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    public void notifyVehicleCreated(Vehicle vehicle) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", "created");
        message.put("vehicle", vehicle);
        messagingTemplate.convertAndSend("/topic/vehicles", message);
    }
    
    public void notifyVehicleUpdated(Vehicle vehicle) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", "updated");
        message.put("vehicle", vehicle);
        messagingTemplate.convertAndSend("/topic/vehicles", message);
    }
    
    public void notifyVehicleDeleted(int vehicleId) {
        Map<String, Object> message = new HashMap<>();
        message.put("action", "deleted");
        message.put("vehicleId", vehicleId);
        messagingTemplate.convertAndSend("/topic/vehicles", message);
    }
}
