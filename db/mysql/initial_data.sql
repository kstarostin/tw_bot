-- Insert for BotFeatureTypeEntity
INSERT INTO TW_BOT.BOT_FEATURE_TYPE (BOT_FEATURE_TYPE_ID, BOT_FEATURE_TYPE_TIMESTAMP, BOT_FEATURE_TYPE_CREATION_TIME, BOT_FEATURE_TYPE_CODE)
VALUES (1, 0, NOW(), 'logging'),
       (2, 0, NOW(), 'donation'),
       (3, 0, NOW(), 'follow'),
       (4, 0, NOW(), 'subscription'),
       (5, 0, NOW(), 'command'),
       (6, 0, NOW(), 'alive');

-- Insert for BotCommandTypeEntity
INSERT INTO TW_BOT.BOT_COMMAND_TYPE (BOT_COMMAND_TYPE_ID, BOT_COMMAND_TYPE_TIMESTAMP, BOT_COMMAND_TYPE_CREATION_TIME, BOT_COMMAND_TYPE_CODE)
VALUES (1, 0, NOW(), 'simple_response');

-- Insert for BotCommandTypeEntity
INSERT INTO TW_BOT.BOT_COMMAND_ACTION_TYPE (BOT_COMMAND_ACTION_TYPE_ID, BOT_COMMAND_ACTION_TYPE_TIMESTAMP, BOT_COMMAND_ACTION_TYPE_CREATION_TIME, BOT_COMMAND_ACTION_TYPE_CODE)
VALUES (1, 0, NOW(), 'simple_response');

-- Insert for ChannelConfigurationEntity
-- superadmin
-- test channel
INSERT INTO TW_BOT.CHANNEL (CHANNEL_ID, CHANNEL_TIMESTAMP, CHANNEL_CREATION_TIME, CHANNEL_NAME, GLOBAL_CONFIG_ID, CHANNEL_CONFIG_ID)
VALUES (1, 0, NOW(), '0mskBird', null, null);

-- Insert for GlobalConfigurationEntity
INSERT INTO TW_BOT.GLOBAL_CONFIG (GLOBAL_CONFIG_ID, GLOBAL_CONFIG_TIMESTAMP, GLOBAL_CONFIG_CREATION_TIME, GLOBAL_CONFIG_CODE, GLOBAL_CONFIG_SUPER_ADMIN_ID)
VALUES (1, 0, NOW(),'0mskBot', 1);

UPDATE TW_BOT.CHANNEL SET GLOBAL_CONFIG_ID = 1 WHERE CHANNEL_ID = 1;

-- Insert for GlobalConfigurationEntity.activeGlobalFeatures
INSERT INTO TW_BOT.GLOBAL_CONFIG_GLOBAL_FEATURE (GLOBAL_CONFIG_BOT_FEATURE_ID, GLOBAL_CONFIG_ID, BOT_FEATURE_TYPE_ID)
VALUES (1, 1, 1),
       (2, 1, 2),
       (3, 1, 3),
       (4, 1, 4),
       (5, 1, 5),
       (6, 1, 6);
