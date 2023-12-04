package ch.brix.gql.client;

import java.util.HashMap;
import java.util.Map;

/**
 * The type registry's main purpose is to avoid Class.forName().
 */
public class TypeRegistry {

    private final Map<String, Class<?>> types = new HashMap<>();

    public Class<?> getType(String name) {
        return types.get(name);
    }

    public void addType(String name, Class<?> type) {
        types.put(name, type);
    }

}
