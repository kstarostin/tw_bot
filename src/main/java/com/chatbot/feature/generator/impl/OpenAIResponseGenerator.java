package com.chatbot.feature.generator.impl;

import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.feature.generator.impl.util.ResponseGeneratorUtil;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.impl.DefaultConfigurationServiceImpl;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAIResponseGenerator implements ResponseGenerator {
    private static OpenAIResponseGenerator instance;

    private final Logger LOG = LoggerFactory.getLogger(OpenAIResponseGenerator.class);

    private final OpenAiService service;

    // see <a href="https://openai.com/api/pricing/"/>
    private static final String MODEL_DAVINCI = "text-davinci-003"; // $0.0200/1K tokens
    private static final String MODEL_CURIE = "text-curie-001"; // $0.0020/1K tokens
    private static final String MODEL_BABBAGE = "text-babbage-001"; // $0.0005/1K tokens
    private static final String MODEL_ADA = "text-ada-001"; // $0.0004/1K tokens

    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();

    private OpenAIResponseGenerator() {
        final String apiToken = configurationService.getCredentialProperties().getProperty("openai.credentials.api.key");
        service = new OpenAiService(apiToken);
    }

    public static synchronized OpenAIResponseGenerator getInstance() {
        if (instance == null) {
            instance = new OpenAIResponseGenerator();
        }
        return instance;
    }

    @Override
    public String generate(final String requesterId, String requestMessage, final Integer maxResponseLength, boolean sanitizeResponse, boolean includeRequest) {
        final CompletionRequest.CompletionRequestBuilder completionRequestBuilder = CompletionRequest.builder()
                .prompt(requestMessage)
                .model(MODEL_CURIE)
                .echo(includeRequest)
                .user(requesterId);
        if (maxResponseLength != null) {
            completionRequestBuilder.maxTokens((int) (maxResponseLength * 1.33)); // todo tune length
        }
        LOG.info(String.format("OpenAI request: %s", completionRequestBuilder.toString()));

        final CompletionResult result = service.createCompletion(completionRequestBuilder.build());

        LOG.info(String.format("OpenAI response: %s", result.toString()));

        String generatedMessage = result.getChoices().iterator().next().getText();

        if (sanitizeResponse) {
            generatedMessage = ResponseGeneratorUtil.sanitize(generatedMessage);
        }
        return maxResponseLength != null ? ResponseGeneratorUtil.shorten(generatedMessage, maxResponseLength, ResponseGeneratorUtil.SENTENCE_SHORTENER) : generatedMessage;
    }

    @Override
    public String generate(final String requesterId, String requestMessage, Integer maxResponseLength, boolean sanitizeResponse, boolean includeRequest, BalabobaResponseGenerator.Style style) {
        return generate(requesterId, requestMessage, maxResponseLength, sanitizeResponse, includeRequest);
    }
}
