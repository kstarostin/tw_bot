package com.chatbot.service.impl;

import com.chatbot.service.DayCacheService;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

// todo resolve highlighting, make more generic
public class DefaultDayCacheServiceImpl implements DayCacheService {
    private static DefaultDayCacheServiceImpl instance;

    private static final Map<String, DayCache> DAY_CACHE_MAP = new HashMap<>();

    private static final String GREETING = "cache.day.greeting";

    private static final String YT_VIDEO_OF_DAY = "cache.day.video";

    private DefaultDayCacheServiceImpl() {
    }

    public static synchronized DefaultDayCacheServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultDayCacheServiceImpl();
        }
        return instance;
    }

    @Override
    public void cacheGreeting(final String userName) {
        Set<String> greetedUsers;
        final Optional<DayCache<Long, Set>> greetingDayCacheOptional = getCacheByName(GREETING, Set.class);
        if (greetingDayCacheOptional.isPresent()) {
            greetedUsers = (Set<String>) greetingDayCacheOptional.get().getDayCache().get(getTodayAsMilli());
            if (greetedUsers == null) {
                greetedUsers = new HashSet<>();
                greetedUsers.add(userName);
                cache(greetedUsers, GREETING, Set.class);
            } else {
                greetedUsers.add(userName);
            }
        } else {
            greetedUsers = new HashSet<>();
            greetedUsers.add(userName);
            cache(greetedUsers, GREETING, Set.class);
        }
    }

    @Override
    public Optional<Set> getCachedGreetings() {
        final Optional<DayCache<Long, Set>> greetingDayCacheOptional = getCacheByName(GREETING, Set.class);
        if (greetingDayCacheOptional.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(greetingDayCacheOptional.get().getDayCache().get(getTodayAsMilli()));
    }

    @Override
    public void cacheYtVideo(final String videoURL) {
        final Optional<DayCache<Long, String>> ytVideoDayCacheOptional = getCacheByName(YT_VIDEO_OF_DAY, String.class);
        if (ytVideoDayCacheOptional.isPresent()) {
            ytVideoDayCacheOptional.get().getDayCache().put(getTodayAsMilli(), videoURL);
        } else {
            cache(videoURL, YT_VIDEO_OF_DAY, String.class);
        }
    }

    @Override
    public Optional<String> getCachedYtVideo() {
        final Optional<DayCache<Long, String>> ytVideoDayCacheOptional = getCacheByName(YT_VIDEO_OF_DAY, String.class);
        if (ytVideoDayCacheOptional.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(ytVideoDayCacheOptional.get().getDayCache().get(getTodayAsMilli()));
    }

    private <T> void cache(final Object cachedObject, final String cacheName, final Class<T> type) {
        final DayCache<Long, T> dayCache = new DayCache(getTodayAsMilli(), cachedObject);
        DAY_CACHE_MAP.put(cacheName, dayCache);
    }

    private <T> Optional<DayCache<Long, T>> getCacheByName(final String cacheName, final Class<T> type) {
        return Optional.ofNullable(DAY_CACHE_MAP.get(cacheName));
    }

    private Long getTodayAsMilli() {
        return LocalDate.now().atStartOfDay().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
    }

    private static class DayCache<Long, T> {
        private final Map<Long, T> dayCache = new LinkedHashMap<>(1) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, T> entry) {
                return size() > 1;
            }
        };

        DayCache(final Long todayAsMilli, final T cachedObject) {
            dayCache.put(todayAsMilli, cachedObject);
        }

        public Map<Long, T> getDayCache() {
            return dayCache;
        }
    }
}
