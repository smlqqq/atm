package com.alex.d.springbootatm.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepositeResponse {

    @JsonProperty("card number")
    @Schema(description = "card number", example = "4000009739800475")
    private String cardNumber;

    @JsonProperty("deposit")
    @Schema(description = "deposit amount", example = "1000")
    private BigDecimal deposit;

}


