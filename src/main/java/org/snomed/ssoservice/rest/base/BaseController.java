package org.snomed.ssoservice.rest.base;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class BaseController {
    public static void invalidateCookieAndAddToResponse(HttpServletResponse response, Cookie cookie) {
        cookie.setMaxAge(0);
        cookie.setValue("");
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
