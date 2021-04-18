package com.portfolio.postgreslocks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransactionEntity, Long> {

    List<AccountTransactionEntity> findByUserId(Long userId);
}
