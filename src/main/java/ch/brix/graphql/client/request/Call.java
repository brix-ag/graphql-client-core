package ch.brix.graphql.client.request;

import lombok.*;

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
