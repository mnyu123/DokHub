package com.DokHub.backend.controller;

import com.DokHub.backend.dto.ChannelDto;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // Vue 개발서버 주소 - CORS 문제때문에
public class ChannelController {

    @GetMapping("/api/channels")
    public List<ChannelDto> getChannels() {
        List<ChannelDto> channels = new ArrayList<>();

        // JSON 데이터들 하드코딩으로 나오게 한거임
        channels.add(new ChannelDto(
                "clip",
                "버추얼물류창고",
                "https://www.youtube.com/@%EB%B2%84%EB%AC%BC%EC%B0%BD",
                "https://img.youtube.com/vi/9SS-j0iWJiU/hqdefault.jpg"
        ));
        channels.add(new ChannelDto(
                "clip",
                "독케익영상저장용",
                "https://www.youtube.com/@poisoncakearchive",
                "https://img.youtube.com/vi/fmT0nJWokzE/hqdefault.jpg"
        ));
        channels.add(new ChannelDto(
                "song",
                "독래방저장소",
                "https://www.youtube.com/@%EA%B8%B0%EB%AC%B4%EC%A3%BC",
                "https://img.youtube.com/vi/PA-HVJo4poY/hqdefault.jpg"
        ));
        channels.add(new ChannelDto(
                "main",
                "독케익 본채널",
                "https://www.youtube.com/@%EB%8F%85%EC%BC%80%EC%9D%B5%EC%9C%A0%ED%8A%9C%EB%B8%8C",
                "https://img.youtube.com/vi/x7vWA6vqwZE/hqdefault.jpg"
        ));

        return channels;
    }
}