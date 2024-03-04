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
public class TransferResponse {

    @JsonProperty("sender card number")
    @Schema(description = "sender card number", example = "4000003813378680")
    private String senderCardNumber;

    @JsonProperty("recipient card number")
    @Schema(description = "recipient card number", example = "4000007329214081")
    private String recipientCardNumber;

    @JsonProperty("transferred funds")
    @Schema (description = "transferred funds", example = " 500")
    private BigDecimal transferred_funds;

    @JsonProperty("sender balance")
    @Schema(description = "check sender's balance after transfer", example = "500")
    private BigDecimal sender_balance;

    @JsonProperty("recipient balance")
    @Schema(description = "check recipient's balance after transfer", example = "1000")
    private BigDecimal recipient_balance;

}
