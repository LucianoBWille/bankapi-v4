package br.edu.utfpr.bankapi.validations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.utfpr.bankapi.exception.NotFoundException;
import br.edu.utfpr.bankapi.model.Account;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class AvailableAccountValidationTest {
  @Autowired
  MockMvc mvc;

  // Gerenciador de persistência para os testes des classe
  @Autowired
  TestEntityManager entityManager;

  Account account; // Conta para os testes

  @Autowired
  AvailableAccountValidation availableAccountValidation;

  @BeforeEach
  void setup() {
      account = new Account("Lauro Lima",
              12346, 1000, 0);
      entityManager.persist(account); // salvando uma conta
  }

  @Test
  void contaNaoEncontrada() {
      // ARRANGE
      var accountNumber = 12345; // Conta inexistente

      // ACT
      var exception = assertThrows(NotFoundException.class, () -> {
          availableAccountValidation.validate(accountNumber);
      });

      // ASSERT
      assertEquals("Conta " + accountNumber + " inexistente", exception.getMessage());
  }

  @Test
  void contaEncontrada() throws NotFoundException {
      // ARRANGE
      var accountNumber = 12346; // Conta existente

      // ACT
      var account = availableAccountValidation.validate(accountNumber);

      // ASSERT
      assertEquals(accountNumber, account.getNumber());
      assertEquals("Lauro Lima", account.getName());
      assertEquals(1000, account.getBalance());
      assertEquals(0, account.getSpecialLimit());
  }

  
}
