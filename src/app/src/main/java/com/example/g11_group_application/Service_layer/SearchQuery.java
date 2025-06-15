package com.example.g11_group_application.Service_layer;

import java.util.List;

/**
 * @Author: Saksham Gupta (u7726995)
 * Created: 02-May-2024
 * Represents an abstract class defining the structure of search queries within the application.
 * This class provides a template method `execute` which must be implemented by subclasses
 * to specify the logic for executing a search query over a list of users.
 */
public abstract class SearchQuery {

    /**
     * Executes a search query over the provided list of all users and returns a list of users
     * that match the criteria specified in the subclass implementations.
     *
     * @param allUsers A list of all users to be filtered based on the search query.
     * @return A list of users matching the search criteria.
     */
    public abstract List<User> execute(List<User> allUsers);
}
