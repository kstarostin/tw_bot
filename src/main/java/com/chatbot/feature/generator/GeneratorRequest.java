package com.chatbot.feature.generator;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;

public class GeneratorRequest {
    private String requestMessage = null;
    private String channelId = null;
    private String channelName = null;
    private String userName = null;
    private String requesterId = null;
    private boolean responseSanitized = false;
    private Integer maxResponseLength = null;
    private boolean requestMessageIncluded = false;
    private BalabobaResponseGenerator.Style responseStyle = null;
    private boolean isTwitchRequest = false;
    private boolean isDiscordRequest = false;
    private boolean isImageResponse = false;

    private GeneratorRequest() {
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getUserName() {
        return userName;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public boolean isResponseSanitized() {
        return responseSanitized;
    }

    public Integer getMaxResponseLength() {
        return maxResponseLength;
    }

    public boolean isRequestMessageIncluded() {
        return requestMessageIncluded;
    }

    public BalabobaResponseGenerator.Style getResponseStyle() {
        return responseStyle;
    }

    public boolean isTwitchRequest() {
        return isTwitchRequest;
    }

    public boolean isDiscordRequest() {
        return isDiscordRequest;
    }

    public boolean isImageResponse() {
        return isImageResponse;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final GeneratorRequest request = new GeneratorRequest();

        public Builder withRequestMessage(final String requestMessage) {
            request.requestMessage = requestMessage;
            return this;
        }

        public Builder withChannelId(final String channelId) {
            request.channelId = channelId;
            return this;
        }

        public Builder withChannelName(final String channelName) {
            request.channelName = channelName;
            return this;
        }

        public Builder withUserName(final String userName) {
            request.userName = userName;
            return this;
        }

        public Builder withResponseSanitized() {
            request.responseSanitized = true;
            return this;
        }

        public Builder withMaxResponseLength(final int maxResponseLength) {
            request.maxResponseLength = maxResponseLength;
            return this;
        }

        public Builder withRequestMessageIncluded() {
            request.requestMessageIncluded = true;
            return this;
        }

        public Builder withResponseStyle(final BalabobaResponseGenerator.Style responseStyle) {
            request.responseStyle = responseStyle;
            return this;
        }

        public Builder withImageResponse() {
            request.isImageResponse = true;
            return this;
        }

        public GeneratorRequest buildForTwitch() {
            request.requesterId = "tw:" + request.getChannelId() + ":" + request.getUserName();
            request.isTwitchRequest = true;
            return request;
        }

        public GeneratorRequest buildForDiscord() {
            request.requesterId = "ds:" + request.getChannelId() + ":" + request.getUserName();
            request.isDiscordRequest = true;
            return request;
        }
    }
}
