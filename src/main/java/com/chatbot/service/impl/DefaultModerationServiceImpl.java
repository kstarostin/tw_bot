package com.chatbot.service.impl;

import com.chatbot.service.ModerationService;
import com.chatbot.service.StaticConfigurationService;
import com.github.twitch4j.common.enums.CommandPermission;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultModerationServiceImpl implements ModerationService {

    private static DefaultModerationServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultModerationServiceImpl.class);

    private static final String SUSPICIOUS_KEYWORDS_PATH = "moderation/suspicious-keywords.txt";

    private final Set<String> suspiciousKeyWords = getSuspiciousKeyWordList();

    private final StaticConfigurationService configurationService = DefaultStaticConfigurationServiceImpl.getInstance();

    private DefaultModerationServiceImpl() {
    }

    public static synchronized DefaultModerationServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultModerationServiceImpl();
        }
        return instance;
    }

    @Override
    public boolean isSuspiciousMessage(final String message, final Set<CommandPermission> userPermissions) {
        final int matchThreshold = configurationService.getStaticConfiguration().getModerationWordNumberThreshold();
        return !isWhiteListedUser(userPermissions) && getSuspiciousWordsMatchCount(message, matchThreshold) >= matchThreshold;
    }

    @Override
    public int getSuspiciousWordsMatchCount(final String message) {
        return getSuspiciousWordsMatchCount(message, null);
    }

    @Override
    public int getSuspiciousWordsMatchCount(final String message, final Integer matchThreshold) {
        final int threshold = matchThreshold != null ? matchThreshold : configurationService.getStaticConfiguration().getModerationMaxWordNumber();
        int matchCounter = 0;
        for (final String keyword : suspiciousKeyWords) {
            final List<String> keywordTokens = Arrays.stream(keyword.split("\\|")).filter(token -> !token.isEmpty()).collect(Collectors.toList());
            if (keywordTokens.stream().anyMatch(token -> StringUtils.containsIgnoreCase(message, token))) {
                matchCounter++;
            }
            if (matchCounter >= threshold) {
                break;
            }
        }
        return matchCounter;
    }

    private boolean isWhiteListedUser(final Set<CommandPermission> userPermissions) {
        return getMessageWhitelistedPermissions().containsAll(userPermissions);
    }

    private Set<CommandPermission> getMessageWhitelistedPermissions() {
        return configurationService.getStaticConfiguration().getMessageWhitelistedPermissions().stream().map(CommandPermission::valueOf).collect(Collectors.toSet());
    }

    private Set<String> getSuspiciousKeyWordList() {
        return new HashSet<>(readDictionary(SUSPICIOUS_KEYWORDS_PATH));
    }

    private Set<String> readDictionary(final String path) {
        final Set<String> keywords = new HashSet<>();
        final URL url = getClass().getClassLoader().getResource(path);
        final File keywordFilePath = url != null ? new File(url.getFile()) : null;
        if (keywordFilePath == null || !keywordFilePath.exists()) {
            return keywords;
        }
        try {
            final Scanner scanner = new Scanner(keywordFilePath);
            scanner.useDelimiter("\r\n");
            while (scanner.hasNext()){
                final String line = scanner.next();
                if (StringUtils.isNotBlank(line) && !line.startsWith("#")) {
                    keywords.add(line);
                }
            }
            scanner.close();
        } catch (final FileNotFoundException e) {
            LOG.error(String.format("Can't read file %s", keywordFilePath));
        }
        return keywords;
    }
}
