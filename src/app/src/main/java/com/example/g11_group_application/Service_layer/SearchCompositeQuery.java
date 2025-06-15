package com.example.g11_group_application.Service_layer;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 03-May-2024
 * Extends the abstract SearchQuery class to implement composite search functionality.
 * This class allows combining multiple search queries into a single composite query. Each sub-query
 * can filter the users independently, and the results are compounded sequentially.
 */
public class SearchCompositeQuery extends SearchQuery {
    private List<SearchQuery> queries;

    public SearchCompositeQuery(List<SearchQuery> queries) {
        this.queries = queries;
    }

    /**
     * Executes the composite search query by sequentially applying each individual query in the list.
     * Starts with the complete list of users and progressively filters it down through each query.
     *
     * @param allUsers A list of all users to be filtered through the composite query.
     * @return A list of users that meet all the criteria specified by the composite search queries.
     */
    @Override
    public List<User> execute(List<User> allUsers) {
        List<User> filteredUsers = new ArrayList<>(allUsers);

        for (SearchQuery query : queries) {
            filteredUsers = query.execute(filteredUsers);
        }

        return filteredUsers;
    }
}