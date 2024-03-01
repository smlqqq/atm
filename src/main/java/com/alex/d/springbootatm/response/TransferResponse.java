package com.alex.d.springbootatm.response;


import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String senderCardNumber;

    @JsonProperty("recipient card number")
    private String recipientCardNumber;

    @JsonProperty("transferred funds")
    private BigDecimal transferred_funds;

    @JsonProperty("sender balance")
    private BigDecimal sender_balance;

    @JsonProperty("recipient balance")
    private BigDecimal recipient_balance;

}
