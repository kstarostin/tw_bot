package com.chatbot.service.impl;

import com.chatbot.service.DayCacheService;
import com.chatbot.service.ConfigurationService;
import com.chatbot.service.YouTubeService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class DefaultYouTubeServiceImpl implements YouTubeService {
    private static DefaultYouTubeServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultYouTubeServiceImpl.class);

    /**
     * Define a global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    /**
     * Define a global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private GoogleCredential googleCredential;

    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();
    private final DayCacheService dayCacheService = DefaultDayCacheServiceImpl.getInstance();

    private DefaultYouTubeServiceImpl() {
    }

    public static synchronized DefaultYouTubeServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultYouTubeServiceImpl();
        }
        return instance;
    }

    @Override
    public String getRandomVideo(final Map<String, Integer> channels) {
        final Optional<String> videoUrlOptional = getCachedRandomVideo();
        if (videoUrlOptional.isPresent()) {
            return videoUrlOptional.get();
        }
        return getRandomVideoFromYuoTube(channels);
    }

    @Override
    public Optional<String> getCachedRandomVideo() {
        return dayCacheService.getCachedYtVideo();
    }

    private String getRandomVideoFromYuoTube(final Map<String, Integer> channels) {
        try {
            // Authorize the request.
            final GoogleCredential credential = getCredential();

            // This object is used to make YouTube Data API requests.
            final YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(configurationService.getBotName()).build();

            final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            final List<SearchResult> searchResultList = new ArrayList<>();

            final long maxTotalResults = channels.values().stream().reduce(0, Integer::sum);

            for (final String channelId : channels.keySet()) {
                String nextPageToken = null;

                final long maxResults = channels.get(channelId);
                int requestCounter = 0;

                do {
                    requestCounter ++;
                    final YouTube.Search.List searchRequest = youtube.search().list("snippet,id");
                    searchRequest.setChannelId(channelId);
                    searchRequest.setKey(configurationService.getCredentialProperties().getProperty("google.credentials.api.key"));
                    searchRequest.setMaxResults(maxTotalResults);
                    searchRequest.setOrder("date");
                    searchRequest.setType("video");
                    if (nextPageToken != null) {
                        searchRequest.setPageToken(nextPageToken);
                    }

                    LOG.info("YouTube[{}]-[{}]:Request#{} [{}]", channelId, formatter.format(new Date()), requestCounter, searchRequest);
                    final SearchListResponse searchListResponse = searchRequest.execute();

                    final long limit = (maxTotalResults - searchResultList.size());

                    searchResultList.addAll(searchListResponse.getItems().stream()
                            .filter(item -> "youtube#video".equals(item.getId().getKind()))
                            .limit(limit)
                            .collect(Collectors.toList()));

                    nextPageToken = StringUtils.isNotEmpty(searchListResponse.getNextPageToken()) ? searchListResponse.getNextPageToken() : null;

                } while (StringUtils.isNotEmpty(nextPageToken) && searchResultList.size() < maxTotalResults);
            }
            if (CollectionUtils.isEmpty(searchResultList)) {
                return StringUtils.EMPTY;
            }
            final long seed = LocalDate.now().atStartOfDay().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
            final int index = new Random(seed).nextInt(searchResultList.size() - 1);

            final String videoId = searchResultList.get(index).getId().getVideoId();

            String videoURL = StringUtils.EMPTY;
            if (StringUtils.isNotEmpty(videoId)) {
                videoURL = String.format("https://www.youtube.com/watch?v=%s", videoId);
                dayCacheService.cacheYtVideo(videoURL);
            }
            return videoURL;
        } catch (final GoogleJsonResponseException e) {
            LOG.error(e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (final Throwable t) {
            LOG.error(t.getMessage());
            LOG.debug("Error details: ", t);
        }
        return StringUtils.EMPTY;
    }

    private GoogleCredential getCredential() {
        if (googleCredential == null) {
            googleCredential = createCredential();
        }
        return googleCredential;
    }

    private GoogleCredential createCredential() {
        final String resource = "googleapi/omskbot-service-account.json";
        try {
            LOG.debug("Load Google Service Account Key from the resource [{}] ...", resource);
            final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            final InputStream inputStream = classloader.getResourceAsStream(resource);

            final GoogleCredential credential = GoogleCredential.fromStream(inputStream).createScoped(List.of(YouTubeScopes.YOUTUBE_READONLY));
            LOG.debug("Loaded Google Service Account Key from the resource [{}]", resource);
            return credential;
        } catch (final Exception e) {
            LOG.error("Unable to load Google Service Account Key from the resource [{}]. Exiting application...", resource, e);
            System.exit(1);
        }
        return null;
    }
}
