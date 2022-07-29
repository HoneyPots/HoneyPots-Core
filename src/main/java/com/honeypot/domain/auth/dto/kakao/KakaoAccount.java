package com.honeypot.domain.auth.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoAccount {

    private boolean profileNeedsAgreement;

    private boolean profileNicknameNeedsAgreement;

    private boolean profileImageNeedsAgreement;

    private KakaoProfile profile;

    private boolean nameNeedsAgreement;

    private String name;

    private boolean emailNeedsAgreement;

    @JsonProperty("is_email_valid")
    private boolean isEmailValid;

    @JsonProperty("is_email_verified")
    private boolean isEmailVerified;

    private String email;

    private boolean ageRangeNeedsAgreement;

    private String ageRange;

    private boolean birthYearNeedsAgreement;

    private String birthyear;

    private boolean birthdayNeedsAgreement;

    private String birthday;

    private String birthdayType;

    private boolean genderNeedsAgreement;

    private String gender;

    private boolean phoneNumberNeedsAgreement;

    private String phoneNumber;

    private boolean ciNeedsAgreement;

    private String ci;

    private LocalDateTime ciAuthenticatedAt;

}
