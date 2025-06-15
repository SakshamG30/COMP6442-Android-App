package com.example.g11_group_application.Service_layer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 02-May-2024
 * Extends the abstract SearchQuery class to implement a search that filters users based on their name.
 * The search can be customized to match first, middle, and last names in various combinations.
 */
public class SearchNameQuery extends SearchQuery {

    private String firstName;
    private String middleName;
    private String lastName;

    public SearchNameQuery(String firstName, String middleName, String lastName){
        this.firstName = firstName!=null ? firstName.toLowerCase() : null;
        this.middleName = middleName!=null ? middleName.toLowerCase() : null;
        this.lastName = lastName!=null ? lastName.toLowerCase() : null;
    }

    /**
     * Executes the search query by filtering users based on the provided name criteria.
     * Supports searching by different combinations of first, middle, and last names.
     * @param allUsers A list of all users to be filtered.
     * @return A list of users matching the search criteria. Duplicate entries are removed, and the list is sorted based on the name fields used for searching.
     */
    @Override
    public List<User> execute(List<User> allUsers) {
        List<User> firstNameMatches = new ArrayList<>();
        List<User> lastNameMatches = new ArrayList<>();
        List<User> middleNameMatches = new ArrayList<>();

        // Iterate over all users and classify them based on where the substring is found
        for (User user : allUsers) {
            String userFirstName = user.getFirstName().toLowerCase();
            String userMiddleName = user.getMiddleName() != null ? user.getMiddleName().toLowerCase() : "";
            String userLastName = user.getLastName().toLowerCase();

            if (firstName != null && middleName == null && lastName == null) {
                // Case 1: Single input, treated as a partial match against all name fields
                if (userFirstName.contains(firstName)) {
                    firstNameMatches.add(user);
                }
                if (userLastName.contains(firstName)) {
                    lastNameMatches.add(user);
                }
                if (userMiddleName.contains(firstName)) {
                    middleNameMatches.add(user);
                }
            } else if (firstName != null && middleName == null && lastName != null) {
                // Case 2: First name and partial last name provided
                if (userFirstName.equals(firstName)) {
                    if (userLastName.contains(lastName)) {
                        lastNameMatches.add(user);
                    }
                    if (userMiddleName.contains(lastName)) {
                        middleNameMatches.add(user);
                    }
                }
            } else if (firstName != null && middleName != null && lastName != null) {
                // Case 3: All name parts are provided
                if (userFirstName.equals(firstName) && userMiddleName.equals(middleName) && userLastName.contains(lastName)) {
                    lastNameMatches.add(user);  // Prioritize by last name
                }
            }
        }

        // Combine results in the prioritized order for Case 1
        List<User> result = new ArrayList<>();
        if (firstName != null && middleName == null && lastName == null) {
            result.addAll(firstNameMatches);
            result.addAll(lastNameMatches);
            result.addAll(middleNameMatches);
        } else if (firstName != null && middleName == null) {
            // Combine for Case 2
            result.addAll(middleNameMatches);
            result.addAll(lastNameMatches);
            // Sort the final list alphabetically by first name, then last name
            Collections.sort(result, (u1, u2) -> {
                int firstNameComparison = u1.getFirstName().compareToIgnoreCase(u2.getFirstName());
                if (firstNameComparison != 0) return firstNameComparison;

                return u1.getLastName().compareToIgnoreCase(u2.getLastName());
            });
        } else {
            // Add for Case 3
            result.addAll(lastNameMatches);
            // Sort the final list alphabetically by last name
            Collections.sort(result, (u1, u2) -> {
                return u1.getLastName().compareToIgnoreCase(u2.getLastName());
            });
        }

        // Remove duplicates and maintain insertion order using LinkedHashSet
        Set<User> uniqueUsers = new LinkedHashSet<>(result);
        result = new ArrayList<>(uniqueUsers);

        return result;
    }

    private int compareNullableStrings(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return 1;  // Consider null greater than any string
        if (s2 == null) return -1;
        return s1.compareToIgnoreCase(s2);
    }
}
