package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.user.UserService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class StartCommand implements Command {

    private final UserService userService;
    private final ScrapperClient scrapperClient;

    private static final String COMMAND = "/start";
    private static final String DESCRIPTION = "Регистрация пользователя";
    private static final String SUCCESS_MESSAGE =
        "Вы успешно зарегистрированы. Теперь вы можете использовать все команды.";
    private static final String ERROR_MESSAGE = "Произошла ошибка при регистрации.";

    public StartCommand(ScrapperClient scrapperClient, UserService userService) {
        this.userService = userService;
        this.scrapperClient = scrapperClient;
    }

    @Override
    public String command() {
        return COMMAND;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public SendMessage handle(Update update) {
        Long userId = update.message().from().id();
        Long chatId = update.message().chat().id();

        try {
            String finalMessage = scrapperClient.registerChat(userId)
                .then(Mono.just(SUCCESS_MESSAGE))
                .onErrorResume(e -> Mono.just(ERROR_MESSAGE))
                .block();
            userService.registerUser(userId);
            return new SendMessage(chatId.toString(), finalMessage);
        } catch (Exception e) {
            return new SendMessage(chatId.toString(), ERROR_MESSAGE);
        }
    }

    @Override
    public boolean supports(Update update, UserService userService) {
        if (update.message() != null && update.message().text() != null) {
            String messageText = update.message().text();
            Long userId = update.message().from().id();
            boolean isCommandMatch = messageText.startsWith(command());
            boolean isUserRegistered = userService.isRegistered(userId) || command().equals(COMMAND);

            return isCommandMatch && isUserRegistered;
        }
        return false;
    }
}
