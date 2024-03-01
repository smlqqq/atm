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
public class WithdrawResponse {

    @JsonProperty("card number")
    private String cardNumber;

    @JsonProperty("withdraw")
    private String withdraw;

    @JsonProperty("balance")
    private BigDecimal balance;

}
