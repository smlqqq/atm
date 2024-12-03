package com.alex.d.springbootatm.model.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TransactionResponse {

    @JsonProperty("sender card number")
    @Schema(description = "sender card number", example = "4000003813378680")
    private String senderCardNumber;

    @JsonProperty("recipient card number")
    @Schema(description = "recipient card number", example = "4000007329214081")
    private String recipientCardNumber;

    @JsonProperty("transferred funds")
    @Schema (description = "transferred funds", example = " 500")
    private BigDecimal transferredFunds;

    @JsonProperty("sender balance")
    @Schema(description = "check sender's balance after transfer", example = "500")
    private BigDecimal senderBalance;

    @JsonProperty("recipient balance")
    @Schema(description = "check recipient's balance after transfer", example = "1000")
    private BigDecimal recipientBalance;

}
