package ch.brix.graphQL.client.request;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Deserializer for GraphQL responses.
 */
public class Deserializer {

    public static <T> T deserializeResult(JsonElement json, Call<T> call, TypeRegistry typeRegistry) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (!json.isJsonObject())
            throw new RuntimeException("JsonObject expected");
        JsonObject obj = json.getAsJsonObject();
        if (obj.has("errors")) {
            JsonElement errors = obj.get("errors");
            if (errors.isJsonArray() && !errors.getAsJsonArray().isEmpty()) {
                throw new RuntimeException(errors.getAsJsonArray().asList().stream()
                        .filter(JsonElement::isJsonObject)
                        .map(JsonElement::getAsJsonObject)
                        .filter(o -> o.has("message"))
                        .map(o -> o.get("message"))
                        .filter(JsonElement::isJsonPrimitive)
                        .map(JsonElement::getAsString)
                        .filter(Objects::nonNull)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.joining("\n\n")));
            } else {
                throw new RuntimeException("Received errors in unknown format");
            }
        } else if (obj.has("error")) {
            throw new RuntimeException(obj.get("error").getAsString());
        } else if (obj.has("data")) {
            if (!obj.get("data").isJsonObject())
                throw new RuntimeException("data object expected");
            obj = obj.get("data").getAsJsonObject();
            if (!obj.has(call.getName()))
                return null;
            return (T) deserialize(obj.get(call.getName()), call, typeRegistry);
        } else {
            return null;
        }
    }

    private static Object deserialize(JsonElement json, Call call, TypeRegistry typeRegistry) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (json.isJsonNull())
            return null;
        if (json.isJsonArray())
            return deserializeList(json.getAsJsonArray(), call, typeRegistry);
        if (json.isJsonPrimitive()) {
            Class type = typeRegistry.getType(call.getInnerReturnType());
            if (type == null)
                throw new RuntimeException("Unknown type " + call.getInnerReturnType());
            if (type.isEnum())
                return Enum.valueOf(type, json.getAsString());
            if (Scalar.class.isAssignableFrom(type))
                return type.getDeclaredConstructor(String.class).newInstance(json.getAsString());
            throw new RuntimeException("Json primitive is not enum and not scalar");
        }
        JsonObject obj = json.getAsJsonObject();
        Class type = typeRegistry.getType(obj.get("__typename").getAsString());
        if (type == null)
            type = typeRegistry.getType(call.getInnerReturnType());
        return getObject(type, typeRegistry, obj);
    }

    private static Object deserialize(JsonElement json, Class<?> type, TypeRegistry typeRegistry) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (json.isJsonNull())
            return null;
        if (json.isJsonArray()) {
            List ret = new ArrayList(json.getAsJsonArray().size());
            for (JsonElement element : json.getAsJsonArray())
                ret.add(deserialize(element, type, typeRegistry));
            return ret;
        }
        if (json.isJsonPrimitive()) {
            if (type.isEnum())
                return Enum.valueOf((Class<? extends Enum>) type, json.getAsString());
            if (Scalar.class.isAssignableFrom(type))
                return type.getDeclaredMethod("of", Object.class).invoke(null, json.getAsString());
            throw new RuntimeException("Json primitive is not enum or scalar");
        }
        // object
        JsonObject obj = json.getAsJsonObject();
        if (obj.has("__typename"))
            type = typeRegistry.getType(obj.get("__typename").getAsString());
        return getObject(type, typeRegistry, obj);
    }

    private static Object getObject(Class<?> type, TypeRegistry typeRegistry, JsonObject obj) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object ret = type.getDeclaredConstructor().newInstance();
        for (Field field : type.getDeclaredFields()) {
            SerializedName serializedName = field.getDeclaredAnnotation(SerializedName.class);
            if (serializedName != null && obj.has(serializedName.value()) && !obj.get(serializedName.value()).isJsonNull()) {
                JsonElement value = obj.get(serializedName.value());
                String setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                Method setter = type.getDeclaredMethod(setterName, field.getType());
                InnerType innerType = field.getDeclaredAnnotation(InnerType.class);
                setter.invoke(ret, deserialize(value, innerType == null ? field.getType() : innerType.value(), typeRegistry));
            }
        }
        return ret;
    }

    private static Object deserializeList(JsonArray arr, Call call, TypeRegistry typeRegistry) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List list = new ArrayList();
        for (JsonElement element : arr)
            list.add(deserialize(element, call, typeRegistry));
        return list;
    }

}
