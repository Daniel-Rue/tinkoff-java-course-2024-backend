package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.user.UserService;
import edu.java.bot.user.UserState;
import edu.java.model.dto.response.ListLinksResponse;
import java.util.StringJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ListCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListCommand.class);
    private final ScrapperClient scrapperClient;

    private static final String COMMAND = "/list";
    private static final String DESCRIPTION = "Вывести все отслеживаемые ссылки.";
    private static final String EMPTY_LINKS_MESSAGE = "На данный момент вы не отслеживаете никакие ссылки.";
    private static final String TRACKED_LINKS_MESSAGE_TITLE = "Ваши отслеживаемые ссылки:";
    private static final String ERROR_MESSAGE = "Произошла ошибка при получении списка ссылок.";

    public ListCommand(ScrapperClient scrapperClient) {
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
            ListLinksResponse response = scrapperClient.getAllLinks(userId)
                .block();

            if (response.links().isEmpty()) {
                return new SendMessage(chatId.toString(), EMPTY_LINKS_MESSAGE);
            } else {
                StringJoiner message = new StringJoiner("\n", TRACKED_LINKS_MESSAGE_TITLE + "\n", "");
                response.links().forEach(link -> message.add(link.url().toString()));
                return new SendMessage(chatId.toString(), message.toString());
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching links for userId: {}", userId, e);
            return new SendMessage(chatId.toString(), ERROR_MESSAGE);
        }
    }

    @Override
    public boolean supports(Update update, UserService userService) {
        if (update.message() != null && update.message().text() != null) {
            Long userId = update.message().from().id();
            UserState userState = userService.getUserState(userId);
            return COMMAND.equals(update.message().text()) && userState == UserState.NONE;
        }
        return false;
    }
}
