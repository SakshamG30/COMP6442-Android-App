package com.example.g11_group_application.Service_layer;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 03-May-2024
 * This class parses a sequence of tokens into structured search queries based on specific keywords and types.
 * It supports parsing for complex queries that involve date ranges and names, creating the appropriate
 * SearchQuery instances based on the sequence and type of tokens provided.
 */
public class SearchParser {
    private ArrayList<SearchToken> tokens;
    private int currentTokenIndex = 0;
    List<SearchQuery> queries = new ArrayList<>();

    public SearchParser(ArrayList<SearchToken> tokens){
        this.tokens = tokens;
    }

    /**
     * Advances to the next token in the sequence.
     */
    private void next(){
        currentTokenIndex++;
    }

    /**
     * Parses the list of tokens and returns the corresponding SearchQuery.
     * It handles different formats of queries such as date range and name-based searches.
     * @return A SearchQuery derived from the parsed tokens, which could be a single query or a composite of multiple queries.
     * @throws IllegalStateException If the tokens are in an unexpected format or if required tokens are missing.
     */
    public SearchQuery parse(){
        if(tokens.isEmpty()){
            throw new IllegalStateException("No tokens to parse");
        }

        while (currentTokenIndex < tokens.size()) {
            SearchToken token = tokens.get(currentTokenIndex);

            if (token.getType() == SearchToken.TokenType.KEYWORD) {
                switch (token.getValue().toLowerCase()) {
                    case "from:":
                        queries.add(parseFromToDateRangeQuery());
                        break;
                    case "to:":
                        queries.add(parseToDateRangeQuery());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected keyword: " + token.getValue());
                }
            }
            else if (token.getType() == SearchToken.TokenType.EOF){
                next(); // Skip EOF
            }
            else {
                queries.add(parseNameQuery());
                break;
            }
        }

        if (queries.size() == 1) {
            return queries.get(0);
        } else {
            return new SearchCompositeQuery(queries);
        }
    }

    /**
     * Parses tokens to create a date range query starting from a specified date.
     * @return A SearchQuery for filtering based on a start date and optional end date.
     * @throws IllegalStateException if the expected date token is missing or incorrect.
     */
    private SearchQuery parseFromToDateRangeQuery() {
        next(); //skip 'From:'

        //Consumes date tokentype to get value of the current token
        String fromDate = consume(SearchToken.TokenType.DATE);
        //Checks if there is a 'to:' afterwards
        if(currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).getValue().equalsIgnoreCase("To:")){
            consume(SearchToken.TokenType.KEYWORD,"To:");
            String toDate = consume(SearchToken.TokenType.DATE);
            return new SearchDateRangeQuery(fromDate, toDate);
        }
        return new SearchDateRangeQuery(fromDate, null);
    }

    /**
     * Parses tokens to create a date range query ending on a specified date.
     * @return A SearchQuery for filtering based on an end date.
     * @throws IllegalStateException if the expected date token is missing or incorrect.
     */
    private SearchQuery parseToDateRangeQuery() {
        next(); // skip 'To:'

        //Consumes date tokentype to get value of the current token
        if(tokens.get(currentTokenIndex).getType()!= SearchToken.TokenType.DATE){
            parseNameQuery();
        }
        String toDate = consume(SearchToken.TokenType.DATE);
        return new SearchDateRangeQuery(null, toDate);
    }

    /**
     * Parses tokens to construct a name-based query, utilizing distinct tokens for first, middle, and last names.
     * @return NameQuery constructed from parsed name tokens.
     * @throws IllegalStateException if tokens are in unexpected sequence or types.
     */
    private SearchQuery parseNameQuery() {
        String firstName = null, middleName = null, lastName = null;
        // Parse through tokens and assign name parts accordingly
        while (currentTokenIndex < tokens.size() && tokens.get(currentTokenIndex).getType() != SearchToken.TokenType.EOF) {
            SearchToken token = tokens.get(currentTokenIndex);
            switch (token.getType()) {
                case FIRST_NAME:
                    firstName = token.getValue();
                    next();
                    break;
                case MIDDLE_NAME:
                    middleName = token.getValue();
                    next();
                    break;
                case LAST_NAME:
                    lastName = token.getValue();
                    next();
                    break;
                default:
                    if (token.getType() == SearchToken.TokenType.KEYWORD) {
                        switch (token.getValue().toLowerCase()) {
                            case "from:":
                                queries.add(parseFromToDateRangeQuery());
                                break;
                            case "to:":
                                queries.add(parseToDateRangeQuery());
                                break;
                            default:
                                throw new IllegalStateException("Unexpected keyword: " + token.getValue());
                        }
                    }
                    else{
                        throw new IllegalStateException("No proper keyword to parse: " + token.getValue());
                    }
            }
        }

        // Return a NameQuery with potentially partial names
        return new SearchNameQuery(firstName, middleName, lastName);
    }

    /**
     * Consumes a token and returns its value, ensuring it matches the expected type.
     * @param expectedType The expected type of the next token.
     * @return The value of the consumed token.
     * @throws IllegalStateException If the next token does not match the expected type.
     */
    private String consume(SearchToken.TokenType expectedType) {
        if (currentTokenIndex >= tokens.size()) {
            throw new IllegalStateException("Not enough tokens");
        }
        SearchToken token = tokens.get(currentTokenIndex++);
        if (token.getType() != expectedType) {
            if(expectedType == SearchToken.TokenType.DATE){
                throw new IllegalStateException("Expected token of type " + expectedType + ", date format is likely wrong. Must be in the format (yyyy-MM-dd).");
            }
            throw new IllegalStateException("Expected token of type " + expectedType + " but found " + token.getType());
        }
        return token.getValue();
    }

    /**
     * Consumes a token after verifying it matches both the expected type and value.
     * @param expectedType The expected type of the token.
     * @param expectedValue The expected value of the token.
     * @throws IllegalStateException If the token does not match the expected criteria.
     */
    private void consume(SearchToken.TokenType expectedType, String expectedValue) {
        SearchToken token = tokens.get(currentTokenIndex++);
        if (!(token.getType() == expectedType && token.getValue().equalsIgnoreCase(expectedValue))) {
            throw new IllegalStateException("Expected " + expectedValue + " but found " + token.getValue());
        }
    }
}
