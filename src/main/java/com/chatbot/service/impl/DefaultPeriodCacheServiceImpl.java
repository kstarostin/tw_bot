package com.chatbot.service.impl;

import com.chatbot.service.PeriodCacheService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

// todo resolve highlighting, make more generic
public class DefaultPeriodCacheServiceImpl implements PeriodCacheService {
    private static DefaultPeriodCacheServiceImpl instance;

    private static final Map<String, PeriodCache> PERIOD_CACHE_MAP = new HashMap<>();

    private static final String CACHE_GREETING = "cache.greeting.";

    private static final String CACHE_EMOTES = "cache.emotes.";

    private static final String CACHE_VIDEO = "cache.video";

    private DefaultPeriodCacheServiceImpl() {
    }

    public static synchronized DefaultPeriodCacheServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultPeriodCacheServiceImpl();
        }
        return instance;
    }

    @Override
    public void cacheGreeting(final String channelName, final String userName) {
        Set<String> greetedUsersPerChannel;
        final String cacheName = CACHE_GREETING + channelName;

        final Optional<PeriodCache<Long, Set<String>>> greetingDayCacheOptional = getCacheByName(cacheName, (Class<Set<String>>) (Class<?>) Set.class);

        if (greetingDayCacheOptional.isPresent()) {
            greetedUsersPerChannel = greetingDayCacheOptional.get().getDayCache().get(getCacheKeyForPeriod(CachePeriod.DAY));
            if (greetedUsersPerChannel == null) {
                greetedUsersPerChannel = new HashSet<>();
                greetedUsersPerChannel.add(userName);
                cache(greetedUsersPerChannel, cacheName, CachePeriod.DAY);
            } else {
                greetedUsersPerChannel.add(userName);
            }
        } else {
            greetedUsersPerChannel = new HashSet<>();
            greetedUsersPerChannel.add(userName);
            cache(greetedUsersPerChannel, cacheName, CachePeriod.DAY);
        }
    }

    @Override
    public Optional<Set<String>> getCachedGreetings(final String channelName) {
        final String cacheName = CACHE_GREETING + channelName;
        final Optional<PeriodCache<Long, Set<String>>> greetingDayCacheOptional = getCacheByName(cacheName, (Class<Set<String>>) (Class<?>) Set.class);
        return greetingDayCacheOptional.map(longSetDayCache -> longSetDayCache.getDayCache().get(getCacheKeyForPeriod(CachePeriod.DAY)));
    }

    @Override
    public void cacheEmotes(final String channelName, final DefaultTwitchEmoteService.EmoteProvider provider, final List emoteList, final CachePeriod period) {
        Map<DefaultTwitchEmoteService.EmoteProvider, List> emotesPerProviderPerChannel;
        final String cacheName = CACHE_EMOTES + channelName;

        final Optional<PeriodCache<Long, Map<DefaultTwitchEmoteService.EmoteProvider, List>>> emotesMinuteCacheOptional =
                getCacheByName(cacheName, (Class<Map<DefaultTwitchEmoteService.EmoteProvider, List>>) (Class<?>) Map.class);

        if (emotesMinuteCacheOptional.isPresent()) {
            emotesPerProviderPerChannel = emotesMinuteCacheOptional.get().getDayCache().get(getCacheKeyForPeriod(period));
            if (emotesPerProviderPerChannel == null) {
                emotesPerProviderPerChannel = new HashMap<>();
                emotesPerProviderPerChannel.put(provider, emoteList);
                cache(emotesPerProviderPerChannel, cacheName, period);
            } else {
                emotesPerProviderPerChannel.put(provider, emoteList);
            }
        } else {
            emotesPerProviderPerChannel = new HashMap<>();
            emotesPerProviderPerChannel.put(provider, emoteList);
            cache(emotesPerProviderPerChannel, cacheName, period);
        }
    }

    @Override
    public Optional<List> getCachedEmotes(final String channelName, final DefaultTwitchEmoteService.EmoteProvider provider, final CachePeriod period) {
        final String cacheName = CACHE_EMOTES + channelName;
        final Optional<PeriodCache<Long, Map<DefaultTwitchEmoteService.EmoteProvider, List>>> emotesMinuteCacheOptional =
                getCacheByName(cacheName, (Class<Map<DefaultTwitchEmoteService.EmoteProvider, List>>) (Class<?>) Map.class);
        return emotesMinuteCacheOptional
                .map(longMapPeriodCache -> longMapPeriodCache.getDayCache().get(getCacheKeyForPeriod(period)))
                .map(emoteMap -> emoteMap.get(provider));
    }

    @Override
    public void cacheYtVideo(final String videoURL) {
        final Optional<PeriodCache<Long, String>> ytVideoDayCacheOptional = getCacheByName(CACHE_VIDEO, String.class);
        if (ytVideoDayCacheOptional.isPresent()) {
            ytVideoDayCacheOptional.get().getDayCache().put(getCacheKeyForPeriod(CachePeriod.DAY), videoURL);
        } else {
            cache(videoURL, CACHE_VIDEO, CachePeriod.DAY);
        }
    }

    @Override
    public Optional<String> getCachedYtVideo() {
        final Optional<PeriodCache<Long, String>> ytVideoDayCacheOptional = getCacheByName(CACHE_VIDEO, String.class);
        return ytVideoDayCacheOptional.map(longStringDayCache -> longStringDayCache.getDayCache().get(getTodayAsMilli()));
    }

    private <T> void cache(final T cachedObject, final String cacheName, final CachePeriod period) {
        final PeriodCache<Long, T> dayCache = new PeriodCache<>(getCacheKeyForPeriod(period), cachedObject);
        PERIOD_CACHE_MAP.put(cacheName, dayCache);
    }

    private <T> Optional<PeriodCache<Long, T>> getCacheByName(final String cacheName, final Class<T> type) {
        return Optional.ofNullable((PeriodCache<Long, T>) PERIOD_CACHE_MAP.get(cacheName));
    }

    private Long getTodayAsMilli() {
        return LocalDate.now().atStartOfDay().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
    }

    private Long getCurrentHourAsMilli() {
        final long hoursAsMilli = Duration.ofHours(LocalDateTime.now().getHour()).toMillis();
        return getTodayAsMilli() + hoursAsMilli;
    }

    private Long getCurrentMinuteAsMilli() {
        final long minutesAsMilli = Duration.ofMinutes(LocalDateTime.now().getMinute()).toMillis();
        return getCurrentHourAsMilli() + minutesAsMilli;
    }

    private static class PeriodCache<Long, T> {
        private final Map<Long, T> periodCache = new LinkedHashMap<>(1) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, T> entry) {
                return size() > 1;
            }
        };

        PeriodCache(final Long todayAsMilli, final T cachedObject) {
            periodCache.put(todayAsMilli, cachedObject);
        }

        public Map<Long, T> getDayCache() {
            return periodCache;
        }
    }

    private long getCacheKeyForPeriod(final CachePeriod period) {
        switch (period) {
            case MINUTE:
                return getCurrentMinuteAsMilli();
            case HOUR:
                return getCurrentHourAsMilli();
            case DAY:
            default:
                return getTodayAsMilli();
        }
    }

    public enum CachePeriod {
        DAY,
        HOUR,
        MINUTE
    }
}
