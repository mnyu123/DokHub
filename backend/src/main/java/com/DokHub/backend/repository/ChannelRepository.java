package com.DokHub.backend.repository;

import com.DokHub.backend.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {

    // 카테고리별 데이터 찾기
    List<ChannelEntity> findByCategoryIgnoreCase(String category);

}
