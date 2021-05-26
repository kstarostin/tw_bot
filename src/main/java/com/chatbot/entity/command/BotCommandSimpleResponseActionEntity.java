package com.chatbot.entity.command;

import com.chatbot.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "BOT_COMMAND_SIMPLE_RESPONSE_ACTION")
@PrimaryKeyJoinColumn(name = "BOT_COMMAND_SIMPLE_RESPONSE_ACTION_ID")
public class BotCommandSimpleResponseActionEntity extends BotCommandActionEntity implements Serializable {
    private String responseTemplate;

    public BotCommandSimpleResponseActionEntity() {
        this.creationTime = new Date();
    }

    @Column(name="BOT_COMMAND_SIMPLE_RESPONSE_ACTION_RESPONSE_TEMPLATE")
    public String getResponseTemplate() {
        return responseTemplate;
    }

    public void setResponseTemplate(String responseTemplate) {
        this.responseTemplate = responseTemplate;
    }

}
