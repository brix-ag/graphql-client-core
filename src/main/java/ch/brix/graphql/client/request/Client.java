package ch.brix.graphql.client.request;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

/**
 * Base for GraphQL clients.
 */
@Slf4j
@RequiredArgsConstructor
public class Client {

    private final String endpoint;
    private final OkHttpClient client;
    private final TypeRegistry typeRegistry;
    private final Gson gson = new Gson();

    public Client(String endpoint, TypeRegistry typeRegistry) {
        this(endpoint, new OkHttpClient.Builder().build(), typeRegistry);
    }

    public <T> T execute(CallBuilder<T> callBuilder) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String serializedCall = Serializer.serializeRootCall(callBuilder._call);
        log.debug(serializedCall);
        Call call = client.newCall(new Request.Builder()
                .url(endpoint)
                .post(RequestBody.create(MediaType.get("application/json"), gson.toJson(new Query(serializedCall))))
                .build());
        JsonElement jsonResult;
        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                if (response.body() != null) {
                    String rspns = response.body().string();
                    log.debug(rspns);
                    Deserializer.deserializeResult(JsonParser.parseString(rspns), callBuilder._call, typeRegistry);
                }
                throw new RemoteException("Request failed with code = " + response.code() + " and message: " + response.message());
            }
            if (response.body() == null)
                return null;
            jsonResult = JsonParser.parseReader(response.body().charStream());
        }
        return Deserializer.deserializeResult(jsonResult, callBuilder._call, typeRegistry);
    }

}
