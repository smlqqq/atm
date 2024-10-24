package com.alex.d.springbootatm.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class WithdrawResponse extends TransactionResponse {

    @JsonProperty("card number")
    @Schema(description = "card number", example = "4000007329214081")
    private String cardNumber;

    @JsonProperty("withdraw")
    @Schema(description = "withdraw sum", example = "500")
    private BigDecimal withdraw;

    @JsonProperty("balance")
    @Schema(description = "balance", example = "300")
    private BigDecimal balance;

}
