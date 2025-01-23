package com.alex.d.springbootatm.model.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawResponse extends CardResponse {

    @JsonProperty("withdraw")
    @Schema(description = "withdraw amount", example = "500")
    private String withdrawAmount;

    @Builder
    public WithdrawResponse(String cardNumber, String withdrawAmount) {
        super(cardNumber);
        this.withdrawAmount = withdrawAmount;
    }
}
