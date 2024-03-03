package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.model.dto.response.LinkResponse;
import edu.java.model.dto.response.ListLinksResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class ListCommandTest {

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

    private ListCommand listCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(update.message()).thenReturn(message);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(123L);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);

        listCommand = new ListCommand(scrapperClient);
    }

    @Test
    void testEmptyListMessage() {
        when(scrapperClient.getAllLinks(anyLong())).thenReturn(Mono.just(new ListLinksResponse(List.of(), 0)));

        SendMessage response = listCommand.handle(update);

        Assertions.assertEquals(
            "На данный момент вы не отслеживаете никакие ссылки.",
            response.getParameters().get("text")
        );
    }

    @Test
    void testSingleLinkMessage() {
        List<LinkResponse> links = List.of(new LinkResponse(1L, URI.create("https://example.com")));
        when(scrapperClient.getAllLinks(anyLong())).thenReturn(Mono.just(new ListLinksResponse(links, 1)));

        SendMessage response = listCommand.handle(update);

        Assertions.assertEquals(
            "Ваши отслеживаемые ссылки:\nhttps://example.com",
            response.getParameters().get("text")
        );
    }

    @Test
    void testMultipleLinksMessage() {
        List<LinkResponse> links = Arrays.asList(
            new LinkResponse(1L, URI.create("https://example.com")),
            new LinkResponse(2L, URI.create("https://example.org"))
        );
        when(scrapperClient.getAllLinks(anyLong())).thenReturn(Mono.just(new ListLinksResponse(links,2)));

        SendMessage response = listCommand.handle(update);

        Assertions.assertEquals(
            "Ваши отслеживаемые ссылки:\nhttps://example.com\nhttps://example.org",
            response.getParameters().get("text")
        );
    }
}
