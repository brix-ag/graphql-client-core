package ch.brix.gql.client;

/**
 * Used to modify package private 'call' field that should not be made public.
 */
public class CallModifier {
    public static <T extends CallBuilder<?>> T makeMutation(T callBuilder) {
        callBuilder._call.setMutation(true);
        return callBuilder;
    }
}
