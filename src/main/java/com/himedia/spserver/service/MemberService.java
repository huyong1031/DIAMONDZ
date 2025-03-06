package com.himedia.spserver.service;

import com.himedia.spserver.entity.Member;
import com.himedia.spserver.entity.MemberRole;
import com.himedia.spserver.repository.MemberRepository;
import com.himedia.spserver.repository.OrdersRepository;
import com.himedia.spserver.repository.ProductLikeRepository;
import com.himedia.spserver.repository.ReviewRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final OrdersRepository ordersRepository;
    private final ProductLikeRepository productLikeRepository;

    public MemberService(MemberRepository memberRepository,
                         ReviewRepository reviewRepository,
                         OrdersRepository ordersRepository,
                         ProductLikeRepository productLikeRepository) {
        this.memberRepository = memberRepository;
        this.reviewRepository = reviewRepository;
        this.ordersRepository = ordersRepository;
        this.productLikeRepository = productLikeRepository;
    }

    private final BCryptPasswordEncoder pe = new BCryptPasswordEncoder();

    public Member getMember(String memberId) {
        Optional<Member> member = memberRepository.findByMemberId(memberId);
        return member.orElse(null);
    }

    public void insertMember(Member member) {
        // MemberRole 생성
        List<MemberRole> roles = new ArrayList<MemberRole>();
        // 권한 하나 추가
        roles.add(MemberRole.USER);
        // member 객체에 저장
        member.setMemberRoleList(roles);
        // 패스워드 암호화
        member.setMemberPwd( pe.encode(member.getMemberPwd()) );
        member.setMemberRoleList(Collections.singletonList(MemberRole.USER));
        // 레코드 추가
        memberRepository.save(member);
    }


    public boolean checkPassword(String memberId, String inputPassword) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        // ✅ 회원이 존재하지 않는 경우
        if (optionalMember.isEmpty()) {
            return false;
        }

        Member member = optionalMember.get();

        // ✅ 입력된 비밀번호와 DB에 저장된 비밀번호 비교
        return inputPassword.equals(member.getMemberPwd());
    }


    @Transactional
    public void updateMember(Member member) {
        memberRepository.save(member);
    }

    // ✅ 추가: 토큰이 곧 `memberId`라면, 토큰 값을 그대로 사용하여 조회
    public Member getMemberByToken(String token) {
        return memberRepository.findByMemberId(token).orElse(null); // ✅ memberId로 조회
    }

    @Transactional
    public boolean withdrawMember(String memberId, String password) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        if (optionalMember.isEmpty()) {
            System.out.println("❌ 회원 정보 없음: " + memberId);
            return false;
        }

        Member member = optionalMember.get();

        // ✅ 좋아요한 상품 삭제 (ProductLikeRepository 필요)
        productLikeRepository.deleteByMemberId(memberId);
        System.out.println("✅ 좋아요한 상품 삭제 완료");

        // ✅ 회원이 주문한 데이터 삭제
        ordersRepository.deleteByMemberId(memberId);
        System.out.println("✅ 주문 데이터 삭제 완료");

        // ✅ 회원이 작성한 리뷰 삭제
        reviewRepository.deleteByMemberId(memberId);
        System.out.println("✅ 리뷰 삭제 완료");

        // ✅ 카카오 로그인 사용자는 비밀번호 없이 탈퇴 가능
        if ("kakao".equals(member.getProvider())) {
            memberRepository.delete(member);
            System.out.println("✅ 카카오 로그인 사용자 회원 탈퇴 완료: " + memberId);
            return true;
        }

        // ✅ 일반 로그인 사용자는 비밀번호 확인 후 삭제
        if (member.getMemberPwd() == null || !member.getMemberPwd().equals(password)) {
            System.out.println("❌ 비밀번호 불일치: " + memberId);
            return false;
        }

        // ✅ 회원 삭제
        memberRepository.delete(member);
        System.out.println("✅ 일반 사용자 회원 탈퇴 완료: " + memberId);

        return true;
    }

    @Transactional
    public Member findOrCreateMember(String memberId, String nickname, String provider) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        if (optionalMember.isPresent()) {
            return optionalMember.get();
        }

        Member newMember = new Member();
        newMember.setMemberId(memberId);
        newMember.setMemberPwd(pe.encode("kakao"));
        newMember.setMemberName(nickname);
        newMember.setMemberRoleList(Collections.singletonList(MemberRole.USER));
        newMember.setProvider(provider);


        try {
            Member savedMember = memberRepository.save(newMember);
            if (savedMember == null) {  // ✅ 저장 실패 확인
                System.out.println("🚨 회원 저장 실패! memberId=" + memberId);
                return null;
            }
            return savedMember;
        } catch (Exception e) {
            System.out.println("🚨 회원 저장 중 오류 발생: " + e.getMessage());
            return null;
        }
    }


}