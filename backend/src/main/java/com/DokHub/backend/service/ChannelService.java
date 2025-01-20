package com.DokHub.backend.service;

import com.DokHub.backend.dto.ChannelDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChannelService {

    private final List<ChannelDto> allChannels;

    public ChannelService() {
        // 기존의 Controller에 있던 하드코딩된 리스트를 옮깁니다.
        this.allChannels = new ArrayList<>();
        allChannels.add(new ChannelDto(
                "clip",
                "버추얼물류창고",
                "https://www.youtube.com/@%EB%B2%84%EB%AC%BC%EC%B0%BD",
                "https://img.youtube.com/vi/9SS-j0iWJiU/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "독케익영상저장용",
                "https://www.youtube.com/@poisoncakearchive",
                "https://img.youtube.com/vi/fmT0nJWokzE/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "song",
                "독씌애앰",
                "https://www.youtube.com/@%EB%8F%85%EC%94%8C%EC%95%A0%EC%95%B0",
                "https://img.youtube.com/vi/114i8k__oV8/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "song",
                "S Y",
                "https://www.youtube.com/@SY-qt7ld",
                "https://img.youtube.com/vi/19tDI9kyQGc/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "거꾸로 서있는 사람",
                "https://www.youtube.com/@%EA%B1%B0%EA%BE%B8%EB%A1%9C%EC%84%9C%EC%9E%88%EB%8A%94%EC%82%AC%EB%9E%8C",
                "https://img.youtube.com/vi/mxuKBGSs-2I/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "익명개떡이",
                "https://www.youtube.com/@Dog_mochi",
                "https://img.youtube.com/vi/rbUS6F9BicE/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "짭케익 다시보기",
                "https://www.youtube.com/@%EC%A7%AD%EC%BC%80%EC%9D%B5",
                "https://img.youtube.com/vi/523QLAiqYvk/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "독케익 팬튜브",
                "https://www.youtube.com/@%EB%8F%85%EC%BC%80%EC%9D%B5%ED%8C%AC%ED%8A%9C%EB%B8%8C-e2v",
                "https://img.youtube.com/vi/4D9folAzYFE/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "마이페이버릿파트",
                "https://www.youtube.com/@%EB%A7%88%ED%8E%98%ED%8C%8C",
                "https://img.youtube.com/vi/vau48wQ6VNQ/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "먹기 좋게 자른 독케익",
                "https://www.youtube.com/@%EB%A8%B9%EA%B8%B0%EC%A2%8B%EA%B2%8C%EC%9E%90%EB%A5%B8%EB%8F%85%EC%BC%80%EC%9D%B5",
                "https://img.youtube.com/vi/StMNTXju_xA/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "개떡떡이",
                "https://www.youtube.com/@%EA%B0%9C%EB%96%A1%EB%96%A1%EC%9D%B4",
                "https://img.youtube.com/vi/nyhrccMwKfQ/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "song",
                "하면하지",
                "https://www.youtube.com/@%ED%95%98%EB%A9%B4%ED%95%98%EC%A7%80-z7d",
                "https://img.youtube.com/vi/h36PgYY9w1E/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "자기개발 개떡이",
                "https://www.youtube.com/@%EC%9E%90%EA%B8%B0%EA%B0%9C%EB%B0%9C%EA%B0%9C%EB%96%A1%EC%9D%B4",
                "https://img.youtube.com/vi/IXmYh70ecDQ/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "song",
                "독래방저장소",
                "https://www.youtube.com/@%EA%B8%B0%EB%AC%B4%EC%A3%BC",
                "https://img.youtube.com/vi/PA-HVJo4poY/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "중졸무직백수",
                "https://www.youtube.com/channel/UCs8kwn-14cOEKquR68mh4Zg",
                "https://img.youtube.com/vi/dWVch0FeFlI/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "버츄얼_놀이터",
                "https://www.youtube.com/@%EB%B2%84%EC%B8%84%EC%96%BC_%EB%86%80%EC%9D%B4%ED%84%B0/videos",
                "https://img.youtube.com/vi/nDWpyGt_Pxc/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "버츄얼 누렁이",
                "https://www.youtube.com/channel/UCch1rtV23tpy_sXl-40fmVA",
                "https://img.youtube.com/vi/uNwwaOzPX5w/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "버츄얼발사대",
                "https://www.youtube.com/channel/UC7L6SFyG4n_2fFYjAsSbr1A",
                "https://img.youtube.com/vi/DH6lQO9itcE/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "독케익 쇼츠 공방",
                "https://www.youtube.com/@dogcakeshorts",
                "https://img.youtube.com/vi/0q4Mntonmqw/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "디지털 짬통",
                "https://www.youtube.com/channel/UCFWy7mbDZxpLv1N66O5xQWg",
                "https://img.youtube.com/vi/vGmGesA2npA/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "clip",
                "자계계",
                "https://www.youtube.com/channel/UCqn9I3-R_1wBpM2P6vM0TBA",
                "https://img.youtube.com/vi/raLrru8ttaA/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "song",
                "노래하는독케익",
                "https://www.youtube.com/channel/UC33I4AsN0E2IPyO_JVwDpVQ",
                "https://img.youtube.com/vi/Bmuhreba5O0/hqdefault.jpg"
        ));
        allChannels.add(new ChannelDto(
                "main",
                "독케익 본채널",
                "https://www.youtube.com/@%EB%8F%85%EC%BC%80%EC%9D%B5%EC%9C%A0%ED%8A%9C%EB%B8%8C",
                "https://img.youtube.com/vi/x7vWA6vqwZE/hqdefault.jpg"
        ));
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

