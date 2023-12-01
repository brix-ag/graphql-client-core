package ch.brix.graphQL.client.request;

import lombok.RequiredArgsConstructor;

/**
 * Base for type builders.
 */
@RequiredArgsConstructor
public class TypeBuilder {

    final On _on;

    protected void _add_field(String name) {
        if (_on.getFields().contains(name))
            throw new RuntimeException("Field " + name + " already set");
        _on.getFields().add(name);
    }

    protected void _add_call(CallBuilder<?> callBuilder) {
        if (_on.getCalls().contains(callBuilder._call))
            throw new RuntimeException("Call already set");
        _on.getCalls().add(callBuilder._call);
    }

}
