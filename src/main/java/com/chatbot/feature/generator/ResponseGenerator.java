package com.chatbot.feature.generator;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;

public interface ResponseGenerator {

    String generate(String requesterId, String requestMessage, Integer maxResponseLength, boolean sanitizeResponse, boolean includeRequest);
    String generate(String requesterId, String requestMessage, Integer maxResponseLength, boolean sanitizeResponse, boolean includeRequest, BalabobaResponseGenerator.Style style);
}
