package com.example.g11_group_application.Service_layer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 02-May-2024
 * Extends the abstract SearchQuery class to implement a search that filters users based on a specified date range.
 * This class specifically handles queries where users are filtered by their date of birth falling within a certain range.
 */
public class SearchDateRangeQuery extends SearchQuery {
    private String fromDate, toDate;

    public SearchDateRangeQuery(String fromDate, String toDate){
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    /**
     * Executes the search query by filtering users whose dates of birth fall within the specified date range.
     * The method can handle cases where only the from date, only the to date, or both are specified.
     * If neither date is specified, all users are returned.
     *
     * @param allUsers A list of all users to be filtered.
     * @return A list of users whose date of birth falls within the specified range.
     */
    @Override
    public List<User> execute(List<User> allUsers){
        if (fromDate != null && toDate != null) {
            return allUsers.stream()
                    .filter(user -> user.getDob().compareTo(fromDate) >= 0 && user.getDob().compareTo(toDate) <= 0)
                    .collect(Collectors.toList());
        } else if (fromDate != null) {
            return allUsers.stream()
                    .filter(user -> user.getDob().compareTo(fromDate) >= 0)
                    .collect(Collectors.toList());
        } else if (toDate != null) {
            return allUsers.stream()
                    .filter(user -> user.getDob().compareTo(toDate) <= 0)
                    .collect(Collectors.toList());
        } else {
            return allUsers;
        }
    }
}
