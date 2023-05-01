package me.datafox.ticktacktoe.backend.security;

import me.datafox.ticktacktoe.backend.model.Player;
import me.datafox.ticktacktoe.backend.model.Role;
import me.datafox.ticktacktoe.backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author datafox
 */
@Service
public class PlayerDetailsService implements UserDetailsService {
    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player player = playerRepository.findByUsername(username).orElseThrow(this::exception);
        return new User(player.getUsername(), player.getPassword(),
                player.getRoles().stream().map(Role::getId).map(SimpleGrantedAuthority::new).toList());
    }

    private UsernameNotFoundException exception() {
        return new UsernameNotFoundException("Invalid username or password");
    }
}
