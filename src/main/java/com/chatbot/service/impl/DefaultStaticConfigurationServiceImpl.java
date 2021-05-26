package com.chatbot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.chatbot.configuration.Configuration;
import com.chatbot.service.StaticConfigurationService;

import java.io.InputStream;

public class DefaultStaticConfigurationServiceImpl implements StaticConfigurationService {
    private static DefaultStaticConfigurationServiceImpl instance;

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

    private void loadConfigurationInternal() {
        try {
            final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            final InputStream is = classloader.getResourceAsStream("config.yaml");
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            configuration = mapper.readValue(is, Configuration.class);
        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println("Unable to load Configuration. Exiting application.");
            System.exit(1);
        }
    }
}
