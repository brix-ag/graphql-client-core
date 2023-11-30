package ch.brix.graphQL.client.request;

import lombok.RequiredArgsConstructor;

/**
 * Base for call builders.
 * @param <T> return type
 */
@RequiredArgsConstructor
public class CallBuilder<T> {

    final Call<T> _call;

    protected void _set_args(ArgsBuilder argsBuilder) {
        if (_call.getArgs() != null)
            throw new RuntimeException("Arguments already set for call");
        _call.setArgs(argsBuilder._args);
    }

    protected void _add_on(TypeBuilder typeBuilder) {
        if (_call.getOns().contains(typeBuilder._on))
            throw new RuntimeException("On already set for this type");
        _call.getOns().add(typeBuilder._on);
    }

    // args(ArgsBuilder)
    // onType(typeBuilder)

}
