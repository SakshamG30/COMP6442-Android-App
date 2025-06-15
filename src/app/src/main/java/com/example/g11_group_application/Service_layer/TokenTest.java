package com.example.g11_group_application.Service_layer;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 08-May-2024
 * JUnit tests for the SearchTokenizer class, verifying the correct tokenization of various input strings.
 */
public class TokenTest {

    private SearchTokenizer tokenizer;

    @Test
    public void testInput1() {
        tokenizer = new SearchTokenizer("From: 2021-05-12");
        ArrayList<SearchToken> expectedTokens = new ArrayList<>();
        expectedTokens.add(new SearchToken(SearchToken.TokenType.KEYWORD, "From:"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.DATE, "2021-05-12"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.EOF, ""));
        assertEquals(expectedTokens, tokenizer.getTokens());
    }

    @Test
    public void testInput2() {
        tokenizer = new SearchTokenizer("Alice Bob Nakiri");
        ArrayList<SearchToken> expectedTokens = new ArrayList<>();
        expectedTokens.add(new SearchToken(SearchToken.TokenType.FIRST_NAME, "Alice"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.LAST_NAME, "Nakiri"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.MIDDLE_NAME, "Bob"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.EOF, ""));
        assertEquals(expectedTokens, tokenizer.getTokens());
    }

    @Test
    public void testInput3() {
        tokenizer = new SearchTokenizer("To: 2020-01-01 Bob Ross");
        ArrayList<SearchToken> expectedTokens = new ArrayList<>();
        expectedTokens.add(new SearchToken(SearchToken.TokenType.KEYWORD, "To:"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.DATE, "2020-01-01"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.FIRST_NAME, "Bob"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.LAST_NAME, "Ross"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.EOF, ""));
        assertEquals(expectedTokens, tokenizer.getTokens());
    }

    @Test
    public void testInput4() {
        tokenizer = new SearchTokenizer("Alice From: 2021-05-12 To: 2020-01-01");
        ArrayList<SearchToken> expectedTokens = new ArrayList<>();
        expectedTokens.add(new SearchToken(SearchToken.TokenType.FIRST_NAME, "Alice"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.KEYWORD, "From:"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.DATE, "2021-05-12"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.KEYWORD, "To:"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.DATE, "2020-01-01"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.EOF, ""));
        assertEquals(expectedTokens, tokenizer.getTokens());
    }

    @Test
    public void testInput5() {
        tokenizer = new SearchTokenizer("Alex To: 1994-01-01");
        ArrayList<SearchToken> expectedTokens = new ArrayList<>();
        expectedTokens.add(new SearchToken(SearchToken.TokenType.FIRST_NAME, "Alex"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.KEYWORD, "To:"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.DATE, "1994-01-01"));
        expectedTokens.add(new SearchToken(SearchToken.TokenType.EOF, ""));
        assertEquals(expectedTokens, tokenizer.getTokens());
    }

    @Test
    public void testInvalidToken() {
        try {
            new SearchTokenizer("Alice:72");
            // Manually fail the test if no exception is thrown
            Assert.fail("IllegalTokenException was expected");
        } catch (SearchToken.IllegalTokenException e) {
            // Test passed, optionally check exception details
            Assert.assertEquals("Token does not correlate to any type", e.getMessage());
        }
    }
}
