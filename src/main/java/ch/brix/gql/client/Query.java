package ch.brix.gql.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Only used to serialize the query to get the JSON in the correct format.
 */
@Getter
@RequiredArgsConstructor
public class Query {
    private final String query;
}
