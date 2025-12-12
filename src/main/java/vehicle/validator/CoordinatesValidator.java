package vehicle.validator;

import vehicle.model.Coordinates;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("coordinatesValidator")
public class CoordinatesValidator implements Validator {

    private static final long MIN_X = -540; // больше -541
    private static final long MAX_Y = 568;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }

        Coordinates coordinates = (Coordinates) value;
        
        if (coordinates.getX() <= MIN_X) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Validation Error", "X coordinate must be greater than -541");
            throw new ValidatorException(message);
        }
        
        if (coordinates.getY() == null || coordinates.getY() > MAX_Y) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Validation Error", "Y coordinate cannot exceed " + MAX_Y);
            throw new ValidatorException(message);
        }
    }
}