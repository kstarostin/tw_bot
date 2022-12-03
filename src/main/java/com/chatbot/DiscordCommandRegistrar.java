package com.chatbot;

import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DiscordCommandRegistrar {
    private final Logger LOG = LoggerFactory.getLogger(DiscordCommandRegistrar.class);

    // The name of the folder the commands json is in, inside our resources folder
    private static final String COMMANDS_FOLDER_NAME = "dscommands/";

    private final RestClient restClient;

    public DiscordCommandRegistrar(final RestClient restClient) {
        this.restClient = restClient;
    }

    // Since this will only run once on startup, blocking is okay.
    protected void registerCommands(final List<String> fileNames) throws IOException {
        // Create an ObjectMapper that supports Discord4J classes
        final JacksonResources d4jMapper = JacksonResources.create();

        // Convenience variables for the sake of easier to read code below
        final ApplicationService applicationService = restClient.getApplicationService();
        final long applicationId = restClient.getApplicationId().block();

        // Get our commands json from resources as command data
        final List<ApplicationCommandRequest> commands = new ArrayList<>();
        for (String json : getCommandsJson(fileNames)) {
            ApplicationCommandRequest request = d4jMapper.getObjectMapper()
                    .readValue(json, ApplicationCommandRequest.class);

            commands.add(request); // Add to our array list
        }

        // Bulk overwrite commands. This is now idempotent, so it is safe to use this even when only 1 command is changed/added/removed
        applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, commands)
                .doOnNext(cmd -> LOG.info("Successfully registered Discord command: " + cmd.name()))
                .doOnError(e -> LOG.error("Failed to register global commands", e))
                .subscribe();
    }

    /* The two below methods are boilerplate that can be completely removed when using Spring Boot */

    private static List<String> getCommandsJson(List<String> fileNames) throws IOException {
        // Confirm that the commands folder exists
        final URL url = DiscordCommandRegistrar.class.getClassLoader().getResource(COMMANDS_FOLDER_NAME);
        Objects.requireNonNull(url, COMMANDS_FOLDER_NAME + " could not be found");

        //Get all the files inside this folder and return the contents of the files as a list of strings
        final List<String> list = new ArrayList<>();
        for (String file : fileNames) {
            String resourceFileAsString = getResourceFileAsString(COMMANDS_FOLDER_NAME + file);
            list.add(Objects.requireNonNull(resourceFileAsString, "Command file not found: " + file));
        }
        return list;
    }

    /**
     * Gets a specific resource file as String
     *
     * @param fileName The file path omitting "resources/"
     * @return The contents of the file as a String, otherwise throws an exception
     */
    private static String getResourceFileAsString(String fileName) throws IOException {
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (final InputStream resourceAsStream = classLoader.getResourceAsStream(fileName)) {
            if (resourceAsStream == null) return null;
            try (final InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
                final BufferedReader reader = new BufferedReader(inputStreamReader)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }
}
