package com.alex.d.springbootatm.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class BalanceResponse extends CardResponse{

    @JsonProperty("balance")
    @Schema(description = "Balance after operation", example = "1300")
    private BigDecimal balance;

    @Builder
    public BalanceResponse(String cardNumber, BigDecimal balance) {
        super(cardNumber);
        this.balance = balance;
    }
}

