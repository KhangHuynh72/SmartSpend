package ca.humber.huynh.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ca.humber.huynh.database.DatabaseAccess;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	@Autowired
    private DatabaseAccess da;
	
	@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Find the user in the database by email
        ca.humber.huynh.beans.User user = da.findUserAccount(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("User " + username + " was not found.");
        }

        // 2. Get the roles for this user from the DB
        List<String> roleNames = da.getRolesById(user.getUserId());

        // 3. Convert role strings (like "ROLE_USER") into GrantedAuthority objects
        List<GrantedAuthority> grantList = new ArrayList<>();
        if (roleNames != null) {
            for (String role : roleNames) {
                grantList.add(new SimpleGrantedAuthority(role));
            }
        }

        // 4. Return the standard Spring User object
        return new User(user.getEmail(), user.getEncryptedPassword(), grantList);
    }
}
