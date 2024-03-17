package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.exception.ApiErrorResponseException;
import edu.java.bot.user.UserService;
import edu.java.bot.user.UserState;
import edu.java.model.dto.request.RemoveLinkRequest;
import java.net.URI;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UntrackCommand implements Command {

    private final ScrapperClient scrapperClient;
    private final UserService userService;

    private static final String COMMAND = "/untrack";
    private static final String DESCRIPTION = "Удалить указанную ссылку или все ссылки.";
    private static final String REQUEST_LINK_MESSAGE = "Пожалуйста, укажите ссылку или 'all' для удаления.";
    private static final String ALL_LINKS_REMOVED_MESSAGE = "Все ссылки удалены.";
    private static final String LINK_REMOVED_SUCCESS_MESSAGE = "Ссылка успешно удалена.";
    private static final String LINK_NOT_FOUND_MESSAGE = "Указанная ссылка не найдена.";
    private static final String UNKNOWN_COMMAND = "Неизвестная команда.";
    private static final String ERROR_MESSAGE = "Произошла ошибка при удалении ссылки.";

    public UntrackCommand(ScrapperClient scrapperClient, UserService userService) {
        this.scrapperClient = scrapperClient;
        this.userService = userService;
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
        UserState state = userService.getUserState(userId);

        if (state == UserState.NONE) {
            userService.setUserState(userId, UserState.AWAITING_UNTRACK_LINK);
            return new SendMessage(chatId.toString(), REQUEST_LINK_MESSAGE);
        } else if (state == UserState.AWAITING_UNTRACK_LINK) {
            userService.setUserState(userId, UserState.NONE);
            String messageText = update.message().text().trim();

            return removeLink(userId, messageText, chatId);
        }

        return new SendMessage(chatId.toString(), UNKNOWN_COMMAND);
    }

    private SendMessage removeLink(Long userId, String linkText, Long chatId) {
        try {
            URI link = URI.create(linkText.trim());
            RemoveLinkRequest request = new RemoveLinkRequest(link);
            String finalMessage = scrapperClient.removeLink(userId, request)
                .then(Mono.just(LINK_REMOVED_SUCCESS_MESSAGE))
                .onErrorResume(ApiErrorResponseException.class, ex -> Mono.just(ex.getApiErrorResponse().description()))
                .block();
            return new SendMessage(chatId.toString(), finalMessage);
        } catch (Exception e) {
            return new SendMessage(chatId.toString(), ERROR_MESSAGE);
        }
    }

    @Override
    public boolean supports(Update update, UserService userService) {
        Long userId = update.message().from().id();
        UserState state = userService.getUserState(userId);
        String messageText = update.message().text();

        boolean isUntrackCommand = COMMAND.equals(messageText);
        boolean isUserAwaitingUntrackLink = state == UserState.AWAITING_UNTRACK_LINK;

        return isUntrackCommand || isUserAwaitingUntrackLink;
    }
}

