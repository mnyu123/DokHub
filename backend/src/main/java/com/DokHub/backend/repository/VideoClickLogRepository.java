package com.DokHub.backend.repository;

import com.DokHub.backend.entity.VideoClickLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoClickLogRepository extends JpaRepository<VideoClickLogEntity, Long> {
}