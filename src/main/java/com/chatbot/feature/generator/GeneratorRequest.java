package com.chatbot.feature.generator;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;

public class GeneratorRequest {
    private final String requestMessage;
    private final String requesterId;
    private final boolean responseSanitized;
    private final Integer maxResponseLength;
    private final boolean requestIncluded;
    private final BalabobaResponseGenerator.Style responseStyle;

    public GeneratorRequest(final String requestMessage, final String requesterId, final boolean responseSanitized, final Integer maxResponseLength, final boolean requestIncluded) {
        this(requestMessage, requesterId, responseSanitized, maxResponseLength, requestIncluded, null);
    }

    public GeneratorRequest(final String requestMessage, final String requesterId, final boolean responseSanitized, final Integer maxResponseLength, final boolean requestIncluded,
                            final BalabobaResponseGenerator.Style responseStyle) {
        this.requestMessage = requestMessage;
        this.requesterId = requesterId;
        this.responseSanitized = responseSanitized;
        this.maxResponseLength = maxResponseLength;
        this.requestIncluded = requestIncluded;
        this.responseStyle = responseStyle;
    }

    public String getRequestMessage() {
        return requestMessage;
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

    public boolean isRequestIncluded() {
        return requestIncluded;
    }

    public BalabobaResponseGenerator.Style getResponseStyle() {
        return responseStyle;
    }
}
