package vehicle.controller;

import vehicle.model.ImportHistory;
import vehicle.model.User;
import vehicle.service.ImportService;
import vehicle.dao.UserDAO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/import")
public class ImportController {

    private final ImportService importService;
    private final UserDAO userDAO;

    public ImportController(ImportService importService, UserDAO userDAO) {
        this.importService = importService;
        this.userDAO = userDAO;
    }

    @GetMapping
    public String showImportPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        User user = userDAO.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new IllegalStateException("Пользователь не найден: " + userDetails.getUsername()));
        List<ImportHistory> history = importService.getHistory(user);
        model.addAttribute("history", history);
        return "import";
    }

    @PostMapping
    public String handleImport(@RequestParam("file") MultipartFile file,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to upload");
            return "redirect:/import";
        }

        User user = userDAO.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new IllegalStateException("Пользователь не найден: " + userDetails.getUsername()));
        ImportHistory history = importService.logStart(user);

        try {
            int count = importService.executeImport(file);
            importService.logSuccess(history.getId(), count);
            redirectAttributes.addFlashAttribute("success", "Import successful! Added " + count + " vehicles.");
        } catch (Exception e) {
            importService.logFailure(history.getId());
            redirectAttributes.addFlashAttribute("error", "Import failed: " + e.getMessage());
        }

        return "redirect:/import";
    }
}
