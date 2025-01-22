//package com.DokHub.backend.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@Table(name = "channels", schema = "dokhub") // 명시적으로 테이블 이름 지정
//public class ChannelEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id; // PK (Auto-Increment)
//
//    private String category;         // clip, song, main
//    private String channelName;      // 채널 이름
//    private String channelLink;      // 채널 링크
//    private String videoPreviewUrl;  // 썸네일 URL
//
//    // 필요 시 생성자, toString 등 추가
//}
//
