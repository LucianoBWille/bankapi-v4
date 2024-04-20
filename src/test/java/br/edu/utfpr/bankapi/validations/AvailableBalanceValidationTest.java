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

import br.edu.utfpr.bankapi.exception.WithoutBalanceException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.model.Transaction;
import br.edu.utfpr.bankapi.model.TransactionType;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class AvailableBalanceValidationTest {
  @Autowired
  MockMvc mvc;

  // Gerenciador de persistência para os testes des classe
  @Autowired
  TestEntityManager entityManager;

  Account account1; // Conta para os testes
  Account account2; // Conta para os testes
  Transaction transactionValid;
  Transaction transactionInvalid;

  @Autowired
  AvailableBalanceValidation availableBalanceValidation;

  @BeforeEach
  void setup() {
      account1 = new Account(
        "Lauro Lima",12346, 1000, 0
        );
      entityManager.persist(account1); // salvando uma conta

      account2 = new Account(
              "João da Silva", 12347, 1000, 0
        );
      entityManager.persist(account2); // salvando uma conta

      transactionValid = new Transaction(
              account1, account2, 100, TransactionType.TRANSFER
      );

      transactionInvalid = new Transaction(
              account1, account2, 1100, TransactionType.TRANSFER
      );
  }

  @Test
  void saldoInsuficiente() {
      // ARRANGE
      // ACT
      var exception = assertThrows(WithoutBalanceException.class, () -> {
          availableBalanceValidation.validate(transactionInvalid);
      });

      // ASSERT
      assertEquals("No balance in account", exception.getMessage());
  }

  @Test
  void saldoSuficiente() throws WithoutBalanceException {
      // ARRANGE
      // ACT
      availableBalanceValidation.validate(transactionValid);
  }

}
