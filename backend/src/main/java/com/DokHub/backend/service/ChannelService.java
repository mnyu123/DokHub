package com.DokHub.backend.service;

import com.DokHub.backend.dto.ChannelDto;
import com.DokHub.backend.entity.ChannelEntity;
import com.DokHub.backend.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
public class ChannelService {

    @Autowired
    private ChannelRepository channelRepository;

    /**
     * 특정 카테고리(category)로 필터링 후, 페이지 번호(page), 페이지 크기(size)에 맞춰 채널 목록을 반환
     */
    public List<ChannelDto> getChannelsPaged(String category, int page, int size) {
        // 1) DB에서 해당 카테고리 목록 조회 (대소문자 무시)
        List<ChannelEntity> filtered = channelRepository.findByCategoryIgnoreCase(category);

        // 2) 수동 페이징 처리
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, filtered.size());
        if (startIndex >= filtered.size()) {
            return Collections.emptyList();
        }

        // 3) subList로 페이지에 해당하는 엔티티만 추출
        List<ChannelEntity> pageList = filtered.subList(startIndex, endIndex);

        // 4) 엔티티 → DTO 변환
        return pageList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 카테고리(category)에 해당하는 채널 개수 반환
     */
    public int getTotalCount(String category) {
        // DB에서 해당 카테고리의 목록을 찾아 size를 구함
        List<ChannelEntity> list = channelRepository.findByCategoryIgnoreCase(category);
        return list.size();
    }

    /**
     * 엔티티를 DTO로 변환하는 메서드
     */
    private ChannelDto entityToDto(ChannelEntity entity) {
        ChannelDto dto = new ChannelDto();
        dto.setCategory(entity.getCategory());
        dto.setChannelName(entity.getChannelName());
        dto.setChannelLink(entity.getChannelLink());
        dto.setVideoPreviewUrl(entity.getVideoPreviewUrl());
        return dto;
    }
}
