package ch.brix.graphQL.client.request;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GraphQL request serializer.
 */
public class Serializer {

    public static String serializeRootCall(Call<?> call) {
        return (call.isMutation() ? "mutation" : "") + "{" + serialize(call) + "}";
    }

    private static String serialize(Call<?> call) {
        return call.getName() + serialize(call.getArgs()) + (call.getOns().isEmpty() ? "" : ("{" + serialize(call.getOns(), call.getInnerReturnType()) + "}"));
    }

    private static String serialize(Set<On> ons, String innerReturnType) {
        return ons.stream()
                .map(on -> (on.getType().equals(innerReturnType) ? "" : ("...on " + on.getType() + "{")) + serialize(on) + (on.getType().equals(innerReturnType) ? "" : "}"))
                .collect(Collectors.joining(" "));
    }

    private static String serialize(On on) {
        return "__typename " + String.join(" ", on.getFields()) + (on.getCalls().isEmpty() ? "" : (" " + serialize(on.getCalls())));
    }

    private static String serialize(Set<Call<?>> calls) {
        return calls.stream()
                .map(Serializer::serialize)
                .collect(Collectors.joining(" "));
    }

    private static String serialize(Map<String, Object> args) {
        if (args == null || args.isEmpty())
            return "";
        return "(" + args.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + serializeObject(entry.getValue()))
                .collect(Collectors.joining(",")) + ")";
    }

    private static String serializeObject(Object value) {
        if (value instanceof List) {
            return "[" + ((List)value).stream().map(Serializer::serializeObject).collect(Collectors.joining(",")) + "]";
        } else if (value instanceof Scalar) {
            String v = ((Scalar) value).getValue().replace("\\", "\\\\").replace("\"", "\\\"");
            switch (value.getClass().getSimpleName()) {
                case "IntScalar":
                case "BooleanScalar":
                case "FloatScalar":
                    return v;
                default:
                    return '"' + v.replace("\r", "")
                            .replace("\n", "\\n")
                            .replace("\t", "\\t")+ '"';
            }
        } else if (value instanceof Enum) {
            return value.toString();
        } else if (value == null) {
            return "null";
        } else { // input object
            return "{" + ((InputObject) value).values.entrySet().stream()
                    .map(entry -> entry.getKey() + ":" + serializeObject(entry.getValue()))
                    .collect(Collectors.joining(",")) + "}";
        }
    }

}
