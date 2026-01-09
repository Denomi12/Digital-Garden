package si.um.feri.maprri.raster.backendCalls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import si.um.feri.maprri.raster.Garden;

public class FetchGardens {

    public interface GardensCallback {
        void onSuccess(Array<Garden> gardens);
        void onError(Throwable t);
    }


    public static void getAllGardens(String backendUrl, GardensCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest request = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .url(backendUrl + "/garden/")
                .build();

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                try {
                    String json = httpResponse.getResultAsString();
//                    System.out.println("Prejeli JSON: " + json);

                    Json gdxJson = new Json();

                    if (json.trim().startsWith("[")) {
                        Array<Garden> gardensArray = gdxJson.fromJson(Array.class, Garden.class, json);
                        callback.onSuccess(gardensArray);
                    } else {
                        System.err.println("Napaka od backend-a: " + json);
                        callback.onError(new Exception("Invalid JSON from backend: " + json));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError(e);
                }
            }

            @Override
            public void failed(Throwable t) {
                t.printStackTrace();
                callback.onError(t);
            }

            @Override
            public void cancelled() {
                callback.onError(new Exception("Request cancelled"));
            }
        });

    }

}
