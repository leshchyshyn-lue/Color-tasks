package com.example.colortasks;


import com.example.colortasks.entity.User;
import com.example.colortasks.repository.UserRepository;
import com.example.colortasks.security.UserDetailsImpl;
import com.example.colortasks.service.UserDetailsServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceImplTest {

    private static final int USER_ID = 1;
    private static final String PASSWORD = "Password12345";
    private static final String USERNAME = "username";

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void loadUserByUsernameSuccess() {
        User userBefore = new User();
        userBefore.setId(USER_ID);

        UserDetails userDetails = new UserDetailsImpl(userBefore);

        User userAfter = new User();
        userAfter.setUsername(userDetails.getUsername());
        userAfter.setPassword(userDetails.getPassword());

        when(userRepository.findByUsername(userBefore.getUsername())).thenReturn(Optional.of(userBefore));

        UserDetails result = userDetailsService.loadUserByUsername(userBefore.getUsername());

        assertEquals(result.getUsername(), userAfter.getUsername());
        assertEquals(result.getPassword(), userAfter.getUsername());
    }

    @Test
    public void loadUserByUsernameFailUsernameNotFoundExceptionOrRunTimeException() {
        User user = new User();
        user.setUsername(USERNAME);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> userDetailsService.loadUserByUsername(user.getUsername()));
    }

    @Test
    public void findUserBySessionSuccess() {
        User user = new User();
        user.setPassword(PASSWORD);
        user.setUsername(USERNAME);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        User result = userDetailsService.findUserBySession();
        assertEquals(user, result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
    }
}
