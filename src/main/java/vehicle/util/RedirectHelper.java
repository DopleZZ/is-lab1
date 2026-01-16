package vehicle.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class RedirectHelper {

    private RedirectHelper() {
    }

    public static String redirectWithError(String baseUrl, String errorMessage) {
        try {
            return "redirect:" + baseUrl + "?error=" + URLEncoder.encode(errorMessage, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return "redirect:" + baseUrl + "?error=Error";
        }
    }

    public static String redirectWithSuccess(String baseUrl, String paramName) {
        return "redirect:" + baseUrl + "?" + paramName + "=true";
    }
}
