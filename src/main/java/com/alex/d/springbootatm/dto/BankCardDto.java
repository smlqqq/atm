package com.alex.d.springbootatm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Value
@Builder
public class BankCardDto {

//    @JsonProperty("card number")
//    @Schema(description = "card number", example = "4000009739800475")
    String cardNumber;

//    @JsonProperty("balance")
//    @Schema(description = "card balance", example = "1000")
    BigDecimal balance;

}
