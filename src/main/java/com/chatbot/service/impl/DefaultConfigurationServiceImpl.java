package com.chatbot.service.impl;

import com.chatbot.configuration.GlobalConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.chatbot.configuration.Configuration;
import com.chatbot.service.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class DefaultConfigurationServiceImpl implements ConfigurationService {
    private static DefaultConfigurationServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultConfigurationServiceImpl.class);

    private static final String BOT_PATH = "bot";
    private static final String CONFIG_PATH = "config";
    private static final String CHANNELS_PATH = "channels";
    private static final String DEFAULT_CONFIG_NAME = "default";
    private static final String CONFIG_FILE_APPENDER = ".config.yaml";

    private GlobalConfiguration globalConfiguration;

    private DefaultConfigurationServiceImpl() {
        loadConfiguration();
    }

    public static synchronized DefaultConfigurationServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultConfigurationServiceImpl();
        }
        return instance;
    }

    @Override
    public GlobalConfiguration getConfiguration() {
        if (this.globalConfiguration == null) {
            loadConfiguration();
        }
        return this.globalConfiguration;
    }

    @Override
    public Configuration getConfiguration(String channelName) {
        if (!this.globalConfiguration.getChannelConfigurations().containsKey(channelName)) {
            loadConfiguration(channelName);
        }
        return this.globalConfiguration.getChannelConfigurations().get(channelName);
    }

    @Override
    public void loadConfiguration() {
        this.globalConfiguration = loadGlobalConfiguration();
    }

    @Override
    public void loadConfiguration(final String channelName) {
        loadChannelConfiguration(channelName)
                .or(this::loadDefaultConfiguration)
                .ifPresent(configuration -> getConfiguration().getChannelConfigurations().put(channelName.toLowerCase(), configuration));
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

    private GlobalConfiguration loadGlobalConfiguration() {
        final String configPath = CONFIG_PATH + File.separator + DEFAULT_CONFIG_NAME + CONFIG_FILE_APPENDER;
        try {
            final GlobalConfiguration globalConfiguration = loadDefaultConfigurationInternally(configPath, GlobalConfiguration.class);
            LOG.info("Loaded default configuration");
            return globalConfiguration;
        } catch (final Exception e) {
            LOG.error("Unable to load configuration from the resource [{}]. Exiting application...", configPath, e);
            System.exit(-1);
        }
        return null;
    }

    private Optional<Configuration> loadDefaultConfiguration() {
        final String configPath = CONFIG_PATH + File.separator + DEFAULT_CONFIG_NAME + CONFIG_FILE_APPENDER;
        try {
            final Configuration configuration = loadDefaultConfigurationInternally(configPath, Configuration.class);
            LOG.info("Loaded [{}] configuration", DEFAULT_CONFIG_NAME);
            return Optional.of(configuration);
        } catch (final Exception e) {
            LOG.error("Can't load [{}] configuration", DEFAULT_CONFIG_NAME);
        }
        return Optional.empty();
    }

    private Optional<Configuration> loadChannelConfiguration(final String channelName) {
        final String configPath = System.getProperty("user.home")  + File.separator + BOT_PATH + File.separator + CONFIG_PATH + File.separator + CHANNELS_PATH + File.separator + channelName.toLowerCase() + CONFIG_FILE_APPENDER;
        try {
            final Configuration configuration = loadChannelConfigurationInternally(configPath);
            LOG.info("Loaded [{}] configuration", channelName);
            return Optional.of(configuration);
        } catch (final Exception e) {
            LOG.error("Can't load [{}] configuration", channelName);
        }
        return Optional.empty();
    }

    private <T> T loadDefaultConfigurationInternally(final String path, Class<T> clazz) throws IOException {
        LOG.debug("Load configuration from the resource [{}] ...", path);
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        final InputStream is = classloader.getResourceAsStream(path);
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        final T configuration = mapper.readValue(is, clazz);
        LOG.debug("Loaded configuration from the resource [{}]", path);
        return configuration;
    }

    private Configuration loadChannelConfigurationInternally(final String path) throws IOException {
        LOG.debug("Load configuration from the path [{}] ...", path);
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        final Configuration configuration = mapper.readValue(new FileInputStream(path), Configuration.class);
        LOG.debug("Loaded configuration from the path [{}]", path);
        return configuration;
    }

    @Override
    public String getTwitchBotName() {
        return getConfiguration().getTwitchBotName();
    }

    @Override
    public String getTwitchBotId() {
        return getConfiguration().getTwitchBotId();
    }

    @Override
    public String getDiscordBotName() {
        return getConfiguration().getDiscordBotName();
    }

    @Override
    public String getDiscordBotDiscriminator() {
        return getConfiguration().getDiscordBotDiscriminator();
    }

    @Override
    public String getSuperAdminName() {
        return getConfiguration().getSuperAdmin();
    }

    @Override
    public boolean isActiveOnLiveStreamOnly(final String channelName) {
        return getConfiguration(channelName).isActiveOnLiveStreamOnly();
    }
}
