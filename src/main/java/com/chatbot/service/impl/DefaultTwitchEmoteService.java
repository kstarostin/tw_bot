package com.chatbot.service.impl;

import com.chatbot.service.TwitchEmoteService;
import com.chatbot.util.emotes.seventv.SevenTVEmote;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class DefaultTwitchEmoteService implements TwitchEmoteService {
    private static DefaultTwitchEmoteService instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultTwitchEmoteService.class);

    private static final String USER_PATH_VAR = "${user}";

    private static final String API_URL_7TV = "https://api.7tv.app/v2/";
    private static final String API_CHANNEL_EMOTES_PATH_7TV = "users/" + USER_PATH_VAR + "/emotes";
    private static final String API_GLOBAL_EMOTES_PATH_7TV = "emotes/global";

    final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private DefaultTwitchEmoteService() {
    }

    public static synchronized DefaultTwitchEmoteService getInstance() {
        if (instance == null) {
            instance = new DefaultTwitchEmoteService();
        }
        return instance;
    }

    @Override
    public List<SevenTVEmote> getGlobal7TVEmotes() {
        final String requestUrl = API_URL_7TV + API_GLOBAL_EMOTES_PATH_7TV;
        return get7TVEmotes(requestUrl);
    }

    @Override
    public List<SevenTVEmote> getChannel7TVEmotes(final String channelName) {
        final String requestUrl = API_URL_7TV + API_CHANNEL_EMOTES_PATH_7TV.replace(USER_PATH_VAR, channelName.toLowerCase());
        return get7TVEmotes(requestUrl);
    }

    private List<SevenTVEmote> get7TVEmotes(final String url) {
        final List<SevenTVEmote> emotes = new ArrayList<>();
        try {
            doTrustToCertificates();
            final JSONArray jsonArray = new JSONArray(IOUtils.toString(new URL(url), StandardCharsets.UTF_8));
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    emotes.add(objectMapper.readValue(jsonArray.getJSONObject(i).toString(), SevenTVEmote.class));
                }
            }
        } catch (final Exception e) {
            LOG.error("Error: ", e);
        }
        return emotes;
    }

    private void doTrustToCertificates() throws Exception {
        // configure the SSLContext with a dummy TrustManager
        final SSLContext ctx = SSLContext.getInstance("SSL");
        ctx.init(null, getTrustManager(), new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        final HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(final String urlHostName, final SSLSession session) {
                if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
                    LOG.warn("Warning: URL host '" + urlHostName + "' is different to SSLSession host '" + session.getPeerHost() + "'.");
                }
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    private TrustManager[] getTrustManager() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    @Override
                    public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
                    }
                    @Override
                    public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
                    }
                }
        };
    }
}
