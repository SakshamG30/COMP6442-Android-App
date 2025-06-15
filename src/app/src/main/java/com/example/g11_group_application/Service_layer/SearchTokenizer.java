package com.example.g11_group_application.Service_layer;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 01-May-2024
 * This class is responsible for tokenizing search input based on specified patterns such as keywords, dates, and names.
 * It parses the input string to identify and categorize parts of the string into tokens that are defined by the TokenType.
 */
public class SearchTokenizer {
    private static final Pattern TOKEN_PATTERNS = Pattern.compile(
            "(?<Keyword>(?i)(from:|to:))|" +            // Matches keywords "from:" and "to:", case-insensitively
                    "(?<Date>\\d{4}-\\d{2}-\\d{2})|" +          // Matches dates in the format DD-MM-YYYY
                    "(?<Name>[a-zA-Z]+(\\s+[a-zA-Z]+){0,2})(?![^\\s:]*:)"   // Matches names; first and potentially middle and last names
    );

    private final String input;                      // Input string to tokenize
    private final ArrayList<SearchToken> tokens = new ArrayList<>(); // List to store generated tokens

    public SearchTokenizer(String input) {
        this.input = input;
        tokenize();
    }

    /**
     * Processes the input string to generate tokens based on the defined patterns.
     * Throws IllegalTokenException if the input does not match any token type.
     */
    private void tokenize() {
        Matcher matcher = TOKEN_PATTERNS.matcher(input); // Create a matcher to find patterns
        if(!matcher.find()){
            throw new SearchToken.IllegalTokenException("Token does not correlate to any type");
        }
        matcher.reset();
        while (matcher.find()) { // Iterate over all matches found in the input string
            if (matcher.group("Keyword") != null) { // Check if the current match is a keyword
                tokens.add(new SearchToken(SearchToken.TokenType.KEYWORD, matcher.group("Keyword")));
            } else if (matcher.group("Date") != null) { // Check if it's a date
                tokens.add(new SearchToken(SearchToken.TokenType.DATE, matcher.group("Date")));
            } else if (matcher.group("Name") != null) {
                String[] names = matcher.group("Name").split("\\s+");
                if (names.length > 0) {
                    tokens.add(new SearchToken(SearchToken.TokenType.FIRST_NAME, names[0]));
                }
                if (names.length > 1) {
                    tokens.add(new SearchToken(SearchToken.TokenType.LAST_NAME, names[names.length - 1])); // Last word is always last name
                }
                if (names.length == 3) {
                    tokens.add(new SearchToken(SearchToken.TokenType.MIDDLE_NAME, names[1])); // Middle name if three words
                }
            }
        }
        tokens.add(new SearchToken(SearchToken.TokenType.EOF, "")); // Add a token to signify the end of input
    }

    /**
     * Returns the list of tokens generated from the input string.
     * @return An ArrayList of SearchToken objects.
     */
    public ArrayList<SearchToken> getTokens() {
        return tokens;
    }
}