package ch.brix.graphQL.client.request;

import java.util.HashMap;
import java.util.Map;

/**
 * Base for argument builders.
 */
public class ArgsBuilder {

    final Map<String, Object> _args = new HashMap<>();

    protected void _add_arg(String name, Object value) {
        if (_args.containsKey(name))
            throw new RuntimeException("Argument " + name + " already set");
        _args.put(name, value);
    }
}
