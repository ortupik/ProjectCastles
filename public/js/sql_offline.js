 alasql("CREATE TABLE IF NOT EXISTS `chats` ( \n\
            `user_id` INTEGER NOT NULL,\n\
            `chat_id` INTEGER NOT NULL,\n\
            `message_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n\
            `temp_message_id` TEXT NOT NULL,\n\
            `message` TEXT NOT NULL,\n\
            `type` text NOT NULL DEFAULT 'text',\n\
            `filepath` text NOT NULL,\n\
            `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n\
            `delivered` INTEGER NOT NULL DEFAULT '0'\n\
             );");
      