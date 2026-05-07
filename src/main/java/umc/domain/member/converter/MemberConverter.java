package umc.domain.member.converter;

import umc.domain.member.dto.MemberResDTO;
import umc.domain.member.entity.Member;

public class MemberConverter {

    public static MemberResDTO.GetInfo toGetInfo(Member member){
        return MemberResDTO.GetInfo.builder()
                .name(member.getName())
                .profileUrl(null)
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .point(member.getCurrentPoint().intValue())
                .build();
    }
}
