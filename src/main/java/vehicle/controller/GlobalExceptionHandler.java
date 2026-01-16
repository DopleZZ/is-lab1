package vehicle.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.transaction.UnexpectedRollbackException;
import java.util.logging.Level;
import java.util.logging.Logger;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }

    @ExceptionHandler(UnexpectedRollbackException.class)
    public String handleUnexpectedRollbackException(UnexpectedRollbackException e, Model model) {
        Throwable cause = e.getCause();
        if (cause instanceof IllegalArgumentException) {
            model.addAttribute("error", cause.getMessage());
        } else {
            model.addAttribute("error", "Операция была отменена. Попробуйте ещё раз.");
        }
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        logger.log(Level.SEVERE, "Unexpected error", e);
        model.addAttribute("error", "Произошла неожиданная ошибка: " + e.getMessage());
        return "error";
    }
}
