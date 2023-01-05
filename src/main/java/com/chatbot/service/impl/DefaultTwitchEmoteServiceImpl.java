package com.chatbot.service.impl;

import com.chatbot.service.ConfigurationService;
import com.chatbot.service.PeriodCacheService;
import com.chatbot.service.RandomizerService;
import com.chatbot.service.TwitchClientService;
import com.chatbot.service.TwitchEmoteService;
import com.chatbot.util.emotes.TwitchEmote;
import com.chatbot.util.emotes.bttv.BTTV;
import com.chatbot.util.emotes.bttv.BTTVEmote;
import com.chatbot.util.emotes.ffz.FFZ;
import com.chatbot.util.emotes.ffz.FFZEmoticon;
import com.chatbot.util.emotes.ffz.FFZGlobal;
import com.chatbot.util.emotes.ffz.FFZRootObject;
import com.chatbot.util.emotes.seventv.SevenTVEmote;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.twitch4j.helix.domain.Emote;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.chatbot.util.emotes.TwitchEmote.Sets.EMOTE_COMBINATIONS;

public class DefaultTwitchEmoteServiceImpl implements TwitchEmoteService {
    private static DefaultTwitchEmoteServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultTwitchEmoteServiceImpl.class);

    private static final String GLOBAL = "global";

    private static final String USER_ID_PATH_VAR = "${userId}";

    private static final String API_URL_7TV = "https://api.7tv.app/v2/";
    private static final String API_CHANNEL_EMOTES_PATH_7TV = "users/" + USER_ID_PATH_VAR + "/emotes";
    private static final String API_GLOBAL_EMOTES_PATH_7TV = "emotes/global";

    private static final String API_URL_FFZ = "https://api.frankerfacez.com/v1/";
    private static final String API_ROOM_PATH_FFZ = "room/id/" + USER_ID_PATH_VAR;
    private static final String API_GLOBAL_EMOTES_PATH_FFZ = "set/global";

    private static final String API_URL_BTTV = "https://api.betterttv.net/3/";
    private static final String API_CHANNEL_EMOTES_PATH_BTTV = "cached/users/twitch/" + USER_ID_PATH_VAR;
    private static final String API_GLOBAL_EMOTES_PATH_BTTV = "cached/emotes/global";

    private static final Set<Long> FFZ_SET_ID_LIST_TO_IGNORE = Set.of(4330L);

    final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final TwitchClientService twitchClientService = DefaultTwitchClientServiceImpl.getInstance();
    private final ConfigurationService configurationService = DefaultConfigurationServiceImpl.getInstance();
    private final PeriodCacheService cacheService = DefaultPeriodCacheServiceImpl.getInstance();
    private final RandomizerService randomizerService = DefaultRandomizerServiceImpl.getInstance();

    private DefaultTwitchEmoteServiceImpl() {
    }

    public static synchronized DefaultTwitchEmoteServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultTwitchEmoteServiceImpl();
        }
        return instance;
    }

    @Override
    public List<SevenTVEmote> getGlobal7TVEmotes() {
        final String requestUrl = API_URL_7TV + API_GLOBAL_EMOTES_PATH_7TV;
        return get7TVEmotes(GLOBAL, requestUrl);
    }

    @Override
    public List<SevenTVEmote> getChannel7TVEmotes(final String channelId) {
        final String requestUrl = API_URL_7TV + API_CHANNEL_EMOTES_PATH_7TV.replace(USER_ID_PATH_VAR, channelId.toLowerCase());
        return get7TVEmotes(channelId, requestUrl);
    }

    @Override
    public List<BTTVEmote> getGlobalBTTVEmotes() {
        final String requestUrl = API_URL_BTTV + API_GLOBAL_EMOTES_PATH_BTTV;
        final List<BTTVEmote> emotes = new ArrayList<>();
        final Optional<List> globalEmotesOptional = cacheService.getCachedEmotes(GLOBAL, EmoteProvider.BTTV_GLOBAL, DefaultPeriodCacheServiceImpl.CachePeriod.DAY);
        if (globalEmotesOptional.isPresent()) {
            LOG.debug("Global BTTV emotes loaded from the cache...");
            emotes.addAll(globalEmotesOptional.get());
        } else {
            try {
                doTrustToCertificates();
                final JSONArray jsonArray = new JSONArray(IOUtils.toString(new URL(requestUrl), StandardCharsets.UTF_8));
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        emotes.add(objectMapper.readValue(jsonArray.getJSONObject(i).toString(), BTTVEmote.class));
                    }
                }
            } catch (final Exception e) {
                LOG.debug("Error details: ", e);
            }
            LOG.debug("Global BTTV emotes loaded from API...");
            cacheService.cacheEmotes(GLOBAL, EmoteProvider.BTTV_GLOBAL, emotes, DefaultPeriodCacheServiceImpl.CachePeriod.DAY);
        }
        return emotes;
    }

    @Override
    public List<BTTVEmote> getChannelBTTVEmotes(final String channelId) {
        final String requestUrl = API_URL_BTTV + API_CHANNEL_EMOTES_PATH_BTTV.replace(USER_ID_PATH_VAR, channelId.toLowerCase());
        final List<BTTVEmote> emotes = new ArrayList<>();
        final Optional<List> channelEmotesOptional = cacheService.getCachedEmotes(channelId, EmoteProvider.BTTV_CHANNEL, DefaultPeriodCacheServiceImpl.CachePeriod.MINUTE);
        if (channelEmotesOptional.isPresent()) {
            LOG.debug("Channel BTTV emotes loaded from the cache...");
            emotes.addAll(channelEmotesOptional.get());
        } else {
            try {
                doTrustToCertificates();
                final JSONObject json = new JSONObject(IOUtils.toString(new URL(requestUrl), StandardCharsets.UTF_8));
                final BTTV bttv = objectMapper.readValue(json.toString(), BTTV.class);
                emotes.addAll(CollectionUtils.emptyIfNull(Arrays.asList(bttv.getSharedEmotes())));
            } catch (final Exception e) {
                LOG.debug("Error details: ", e);
            }
            LOG.debug("Channel BTTV emotes loaded from API...");
            cacheService.cacheEmotes(channelId, EmoteProvider.BTTV_CHANNEL, emotes, DefaultPeriodCacheServiceImpl.CachePeriod.MINUTE);
        }
        return emotes;
    }

    @Override
    public List<FFZEmoticon> getGlobalFFZEmotes() {
        final String requestUrl = API_URL_FFZ + API_GLOBAL_EMOTES_PATH_FFZ;
        return getFFZEmotes(GLOBAL, requestUrl, FFZGlobal.class);
    }

    @Override
    public List<FFZEmoticon> getChannelFFZEmotes(final String channelId) {
        final String requestUrl = API_URL_FFZ + API_ROOM_PATH_FFZ.replace(USER_ID_PATH_VAR, channelId.toLowerCase());
        return getFFZEmotes(channelId, requestUrl, FFZ.class);
    }

    @Override
    public List<Emote> getGlobalTwitchEmotes() {
        final List<Emote> globalTwitchEmotes = new ArrayList<>();
        final Optional<List> globalEmotesOptional = cacheService.getCachedEmotes(GLOBAL, EmoteProvider.TWITCH_GLOBAL, DefaultPeriodCacheServiceImpl.CachePeriod.DAY);
        if (globalEmotesOptional.isPresent()) {
            LOG.debug("Global Twitch emotes loaded from the cache...");
            globalTwitchEmotes.addAll(globalEmotesOptional.get());
        } else {
            final String autToken = configurationService.getCredentialProperties().getProperty("twitch.credentials.access.token");
            try {
                globalTwitchEmotes.addAll(twitchClientService.getTwitchHelixClient().getGlobalEmotes(autToken).execute().getEmotes());
            } catch (final Exception e) {
                LOG.debug("Error details: ", e);
            }
            LOG.debug("Global Twitch emotes loaded from API...");
            cacheService.cacheEmotes(GLOBAL, EmoteProvider.TWITCH_GLOBAL, globalTwitchEmotes, DefaultPeriodCacheServiceImpl.CachePeriod.DAY);
        }
        return globalTwitchEmotes;
    }

    @Override
    public List<Emote> getChannelTwitchEmotes(final String channelId) {
        final List<Emote> channelTwitchEmotes = new ArrayList<>();
        final Optional<List> channelEmotesOptional = cacheService.getCachedEmotes(channelId, EmoteProvider.TWITCH_CHANNEL, DefaultPeriodCacheServiceImpl.CachePeriod.MINUTE);
        if (channelEmotesOptional.isPresent()) {
            LOG.debug("Channel Twitch emotes loaded from the cache...");
            channelTwitchEmotes.addAll(channelEmotesOptional.get());
        } else {
            final String autToken = configurationService.getCredentialProperties().getProperty("twitch.credentials.access.token");
            try {
                channelTwitchEmotes.addAll(twitchClientService.getTwitchHelixClient().getChannelEmotes(autToken, channelId).execute().getEmotes());
            } catch (final Exception e) {
                LOG.debug("Error details: ", e);
            }
            LOG.debug("Channel Twitch emotes loaded from API...");
            cacheService.cacheEmotes(channelId, EmoteProvider.TWITCH_CHANNEL, channelTwitchEmotes, DefaultPeriodCacheServiceImpl.CachePeriod.MINUTE);
        }
        return channelTwitchEmotes;
    }

    @Override
    public Set<String> getValidEmoteCodes(final String channelId) {
        final Set<String> emoteCodes = getGlobalTwitchEmotes().stream().map(Emote::getName).collect(Collectors.toSet());
        emoteCodes.addAll(getChannelTwitchEmotes(channelId).stream().map(Emote::getName).collect(Collectors.toSet()));
        emoteCodes.addAll(getGlobalBTTVEmotes().stream().map(BTTVEmote::getCode).collect(Collectors.toSet()));
        emoteCodes.addAll(getChannelBTTVEmotes(channelId).stream().map(BTTVEmote::getCode).collect(Collectors.toSet()));
        //emoteCodes.addAll(getGlobalFFZEmotes().stream().map(FFZEmoticon::getName).collect(Collectors.toSet()));
        //emoteCodes.addAll(getChannelFFZEmotes(channelId).stream().map(FFZEmoticon::getName).collect(Collectors.toSet()));
        emoteCodes.addAll(getGlobal7TVEmotes().stream().map(SevenTVEmote::getName).collect(Collectors.toSet()));
        emoteCodes.addAll(getChannel7TVEmotes(channelId).stream().map(SevenTVEmote::getName).collect(Collectors.toSet()));
        return emoteCodes;
    }

    @SafeVarargs
    @Override
    public final String buildRandomEmoteLine(final String channelId, final int maxNumberOfEmotes, final List<TwitchEmote>... emoteSets) {
        final List<TwitchEmote> selectedEmotes = buildRandomEmoteList(channelId, maxNumberOfEmotes, emoteSets);
        return buildEmoteLine(channelId, selectedEmotes);
    }

    @SafeVarargs
    @Override
    public final List<TwitchEmote> buildRandomEmoteList(final String channelId, final int maxNumberOfEmotes, final List<TwitchEmote>... emoteSets) {
        final int numberOfEmotes = randomizerService.rollDiceExponentially(maxNumberOfEmotes, 2) + 1;

        final List<TwitchEmote> selectedEmotes = new ArrayList<>();
        for (int i = 0; i < numberOfEmotes; i++) {
            if (i > 0) {
                final TwitchEmote previousEmote = selectedEmotes.get(i - 1);
                if (EMOTE_COMBINATIONS.containsKey(previousEmote) && randomizerService.flipCoin() && isEmote(channelId, previousEmote.toString())) {
                    selectedEmotes.add(EMOTE_COMBINATIONS.get(previousEmote));
                } else if (randomizerService.flipCoin()) {
                    selectedEmotes.add(previousEmote);
                } else {
                    selectedEmotes.add(getRandomEmoteFromSets(channelId, emoteSets));
                }
            } else {
                selectedEmotes.add(getRandomEmoteFromSets(channelId, emoteSets));
            }
        }
        return selectedEmotes;
    }

    @Override
    public boolean isEmote(final String channelId, final String text) {
        return getValidEmoteCodes(channelId).contains(text);
    }

    @SafeVarargs
    private TwitchEmote getRandomEmoteFromSets(final String channelId, final List<TwitchEmote>... emoteSets) {
        final int setNumber = randomizerService.rollDiceExponentially(emoteSets.length, 2);
        final List<TwitchEmote> selectedSet = emoteSets[setNumber].parallelStream().filter(emote -> isEmote(channelId, emote.toString())).collect(Collectors.toList());
        final int index = randomizerService.rollDiceExponentially(selectedSet.size(), 2);
        return selectedSet.get(index);
    }

    private String buildEmoteLine(final String channelId, final List<TwitchEmote> emotes) {
        final StringBuilder emotePart = new StringBuilder();
        emotes.stream()
                .filter(emote -> isEmote(channelId, emote.toString()))
                .forEach(emote -> emotePart.append(StringUtils.SPACE).append(emote));
        return emotePart.toString().trim();
    }

    private List<SevenTVEmote> get7TVEmotes(final String channelId, final String url) {
        final List<SevenTVEmote> emotes = new ArrayList<>();
        final DefaultPeriodCacheServiceImpl.CachePeriod period = GLOBAL.equals(channelId) ? DefaultPeriodCacheServiceImpl.CachePeriod.DAY : DefaultPeriodCacheServiceImpl.CachePeriod.MINUTE;
        final EmoteProvider provider = GLOBAL.equals(channelId) ? EmoteProvider.SEVEN_TV_GLOBAL : EmoteProvider.SEVEN_TV_CHANNEL;
        final Optional<List> emotesOptional = cacheService.getCachedEmotes(channelId, provider, period);
        if (emotesOptional.isPresent()) {
            LOG.debug("{} 7TV emotes loaded from the cache...", GLOBAL.equals(channelId) ? "Global" : "Channel");
            emotes.addAll(emotesOptional.get());
        } else {
            try {
                doTrustToCertificates();
                final JSONArray jsonArray = new JSONArray(IOUtils.toString(new URL(url), StandardCharsets.UTF_8));
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        emotes.add(objectMapper.readValue(jsonArray.getJSONObject(i).toString(), SevenTVEmote.class));
                    }
                }
            } catch (final Exception e) {
                LOG.debug("Error details: ", e);
            }
            LOG.debug("{} 7TV emotes loaded from API...", GLOBAL.equals(channelId) ? "Global" : "Channel");
            cacheService.cacheEmotes(channelId, provider, emotes, period);
        }
        return emotes;
    }

    private <T extends FFZRootObject> List<FFZEmoticon> getFFZEmotes(final String channelId, final String url, final Class<T> rootClass) {
        final List<FFZEmoticon> emotes = new ArrayList<>();
        final DefaultPeriodCacheServiceImpl.CachePeriod period = GLOBAL.equals(channelId) ? DefaultPeriodCacheServiceImpl.CachePeriod.DAY : DefaultPeriodCacheServiceImpl.CachePeriod.HOUR;
        final EmoteProvider provider = GLOBAL.equals(channelId) ? EmoteProvider.FFZ_GLOBAL : EmoteProvider.FFZ_CHANNEL;
        final Optional<List> emotesOptional = cacheService.getCachedEmotes(channelId, provider, period);
        if (emotesOptional.isPresent()) {
            LOG.debug("{} FFZ emotes loaded from the cache...", GLOBAL.equals(channelId) ? "Global" : "Channel");
            emotes.addAll(emotesOptional.get());
        } else {
            try {
                doTrustToCertificates();
                final JSONObject json = new JSONObject(IOUtils.toString(new URL(url), StandardCharsets.UTF_8));
                final T ffz = objectMapper.readValue(json.toString(), rootClass);
                emotes.addAll(CollectionUtils.emptyIfNull(ffz.getSets().values()).stream()
                        .filter(set -> !FFZ_SET_ID_LIST_TO_IGNORE.contains(set.getId()))
                        .map(set -> Arrays.asList(set.getEmoticons()))
                        .flatMap(Collection::stream)
                        .filter(emoticon -> !emoticon.isHidden())
                        .collect(Collectors.toList()));
            } catch (final Exception e) {
                LOG.debug("Error details: ", e);
            }
            LOG.debug("{} FFZ emotes loaded from API...", GLOBAL.equals(channelId) ? "Global" : "Channel");
            cacheService.cacheEmotes(channelId, provider, emotes, period);
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

    public enum EmoteProvider {
        TWITCH_CHANNEL,
        TWITCH_GLOBAL,
        FFZ_CHANNEL,
        FFZ_GLOBAL,
        BTTV_CHANNEL,
        BTTV_GLOBAL,
        SEVEN_TV_CHANNEL,
        SEVEN_TV_GLOBAL
    }
}
