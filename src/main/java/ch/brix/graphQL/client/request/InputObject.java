package ch.brix.graphQL.client.request;

import java.util.HashMap;
import java.util.Map;

/**
 * Base for input objects.
 */
public class InputObject {
    protected final Map<String, Object> values = new HashMap<>();
}
