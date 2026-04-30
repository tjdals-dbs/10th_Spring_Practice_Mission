package umc.domain.mission.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.global.apiPayload.code.BaseSuccessCode;

@Getter
@RequiredArgsConstructor
public enum MissionSuccessCode implements BaseSuccessCode {

    OK(HttpStatus.OK,
            "MISSION200_1",
            "미션 목록을 성공적으로 조회했습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
