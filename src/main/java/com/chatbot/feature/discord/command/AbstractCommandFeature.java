package com.chatbot.feature.discord.command;

import com.chatbot.feature.discord.AbstractDiscordFeature;
import com.chatbot.service.MessageService;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.YouTubeService;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.chatbot.service.impl.DefaultConfigurationServiceImpl;
import com.chatbot.service.impl.DefaultYouTubeServiceImpl;
import discord4j.core.event.domain.Event;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public abstract class AbstractCommandFeature<T extends Event> extends AbstractDiscordFeature<T> {
    private final Logger LOG = LoggerFactory.getLogger(AbstractCommandFeature.class);

    private static final String YOUTUBE_CHANNEL_ID_1 = "UC2WNW0NZVyMeEPvtLmScgvQ"; // SUNBOYUNITED
    private static final String YOUTUBE_CHANNEL_ID_2 = "UCBF5sbrlpTYECHMSfPT8wKw"; // Архив гениальных видео

    private final YouTubeService youTubeService = DefaultYouTubeServiceImpl.getInstance();
    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();
    protected final MessageService messageService = DefaultMessageServiceImpl.getInstance();

    protected String handleCommand(final Message message) {
        final String channelId = message.getChannelId().asString();
        final String userName = message.getAuthor().map(User::getUsername).orElse(StringUtils.EMPTY);
        final String content = message.getContent();
        if (!getWhitelistedChannelsForCommands().contains(channelId)) {
            return StringUtils.EMPTY;
        }
        return handleCommand(channelId, userName, content);
    }

    protected String handleCommand(final String channelId, final String userName, final String content) {
        return handleCommand(channelId, userName, content, null);
    }

    protected String handleCommand(final String channelId, final String userName, final String content, final String customResponseText) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        LOG.info("Discord[{}]-[{}]:[{}]:[{}]", channelId, formatter.format(new Date()), userName, content);

        final String videoURL = youTubeService.getRandomVideo(Map.of(YOUTUBE_CHANNEL_ID_1, 150, YOUTUBE_CHANNEL_ID_2, 50));
        final String responseMessage = StringUtils.isNotBlank(customResponseText)
                ? customResponseText + StringUtils.SPACE + videoURL
                : String.format(messageService.getStandardMessageForKey("message.discord.sunboy"), videoURL);

        LOG.info("Discord[{}]-[{}]:[{}]:[{}]", channelId, formatter.format(new Date()), configurationService.getBotName(), responseMessage);
        return responseMessage;
    }

    protected boolean hasCachedVideo() {
        return youTubeService.getCachedRandomVideo().isPresent();
    }
}
