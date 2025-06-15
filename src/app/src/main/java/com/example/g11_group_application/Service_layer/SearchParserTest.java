package com.example.g11_group_application.Service_layer;

import static com.example.g11_group_application.Service_layer.User.displayUsers;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 8-May-2024
 * JUnit test class for testing SearchNameQuery and SearchDateRangeQuery functionalities
 * within a search parser framework. This class validates the functionality of parsing and
 * executing queries based on specific criteria like name parts and date ranges.
 */
public class SearchParserTest {

    private List<User> users;

    @Before
    public void setUp() {
        users = SearchParserTest.getUsers();
    }

    @Test
    public void testNameQueryWithPartialFirstName() {
        SearchNameQuery query = new SearchNameQuery("al", null, null);
        List<User> results = query.execute(users);
        Assert.assertTrue("Should return users with 'al' in their first names",
                results.stream().anyMatch(user -> user.getFirstName().toLowerCase().contains("al")));
    }

    @Test
    public void testNameQueryWithFirstNameAndPartialLastName() {
        SearchNameQuery query = new SearchNameQuery("Alice", null, "na");
        List<User> results = query.execute(users);
        boolean valid = results.stream().anyMatch(user ->
                user.getFirstName().equalsIgnoreCase("Alice") &&
                        (user.getLastName().contains("na") || (user.getMiddleName() != null && user.getMiddleName().contains("na")))
        );
        Assert.assertTrue("Should return users with first name Alice and 'na' in their last names or middle names", valid);
    }

    @Test
    public void testNameQueryWithFullNames() {
        SearchNameQuery query = new SearchNameQuery("Alice", "Nakiri", "mo");
        List<User> results = query.execute(users);
        displayUsers(results);
        Assert.assertTrue("Should return users with full name Alice Nakiri and 'mo' in last name",
                results.stream().anyMatch(user -> user.getFirstName().equalsIgnoreCase("Alice")
                        && user.getMiddleName().equalsIgnoreCase("Nakiri") && user.getLastName().toLowerCase().contains("mo")));
    }

    @Test
    public void testDateRangeQueryFromTo() {
        SearchDateRangeQuery query = new SearchDateRangeQuery("1992-01-01", "1997-01-01");
        List<User> results = query.execute(users);
        Assert.assertTrue("Should return users within the date range 1992-01-01 to 1997-01-01",
                results.stream().allMatch(user -> user.getDob().compareTo("1992-01-01") >= 0 && user.getDob().compareTo("1997-01-01") <= 0));
    }

    @Test
    public void testDateRangeQueryFromOnly() {
        SearchDateRangeQuery query = new SearchDateRangeQuery("1997-01-01", null);
        List<User> results = query.execute(users);
        Assert.assertTrue("Should return users with DOB from 1997-01-01 onwards",
                results.stream().allMatch(user -> user.getDob().compareTo("1997-01-01") >= 0));
    }

    @Test
    public void testDateRangeQueryToOnly() {
        SearchDateRangeQuery query = new SearchDateRangeQuery(null, "1991-10-10");
        List<User> results = query.execute(users);
        Assert.assertTrue("Should return users with DOB up to 1991-10-10",
                results.stream().allMatch(user -> user.getDob().compareTo("1991-10-10") <= 0));
    }

    @Test
    public void testParserWithDateRangeQuery() {
        String input = "from: 1995-05-05 to: 1995-05-05";
        SearchTokenizer tokenizer = new SearchTokenizer(input);
        SearchParser parser = new SearchParser(tokenizer.getTokens());
        List<User> results = parser.parse().execute(users);
        Assert.assertTrue("Should return users with DOB on 1995-05-05",
                results.stream().allMatch(user -> user.getDob().equals("1995-05-05")));
    }

    @Test
    public void testInvalidTokenException() {
        try {
            new SearchTokenizer("Alice:72");
            Assert.fail("IllegalTokenException was expected");
        } catch (SearchToken.IllegalTokenException e) {
            Assert.assertEquals("Token does not correlate to any type", e.getMessage());
        }
    }




    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();
        // Users with first names starting with "Al"
        users.add(new User("ANCW1", "Alchilles", null, "Smith", "1990-01-01"));
        users.add(new User("ANCW2", "Barie", "Alalie", "Jones", "1992-02-02"));
        users.add(new User("ANCW3", "Bonnie", null, "Alex", "1995-03-22"));

        // Names with first name as "Alice" and a last name starting with "Na"
        users.add(new User("ANCW4", "Alice", null, "Nash", "1993-03-03"));
        users.add(new User("ANCW5", "Alice", "Narvaez", "Lynn", "1994-04-04"));
        users.add(new User("ANCW6", "Alice", "Erina", "Natsuki", "1996-04-05"));

        // Names starting with "Alice Nakiri" and the last name starting with "Mo"
        users.add(new User("ANCW7", "Alice", "Nakiri", "Moriarty", "1995-05-05"));
        users.add(new User("ANCW8", "Alice", "Nakiri", "Morgan", "1996-06-06"));

        // Additional diverse set of users to ensure robust testing
        users.add(new User("ANCW9", "Bob", null, "White", "1985-05-05"));
        users.add(new User("ANCW10", "Charlie", "Edward", "Black", "1986-06-06"));
        users.add(new User("ANCW11", "Diana", "Rose", "Brown", "1987-07-07"));
        users.add(new User("ANCW12", "Edward", "Oscar", "Clark", "1988-08-08"));
        users.add(new User("ANCW13", "Fiona", "Grace", "Adler", "1989-09-09"));
        users.add(new User("ANCW14", "George", "Max", "Hopper", "1991-10-10"));
        users.add(new User("ANCW15", "Hannah", "Joy", "Owen", "1992-11-11"));
        users.add(new User("ANCW16", "Ian", "Pete", "Knight", "1993-12-12"));
        users.add(new User("ANCW17", "Jane", "April", "King", "1995-01-13"));
        users.add(new User("ANCW18", "Kyle", "Sean", "Prince", "1996-02-14"));
        users.add(new User("ANCW19", "Laura", "Beth", "Knightly", "1997-03-15"));
        users.add(new User("ANCW20", "Michael", "Neal", "Archer", "1998-04-16"));
        users.add(new User("ANCW21", "Natalie", "Faith", "Arden", "1999-05-17"));
        users.add(new User("ANCW22", "Oscar", "Duke", "East", "2000-06-18"));
        users.add(new User("ANCW23", "Patricia", "Quinn", "West", "2001-07-19"));
        return users;
    }
}
