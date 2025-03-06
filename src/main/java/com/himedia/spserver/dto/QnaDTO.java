package com.himedia.spserver.dto;

import com.himedia.spserver.entity.Qna;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor  // ✅ 기본 생성자 추가
@AllArgsConstructor  // ✅ 모든 필드 포함 생성자 추가
public class QnaDTO {
    private int qnaSeq; // 게시글 고유번호
    private String subject; // 질문 제목
    private String content; // 질문 내용
    private String security;  // 비밀글 여부 (true=비밀글, false=공개)
    private Timestamp inDate; // 작성 날짜
    private String reply; // 답변 내용 (NULL 가능)
    private String memberId; // 질문 작성 고객 ID
    private String guestName;  // ✅ 비회원 이름 저장
    private String pass;

    public QnaDTO(Qna qna) {
        this.qnaSeq = qna.getQnaSeq();
        this.subject = qna.getSubject();
        this.content = qna.getContent();
        this.security = qna.getSecurity();
        this.inDate = qna.getInDate();
        this.reply = qna.getReply();

        // Lazy Loading 방지: Member의 ID만 가져오기
        this.memberId = qna.getMember() != null ? qna.getMember().getMemberId() : null;
        this.pass = qna.getPass();
        this.guestName = qna.getGuestName();  // ✅ 비회원 이름 저장
    }

    public String getPass() {
        return this.pass;
    }

    // ✅ 작성자 이름을 반환하는 메서드 (회원이면 memberId, 비회원이면 guestName 반환)
    public String getWriterName() {
        return memberId != null ? memberId : guestName;
    }

}
