package br.edu.utfpr.bankapi.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.edu.utfpr.bankapi.model.Account;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
class TransactionControllerTest {

    @Autowired
    MockMvc mvc;

    // Gerenciador de persistência para os testes des classe
    @Autowired
    TestEntityManager entityManager;

    Account account; // Conta para os testes
    Account account2; // Conta para os testes

    @BeforeEach
    void setup() {
        account = new Account("Lauro Lima",
                12346, 1000, 0);
        entityManager.persist(account); // salvando uma conta

        account2 = new Account(
              "João da Silva", 12347, 1000, 0
        );
      entityManager.persist(account2); // salvando uma conta
    }

    // DEPOSIT
    @Test
    void depositDeveriaRetornarStatus400ParaRequisicaoInvalida() throws Exception {
        // ARRANGE
        var json = "{}"; // Body inválido

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/deposit")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
    }

    @Test
    void depositDeveriaRetornarStatus201ParaRequisicaoOK() throws Exception {
        // ARRANGE

        var json = """
                {
                    "receiverAccountNumber": 12346,
                    "amount": 200
                }
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/deposit")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(201, res.getStatus());
        Assertions.assertEquals("application/json", res.getContentType());
    }

    @Test
    void depositDeveriaRetornarDadosCorretosNoJson() throws Exception {
        // ARRANGE
        var json = """
                {
                    "receiverAccountNumber": 12346,
                    "amount": 200
                }
                    """;

        // ACT + ASSERT
        mvc.perform(
                MockMvcRequestBuilders.post("/transaction/deposit")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.receiverAccount.number",
                        Matchers.equalTo(12346)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.amount", 
                        Matchers.equalTo(200.0)));
    }

    // WITHDRAW
    @Test
    void withdrawDeveriaRetornarStatus400ParaRequisicaoInvalida() throws Exception {
        // ARRANGE
        var json = "{}"; // Body inválido

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/withdraw")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
    }

    @Test
    void withdrawDeveriaRetornarStatus201ParaRequisicaoOK() throws Exception {
        // ARRANGE
        var json = """
                {
                    "sourceAccountNumber": 12346,
                    "amount": 200
                }
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/withdraw")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(201, res.getStatus());
        Assertions.assertEquals("application/json", res.getContentType());
    }

    @Test
    void withdrawDeveriaRetornarDadosCorretosNoJson() throws Exception {
        // ARRANGE
        var json = """
                {
                    "sourceAccountNumber": 12346,
                    "amount": 200
                }
                    """;

        // ACT + ASSERT
        mvc.perform(
                MockMvcRequestBuilders.post("/transaction/withdraw")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.sourceAccount.number",
                        Matchers.equalTo(12346)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.amount", Matchers.equalTo(200.0)));
    }

    @Test
    void withdrawDeveriaRetornarSaldoInsuficiente() throws Exception {
        // ARRANGE
        var json = """
                {
                    "sourceAccountNumber": 12346,
                    "amount": 1100
                }
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/withdraw")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
        Assertions.assertEquals("No balance in account", res.getContentAsString());
    }

    // TRANSFER
    @Test
    void transferDeveriaRetornarStatus400ParaRequisicaoInvalida() throws Exception {
        // ARRANGE
        var json = "{}"; // Body inválido

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/transfer")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
    }

    @Test
    void transferDeveriaRetornarStatus201ParaRequisicaoOK() throws Exception {
        // ARRANGE
        var json = """
                {
                    "sourceAccountNumber": 12346,
                    "receiverAccountNumber": 12347,
                    "amount": 200
                }
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/transfer")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(201, res.getStatus());
        Assertions.assertEquals("application/json", res.getContentType());
    }

    @Test
    void transferDeveriaRetornarDadosCorretosNoJson() throws Exception {
        // ARRANGE
        var json = """
                {
                    "sourceAccountNumber": 12346,
                    "receiverAccountNumber": 12347,
                    "amount": 200
                }
                    """;

        // ACT + ASSERT
        mvc.perform(
                MockMvcRequestBuilders.post("/transaction/transfer")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.sourceAccount.number",
                        Matchers.equalTo(12346)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.receiverAccount.number",
                        Matchers.equalTo(12347)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.amount", Matchers.equalTo(200.0)));
    }

    @Test
    void transferDeveriaRetornarSaldoInsuficiente() throws Exception {
        // ARRANGE
        var json = """
                {
                    "sourceAccountNumber": 12346,
                    "receiverAccountNumber": 12347,
                    "amount": 1100
                }
                    """;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/transfer")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
        Assertions.assertEquals("No balance in account", res.getContentAsString());
    }
}
