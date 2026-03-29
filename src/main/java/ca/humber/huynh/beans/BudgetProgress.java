package ca.humber.huynh.beans;

import java.math.BigDecimal;

public class BudgetProgress {
    private String category;
    private BigDecimal limitAmount;
    private BigDecimal amountSpent;
    
    public BudgetProgress(String category, BigDecimal limitAmount, BigDecimal amountSpent) {
        this.category = category;
        this.limitAmount = limitAmount;
        this.amountSpent = amountSpent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public BigDecimal getAmountSpent() {
        return amountSpent;
    }

    public void setAmountSpent(BigDecimal amountSpent) {
        this.amountSpent = amountSpent;
    }
    
    // Helper to calculate percentage for the UI
    public int getPercentage() {
        if (limitAmount == null || limitAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        if (amountSpent == null) {
            return 0;
        }
        // amountSpent might be negative if it's an expense, so we use absolute value
        BigDecimal spent = amountSpent.abs();
        BigDecimal percentage = spent.divide(limitAmount, 2, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        return Math.min(percentage.intValue(), 100); // Cap at 100% for progress bar
    }
    
    // Helper to check if over budget
    public boolean isOverBudget() {
        if (limitAmount == null || amountSpent == null) {
            return false;
        }
        return amountSpent.abs().compareTo(limitAmount) > 0;
    }
}
