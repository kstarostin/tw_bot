package com.chatbot;

import com.chatbot.configuration.GlobalConfiguration;
import com.chatbot.feature.discord.command.CommandMessageFeature;
import com.chatbot.feature.discord.MessageReactionFeature;
import com.chatbot.feature.discord.command.SlashCommandMessageFeature;
import com.chatbot.service.ChannelService;
import com.chatbot.service.impl.DefaultChannelServiceImpl;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;

import com.chatbot.service.BotFeatureService;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.TwitchClientService;
import com.chatbot.service.impl.DefaultBotFeatureServiceImpl;
import com.chatbot.service.impl.DefaultConfigurationServiceImpl;
import com.chatbot.service.impl.DefaultTwitchClientServiceImpl;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

public class Bot {
    private final Logger LOG = LoggerFactory.getLogger(Bot.class);

    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();
    private final TwitchClientService twitchClientService = DefaultTwitchClientServiceImpl.getInstance();
    private final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();
    private final ChannelService channelService = DefaultChannelServiceImpl.getInstance();

    public Bot() {
        configurationService.loadConfiguration();
        configurationService.loadConfiguration(configurationService.getBotName().toLowerCase());
        twitchClientService.buildClient();
        registerFeatures();
    }

    public void start() {
        // Connect to all configured channels
        final GlobalConfiguration globalConfiguration = configurationService.getConfiguration();
        globalConfiguration.getTwitchChannels().forEach(this::joinTwitchChannel);
        logInDiscord();
    }

    private void joinTwitchChannel(final String twitchChannelName) {
        channelService.joinChannel(twitchChannelName);
    }

    private void registerFeatures() {
        final SimpleEventHandler eventHandler = twitchClientService.getTwitchClient().getEventManager().getEventHandler(SimpleEventHandler.class);
        botFeatureService.registerAllTwitchFeatures(eventHandler);
    }

    private void logInDiscord() {
        final String token = configurationService.getCredentialProperties().getProperty("discord.credentials.access.token");
        final DiscordClient discordClient = DiscordClient.create(token);

        final Mono<Void> login = discordClient.withGateway((GatewayDiscordClient gateway) -> {
            final Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
                            Mono.fromRunnable(() -> {
                                final User self = event.getSelf();
                                LOG.info("Logged in Discord as {}#{}", self.getUsername(), self.getDiscriminator());
                            }))
                    .then();


            registerDiscordCommands(gateway);

            final Mono<Void> handleMessageReaction = gateway.on(MessageCreateEvent.class, event -> MessageReactionFeature.getInstance().handle(event)).then();
            final Mono<Void> handleCommandMessage = gateway.on(MessageCreateEvent.class, event -> CommandMessageFeature.getInstance().handle(event)).then();

            final Mono<Void> handleSlashCommandMessage = gateway.on(ChatInputInteractionEvent.class, event -> SlashCommandMessageFeature.getInstance().handle(event)).then();

            return printOnLogin.and(handleMessageReaction).and(handleCommandMessage).and(handleSlashCommandMessage);
        });
        login.block();
    }

    private void registerDiscordCommands(final GatewayDiscordClient gatewayDiscordClient) {
        final List<String> commands = List.of("sunboy.json", "ufa.json", "stalker.json");
        try {
            new DiscordCommandRegistrar(gatewayDiscordClient.getRestClient()).registerCommands(commands);
        } catch (Exception e) {
            LOG.error("Error trying to register discord slash commands", e);
        }
    }
}
