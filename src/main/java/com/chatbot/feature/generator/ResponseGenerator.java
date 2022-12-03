package com.chatbot.feature.generator;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;

public interface ResponseGenerator {

    String generate(String requestMessage, boolean includeRequest);
    String generate(String requestMessage, boolean includeRequest, BalabobaResponseGenerator.Style style);
    String generateSanitized(String requestMessage, boolean includeRequest);
    String generateShortSanitized(String requestMessage, boolean includeRequest);
}
