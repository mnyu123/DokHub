package com.DokHub.backend.repository;

import com.DokHub.backend.entity.VideoClickLogEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VideoClickLogRepository extends JpaRepository<VideoClickLogEntity, Long> {

    @Query(value = """
            SELECT video_id, MAX(video_title), MAX(channel_name), COUNT(*)
            FROM video_click_log
            WHERE clicked_at >= :from
            GROUP BY video_id
            ORDER BY COUNT(*) DESC, MAX(clicked_at) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findTopVideosSince(@Param("from") LocalDateTime from, @Param("limit") int limit);

    @Query(value = """
            SELECT COALESCE(NULLIF(channel_id, ''), CONCAT('name:', COALESCE(NULLIF(channel_name, ''), 'unknown'))),
                   COALESCE(NULLIF(MAX(channel_name), ''), '알 수 없는 채널'),
                   COUNT(*)
            FROM video_click_log
            WHERE clicked_at >= :from
            GROUP BY COALESCE(NULLIF(channel_id, ''), CONCAT('name:', COALESCE(NULLIF(channel_name, ''), 'unknown')))
            ORDER BY COUNT(*) DESC, MAX(clicked_at) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findTopChannelsSince(@Param("from") LocalDateTime from, @Param("limit") int limit);

    @Query(value = """
            SELECT DATE(clicked_at), COUNT(*)
            FROM video_click_log
            WHERE clicked_at >= :from
            GROUP BY DATE(clicked_at)
            ORDER BY DATE(clicked_at)
            """, nativeQuery = true)
    List<Object[]> findDailyClicksSince(@Param("from") LocalDateTime from);

    @Query("SELECT COUNT(log) FROM VideoClickLogEntity log WHERE log.clickedAt >= :from")
    long countClicksSince(@Param("from") LocalDateTime from);

    @Query("SELECT COUNT(DISTINCT log.videoId) FROM VideoClickLogEntity log WHERE log.clickedAt >= :from")
    long countUniqueVideosSince(@Param("from") LocalDateTime from);

    @Query(value = """
            SELECT COUNT(DISTINCT COALESCE(NULLIF(channel_id, ''), NULLIF(channel_name, '')))
            FROM video_click_log
            WHERE clicked_at >= :from
            """, nativeQuery = true)
    long countUniqueChannelsSince(@Param("from") LocalDateTime from);
}
