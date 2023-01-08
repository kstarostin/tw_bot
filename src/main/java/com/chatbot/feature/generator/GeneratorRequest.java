package com.chatbot.feature.generator;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;

public class GeneratorRequest {
    protected String requestMessage = null;
    protected String channelId = null;
    protected String channelName = null;
    protected String userName = null;
    protected String requesterId = null;
    protected boolean responseSanitized = false;
    protected Integer maxResponseLength = null;
    protected boolean requestMessageIncluded = false;
    protected BalabobaResponseGenerator.Style responseStyle = null;
    protected boolean isFromTwitch = false;
    protected boolean isFromDiscord = false;

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

    public boolean isFromTwitch() {
        return isFromTwitch;
    }

    public boolean isFromDiscord() {
        return isFromDiscord;
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

        public GeneratorRequest buildForTwitch() {
            request.requesterId = "tw:" + request.getChannelId() + ":" + request.getUserName();
            request.isFromTwitch = true;
            return request;
        }

        public GeneratorRequest buildForDiscord() {
            request.requesterId = "ds:" + request.getChannelId() + ":" + request.getUserName();
            request.isFromDiscord = true;
            return request;
        }
    }
}
