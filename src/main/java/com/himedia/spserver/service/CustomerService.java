package com.himedia.spserver.service;

import com.himedia.spserver.dto.Paging;
import com.himedia.spserver.dto.QnaDTO;
import com.himedia.spserver.entity.Member;
import com.himedia.spserver.entity.Qna;
import com.himedia.spserver.repository.MemberRepository;
import com.himedia.spserver.repository.QnaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService {

    @Autowired
    QnaRepository qR;

    @Autowired
    MemberRepository mR;

    public HashMap<String, Object> getQnaList(int page, String memberId) {
        HashMap<String, Object> result = new HashMap<>();

        if (page < 1) {
            page = 1; // ✅ 기본값 설정 (NaN 및 0 이하 값 방지)
        }

        Paging paging = new Paging();
        paging.setPage(page);
        paging.setDisplayRow(10);  // 한 페이지에 10개 표시

        int count = (int) qR.findByMember_MemberId(memberId, PageRequest.of(0, 1)).getTotalElements();
        paging.setTotalCount(count);
        paging.calPaging();

        // ✅ Spring Data JPA의 `PageRequest.of()`는 0부터 시작해야 하므로 `page - 1`로 보정
        Pageable pageable = PageRequest.of(page - 1, paging.getDisplayRow(), Sort.by(Sort.Direction.DESC, "qnaSeq"));

        Page<Qna> pageList = qR.findByMember_MemberId(memberId, pageable);
        List<QnaDTO> dtoList = pageList.getContent().stream().map(QnaDTO::new).collect(Collectors.toList());

        // ✅ 프론트엔드에서 사용할 수 있도록 `paging` 객체를 표준화
        HashMap<String, Object> pagingData = new HashMap<>();
        pagingData.put("currentPage", page);
        pagingData.put("totalPages", pageList.getTotalPages());
        pagingData.put("beginPage", Math.max(1, page - 2));
        pagingData.put("endPage", Math.min(pageList.getTotalPages(), page + 2));

        result.put("qnaList", dtoList);
        result.put("paging", pagingData);

        return result;
    }


    public QnaDTO getQna(int qnaSeq, String memberId) {
        System.out.println("getQna 요청 받음: qnaSeq=" + qnaSeq + ", memberId=" + memberId);
        Qna qna = qR.findByQnaSeqAndMember_MemberId(qnaSeq, memberId);
        if (qna == null) {
            System.out.println("QnA 데이터 없음");
        } else {
            System.out.println("QnA 데이터 조회 성공: " + qna.getSubject());
        }
        return qna != null ? new QnaDTO(qna) : null;  // ✅ DTO로 변환 후 반환
    }

    public int getQnaPage(int qnaSeq, String memberId) {
        // 정렬을 포함한 Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "qnaSeq"));

        List<Qna> qnaList = qR.findByMember_MemberId(memberId, pageable).getContent();
        int index = -1;
        for (int i = 0; i < qnaList.size(); i++) {
            if (qnaList.get(i).getQnaSeq() == qnaSeq) {
                index = i;
                break;
            }
        }
        if (index == -1) return 1; // 기본값: 첫 번째 페이지

        int page = (index / 10) + 1; // 한 페이지에 10개씩 표시한다고 가정
        return page;
    }

    public void insertQna(QnaDTO qnaDTO) {
        Member member;

        // ✅ 회원이면 DB에서 memberId 조회
        if (qnaDTO.getMemberId() != null && mR.existsById(qnaDTO.getMemberId())) {
            // ✅ 회원이면 DB에서 memberId 조회
            member = mR.findByMemberId(qnaDTO.getMemberId())
                    .orElseThrow(() -> new RuntimeException("해당 memberId의 회원이 존재하지 않습니다: " + qnaDTO.getMemberId()));
        } else {
            // ✅ 비회원이면 'guest' 계정이 존재하는지 확인 후 없으면 생성
            member = mR.findByMemberId("guest").orElseGet(() -> {
                System.out.println("⚠️ 'guest' 계정이 존재하지 않음. 자동으로 생성합니다.");
                Member guestMember = new Member();
                guestMember.setMemberId("guest");
                guestMember.setMemberName("비회원");
                guestMember.setMemberEmail("guest@example.com");
                guestMember.setMemberPwd("guest1234");
                return mR.save(guestMember);
            });
        }

        // ✅ Qna 객체 생성 (비회원이면 guestName에 입력한 이름 저장)
        Qna qna = Qna.builder()
                .subject(qnaDTO.getSubject())
                .content(qnaDTO.getContent())
                .security(qnaDTO.getSecurity())
                .reply(qnaDTO.getReply())
                .pass(qnaDTO.getPass())
                .inDate(new Timestamp(System.currentTimeMillis()))
                .member(member)  // ✅ 회원이면 Member 설정, 비회원이면 guest 계정 설정
                .guestName(member.getMemberId().equals("guest") ? qnaDTO.getGuestName() : null)  // ✅ 비회원 이름 저장
                .build();

        qR.save(qna);
    }

    // 모든 QnA 내역 가져오기
    public HashMap<String, Object> getAllQnaList(int page) {
        HashMap<String, Object> result = new HashMap<>();

        if (page < 1) {
            page = 1;
        }

        Paging paging = new Paging();
        paging.setPage(page);
        paging.setDisplayRow(10);  // 한 페이지에 10개 표시

        int count = (int) qR.countAllBy();
        paging.setTotalCount(count);
        paging.calPaging();

        Pageable pageable = PageRequest.of(page - 1, paging.getDisplayRow(), Sort.by(Sort.Direction.DESC, "qnaSeq"));
        Page<Qna> pageList = qR.findAll(pageable);
        List<QnaDTO> dtoList = pageList.getContent().stream().map(QnaDTO::new).collect(Collectors.toList());

        HashMap<String, Object> pagingData = new HashMap<>();
        pagingData.put("currentPage", page);
        pagingData.put("totalPages", pageList.getTotalPages());
        pagingData.put("beginPage", Math.max(1, page - 2));
        pagingData.put("endPage", Math.min(pageList.getTotalPages(), page + 2));

        result.put("qnaList", dtoList);
        result.put("paging", pagingData);

        return result;
    }
    // 🔹 비회원도 조회할 수 있도록 memberId 없이 검색 가능
    public QnaDTO getQnaWithoutMember(int qnaSeq) {
        System.out.println("📌 비회원 QnA 조회 요청: qnaSeq = " + qnaSeq);
        Qna qna = qR.findByQnaSeq(qnaSeq);  // ✅ memberId 없이 조회
        return qna != null ? new QnaDTO(qna) : null;
    }
}
