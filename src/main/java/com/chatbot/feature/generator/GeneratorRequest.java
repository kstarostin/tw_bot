package com.chatbot.feature.generator;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;

public class GeneratorRequest {
    private String requestMessage;
    private String requesterId;
    private boolean responseSanitized;
    private Integer maxResponseLength;
    private boolean requestIncluded;
    private BalabobaResponseGenerator.Style responseStyle;

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
