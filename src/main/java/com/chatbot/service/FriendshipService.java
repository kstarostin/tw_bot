package com.chatbot.service;

public interface FriendshipService {

    boolean isFriend(String userName);

    boolean isTwitchFriend(String userName);

    boolean isDiscordFriend(String userNameDiscriminator);
}
