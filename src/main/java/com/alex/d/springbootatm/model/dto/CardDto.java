package com.alex.d.springbootatm.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CardDto {

    @JsonProperty("card number")
    @Schema(description = "card number", example = "4000009739800475")
    String cardNumber;

    @JsonProperty("pin")
    @Schema(description = "pincode", example = "1111")
    String pin;

    @JsonProperty("balance")
    @Schema(description = "card balance", example = "1000")
    BigDecimal balance;

}
