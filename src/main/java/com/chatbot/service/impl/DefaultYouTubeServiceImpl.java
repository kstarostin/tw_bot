package com.chatbot.service.impl;

import com.chatbot.service.DayCacheService;
import com.chatbot.service.StaticConfigurationService;
import com.chatbot.service.YouTubeService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    /**
     * This is the directory that will be used under the user's home directory where OAuth tokens will be stored.
     */
    private static final String CREDENTIALS_DIRECTORY = ".oauth-credentials";

    private final StaticConfigurationService staticConfigurationService = DefaultStaticConfigurationServiceImpl.getInstance();
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
    public String getRandomVideo(final String channelId) {
        final Optional<String> videoUrlOptional = getCachedRandomVideo(channelId);
        if (videoUrlOptional.isPresent()) {
            return videoUrlOptional.get();
        }
        return getVideoFromYuoTube(channelId);
    }

    @Override
    public Optional<String> getCachedRandomVideo(final String channelId) {
        return dayCacheService.getCachedYtVideo();
    }

    private String getVideoFromYuoTube(final String channelId) {
        // This OAuth 2.0 access scope allows for read-only access to the
        // authenticated user's account, but not other types of account access.
        final List<String> scopes = List.of("https://www.googleapis.com/auth/youtube.readonly");
        try {
            // Authorize the request.
            final Credential credential = authorize(scopes);

            // This object is used to make YouTube Data API requests.
            final YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(staticConfigurationService.getBotName()).build();

            final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            final List<SearchResult> searchResultList = new ArrayList<>();
            String nextPageToken = null;
            final long maxResults = 200L;

            int requestCounter = 0;

            do {
                requestCounter ++;
                final YouTube.Search.List searchRequest = youtube.search().list("snippet,id");
                searchRequest.setChannelId(channelId);
                searchRequest.setKey(staticConfigurationService.getCredentialProperties().getProperty("google.credentials.api.key"));
                searchRequest.setMaxResults(maxResults);
                searchRequest.setOrder("date");
                searchRequest.setType("video");
                if (nextPageToken != null) {
                    searchRequest.setPageToken(nextPageToken);
                }

                LOG.info("YouTube[{}]-[{}]:Request#{} [{}]", channelId, formatter.format(new Date()), requestCounter, searchRequest);
                final SearchListResponse searchListResponse = searchRequest.execute();

                final int limit = (int) (maxResults - searchResultList.size());

                searchResultList.addAll(searchListResponse.getItems().stream()
                        .filter(item -> "youtube#video".equals(item.getId().getKind()))
                        .limit(limit)
                        .collect(Collectors.toList()));

                nextPageToken = StringUtils.isNotEmpty(searchListResponse.getNextPageToken()) ? searchListResponse.getNextPageToken() : null;

            } while (StringUtils.isNotEmpty(nextPageToken) && searchResultList.size() < maxResults);

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

    private Credential authorize(final List<String> scopes) throws IOException {
        // Load client secrets.
        final GoogleClientSecrets clientSecrets = getClientSecrets();
        // This creates the credentials datastore at ~/.oauth-credentials/${credentialDatastore}
        final FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(new File(System.getProperty("user.home") + "/" + CREDENTIALS_DIRECTORY));
        final DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore("videos");

        final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setCredentialDataStore(datastore).build();

        // Build the local server and bind it to port 8080
        final LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8080).build();

        // Authorize.
        return new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");
    }

    private GoogleClientSecrets getClientSecrets() {
        final GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        GoogleClientSecrets.Details installed = new GoogleClientSecrets.Details();
        installed.setClientId(staticConfigurationService.getCredentialProperties().getProperty("google.credentials.client.id"));
        installed.setClientSecret(staticConfigurationService.getCredentialProperties().getProperty("google.credentials.client.secret"));
        clientSecrets.setInstalled(installed);
        return clientSecrets;
    }
}
