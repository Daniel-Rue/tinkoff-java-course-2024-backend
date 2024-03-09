package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.user.UserService;
import edu.java.bot.user.UserState;
import edu.java.model.dto.request.AddLinkRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrackCommandTest {

    @Mock
    private ScrapperClient scrapperClient;

    @Mock
    private UserService userService;

    @Mock
    private Update update;

    @Mock
    private Message message;

    @Mock
    private User user;

    @Mock
    private Chat chat;

    @InjectMocks
    private TrackCommand trackCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(update.message()).thenReturn(message);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(123L);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);
    }

    @Test
    void testAwaitingLink() {
        when(message.text()).thenReturn("/track");
        when(userService.getUserState(eq(user.id()))).thenReturn(UserState.NONE);

        when(scrapperClient.addLink(anyLong(), any())).thenReturn(Mono.empty());

        SendMessage response = trackCommand.handle(update);

        verify(userService, times(1)).setUserState(eq(user.id()), eq(UserState.AWAITING_LINK));
        Assertions.assertEquals("Пожалуйста, укажите ссылку для отслеживания.", response.getParameters().get("text"));
    }

    @Test
    void testReceivingLinkAfterCommand() {
        when(message.text()).thenReturn("https://github.com/user/repo");
        when(userService.getUserState(user.id())).thenReturn(UserState.AWAITING_LINK);

        when(scrapperClient.addLink(eq(user.id()), any(AddLinkRequest.class))).thenReturn(Mono.empty());

        SendMessage response = trackCommand.handle(update);

        verify(scrapperClient, times(1)).addLink(eq(user.id()), any(AddLinkRequest.class));
        verify(userService, times(1)).setUserState(eq(user.id()), eq(UserState.NONE));
    }
}
