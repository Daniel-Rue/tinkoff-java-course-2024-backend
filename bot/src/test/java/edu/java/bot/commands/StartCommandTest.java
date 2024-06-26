package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StartCommandTest {

    @Mock
    private UserService userService;

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private Update update;

    @Mock
    private Message message;

    @Mock
    private User user;

    @Mock
    private Chat chat;

    private StartCommand startCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(update.message()).thenReturn(message);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(123L);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);

        startCommand = new StartCommand(scrapperClient, userService);

        when(scrapperClient.registerChat(anyLong())).thenReturn(Mono.empty());
    }

    @Test
    void testUserRegistrationSuccess() {
        when(userService.isRegistered(anyLong())).thenReturn(false);
        SendMessage response = startCommand.handle(update);

        verify(scrapperClient, times(1)).registerChat(123L);
        verify(userService, times(1)).registerUser(123L);
        Assertions.assertEquals(
            "Вы успешно зарегистрированы. Теперь вы можете использовать все команды.",
            response.getParameters().get("text")
        );
    }

    @Test
    void testUserRegistrationFailure() {
        when(scrapperClient.registerChat(anyLong())).thenReturn(Mono.error(new RuntimeException("Registration failed")));

        SendMessage response = startCommand.handle(update);

        Assertions.assertEquals(
            "Произошла ошибка при регистрации.",
            response.getParameters().get("text")
        );
    }
}
