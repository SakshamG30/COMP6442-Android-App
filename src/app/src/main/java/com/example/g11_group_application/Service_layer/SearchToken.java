package com.example.g11_group_application.Service_layer;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 01-May-2024
 * Represents a token in the search functionality of an application. Each token has a type that specifies
 * the kind of information it represents (e.g., first name, last name, date, etc.), and a value that stores the
 * actual content for that type. This class is used in the parsing process where search inputs are tokenized.
 */
public class SearchToken {
    private final TokenType type;
    private final String value;


    /**
     * Defines the types of tokens that can be handled in the search process.
     */
    public enum TokenType {
        FIRST_NAME, MIDDLE_NAME, LAST_NAME, DATE, KEYWORD, EOF;
    }

    /**
     * The following exception should be thrown if a tokenizer attempts to tokenize something that is not of one
     * of the types of tokens.
     */
    public static class IllegalTokenException extends IllegalArgumentException {
        public IllegalTokenException(String errorMessage) {
            super(errorMessage);
        }
    }

    public SearchToken(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type + " '" + value + "'";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SearchToken that = (SearchToken) obj;
        return type == that.type && (value != null ? value.equals(that.value) : that.value == null);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}