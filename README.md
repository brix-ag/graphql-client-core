> This is just the core for GraphQL clients by brix. You will need a client for an actual product to be able to use it.

# GraphQL Client Usage

A general explanation how the GraphQL clients based on this one are working.

## Create a client

In the base package you can find the class `Client` (take this one and not its parent) which can be instantiated. Constructor arguments:

1. String endpoint: the endpoint for the graphQL requests
2. (optional) the OkHttpClient

## Authorization

Authorization is done by passing an OkHttpClient which handles it. E.g.

```java
okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token).build()))
                    .build();
```

## Execute Calls

In the base package are two classes `Query` and `Mutation` with static methods to create calls (actually callBuilders, but they *don't* require a `build()` when finished).

1. Use one of the static methods on the `Query` or `Mutation` class to get a call builder.
2. The call builder has the following methods:
    1. `args(ArgBuilder argBuilder)` if there are any arguments, there will always only be one matching ArgBuilder of that type, so there should be no confusion. An ArgBuilder has a method to set each argument, the method expects a specific scalar or an input object for which there are builders.
    2. `onType(OnTypeBuilder onTypeBuilder)` for each possible return type which has fields (so no unions, only their implementations, but interfaces can be used to request common fields of types, so they don't have to be listed for every type). As before the method expects a typed onTypeBuilder of which there is only one. 
    "on" is to select the fields which should be returned, but the fields can be calls (with arguments) themselves which return objects and so on (this is how graphQL do). This means the onTypeBuilder has methods for every field on this type and (unless the field is not scalar or enum and has no arguments) it requires another call builder of which there will be only one. E.g.
    ```java
    Query.workspaceProject()
        .args(new RootQuery_workspaceProject_Args()
                .id(IdScalar.of("123")))
        .onWorkspace(new On_Workspace()
                .id()
                .assets(new Workspace_assets()
                        .args(new Workspace_assets_Args()) // just using default arg values
                        .onAssetItems(new On_AssetItems()
                                .total()
                                .items(new AssetItems_items()
                                        .onAsset(new On_Asset()
                                                .id()
                                                .title()
                                                .description())
                                        .onImage(new On_Image()
                                                .focalPoint())))));
    ```
    Basically the same thing like when we would write the query manually, except that everything is done almost automatically because of the IDE suggestions and the javadoc including default values and deprecation notes helps too.
3. The callBuilder created in 2. can be executed with the client, it knows the exact return type through generics and returns an object of that type, e.g.
    ```java
   Workspace workspace = client.execute(queryFromAbove);
   List<Asset> assets = workspace.getAssets().getItems();
   assets.forEach(asset -> {
       if (asset instanceof Image)
           System.out.println(((Image) asset).getFocalPoint().getValue());
       ...
   })
    ```