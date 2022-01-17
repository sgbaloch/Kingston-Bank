package com.agbaloch.kingstonbank.Models;

public class SavingAccount extends Account {

    private final int accountType = 2;

    private double interestRate;

    public int getAccountType() {
        return accountType;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
}
