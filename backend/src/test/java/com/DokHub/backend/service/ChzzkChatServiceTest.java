package com.DokHub.backend.service;

import com.DokHub.backend.repository.ChatMessageRepository;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import xyz.r2turntrue.chzzk4j.ChzzkClient;
import xyz.r2turntrue.chzzk4j.chat.ChzzkChat;
import xyz.r2turntrue.chzzk4j.chat.event.ConnectionClosedEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChzzkChatServiceTest {

    private ChzzkChatService service;
    private ChzzkClient client;
    private ChzzkChat chat;
    private OkHttpClient httpClient;
    private CompletableFuture<Void> connection;
    private final AtomicReference<Consumer<ConnectionClosedEvent>> closeHandler = new AtomicReference<>();

    @BeforeEach
    void configureService() throws Exception {
        service = spy(new ChzzkChatService(mock(ChatMessageRepository.class)));
        ReflectionTestUtils.setField(service, "chatEnabled", true);
        ReflectionTestUtils.setField(service, "apiClientId", "test");
        ReflectionTestUtils.setField(service, "apiSecret", "test");
        ReflectionTestUtils.setField(service, "nidAut", "test");
        ReflectionTestUtils.setField(service, "nidSes", "test");
        client = mock(ChzzkClient.class);
        chat = mock(ChzzkChat.class);
        httpClient = mock(OkHttpClient.class, RETURNS_DEEP_STUBS);
        connection = new CompletableFuture<>();
        when(client.getHttpClient()).thenReturn(httpClient);
        when(client.loginAsync()).thenReturn(CompletableFuture.completedFuture(null));
        when(chat.connectAsync()).thenReturn(connection);
        when(chat.closeAsync()).thenReturn(CompletableFuture.completedFuture(null));
        doReturn(client).when(service).createChatClient(anyString(), anyString());
        doReturn(chat).when(service).createChatSession(client);
        doAnswer(invocation -> {
            closeHandler.set(invocation.getArgument(1));
            return null;
        }).when(chat).on(eq(ConnectionClosedEvent.class), any());
    }

    @AfterEach
    void shutdownService() {
        service.shutdown();
        connection.complete(null);
    }

    @Test
    void livePollingDoesNotReplacePendingConnectionAndClosingReleasesClient() throws Exception {
        service.updateChatConnection(true);
        verify(chat, timeout(2000)).connectAsync();
        for (int index = 0; index < 20; index++) {
            service.updateChatConnection(true);
        }
        verify(service, times(1)).createChatClient(anyString(), anyString());
        service.updateChatConnection(false);
        verify(chat).closeAsync();
        verify(httpClient.dispatcher()).cancelAll();
        verify(httpClient.connectionPool()).evictAll();
        verify(httpClient.dispatcher().executorService()).shutdown();
    }

    @Test
    void duplicateCloseEventsQueueOneRetryAndOfflineCancelsIt() {
        service.updateChatConnection(true);
        verify(chat, timeout(2000)).connectAsync();
        ConnectionClosedEvent event = mock(ConnectionClosedEvent.class);
        when(event.getCode()).thenReturn(4003);
        closeHandler.get().accept(event);
        ScheduledFuture<?> firstRetry = (ScheduledFuture<?>) ReflectionTestUtils.getField(service, "reconnectTask");
        assertNotNull(firstRetry);
        closeHandler.get().accept(event);
        service.updateChatConnection(true);
        assertSame(firstRetry, ReflectionTestUtils.getField(service, "reconnectTask"));
        service.updateChatConnection(false);
        assertTrue(firstRetry.isCancelled());
        closeHandler.get().accept(event);
        assertNull(ReflectionTestUtils.getField(service, "reconnectTask"));
    }

    @Test
    void asynchronousFailureReleasesClientAndSchedulesOneRetry() {
        service.updateChatConnection(true);
        verify(chat, timeout(2000)).connectAsync();
        service.updateChatConnection(true);
        connection.completeExceptionally(new IllegalStateException("Test connection failure"));
        verify(httpClient.dispatcher()).cancelAll();
        assertNotNull(ReflectionTestUtils.getField(service, "reconnectTask"));
        service.updateChatConnection(false);
        assertNull(ReflectionTestUtils.getField(service, "reconnectTask"));
    }
}
