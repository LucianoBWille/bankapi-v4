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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.repository.AccountRepository;
import br.edu.utfpr.bankapi.service.AccountService;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
class AccountControllerTest {
    @Autowired
    MockMvc mvc;

    // Gerenciador de persistência para os testes des classe
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    AccountController controller;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    Account account; // Conta para os testes
    Account account2; // Conta para os testes
    Account account3; // Conta para os testes

    @BeforeEach
    void setup() {
        account = new Account("Lauro Lima",
                12346, 1000, 0);
        entityManager.persist(account); // salvando uma conta

        account2 = new Account(
            "João da Silva", 12347, 1000, 0
        );
        entityManager.persist(account2); // salvando uma conta

        account3 = new Account(
            "Maria da Silva", 12348, 1000, 0
        );
        entityManager.persist(account3); // salvando uma conta
    }

    // GET BY NUMBER
    @Test
    void getByNumberDeveriaRetornarStatus404ParaContaNaoEncontrada() throws Exception {
        // ARRANGE
        var number = 9999;

        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.get("/account/" + number))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(404, res.getStatus());
    }
  
    @Test
    void getByNumberDeveriaRetornarConta() throws Exception {
        // ARRANGE
        var number = account.getNumber();

        // ACT
        mvc.perform(
            MockMvcRequestBuilders.get("/account/" + number))
            .andExpect(MockMvcResultMatchers.jsonPath(
                "$.name", 
                Matchers.is(account.getName())))
            .andExpect(MockMvcResultMatchers.jsonPath(
                "$.number", 
                Matchers.equalTo(Long.valueOf(account.getNumber()).intValue())))
            .andExpect(MockMvcResultMatchers.jsonPath(
                "$.balance", 
                Matchers.is(account.getBalance())))
            .andExpect(MockMvcResultMatchers.jsonPath(
                "$.specialLimit", 
                Matchers.is(account.getSpecialLimit())))
            .andReturn().getResponse();
    }

    // GET ALL
    @Test
    void getAllDeveriaRetornarStatus200() throws Exception {
        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.get("/account"))
                .andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(200, res.getStatus());
    }

    @Test
    void getAllDeveriaRetornarStatus200ERetoenarTodasAsContas() throws Exception {
        // ACT
        mvc.perform(
            MockMvcRequestBuilders.get("/account"))
                .andExpect(MockMvcResultMatchers.jsonPath(
                    "$", 
                    Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                    "$[0].name", 
                    Matchers.is(account.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                    "$[1].name", 
                    Matchers.is(account2.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath(
                    "$[2].name", 
                    Matchers.is(account3.getName())))
                .andReturn().getResponse();
    }

    @Test
    void getAllDeveriaRetornarStatus200EListaVazia() throws Exception {
        // ARRANGE
        entityManager.remove(account);
        entityManager.remove(account2);
        entityManager.remove(account3);

        // ACT
        mvc.perform(
            MockMvcRequestBuilders.get("/account"))
                .andExpect(MockMvcResultMatchers.jsonPath(
                    "$", 
                    Matchers.hasSize(0)))
                .andReturn().getResponse();
    }

    // SAVE
    @Test
    void saveDeveriaRetornarStatus201() throws Exception {
        // ARRANGE
        var json = """
                {
                    "name": "Lauro Lima",
                    "number": 12349,
                    "balance": 1000,
                    "specialLimit": 0
                }
                    """;
    
        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/account")
                        .content(json).contentType("application/json"))
                    .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.name", 
                        Matchers.is("Lauro Lima")))
                    .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.number", 
                        Matchers.is(12349)))
                    .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.balance", 
                        Matchers.is(0.0)))
                    .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.specialLimit", 
                        Matchers.is(0.0)))
                    .andReturn().getResponse();
    
        // ASSERT
        Assertions.assertEquals(201, res.getStatus());
    }

    @Test
    void saveDeveriaRetornarStatus400ParaContaJaExistente() throws Exception {
        // ARRANGE
        var json = """
                {
                    "name": "Lauro Lima",
                    "number": 12346,
                    "balance": 1000,
                    "specialLimit": 0
                }
                    """;
    
        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.post("/account")
                        .content(json).contentType("application/json"))
                .andReturn().getResponse();
    
        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
    }

    // UPDATE
    @Test
    void updateDeveriaRetornarStatus200() throws Exception {
        // ARRANGE
        var json = """
                {
                    "name": "Lauro Lima Updated",
                    "number": 12346,
                    "balance": 2000,
                    "specialLimit": 100
                }
                    """;
    
        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.put("/account/12346")
                        .content(json).contentType("application/json"))
                    .andReturn().getResponse();
    
        // ASSERT
        Assertions.assertEquals(200, res.getStatus());
    }

    @Test
    void updateDeveriaRetornarStatus404ParaContaNaoEncontrada() throws Exception {
        // ARRANGE
        var json = """
                {
                    "name": "Lauro Lima Updated",
                    "number": 9999,
                    "balance": 2000,
                    "specialLimit": 100
                }
                    """;
    
        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.put("/account/9999")
                        .content(json).contentType("application/json"))
                .andReturn().getResponse();
    
        // ASSERT
        Assertions.assertEquals(404, res.getStatus());
    }        

    @Test
    void updateDeveriaRetornarStatus400ParaDTOInvalido() throws Exception {
        // ARRANGE
        var json = """
                {
                    "nome": "Lauro Lima Updated",
                    "numero": 12346,
                    "balanco": 2000,
                    "limite": 100
                }
                    """;
    
        // ACT
        var res = mvc.perform(
                MockMvcRequestBuilders.put("/account/12346")
                        .content(json).contentType("application/json"))
                .andReturn().getResponse();
    
        // ASSERT
        Assertions.assertEquals(400, res.getStatus());
    }
}
