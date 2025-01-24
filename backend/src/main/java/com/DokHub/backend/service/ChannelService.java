package com.DokHub.backend.service;

import com.DokHub.backend.dto.ChannelDto;
import com.DokHub.backend.dto.VideoInfoDto;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChannelService {

    private final YouTubeService youTubeService;
    private final List<ChannelDto> allChannels;

    // 생성자 주입 방식
    public ChannelService(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
        this.allChannels = new ArrayList<>();
        initializeChannels();
    }

    private void initializeChannels() {
        // API 호출 없이 기본 정보만 추가
        allChannels.add(new ChannelDto(
                "clip",
                "버추얼물류창고",
                "https://www.youtube.com/@버물창",
                null, // 썸네일 URL은 나중에 호출
                "UC8R4xLGXrTnILbFWSPti8yg",
                Collections.emptyList() // 최신 영상도 나중에 호출
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "독케익영상저장용",
                "https://www.youtube.com/@poisoncakearchive",
                null,
                "UCEnzqilSyRaUWGGwNFfUkzg",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "song",
                "독씌애앰",
                "https://www.youtube.com/@%EB%8F%85%EC%94%8C%EC%95%A0%EC%95%B0",
                null,
                "UCbgYUFVNlPxQqqicll4hHvw",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "main",
                "독케익 본채널",
                "https://www.youtube.com/@%EB%8F%85%EC%BC%80%EC%9D%B5%EC%9C%A0%ED%8A%9C%EB%B8%8C",
                null,
                "UCbWEWggYogZk0iZt9f0nHlA",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "거꾸로 서있는 사람",
                "https://www.youtube.com/@%EA%B1%B0%EA%BE%B8%EB%A1%9C%EC%84%9C%EC%9E%88%EB%8A%94%EC%82%AC%EB%9E%8C",
                null,
                "UCp_KsCexQyj8PgVT0XHbPvw",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "익명개떡이",
                "https://www.youtube.com/@Dog_mochi",
                null,
                "UCIlYdQYPA0BRHBiDU-ITVdQ",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "song",
                "S Y",
                "https://www.youtube.com/@SY-qt7ld",
                null,
                "UCZvqJOoHFb_QiuImH-w2spQ",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "짭케익 다시보기",
                "https://www.youtube.com/@%EC%A7%AD%EC%BC%80%EC%9D%B5",
                null,
                "UCJf5q7jtsBQW31ucEcXyRiQ",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "마이페이버릿파트",
                "https://www.youtube.com/@%EB%A7%88%ED%8E%98%ED%8C%8C",
                null,
                "UC5BehMed6DUNwrtgZhyIXrw",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "먹기 좋게 자른 독케익",
                "https://www.youtube.com/@%EB%A8%B9%EA%B8%B0%EC%A2%8B%EA%B2%8C%EC%9E%90%EB%A5%B8%EB%8F%85%EC%BC%80%EC%9D%B5",
                null,
                "UCL714SyNV-XUOzM7qwLx0FA",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "개떡떡이",
                "https://www.youtube.com/@%EA%B0%9C%EB%96%A1%EB%96%A1%EC%9D%B4",
                null,
                "UCouM900XrwwZ_0dAR7nObIg",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "자기개발 개떡이",
                "https://www.youtube.com/@%EC%9E%90%EA%B8%B0%EA%B0%9C%EB%B0%9C%EA%B0%9C%EB%96%A1%EC%9D%B4",
                null,
                "UCxbbrDTVUHHyvB8WJuX59Qg",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "중졸무직백수",
                "https://www.youtube.com/channel/UCs8kwn-14cOEKquR68mh4Zg",
                null,
                "UCs8kwn-14cOEKquR68mh4Zg",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "독케익 팬튜브",
                "https://www.youtube.com/@dogcakeshorts",
                null,
                "UCxCee1wSqopdOIyqQ75g45Q",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "디지털 짬통",
                "https://www.youtube.com/channel/UCFWy7mbDZxpLv1N66O5xQWg",
                null,
                "UCFWy7mbDZxpLv1N66O5xQWg",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "자계계",
                "https://www.youtube.com/channel/UCqn9I3-R_1wBpM2P6vM0TBA",
                null,
                "UCqn9I3-R_1wBpM2P6vM0TBA",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "song",
                "노래하는독케익",
                "https://www.youtube.com/channel/UC33I4AsN0E2IPyO_JVwDpVQ",
                null,
                "UC33I4AsN0E2IPyO_JVwDpVQ",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "유통기한확인하기",
                "https://www.youtube.com/@%EC%9C%A0%ED%86%B5%EA%B8%B0%ED%95%9C%ED%99%95%EC%9D%B8%ED%95%98%EA%B8%B0/videos",
                null,
                "UC6T0yYvaz24XtrCRcQHtMMg",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "main",
                "독케익 다시보기",
                "https://www.youtube.com/@%EB%8F%85%EC%BC%80%EC%9D%B5%EB%8B%A4%EC%8B%9C%EB%B3%B4%EA%B8%B0",
                null,
                "UCzdsBMcTdToWM4S72p49Dew",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "subchnn",
                "https://www.youtube.com/@subchnn",
                null,
                "UC2aGOk1Ql86hFyXmwuZmqOg",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "크림다롱",
                "https://www.youtube.com/@creamdarong/videos",
                null,
                "UCrXXir3JiWr7I3LAY1qUNVg",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "프레이야",
                "https://www.youtube.com/@%ED%94%84%EB%A0%88%EC%9D%B4%EC%95%BC-y3p",
                null,
                "UCWKF3pA7tfNVQvFgfkfkOLA",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "song",
                "하면하지",
                "https://www.youtube.com/@%ED%95%98%EB%A9%B4%ED%95%98%EC%A7%80-z7d",
                null,
                "UC47VyhReGD7kWhECSb7qFrA",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "song",
                "독래방저장소",
                "https://www.youtube.com/@%EA%B8%B0%EB%AC%B4%EC%A3%BC",
                null,
                "UCMUmreAMn1ddpj7lbPMBOgQ",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "버츄얼_놀이터",
                "https://www.youtube.com/@%EB%B2%84%EC%B8%84%EC%96%BC_%EB%86%80%EC%9D%B4%ED%84%B0/videos",
                null,
                "UCm7UYM-C8wuE19GtRRSPKAA",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "버츄얼 누렁이",
                "https://www.youtube.com/channel/UCch1rtV23tpy_sXl-40fmVA",
                null,
                "UCch1rtV23tpy_sXl-40fmVA",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "버츄얼발사대",
                "https://www.youtube.com/channel/UC7L6SFyG4n_2fFYjAsSbr1A",
                null,
                "UC7L6SFyG4n_2fFYjAsSbr1A",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "a",
                "https://www.youtube.com/@7-jb2ln",
                null,
                "UCdWjK8Nocloug0w3mxE0kjA",
                Collections.emptyList()
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "수리수리 무수리",
                "https://www.youtube.com/@antnfl0128",
                null,
                "UCGic9tqRszDA4XS-UZZqSbg",
                Collections.emptyList()
        ));
        // 필요한 만큼 추가...
    }

    @PostConstruct
    public void preloadInitialPage() {
        // 첫 페이지 로드에 해당하는 채널들 미리 로드
        String initialCategory = "clip"; // 초기 카테고리 설정 (예: "clip")
        int initialPage = 0;
        int size = 7;
        getChannelsPaged(initialCategory, initialPage, size);
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

        List<ChannelDto> pagedChannels = filtered.subList(startIndex, endIndex);

        // 3) 배치로 채널 썸네일 가져오기
        List<String> channelIds = pagedChannels.stream()
                .map(ChannelDto::getChannelId)
                .collect(Collectors.toList());
        Map<String, String> thumbnails = youTubeService.getChannelThumbnailsBatch(channelIds);

        // 4) 각 채널에 대해 썸네일과 최신 영상 정보를 로드
        List<String> channelsToFetchVideos = new ArrayList<>();
        pagedChannels.forEach(channel -> {
            // 썸네일 설정
            if (channel.getThumbnailUrl() == null) {
                channel.setThumbnailUrl(thumbnails.getOrDefault(channel.getChannelId(), "https://example.com/default_thumbnail.jpg"));
            }
            // 최신 영상 설정 필요 여부 확인
            if (channel.getRecentVideos() == null || channel.getRecentVideos().isEmpty()) {
                channelsToFetchVideos.add(channel.getChannelId());
            }
        });

        // 5) 최신 영상 한 번에 가져오기 (배치 처리 가능 시)
        if (!channelsToFetchVideos.isEmpty()) {
            Map<String, List<VideoInfoDto>> recentVideosMap = channelsToFetchVideos.stream()
                    .collect(Collectors.toMap(
                            channelId -> channelId,
                            channelId -> youTubeService.getRecentVideos(channelId)
                    ));

            // 6) 채널에 최신 영상 설정
            pagedChannels.forEach(channel -> {
                if (channelsToFetchVideos.contains(channel.getChannelId())) {
                    channel.setRecentVideos(recentVideosMap.get(channel.getChannelId()));
                }
            });
        }

        return pagedChannels;
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
