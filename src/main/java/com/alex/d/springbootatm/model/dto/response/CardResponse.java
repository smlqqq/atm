package com.alex.d.springbootatm.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CardResponse {

    @JsonProperty("card")
    @Schema(description = "Card number", example = "4000007329214081")
    private String cardNumber;

}