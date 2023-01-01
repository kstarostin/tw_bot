package com.chatbot.feature.generator;

import com.chatbot.feature.generator.impl.BalabobaResponseGenerator;

public interface ResponseGenerator {

    String generate(GeneratorRequest request);
}
