package com.chatbot.feature;

import com.chatbot.service.BotFeatureService;
import com.chatbot.service.impl.DefaultBotFeatureServiceImpl;
import com.chatbot.util.FeatureEnum;

public abstract class AbstractFeature {
    protected final BotFeatureService botFeatureService = DefaultBotFeatureServiceImpl.getInstance();

    private static final String COMMAND_SYNTAX = "!bot";

    protected boolean isFeatureActive(final FeatureEnum featureEnum) {
        return botFeatureService.isFeatureActive(featureEnum);
    }

    protected boolean isCommand(final String message) {
        return message.toLowerCase().startsWith(COMMAND_SYNTAX);
    }

    protected String getCommandSyntax() {
        return COMMAND_SYNTAX;
    }
}
