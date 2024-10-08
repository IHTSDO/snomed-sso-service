package org.snomed.ssoservice.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    private AuthoritiesConstants() {
    }

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String IMS_ADMIN = "ROLE_ims-administrators";
    public static final String ANONYMOUS = "ROLE_ANONYMOUS";
}
