package com.portfolio.postgreslocks;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<AccountTransactionEntity> transactions = new HashSet<>();

    public void addTransaction(AccountTransactionEntity transaction) {
        transaction.setUser(this);
        transactions.add(transaction);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<AccountTransactionEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<AccountTransactionEntity> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
