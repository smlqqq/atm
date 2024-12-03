package com.alex.d.springbootatm.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class DepositeResponse extends CardResponse {

    @JsonProperty("deposit")
    @Schema(description = "deposit amount", example = "1000")
    private String depositAmount;


    @Builder
    public DepositeResponse(String cardNumber, String depositAmount) {
        super(cardNumber);
        this.depositAmount = depositAmount;
    }
}


