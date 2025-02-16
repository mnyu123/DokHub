package com.DokHub.backend.repository;

import com.DokHub.backend.entity.ChannelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {

    // 카테고리별 데이터 찾기
    List<ChannelEntity> findByCategoryIgnoreCase(String category);

    // 최신 업로드 시간을 기준으로 내림차순 정렬하여 페이징 처리(2025.02.16 사용)
    Page<ChannelEntity> findByCategoryIgnoreCaseOrderByLatestUploadDesc(String category, Pageable pageable);

}
