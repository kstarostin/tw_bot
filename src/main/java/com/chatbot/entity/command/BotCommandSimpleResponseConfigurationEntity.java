package com.chatbot.entity.command;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "BOT_COMMAND_SIMPLE_RESPONSE_CONFIGURATION")
@PrimaryKeyJoinColumn(name = "BOT_COMMAND_SIMPLE_RESPONSE_CONFIGURATION_ID")
public class BotCommandSimpleResponseConfigurationEntity extends BotCommandConfigurationEntity implements Serializable {
}
