package org.snomed.ssoservice.config;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.ssoservice.rest.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class CustomCacheEventLogger implements CacheEventListener<Object, Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomCacheEventLogger.class);

    @Override
    public void onEvent(CacheEvent cacheEvent) {
        var value = cacheEvent.getNewValue();

        if (value instanceof UserDTO user) {
            LOGGER.debug("CACHE Event={}, Key={}, login={}", cacheEvent.getType(), cacheEvent.getKey(), user.getLogin());
        }
    }
}
