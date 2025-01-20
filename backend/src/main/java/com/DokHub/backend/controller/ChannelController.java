package com.DokHub.backend.controller;

import com.DokHub.backend.dto.ChannelDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChannelController {

    // 기본 경로("/") 매핑 추가
    @GetMapping("/")
    public String home() {
        return "Dokhub 404 notnot found";
    }

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
                "독씌애앰",
                "https://www.youtube.com/@%EB%8F%85%EC%94%8C%EC%95%A0%EC%95%B0",
                "https://img.youtube.com/vi/114i8k__oV8/hqdefault.jpg"
        ));
        channels.add(new ChannelDto(
                "song",
                "S Y",
                "https://www.youtube.com/@SY-qt7ld",
                "https://img.youtube.com/vi/19tDI9kyQGc/hqdefault.jpg"
        ));
        channels.add(new ChannelDto(
                "clip",
                "거꾸로 서있는 사람",
                "https://www.youtube.com/@%EA%B1%B0%EA%BE%B8%EB%A1%9C%EC%84%9C%EC%9E%88%EB%8A%94%EC%82%AC%EB%9E%8C",
                "https://img.youtube.com/vi/mxuKBGSs-2I/hqdefault.jpg"
        ));
        channels.add(new ChannelDto(
                "clip",
                "익명개떡이",
                "https://www.youtube.com/@Dog_mochi",
                "https://img.youtube.com/vi/rbUS6F9BicE/hqdefault.jpg"
        ));
        channels.add(new ChannelDto(
                "clip",
                "짭케익 다시보기",
                "https://www.youtube.com/@%EC%A7%AD%EC%BC%80%EC%9D%B5",
                "https://img.youtube.com/vi/523QLAiqYvk/hqdefault.jpg"
        ));
        channels.add(new ChannelDto(
                "clip",
                "독케익 팬튜브",
                "https://www.youtube.com/@%EB%8F%85%EC%BC%80%EC%9D%B5%ED%8C%AC%ED%8A%9C%EB%B8%8C-e2v",
                "https://img.youtube.com/vi/4D9folAzYFE/hqdefault.jpg"
        ));
        channels.add(new ChannelDto(
                "clip",
                "마이페이버릿파트",
                "https://www.youtube.com/@%EB%A7%88%ED%8E%98%ED%8C%8C",
                "https://img.youtube.com/vi/vau48wQ6VNQ/hqdefault.jpg"
        ));
        channels.add(new ChannelDto(
                "clip",
                "먹기 좋게 자른 독케익",
                "https://www.youtube.com/@%EB%A8%B9%EA%B8%B0%EC%A2%8B%EA%B2%8C%EC%9E%90%EB%A5%B8%EB%8F%85%EC%BC%80%EC%9D%B5",
                "https://img.youtube.com/vi/StMNTXju_xA/hqdefault.jpg"
        ));
        channels.add(new ChannelDto(
                "clip",
                "개떡떡이",
                "https://www.youtube.com/@%EA%B0%9C%EB%96%A1%EB%96%A1%EC%9D%B4",
                "https://img.youtube.com/vi/nyhrccMwKfQ/hqdefault.jpg"
        ));
        channels.add(new ChannelDto(
                "song",
                "하면하지",
                "https://www.youtube.com/@%ED%95%98%EB%A9%B4%ED%95%98%EC%A7%80-z7d",
                "https://img.youtube.com/vi/h36PgYY9w1E/hqdefault.jpg"
        ));
        channels.add(new ChannelDto(
                "clip",
                "자기개발 개떡이",
                "https://www.youtube.com/@%EC%9E%90%EA%B8%B0%EA%B0%9C%EB%B0%9C%EA%B0%9C%EB%96%A1%EC%9D%B4",
                "https://img.youtube.com/vi/IXmYh70ecDQ/hqdefault.jpg"
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