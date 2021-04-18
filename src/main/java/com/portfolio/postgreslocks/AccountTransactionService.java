package com.portfolio.postgreslocks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Service
@Slf4j
public class AccountTransactionService {

    @Autowired
    private AccountTransactionRepository repository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private AccountTransactionLockService lockService;

    @Transactional
    public void lockUserTransactions(Long userId) throws InterruptedException {
        lockService.lock(userId);
        Thread.sleep(20000L);
    }

    @Transactional
    public boolean tryLockUserTransactions(Long userId) throws InterruptedException {
        String queryString = "SELECT pg_try_advisory_xact_lock(:userId) FROM transactions";
        Boolean lockAcquired = (Boolean) entityManager.createNativeQuery(queryString)
                .setParameter("userId", userId).getSingleResult();
        log.info("lock acquired for user id {}", lockAcquired, userId);
        return lockAcquired;
    }

    @Transactional
    public void lockAndUpdate(long userId) {
        boolean acquired = lockService.lock(userId);
        if (acquired) {
            List<AccountTransactionEntity> transactions = repository.findByUserId(userId);
            transactions.forEach(transactionEntity -> transactionEntity.setAmount(500D));
            repository.saveAll(transactions);
        }
    }
}
