package com.hyd.dao.dialects.h2;

import java.util.Date;

public class Payment {

    private long id;

    private Date payTime;

    private int amount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", payTime=" + payTime +
                ", amount=" + amount +
                '}';
    }
}
