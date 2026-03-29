package ca.humber.huynh.database;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ca.humber.huynh.beans.Budget;
import ca.humber.huynh.beans.Transaction;
import ca.humber.huynh.beans.User;
import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public class DatabaseAccess {
	@Autowired
    protected NamedParameterJdbcTemplate jdbc;

    // 1. Find a user by their email (for Login)
    public User findUserAccount(String email) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "SELECT * FROM sec_user WHERE email = :email";
        namedParameters.addValue("email", email);

        try {
            // BeanPropertyRowMapper automatically maps DB columns to User.java fields
            return jdbc.queryForObject(query, namedParameters, new BeanPropertyRowMapper<User>(User.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 2. Get the roles for a specific user (e.g., ROLE_USER, ROLE_ADMIN)
    public List<String> getRolesById(Long userId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "SELECT sec_role.roleName "
                     + "FROM user_role, sec_role "
                     + "WHERE user_role.roleId = sec_role.roleId "
                     + "AND userId = :userId";
        namedParameters.addValue("userId", userId);

        return jdbc.queryForList(query, namedParameters, String.class);
    }

    // 3. Register a new user with ROLE_USER by default
    public boolean registerUser(String email, String encryptedPassword) {
        if (findUserAccount(email) != null) {
            return false; // User already exists
        }

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("email", email);
        parameters.addValue("encryptedPassword", encryptedPassword);
        parameters.addValue("enabled", true);
        
        String query = "INSERT INTO sec_user (email, encryptedPassword, enabled) VALUES (:email, :encryptedPassword, :enabled)";
        jdbc.update(query, parameters);
        
        User user = findUserAccount(email);
        
        MapSqlParameterSource roleParameters = new MapSqlParameterSource();
        roleParameters.addValue("userId", user.getUserId());
        roleParameters.addValue("roleId", 1); // 1 = ROLE_USER
        
        String roleQuery = "INSERT INTO user_role (userId, roleId) VALUES (:userId, :roleId)";
        jdbc.update(roleQuery, roleParameters);
        
        return true;
    }

    // 4. Get transactions by User ID
    public List<Transaction> getTransactionsByUserId(Long userId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "SELECT * FROM transaction WHERE userId = :userId ORDER BY transactionDate DESC, id DESC";
        namedParameters.addValue("userId", userId);

        return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Transaction>(Transaction.class));
    }

    // 5. Calculate total balance for User ID
    public BigDecimal getTotalBalanceByUserId(Long userId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "SELECT SUM(amount) FROM transaction WHERE userId = :userId";
        namedParameters.addValue("userId", userId);

        BigDecimal total = jdbc.queryForObject(query, namedParameters, BigDecimal.class);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    // 6. Add a new manual transaction
    public void addTransaction(Transaction transaction) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", transaction.getUserId());
        parameters.addValue("amount", transaction.getAmount());
        parameters.addValue("transactionDate", transaction.getTransactionDate());
        parameters.addValue("category", transaction.getCategory());
        parameters.addValue("description", transaction.getDescription());
        
        String query = "INSERT INTO transaction (userId, amount, transactionDate, category, description) " +
                       "VALUES (:userId, :amount, :transactionDate, :category, :description)";
        jdbc.update(query, parameters);
    }
    
    // 7. Get budgets by User ID
    public List<Budget> getBudgetsByUserId(Long userId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "SELECT * FROM budget WHERE userId = :userId";
        namedParameters.addValue("userId", userId);
        
        return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<Budget>(Budget.class));
    }
    
    // 8. Get spent amount in a category for the current month
    public BigDecimal getMonthlySpentByCategory(Long userId, String category, LocalDate startOfMonth, LocalDate endOfMonth) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        // Sum negative transactions (expenses) only for the category within the month
        String query = "SELECT SUM(amount) FROM transaction " +
                       "WHERE userId = :userId AND category = :category AND amount < 0 " +
                       "AND transactionDate >= :startOfMonth AND transactionDate <= :endOfMonth";
        
        namedParameters.addValue("userId", userId);
        namedParameters.addValue("category", category);
        namedParameters.addValue("startOfMonth", startOfMonth);
        namedParameters.addValue("endOfMonth", endOfMonth);
        
        BigDecimal spent = jdbc.queryForObject(query, namedParameters, BigDecimal.class);
        return spent != null ? spent.abs() : BigDecimal.ZERO;
    }
    
    // 9. Save or update a budget limit
    public void saveBudget(Long userId, String category, BigDecimal limitAmount) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId);
        parameters.addValue("category", category);
        parameters.addValue("limitAmount", limitAmount);

        // H2 syntax for upsert based on UNIQUE constraint
        String query = "MERGE INTO budget (userId, category, limitAmount) KEY (userId, category) VALUES (:userId, :category, :limitAmount)";
        jdbc.update(query, parameters);
    }
}
