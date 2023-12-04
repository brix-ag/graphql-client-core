package ch.brix.gql.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Call to GraphQL API.
 * @param <T> return type
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Call<T> {

    @EqualsAndHashCode.Include
    private final String name;
    @Setter
    private Map<String, Object> args;
    private final Set<On> ons = new HashSet<>();
    private final String innerReturnType;
    @Setter
    private boolean mutation;

}
