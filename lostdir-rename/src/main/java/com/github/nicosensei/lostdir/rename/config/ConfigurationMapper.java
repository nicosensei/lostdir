package com.github.nicosensei.lostdir.rename.config;

import com.github.nicosensei.lostdir.helpers.GenericJsonObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by nicos on 11/24/2016.
 */
public final class ConfigurationMapper extends GenericJsonObjectMapper<Configuration> {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationMapper.class);

    private static final String RESOURCE_DEFAULTS = "/default-config.json";

    public static final Configuration getDefaults() throws IOException {
        try {
            return new ConfigurationMapper().deserialize(
                    ConfigurationMapper.class.getResourceAsStream(RESOURCE_DEFAULTS),
                    Configuration.class
            );
        } catch (final IOException | NullPointerException e) {
            throw new IOException("Failed to load defaults from classpath", e);
        }
    }

}
