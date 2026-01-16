package vehicle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vehicle.dto.OperationResult;
import vehicle.dto.VehicleFormDTO;
import vehicle.model.Vehicle;
import vehicle.service.CoordinatesService;
import vehicle.service.VehicleOperationService;
import vehicle.service.VehicleService;
import vehicle.util.RedirectHelper;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private static final String REDIRECT_VEHICLES = "/vehicles";
    private static final String VIEW_FORM = "vehicle-form";
    private static final String VIEW_DASHBOARD = "dashboard";
    private static final String VIEW_DETAILS = "vehicle-details";

    private final VehicleService vehicleService;
    private final CoordinatesService coordinatesService;
    private final VehicleOperationService vehicleOperationService;

    public VehicleController(VehicleService vehicleService,
                             CoordinatesService coordinatesService,
                             VehicleOperationService vehicleOperationService) {
        this.vehicleService = vehicleService;
        this.coordinatesService = coordinatesService;
        this.vehicleOperationService = vehicleOperationService;
    }
    
    @GetMapping
    public String listVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nameFilter,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "true") boolean sortAscending,
            Model model) {

        List<Vehicle> vehicles;
        long totalCount;

        if (nameFilter != null && !nameFilter.trim().isEmpty()) {
            vehicles = vehicleService.getFilteredVehicles(nameFilter, sortField, sortAscending);
            totalCount = vehicles.size();
        } else {
            vehicles = vehicleService.getVehiclesPaginated(page * size, size);
            totalCount = vehicleService.getTotalVehiclesCount();
        }

        model.addAttribute("vehicles", vehicles);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalPages", (int) Math.ceil((double) totalCount / size));
        model.addAttribute("nameFilter", nameFilter);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortAscending", sortAscending);
        model.addAttribute("availableCoordinates", coordinatesService.getAllCoordinates());

        return VIEW_DASHBOARD;
    }

    @GetMapping("/{id}")
    public String getVehicle(@PathVariable int id, Model model) {
        Optional<Vehicle> vehicleOpt = vehicleService.getVehicleById(id);
        if (vehicleOpt.isPresent()) {
            model.addAttribute("vehicle", vehicleOpt.get());
            return VIEW_DETAILS;
        }
        return "redirect:" + REDIRECT_VEHICLES;
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("availableCoordinates", coordinatesService.getAllCoordinates());
        return VIEW_FORM;
    }

    @PostMapping("/create")
    public String createVehicle(@ModelAttribute VehicleFormDTO dto, Model model) {
        OperationResult result = vehicleOperationService.createVehicle(dto);
        return handleOperationResult(result, model, "created");
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model) {
        Optional<Vehicle> vehicleOpt = vehicleService.getVehicleById(id);
        if (vehicleOpt.isPresent()) {
            model.addAttribute("vehicle", vehicleOpt.get());
            model.addAttribute("availableCoordinates", coordinatesService.getAllCoordinates());
            return VIEW_FORM;
        }
        return "redirect:" + REDIRECT_VEHICLES;
    }

    @PostMapping("/{id}/update")
    public String updateVehicle(@PathVariable int id, @ModelAttribute VehicleFormDTO dto, Model model) {
        OperationResult result = vehicleOperationService.updateVehicle(id, dto);
        return handleOperationResult(result, model, "updated");
    }

    @PostMapping("/{id}/delete")
    public String deleteVehicle(@PathVariable int id) {
        OperationResult result = vehicleOperationService.deleteVehicle(id);
        if (result.isSuccess()) {
            return RedirectHelper.redirectWithSuccess(REDIRECT_VEHICLES, "deleted");
        }
        return RedirectHelper.redirectWithError(REDIRECT_VEHICLES, result.getErrorMessage());
    }

    @PostMapping("/{id}/reset-distance")
    public String resetDistance(@PathVariable int id) {
        OperationResult result = vehicleOperationService.resetDistance(id);
        if (result.isSuccess()) {
            return RedirectHelper.redirectWithSuccess(REDIRECT_VEHICLES, "updated");
        }
        return RedirectHelper.redirectWithError(REDIRECT_VEHICLES, result.getErrorMessage());
    }

    @PostMapping("/{id}/add-wheels")
    public String addWheels(@PathVariable int id, @RequestParam long wheelsToAdd) {
        OperationResult result = vehicleOperationService.addWheels(id, wheelsToAdd);
        if (result.isSuccess()) {
            return RedirectHelper.redirectWithSuccess(REDIRECT_VEHICLES, "updated");
        }
        return RedirectHelper.redirectWithError(REDIRECT_VEHICLES, result.getErrorMessage());
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<Vehicle>> getAllVehiclesApi() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    private String handleOperationResult(OperationResult result, Model model, String successParam) {
        if (result.isSuccess()) {
            return RedirectHelper.redirectWithSuccess(REDIRECT_VEHICLES, successParam);
        }

        model.addAttribute("error", result.getErrorMessage());
        model.addAttribute("vehicle", result.getVehicle() != null ? result.getVehicle() : new Vehicle());
        model.addAttribute("availableCoordinates", coordinatesService.getAllCoordinates());
        return VIEW_FORM;
    }
}

