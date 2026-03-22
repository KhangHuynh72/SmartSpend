package ca.humber.huynh.database;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ca.humber.huynh.beans.User;

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
}
