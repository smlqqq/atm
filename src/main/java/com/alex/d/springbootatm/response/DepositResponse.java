package com.alex.d.springbootatm.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositResponse extends TransactionResponse {

    @JsonProperty("card number")
    @Schema(description = "card number", example = "4000009739800475")
    private String cardNumber;

    @JsonProperty("balance")
    @Schema(description = "card balance", example = "1000")
    private BigDecimal balance;

}
