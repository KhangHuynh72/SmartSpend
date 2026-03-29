package ca.humber.huynh.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.humber.huynh.beans.Budget;
import ca.humber.huynh.beans.BudgetProgress;
import ca.humber.huynh.beans.Transaction;
import ca.humber.huynh.beans.User;
import ca.humber.huynh.database.DatabaseAccess;

@Controller
public class DashboardController {

    @Autowired
    private DatabaseAccess databaseAccess;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = databaseAccess.findUserAccount(email);

        if (user != null) {
            model.addAttribute("transactions", databaseAccess.getTransactionsByUserId(user.getUserId()));
            model.addAttribute("totalBalance", databaseAccess.getTotalBalanceByUserId(user.getUserId()));
            
            // For later: Count subscriptions
            long subscriptionCount = databaseAccess.getTransactionsByUserId(user.getUserId())
                    .stream()
                    .filter(t -> "Subscriptions".equalsIgnoreCase(t.getCategory()))
                    .count();
            model.addAttribute("subscriptionCount", subscriptionCount);
            
            // Calculate budget progress
            List<Budget> budgets = databaseAccess.getBudgetsByUserId(user.getUserId());
            List<BudgetProgress> budgetProgresses = new ArrayList<>();
            
            YearMonth currentMonth = YearMonth.now();
            LocalDate startOfMonth = currentMonth.atDay(1);
            LocalDate endOfMonth = currentMonth.atEndOfMonth();
            
            for (Budget budget : budgets) {
                BigDecimal spent = databaseAccess.getMonthlySpentByCategory(user.getUserId(), budget.getCategory(), startOfMonth, endOfMonth);
                budgetProgresses.add(new BudgetProgress(budget.getCategory(), budget.getLimitAmount(), spent));
            }
            model.addAttribute("budgets", budgetProgresses);
        }

        return "dashboard";
    }

    @PostMapping("/addTransaction")
    public String addTransaction(Authentication authentication,
                                 @RequestParam String description,
                                 @RequestParam String category,
                                 @RequestParam BigDecimal amount,
                                 @RequestParam String date) {
        String email = authentication.getName();
        User user = databaseAccess.findUserAccount(email);

        if (user != null) {
            Transaction transaction = new Transaction();
            transaction.setUserId(user.getUserId());
            transaction.setDescription(description);
            transaction.setCategory(category);
            
            // By convention, expenses are usually negative and income positive. 
            // If they pick "Income", ensure it's positive. Otherwise, ensure it's negative.
            if ("Income".equalsIgnoreCase(category)) {
                transaction.setAmount(amount.abs());
            } else {
                transaction.setAmount(amount.abs().negate());
            }
            
            transaction.setTransactionDate(LocalDate.parse(date));

            databaseAccess.addTransaction(transaction);
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/addBudget")
    public String addBudget(Authentication authentication,
                            @RequestParam String category,
                            @RequestParam BigDecimal limitAmount) {
        String email = authentication.getName();
        User user = databaseAccess.findUserAccount(email);

        if (user != null && limitAmount.compareTo(BigDecimal.ZERO) >= 0) {
            databaseAccess.saveBudget(user.getUserId(), category, limitAmount);
        }

        return "redirect:/dashboard";
    }
}