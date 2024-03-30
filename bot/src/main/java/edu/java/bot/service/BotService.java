package edu.java.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BotService {
    private final ApplicationConfig applicationConfig;
    private final CommandService commandService;
    private TelegramBot bot;

    @Autowired
    public BotService(ApplicationConfig applicationConfig, CommandService commandService) {
        this.applicationConfig = applicationConfig;
        this.commandService = commandService;
    }

    @PostConstruct
    public void startBot() {
        bot = new TelegramBot(applicationConfig.telegramToken());
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::handleUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void handleUpdate(Update update) {
        commandService.processUpdate(update, bot);
    }

    public void sendMessage(Long chatId, String message) {
        SendMessage request = new SendMessage(chatId, message);
        bot.execute(request);
    }
}
