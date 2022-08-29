package com.honeypot.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveUsersKpiResponse {

    private String date;

    private String ids;

    private BigInteger count;

}
