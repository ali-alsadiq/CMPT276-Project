package com.cmpt276.project.porject.auth.unit;

import com.cmpt276.project.porject.auth.UserController;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordStrengthTest {
    private final UserController userController = new UserController();

    private int calculateStrength(String password) {
        return (int) ReflectionTestUtils.invokeMethod(userController, "calculatePasswordStrength", password);
    }

    /* Asserts that an empty password returns strength score 0. */
    @Test
    public void passwordStrength_emptyString_scoresZero() {
        assertEquals(0, calculateStrength(""));
    }

    /* Asserts that "a" scores exactly 1 (only the > 0 length criterion). */
    @Test
    public void passwordStrength_lengthOneOnly_scoresOne() {
        assertEquals(1, calculateStrength("a"));
    }

    /* Asserts that "password" (8 chars, lowercase only) scores 2 */
    @Test
    public void passwordStrength_longLowercaseOnly_scoresTwo() {
        assertEquals(2, calculateStrength("password"));
    }

    /* Asserts that "Password" scores exactly 3 */
    @Test
    public void passwordStrength_longMixedCase_scoresTwo_rejected() {
        assertEquals(3, calculateStrength("Password"));
    }

    /* Asserts that "Password1" scores 4 */
    @Test
    public void passwordStrength_longMixedCaseDigit_scoresFour() {
        assertEquals(4, calculateStrength("Password1"));
    }

    /* Asserts that "Password1!" scores 5. */
    @Test
    public void passwordStrength_longMixedCaseDigitSpecial_scoresFive() {
        assertEquals(5, calculateStrength("Password1!"));
    }

    /* Asserts that "Pass1!" gained the special char, but "Pass1_" would not. */
    @Test
    public void passwordStrength_specialChar_onlyListedCharsCount() {
        assertEquals(4, calculateStrength("Pass1!"));
        assertEquals(3, calculateStrength("Pass1_"));
    }
}
