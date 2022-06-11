package ch.tobisyurt.comments.service;

import ch.tobisyurt.comments.model.User;
import ch.tobisyurt.comments.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CommentsUserDetailsService  implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;


    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if(user != null) {
            return buildUserForAuthentication(user);
        } else {
            throw new UsernameNotFoundException("username not found");
        }
    }

    private UserDetails buildUserForAuthentication(User user) {
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), null);
    }

    public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder().encode(user.getPassword()));
        userRepo.save(user);
    }


}
