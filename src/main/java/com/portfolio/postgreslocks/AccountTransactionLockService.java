package com.portfolio.postgreslocks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Service
@Slf4j
public class AccountTransactionLockService {

    @Autowired
    private EntityManager entityManager;

    public boolean lock(final Long userId) {
        String queryString = "SELECT pg_try_advisory_xact_lock(:userId) FROM transactions";
        Query query = entityManager.createNativeQuery(queryString).setParameter("userId", userId);
        Boolean acquired = (Boolean) query.getSingleResult();
        log.info("[lock] lock acquired {} for user id {}", acquired, userId);
        return acquired;
    }
}
