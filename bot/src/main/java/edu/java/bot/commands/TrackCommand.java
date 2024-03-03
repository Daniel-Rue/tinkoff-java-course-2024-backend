package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.exception.ApiErrorResponseException;
import edu.java.bot.user.UserService;
import edu.java.bot.user.UserState;
import edu.java.model.dto.request.AddLinkRequest;
import java.net.URI;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TrackCommand implements Command {
    private final ScrapperClient scrapperClient;
    private final UserService userService;
    private static final String COMMAND = "/track";
    private static final String DESCRIPTION = "Отслеживает новую ссылку";
    private static final String REQUEST_LINK_MESSAGE = "Пожалуйста, укажите ссылку для отслеживания.";
    private static final String INVALID_LINK_FORMAT_MESSAGE =
        "Неправильный формат ссылки. Поддерживаются только ссылки на GitHub и StackOverflow.";
    private static final String LINK_ADDED_SUCCESS_MESSAGE = "Ссылка успешно добавлена для отслеживания.";
    private static final String UNKNOWN_COMMAND = "Неизвестная команда.";
    private static final String ERROR_MESSAGE = "Произошла ошибка при добавлении ссылки.";
    private static final Pattern GITHUB_PATTERN = Pattern.compile("^https://github\\.com/.+/.+$");
    private static final Pattern STACK_OVERFLOW_PATTERN = Pattern.compile("^https://stackoverflow\\.com/questions/.+$");

    public TrackCommand(ScrapperClient scrapperClient, UserService userService) {
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
        String message = UNKNOWN_COMMAND;

        if (state == UserState.NONE || state == UserState.AWAITING_LINK) {
            if (state == UserState.NONE) {
                userService.setUserState(userId, UserState.AWAITING_LINK);
                message = REQUEST_LINK_MESSAGE;
            } else {
                String messageText = update.message().text().trim();
                if (!isLinkValid(messageText)) {
                    userService.setUserState(userId, UserState.NONE);
                    message = INVALID_LINK_FORMAT_MESSAGE;
                } else {
                    try {
                        String finalMessage =
                            scrapperClient.addLink(userId, new AddLinkRequest(URI.create(messageText)))
                                .map(response -> LINK_ADDED_SUCCESS_MESSAGE)
                                .onErrorResume(
                                    ApiErrorResponseException.class,
                                    ex -> Mono.just(ex.getApiErrorResponse().description())
                                )
                                .block();
                        userService.setUserState(userId, UserState.NONE);
                        message = finalMessage;
                    } catch (Exception e) {
                        message = ERROR_MESSAGE;
                    }
                }
            }
        }

        return new SendMessage(chatId.toString(), message);
    }

    @Override
    public boolean supports(Update update, UserService userService) {
        Long userId = update.message().from().id();
        UserState state = userService.getUserState(userId);
        String messageText = update.message().text();
        boolean isTrackCommand = COMMAND.equals(messageText);
        boolean isUserAwaitingLink = state == UserState.AWAITING_LINK;

        return isTrackCommand || isUserAwaitingLink;
    }

    private boolean isLinkValid(String link) {
        return GITHUB_PATTERN.matcher(link).matches() || STACK_OVERFLOW_PATTERN.matcher(link).matches();
    }
}
