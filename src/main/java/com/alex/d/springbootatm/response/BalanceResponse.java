package com.alex.d.springbootatm.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponse {

    @JsonProperty("card number")
    private String cardNumber;

    @JsonProperty("balance")
    private BigDecimal balance;

}

