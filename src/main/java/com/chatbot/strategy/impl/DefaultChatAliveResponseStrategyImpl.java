package com.chatbot.strategy.impl;

import com.github.twitch4j.chat.events.AbstractChannelEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.chatbot.service.EmoteService;
import com.chatbot.service.impl.DefaultEmoteServiceImpl;
import com.chatbot.strategy.ChatResponseStrategy;
import com.chatbot.util.EmoteEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultChatAliveResponseStrategyImpl extends AbstractResponseStrategy implements ChatResponseStrategy {
    private static DefaultChatAliveResponseStrategyImpl instance;

    private final Map<EmoteEnum, String> messageForEmote = Stream.of(new Object[][] {
            { EmoteEnum.MmmHmm, "MmmHmm MmmHmm MmmHmm MmmHmm MmmHmm" },
            { EmoteEnum.pepeJAM, "pepeJAM pepeJAM pepeJAM pepeJAM pepeJAM" },
            { EmoteEnum.BoneZone, "BoneZone BoneZone BoneZone BoneZone BoneZone" },
            { EmoteEnum.pepeGuitar, "pepeGuitar pepeGuitar pepeGuitar pepeGuitar pepeGuitar" },
            { EmoteEnum.hoSway, "hoSway hoSway hoSway hoSway hoSway" },
            { EmoteEnum.boomerTUNE, "boomerTUNE boomerTUNE boomerTUNE boomerTUNE boomerTUNE" },
            { EmoteEnum.catJAM, "catJAM catJAM catJAM catJAM catJAM" }
    }).collect(Collectors.toMap(data -> (EmoteEnum) data[0], data -> (String) data[1]));

    private final EmoteService emoteService = DefaultEmoteServiceImpl.getInstance();

    private DefaultChatAliveResponseStrategyImpl () {
    }

    public static synchronized DefaultChatAliveResponseStrategyImpl getInstance() {
        if (instance == null) {
            instance = new DefaultChatAliveResponseStrategyImpl();
        }
        return instance;
    }

    @Override
    public void respond(final AbstractChannelEvent abstractEvent) {
        final ChannelMessageEvent event = ((ChannelMessageEvent) abstractEvent);
        String responseMessage = StringUtils.EMPTY;
        if (messageService.isBotQuoted(event)) {
            responseMessage = buildResponseMessageOnQuoting(event);
        } else {
            messageService.trackUserMessageForChannel(event.getChannel().getName(), event.getMessage());
            if (messageService.containsTrackedRepeatedEmote(event)) {
                responseMessage = buildResponseMessageOnTrackedEmote(event);
            }
        }
        respond(event, responseMessage, event.getUser().getName());
    }

    private String buildResponseMessageOnQuoting(final ChannelMessageEvent event) {
        // todo
        return "Okayge";
    }

    private String buildResponseMessageOnTrackedEmote(final ChannelMessageEvent event) {
        final String emote = messageService.extractEmotePart(event).toString();
        if (emoteService.getBTTVEmotesForChannel(event.getChannel().getName()).contains(emote) || EmoteEnum.EZY.toString().equals(emote)) {
            return emote;
        }
        return StringUtils.EMPTY;
    }
}
