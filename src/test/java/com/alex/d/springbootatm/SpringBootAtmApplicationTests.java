package com.alex.d.springbootatm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class SpringBootAtmApplicationTests {


    private final MockMvc mockMvc;

    SpringBootAtmApplicationTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void testDepositEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deposit")
                        .queryParam("cardNumber", "1234567890123456")
                        .queryParam("amount", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testWithdrawEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/withdraw")
                        .queryParam("cardNumber", "1234567890123456")
                        .queryParam("amount", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testTransferEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transfer")
                        .queryParam("senderCardNumber", "1234567890123456")
                        .queryParam("recipientCardNumber", "9876543210987654")
                        .queryParam("amount", "50")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testCreateCardEndpoint() throws Exception {
        String requestBody = "{\"cardNumber\": \"1234567890123456\", \"pinNumber\": \"1234\", \"balance\": 0}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/card")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void testGetAllCardsEndpoint() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testGetBalanceEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/balance/{cardNumber}", "1234567890123456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDeleteCardEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/delete/{cardNumber}", "1234567890123456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
