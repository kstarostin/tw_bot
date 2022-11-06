package com.chatbot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.chatbot.configuration.Configuration;
import com.chatbot.service.StaticConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class DefaultStaticConfigurationServiceImpl implements StaticConfigurationService {
    private static DefaultStaticConfigurationServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultStaticConfigurationServiceImpl.class);

    private Configuration configuration;

    private DefaultStaticConfigurationServiceImpl() {
        loadInitialStaticConfiguration();
    }

    public static synchronized DefaultStaticConfigurationServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultStaticConfigurationServiceImpl();
        }
        return instance;
    }

    @Override
    public Configuration getStaticConfiguration() {
        loadInitialStaticConfiguration();
        return configuration;
    }

    @Override
    public void loadInitialStaticConfiguration() {
        if (configuration == null) {
            loadConfigurationInternal();
        }
    }

    @Override
    public Properties getCredentialProperties() {
        return getProperties("credentials.properties");
    }

    @Override
    public Properties getProperties(final String path) {
        Properties messageProperties;
        try {
            LOG.debug("Load properties from the resource [{}] ...", path);
            messageProperties = new Properties();
            messageProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
            LOG.debug("Properties from the resource [{}] loaded", path);
            return messageProperties;
        } catch (final Exception e) {
            LOG.error("Unable to load properties from the resource [{}]. Exiting application...", path, e);
            System.exit(-1);
            return null;
        }
    }

    private void loadConfigurationInternal() {
        final String resource = "config.yaml";
        try {
            LOG.debug("Load static configuration from the resource [{}] ...", resource);
            final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            final InputStream is = classloader.getResourceAsStream(resource);
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            configuration = mapper.readValue(is, Configuration.class);
            LOG.debug("Loaded static configuration from the resource [{}]", resource);
        } catch (final Exception e) {
            LOG.error("Unable to load static configuration from the resource [{}]. Exiting application...", resource, e);
            System.exit(1);
        }
    }

    @Override
    public String getBotName() {
        return getStaticConfiguration().getBot().get("name");
    }

    @Override
    public String getSuperAdminName() {
        return getStaticConfiguration().getSuperAdmin();
    }

    @Override
    public boolean isActiveOnLiveStreamOnly() {
        return getStaticConfiguration().isActiveOnLiveStreamOnly();
    }
}
