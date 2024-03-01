package com.alex.d.springbootatm.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

