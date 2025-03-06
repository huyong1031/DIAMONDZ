package com.himedia.spserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;

@Entity
@Table(name = "qna")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Qna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qnaseq")
    private int qnaSeq; // 게시글 고유번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberid", referencedColumnName = "memberid", nullable = true)
    private Member member; // 질문 작성 고객

    @Column(name = "guest_name", length = 50, nullable = true)
    private String guestName;  // ✅ 비회원 이름 저장 필드 추가

    @Column(name = "subject", length = 100, nullable = false)
    private String subject; // 질문 제목

    @Column(nullable = false, length = 1000)
    private String content; // 질문 내용

    @Column(name = "pass", length = 100)
    private String pass; // 비밀글 비밀번호 (NULL 가능)

    @Column(nullable = false, length = 5) @ColumnDefault("'N'")
    private String security;  // 비밀글 여부 (true=비밀글, false=공개)

    @CreationTimestamp
    @Column(name = "indate", nullable = false, updatable = false)
    private Timestamp inDate; // 작성 날짜

    @Column(length = 500)
    private String reply; // 답변 내용 (NULL 가능)

    // ✅ 비회원 이름을 가져오는 getter 추가
    public String getGuestName() {
        return guestName;
    }

    // ✅ 작성자 이름을 반환하는 메서드 (회원이면 memberId, 비회원이면 guestName 반환)
    public String getWriterName() {
        return member != null ? member.getMemberId() : guestName;
    }

}
