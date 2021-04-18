package com.portfolio.postgreslocks;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = {RowLockTest.Initializer.class})
public class RowLockTest {

    // will be started before and stopped after each test method
    @Container
    private static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer()
            .withDatabaseName("foo")
            .withUsername("foo")
            .withPassword("secret");

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgresqlContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgresqlContainer.getUsername(),
                    "spring.datasource.password=" + postgresqlContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Autowired
    private AccountTransactionRepository transactionRepository;
    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private AccountTransactionService accountTransactionService;

    private EasyRandom generator = new EasyRandom();
    private boolean testData = false;

    @BeforeEach
    void setUp() {
        if (!testData) {
            UserEntity userEntity1 = new UserEntity();
            userEntity1.setName("Raphael");
            UserEntity userEntity2 = new UserEntity();
            userEntity2.setName("Thomas");
            userEntityRepository.saveAll(Arrays.asList(userEntity1, userEntity2));

            for (int i = 0 ; i <= 20 ; i++) {
                AccountTransactionEntity transaction = generator.nextObject(AccountTransactionEntity.class);
                transaction.setId(null);
                userEntity1.addTransaction(transaction);
            }

            userEntityRepository.save(userEntity1);

            for (int i = 0 ; i <= 20 ; i++) {
                AccountTransactionEntity transaction = generator.nextObject(AccountTransactionEntity.class);
                transaction.setId(null);
                userEntity2.addTransaction(transaction);
            }

            userEntityRepository.save(userEntity2);

            testData = true;
        }

    }

    @Test
    void persistOK() {

        List<UserEntity> users = userEntityRepository.findAll();
        assertThat(users, hasSize(2));
        System.out.println(users);

        List<AccountTransactionEntity> transactions = transactionRepository.findAll();
        assertThat(transactions, hasSize(42));
    }

    @Test
    void advisoryLock() throws InterruptedException {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    accountTransactionService.tryLockUserTransactions(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // Execute 1 thread that will lock user 1
        Executors.newFixedThreadPool(1).submit(runnable);
        Thread.sleep(2000L);

        // Execute 5 tasks that will try to lock user 1 but won't lock it
        for (int i = 0 ; i <= 5 ; i++) {
            Executors.newFixedThreadPool(1).submit(runnable);
        }

        // Now try to query by user 1 and it should be OK
        List<AccountTransactionEntity> transactions = transactionRepository.findByUserId(1L);
        System.out.println(transactions);
    }
}
