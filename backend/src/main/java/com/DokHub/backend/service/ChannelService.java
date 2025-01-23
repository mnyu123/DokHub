package com.DokHub.backend.service;

import com.DokHub.backend.dto.ChannelDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChannelService {

    private final YouTubeService youTubeService;
    private final List<ChannelDto> allChannels;

    // 생성자 주입 방식으로 수정
    public ChannelService(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
        this.allChannels = new ArrayList<>();

        // 채널 초기화
        initializeChannels();
    }

    private void initializeChannels() {
        // 채널 데이터 추가 (예시)
        allChannels.add(new ChannelDto(
                "clip",
                "버추얼물류창고",
                "https://www.youtube.com/@버물창",
                youTubeService.getChannelThumbnail("UC8R4xLGXrTnILbFWSPti8yg"), // 썸네일 URL
                "UC8R4xLGXrTnILbFWSPti8yg", // 채널 ID
                youTubeService.getRecentVideos("UC8R4xLGXrTnILbFWSPti8yg") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "독케익영상저장용",
                "https://www.youtube.com/@poisoncakearchive",
                youTubeService.getChannelThumbnail("UCEnzqilSyRaUWGGwNFfUkzg"), // 썸네일 URL
                "UCEnzqilSyRaUWGGwNFfUkzg", // 채널 ID
                youTubeService.getRecentVideos("UCEnzqilSyRaUWGGwNFfUkzg") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "song",
                "독씌애앰",
                "https://www.youtube.com/@%EB%8F%85%EC%94%8C%EC%95%A0%EC%95%B0",
                youTubeService.getChannelThumbnail("UCbgYUFVNlPxQqqicll4hHvw"), // 썸네일 URL
                "UCbgYUFVNlPxQqqicll4hHvw", // 채널 ID
                youTubeService.getRecentVideos("UCbgYUFVNlPxQqqicll4hHvw") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "main",
                "독케익 본채널",
                "https://www.youtube.com/@%EB%8F%85%EC%BC%80%EC%9D%B5%EC%9C%A0%ED%8A%9C%EB%B8%8C",
                youTubeService.getChannelThumbnail("UCbWEWggYogZk0iZt9f0nHlA"), // 썸네일 URL
                "UCbWEWggYogZk0iZt9f0nHlA", // 채널 ID
                youTubeService.getRecentVideos("UCbWEWggYogZk0iZt9f0nHlA") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "거꾸로 서있는 사람",
                "https://www.youtube.com/@%EA%B1%B0%EA%BE%B8%EB%A1%9C%EC%84%9C%EC%9E%88%EB%8A%94%EC%82%AC%EB%9E%8C",
                youTubeService.getChannelThumbnail("UCp_KsCexQyj8PgVT0XHbPvw"), // 썸네일 URL
                "UCp_KsCexQyj8PgVT0XHbPvw", // 채널 ID
                youTubeService.getRecentVideos("UCp_KsCexQyj8PgVT0XHbPvw") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "익명개떡이",
                "https://www.youtube.com/@Dog_mochi",
                youTubeService.getChannelThumbnail("UCIlYdQYPA0BRHBiDU-ITVdQ"), // 썸네일 URL
                "UCIlYdQYPA0BRHBiDU-ITVdQ", // 채널 ID
                youTubeService.getRecentVideos("UCIlYdQYPA0BRHBiDU-ITVdQ") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "song",
                "S Y",
                "https://www.youtube.com/@SY-qt7ld",
                youTubeService.getChannelThumbnail("UCZvqJOoHFb_QiuImH-w2spQ"), // 썸네일 URL
                "UCZvqJOoHFb_QiuImH-w2spQ", // 채널 ID
                youTubeService.getRecentVideos("UCZvqJOoHFb_QiuImH-w2spQ") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "짭케익 다시보기",
                "https://www.youtube.com/@%EC%A7%AD%EC%BC%80%EC%9D%B5",
                youTubeService.getChannelThumbnail("UCJf5q7jtsBQW31ucEcXyRiQ"), // 썸네일 URL
                "UCJf5q7jtsBQW31ucEcXyRiQ", // 채널 ID
                youTubeService.getRecentVideos("UCJf5q7jtsBQW31ucEcXyRiQ") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "독케익 팬튜브",
                "https://www.youtube.com/@%EB%8F%85%EC%BC%80%EC%9D%B5%ED%8C%AC%ED%8A%9C%EB%B8%8C-e2v",
                youTubeService.getChannelThumbnail("UC3BtDxwGTV9sp0Z6fn88hnQ"), // 썸네일 URL
                "UC3BtDxwGTV9sp0Z6fn88hnQ", // 채널 ID
                youTubeService.getRecentVideos("UC3BtDxwGTV9sp0Z6fn88hnQ") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "마이페이버릿파트",
                "https://www.youtube.com/@%EB%A7%88%ED%8E%98%ED%8C%8C",
                youTubeService.getChannelThumbnail("UC5BehMed6DUNwrtgZhyIXrw"), // 썸네일 URL
                "UC5BehMed6DUNwrtgZhyIXrw", // 채널 ID
                youTubeService.getRecentVideos("UC5BehMed6DUNwrtgZhyIXrw") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "먹기 좋게 자른 독케익",
                "https://www.youtube.com/@%EB%A8%B9%EA%B8%B0%EC%A2%8B%EA%B2%8C%EC%9E%90%EB%A5%B8%EB%8F%85%EC%BC%80%EC%9D%B5",
                youTubeService.getChannelThumbnail("UCL714SyNV-XUOzM7qwLx0FA"), // 썸네일 URL
                "UCL714SyNV-XUOzM7qwLx0FA", // 채널 ID
                youTubeService.getRecentVideos("UCL714SyNV-XUOzM7qwLx0FA") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "개떡떡이",
                "https://www.youtube.com/@%EA%B0%9C%EB%96%A1%EB%96%A1%EC%9D%B4",
                youTubeService.getChannelThumbnail("UCouM900XrwwZ_0dAR7nObIg"), // 썸네일 URL
                "UCouM900XrwwZ_0dAR7nObIg", // 채널 ID
                youTubeService.getRecentVideos("UCouM900XrwwZ_0dAR7nObIg") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "song",
                "하면하지",
                "https://www.youtube.com/@%ED%95%98%EB%A9%B4%ED%95%98%EC%A7%80-z7d",
                youTubeService.getChannelThumbnail("UC47VyhReGD7kWhECSb7qFrA"), // 썸네일 URL
                "UC47VyhReGD7kWhECSb7qFrA", // 채널 ID
                youTubeService.getRecentVideos("UC47VyhReGD7kWhECSb7qFrA") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "자기개발 개떡이",
                "https://www.youtube.com/@%EC%9E%90%EA%B8%B0%EA%B0%9C%EB%B0%9C%EA%B0%9C%EB%96%A1%EC%9D%B4",
                youTubeService.getChannelThumbnail("UCxbbrDTVUHHyvB8WJuX59Qg"), // 썸네일 URL
                "UCxbbrDTVUHHyvB8WJuX59Qg", // 채널 ID
                youTubeService.getRecentVideos("UCxbbrDTVUHHyvB8WJuX59Qg") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "song",
                "독래방저장소",
                "https://www.youtube.com/@%EA%B8%B0%EB%AC%B4%EC%A3%BC",
                youTubeService.getChannelThumbnail("UCMUmreAMn1ddpj7lbPMBOgQ"), // 썸네일 URL
                "UCMUmreAMn1ddpj7lbPMBOgQ", // 채널 ID
                youTubeService.getRecentVideos("UCMUmreAMn1ddpj7lbPMBOgQ") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "중졸무직백수",
                "https://www.youtube.com/channel/UCs8kwn-14cOEKquR68mh4Zg",
                youTubeService.getChannelThumbnail("UCs8kwn-14cOEKquR68mh4Zg"), // 썸네일 URL
                "UCs8kwn-14cOEKquR68mh4Zg", // 채널 ID
                youTubeService.getRecentVideos("UCs8kwn-14cOEKquR68mh4Zg") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "버츄얼_놀이터",
                "https://www.youtube.com/@%EB%B2%84%EC%B8%84%EC%96%BC_%EB%86%80%EC%9D%B4%ED%84%B0/videos",
                youTubeService.getChannelThumbnail("UCm7UYM-C8wuE19GtRRSPKAA"), // 썸네일 URL
                "UCm7UYM-C8wuE19GtRRSPKAA", // 채널 ID
                youTubeService.getRecentVideos("UCm7UYM-C8wuE19GtRRSPKAA") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "버츄얼 누렁이",
                "https://www.youtube.com/channel/UCch1rtV23tpy_sXl-40fmVA",
                youTubeService.getChannelThumbnail("UCch1rtV23tpy_sXl-40fmVA"), // 썸네일 URL
                "UCch1rtV23tpy_sXl-40fmVA", // 채널 ID
                youTubeService.getRecentVideos("UCch1rtV23tpy_sXl-40fmVA") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "버츄얼발사대",
                "https://www.youtube.com/channel/UC7L6SFyG4n_2fFYjAsSbr1A",
                youTubeService.getChannelThumbnail("UC7L6SFyG4n_2fFYjAsSbr1A"), // 썸네일 URL
                "UC7L6SFyG4n_2fFYjAsSbr1A", // 채널 ID
                youTubeService.getRecentVideos("UC7L6SFyG4n_2fFYjAsSbr1A") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "독케익 쇼츠 공방",
                "https://www.youtube.com/@dogcakeshorts",
                youTubeService.getChannelThumbnail("UCxCee1wSqopdOIyqQ75g45Q"), // 썸네일 URL
                "UCxCee1wSqopdOIyqQ75g45Q", // 채널 ID
                youTubeService.getRecentVideos("UCxCee1wSqopdOIyqQ75g45Q") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "디지털 짬통",
                "https://www.youtube.com/channel/UCFWy7mbDZxpLv1N66O5xQWg",
                youTubeService.getChannelThumbnail("UCFWy7mbDZxpLv1N66O5xQWg"), // 썸네일 URL
                "UCFWy7mbDZxpLv1N66O5xQWg", // 채널 ID
                youTubeService.getRecentVideos("UCFWy7mbDZxpLv1N66O5xQWg") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "자계계",
                "https://www.youtube.com/channel/UCqn9I3-R_1wBpM2P6vM0TBA",
                youTubeService.getChannelThumbnail("UCqn9I3-R_1wBpM2P6vM0TBA"), // 썸네일 URL
                "UCqn9I3-R_1wBpM2P6vM0TBA", // 채널 ID
                youTubeService.getRecentVideos("UCqn9I3-R_1wBpM2P6vM0TBA") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "song",
                "노래하는독케익",
                "https://www.youtube.com/channel/UC33I4AsN0E2IPyO_JVwDpVQ",
                youTubeService.getChannelThumbnail("UC33I4AsN0E2IPyO_JVwDpVQ"), // 썸네일 URL
                "UC33I4AsN0E2IPyO_JVwDpVQ", // 채널 ID
                youTubeService.getRecentVideos("UC33I4AsN0E2IPyO_JVwDpVQ") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "유통기한확인하기",
                "https://www.youtube.com/@%EC%9C%A0%ED%86%B5%EA%B8%B0%ED%95%9C%ED%99%95%EC%9D%B8%ED%95%98%EA%B8%B0/videos",
                youTubeService.getChannelThumbnail("UC6T0yYvaz24XtrCRcQHtMMg"), // 썸네일 URL
                "UC6T0yYvaz24XtrCRcQHtMMg", // 채널 ID
                youTubeService.getRecentVideos("UC6T0yYvaz24XtrCRcQHtMMg") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "main",
                "독케익 다시보기",
                "https://www.youtube.com/@%EB%8F%85%EC%BC%80%EC%9D%B5%EB%8B%A4%EC%8B%9C%EB%B3%B4%EA%B8%B0",
                youTubeService.getChannelThumbnail("UCzdsBMcTdToWM4S72p49Dew"), // 썸네일 URL
                "UCzdsBMcTdToWM4S72p49Dew", // 채널 ID
                youTubeService.getRecentVideos("UCzdsBMcTdToWM4S72p49Dew") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "subchnn",
                "https://www.youtube.com/@subchnn",
                youTubeService.getChannelThumbnail("UC2aGOk1Ql86hFyXmwuZmqOg"), // 썸네일 URL
                "UC2aGOk1Ql86hFyXmwuZmqOg", // 채널 ID
                youTubeService.getRecentVideos("UC2aGOk1Ql86hFyXmwuZmqOg") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "크림다롱",
                "https://www.youtube.com/@creamdarong/videos",
                youTubeService.getChannelThumbnail("UCrXXir3JiWr7I3LAY1qUNVg"), // 썸네일 URL
                "UCrXXir3JiWr7I3LAY1qUNVg", // 채널 ID
                youTubeService.getRecentVideos("UCrXXir3JiWr7I3LAY1qUNVg") // 최신 영상
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "프레이야",
                "https://www.youtube.com/@%ED%94%84%EB%A0%88%EC%9D%B4%EC%95%BC-y3p",
                youTubeService.getChannelThumbnail("UCWKF3pA7tfNVQvFgfkfkOLA"), // 썸네일 URL
                "UCWKF3pA7tfNVQvFgfkfkOLA", // 채널 ID
                youTubeService.getRecentVideos("UCWKF3pA7tfNVQvFgfkfkOLA") // 최신 영상
        ));
        // 나머지 채널들도 동일하게 추가...
    }

    /**
     * 특정 카테고리(category)로 필터링 후, 페이지 번호(page), 페이지 크기(size)에 맞춰 채널 목록을 반환
     */
    public List<ChannelDto> getChannelsPaged(String category, int page, int size) {
        // 1) category로 필터링
        List<ChannelDto> filtered = allChannels.stream()
                .filter(ch -> ch.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        // 2) 페이징 처리
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, filtered.size());

        if (startIndex >= filtered.size()) {
            return Collections.emptyList();
        }

        return filtered.subList(startIndex, endIndex);
    }

    /**
     * 특정 카테고리(category)에 해당하는 채널 개수 반환
     */
    public int getTotalCount(String category) {
        return (int) allChannels.stream()
                .filter(ch -> ch.getCategory().equalsIgnoreCase(category))
                .count();
    }
}
