package com.example.Problem2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class Problem2ApplicationTests {

    /**
     * Test cases for the Login method in AuthenticateUser class
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */

    //test case for correct credentials
    @Test
    public void testValidCredentials() throws IOException, NoSuchAlgorithmException {
        String username = "karan";
        String password = "k";
        String securityanswer = "k";
        AuthenticateUser auth = new AuthenticateUser();
        boolean result = auth.Login(username, password, securityanswer);
        assertTrue(result);
    }

    //test case for incorrect username
    @Test
    public void testInvalidUsername() throws IOException, NoSuchAlgorithmException {
        String username = "jane";
        String password = "k";
        String securityanswer = "k";
        AuthenticateUser auth = new AuthenticateUser();
        boolean result = auth.Login(username, password, securityanswer);
        assertFalse(result);
    }

    //test case for incorrect password
    @Test
    public void testInvalidPassword() throws IOException, NoSuchAlgorithmException {
        String username = "karan";
        String password = "123456";
        String securityanswer = "k";
        AuthenticateUser auth = new AuthenticateUser();
        boolean result = auth.Login(username, password, securityanswer);
        assertFalse(result);
    }

    //test case for blank credentials
    @Test
    public void testEmptyCredentials() throws IOException, NoSuchAlgorithmException {
        String username = "";
        String password = "";
        String securityanswer = "";
        AuthenticateUser auth = new AuthenticateUser();
        boolean result = auth.Login(username, password, securityanswer);
        assertFalse(result);
    }
}
