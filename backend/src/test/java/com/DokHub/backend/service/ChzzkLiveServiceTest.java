package com.DokHub.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ChzzkLiveServiceTest {

    @Test
    void repeatedRequestsUseCacheAndApiFailureDoesNotDisconnectChat() {
        RestTemplate template = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(template).build();
        ChzzkChatService chat = mock(ChzzkChatService.class);
        ChzzkLiveService service = new ChzzkLiveService(template, new ObjectMapper(), chat);
        ReflectionTestUtils.setField(service, "liveEnabled", true);
        ReflectionTestUtils.setField(service, "clientId", "test");
        ReflectionTestUtils.setField(service, "clientSecret", "test");
        String url = "https://openapi.chzzk.naver.com/open/v1/lives?size=20";
        server.expect(requestTo(url)).andRespond(withSuccess(
                "{\"content\":{\"data\":[{\"channelId\":\"b68af124ae2f1743a1dcbf5e2ab41e0b\"}]}}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(url)).andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        server.expect(requestTo(url)).andRespond(withSuccess(
                "{\"content\":{\"data\":[],\"page\":{}}}", MediaType.APPLICATION_JSON));

        assertTrue(service.isChannelLive());
        assertTrue(service.isChannelLive());
        verify(chat, times(1)).updateChatConnection(true);
        ReflectionTestUtils.setField(service, "cachedAt", 0L);
        assertTrue(service.isChannelLive());
        verify(chat, never()).updateChatConnection(false);
        ReflectionTestUtils.setField(service, "cachedAt", 0L);
        assertFalse(service.isChannelLive());
        verify(chat).updateChatConnection(false);
        server.verify();
    }
}
