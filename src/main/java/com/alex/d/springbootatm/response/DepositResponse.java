package com.alex.d.springbootatm.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepositResponse {

    @JsonProperty("card number")
    private String cardNumber;

    @JsonProperty("balance")
    private BigDecimal balance;

}
