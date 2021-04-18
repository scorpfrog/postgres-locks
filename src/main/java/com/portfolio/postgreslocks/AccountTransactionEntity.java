package com.portfolio.postgreslocks;

import javax.persistence.*;

@Entity
@Table(name = "transactions")
public class AccountTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    private String concept;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "AccountTransactionEntity{" +
                "id=" + id +
                ", amount=" + amount +
                ", concept='" + concept + '\'' +
                ", user=" + user +
                '}';
    }
}
