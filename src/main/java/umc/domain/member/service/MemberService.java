package umc.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.domain.member.converter.MemberConverter;
import umc.domain.member.dto.MemberResDTO;
import umc.domain.member.entity.Member;
import umc.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResDTO.GetInfo getInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow();

        return MemberConverter.toGetInfo(member);
    }
}
