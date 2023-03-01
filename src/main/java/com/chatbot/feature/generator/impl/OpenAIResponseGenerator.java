package com.chatbot.feature.generator.impl;

import com.chatbot.feature.generator.GeneratorRequest;
import com.chatbot.feature.generator.ResponseGenerator;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.MessageService;
import com.chatbot.service.impl.DefaultConfigurationServiceImpl;
import com.chatbot.service.impl.DefaultMessageServiceImpl;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

public class OpenAIResponseGenerator extends AbstractResponseGenerator implements ResponseGenerator {
    private static OpenAIResponseGenerator instance;

    private final Logger LOG = LoggerFactory.getLogger(OpenAIResponseGenerator.class);

    private final OpenAiService service;

    private static final Model DEFAULT_MODEL_DAVINCI = Model.DAVINCI;
    private static final Model DEFAULT_MODEL_CURIE = Model.CURIE;
    private static final Model DEFAULT_MODEL_BABBAGE = Model.BABBAGE;
    private static final Model DEFAULT_MODEL_ADA = Model.ADA;

    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();
    private final MessageService messageService = DefaultMessageServiceImpl.getInstance();

    private OpenAIResponseGenerator() {
        final String apiToken = configurationService.getCredentialProperties().getProperty("openai.credentials.api.key");
        service = new OpenAiService(apiToken, Duration.ofSeconds(25));
    }

    public static synchronized OpenAIResponseGenerator getInstance() {
        if (instance == null) {
            instance = new OpenAIResponseGenerator();
        }
        return instance;
    }

    @Override
    public String generate(final GeneratorRequest request) {
        return request.isImageResponse() ? generateImage(request) : generateCompletion(request);
    }

    private String generateCompletion(final GeneratorRequest request) {
        final String channelName = StringUtils.isNotEmpty(request.getChannelName()) ? request.getChannelName() : "default";
        final Model model = StringUtils.isNotEmpty(configurationService.getConfiguration(channelName).getOpenaiModel())
                ? Model.getForName(configurationService.getConfiguration(channelName).getOpenaiModel())
                : DEFAULT_MODEL_DAVINCI;

        final CompletionRequest.CompletionRequestBuilder completionRequestBuilder = CompletionRequest.builder()
                .prompt(request.getRequestMessage())
                .model(model.toString())
                .echo(request.isRequestMessageIncluded())
                .user(request.getRequesterId());
        if (request.getMaxResponseLength() != null) {
            completionRequestBuilder.maxTokens((int) (request.getMaxResponseLength() * 1.33));
        }
        LOG.info(String.format("OpenAI completion request: %s", completionRequestBuilder.toString()));

        String generatedMessage;
        try {
            generatedMessage = createCompletion(completionRequestBuilder.build(), true);

            if (request.isResponseSanitized()) {
                generatedMessage = request.isTwitchRequest()
                        ? messageService.getMessageSanitizer(generatedMessage).sanitizeForTwitch(request.getChannelId(), request.getChannelName())
                        : messageService.getMessageSanitizer(generatedMessage).sanitizeForDiscord();
            }
            final String response = request.getMaxResponseLength() != null ? shorten(generatedMessage, request.getMaxResponseLength(), SENTENCE_SHORTENER) : generatedMessage;
            return moderate(response);
        } catch (final Exception e) {
            LOG.error("Unexpected error: " + e.getMessage());
            return StringUtils.EMPTY;
        }
    }

    private String generateImage(final GeneratorRequest request) {
        final CreateImageRequest.CreateImageRequestBuilder createImageRequestBuilder = CreateImageRequest.builder()
                .prompt(request.getRequestMessage());
        LOG.info(String.format("OpenAI image request: %s", createImageRequestBuilder.toString()));

        try {
            return createImage(createImageRequestBuilder.build(), true);
        } catch (final Exception e) {
            LOG.error("Unexpected error: " + e.getMessage());
            return StringUtils.EMPTY;
        }
    }

    private String createCompletion(final CompletionRequest request, boolean isRepeatOnFailure) {
        final CompletionResult result;
        try {
            result = service.createCompletion(request);
            LOG.info(String.format("OpenAI completion response: %s", result.toString()));
            final String text = result.getChoices().iterator().next().getText();

            if (StringUtils.isEmpty(text) && isRepeatOnFailure) {
                return createCompletion(request, false);
            }
            return text;
        } catch (final Exception e) {
            LOG.error("Unexpected error: " + e.getMessage());
            if (isRepeatOnFailure) {
                LOG.warn(String.format(String.format("Repeat OpenAI completion request: %s", request)));
                return createCompletion(request, false);
            }
            throw e;
        }
    }

    private String createImage(final CreateImageRequest request, boolean isRepeatOnFailure) {
        final ImageResult result;
        try {
            result = service.createImage(request);
            LOG.info(String.format("OpenAI image response: %s", result.toString()));
            return CollectionUtils.isNotEmpty(result.getData())
                    ? result.getData().iterator().next().getUrl()
                    : StringUtils.EMPTY;
        } catch (final Exception e) {
            LOG.error("Unexpected error: " + e.getMessage());
            if (isRepeatOnFailure) {
                LOG.warn(String.format(String.format("Repeat OpenAI image request: %s", request)));
                return createImage(request, false);
            }
            throw e;
        }
    }

    public enum Model {
        // see <a href="https://openai.com/api/pricing/"/>
        DAVINCI("text-davinci-003"), // $0.0200/1K tokens
        CURIE("text-curie-001"), // $0.0020/1K tokens
        BABBAGE("text-babbage-001"), // $0.0005/1K tokens
        ADA("text-ada-001"); // $0.0004/1K tokens
        private final String value;

        Model(final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static Model getForName(final String name) {
            for (final Model model : Arrays.stream(Model.values()).collect(Collectors.toList())) {
                if (model.name().equalsIgnoreCase(name)) {
                    return model;
                }
            }
            return DAVINCI; // use as default
        }
    }
}
