package ch.brix.graphQL.client.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Base for field selections and calls
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class On {
    @EqualsAndHashCode.Include
    private final String type;
    private final Set<String> fields = new HashSet<>();
    private final Set<Call<?>> calls = new HashSet<>();
}
