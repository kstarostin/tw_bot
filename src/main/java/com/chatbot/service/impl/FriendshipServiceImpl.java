package com.chatbot.service.impl;

import com.chatbot.service.FriendshipService;

import java.util.Set;

public class FriendshipServiceImpl implements FriendshipService {
    private static FriendshipServiceImpl instance;
    private static final Set<String> TWITCH_USER_NAME_FRIEND_LIST = Set.of("0mskbird", "yura_atlet", "1skybox1", "chenushka", "hereticjz", "skvdee", "svetloholmov", "prof_133", "kiber_bober",
            "poni_prancing", "greyraise", "panthermania", "tachvnkin", "tesla013", "shinigamidth", "enteris");
    private static final Set<String> DISCORD_USER_NAME_FRIEND_LIST = Set.of("0mskbird#8322", "saber#1488", "skybox#9021", "чхеа#7542", "heretic#0639", "skadi#6982", "prof133#9685", "kiberbober#8581",
            "poni_prancing#4848", "51/50#4434", "panthermania#0001", "дядя мун#0369", "tesla013#4178", "g-shi#6345", "enteris#7033");

    private FriendshipServiceImpl () {
    }

    public static synchronized FriendshipServiceImpl getInstance() {
        if (instance == null) {
            instance = new FriendshipServiceImpl();
        }
        return instance;
    }

    @Override
    public boolean isFriend(String userName) {
        return isTwitchFriend(userName) || isDiscordFriend(userName);
    }

    @Override
    public boolean isTwitchFriend(String userName) {
        return TWITCH_USER_NAME_FRIEND_LIST.contains(userName.toLowerCase());
    }

    @Override
    public boolean isDiscordFriend(String userNameDiscriminator) {
        return DISCORD_USER_NAME_FRIEND_LIST.contains(userNameDiscriminator.toLowerCase());
    }
}
