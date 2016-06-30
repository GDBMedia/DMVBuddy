package com.example.guest.apitest;

/**
 * Created by Guest on 6/29/16.
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GooglePlacesService {
    public static void findGod( String longitude, String latitude,  Callback callback) {

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.GOOGLE_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(Constants.LOCATIONP,  latitude+ "," +  longitude)
                  .addQueryParameter(Constants.KEYWORDP, Constants.KEYWORD)
                  .addQueryParameter(Constants.RANKBYP, Constants.RANKBY)
                  .addQueryParameter("type", "local_government_office")
//                 .addQueryParameter("pagetoken", "CoQC_QAAANmfsLgGkYXoKhHGBHG_leKG49nnaSBNfyo7LL2IGi6i4wlmnPLiwFmNi_6m_YosXIrk05zDC-ylwnkeFktti0Vc-Jz6rZKg34wUZ7U4o_KaBkJ1ENP48KVCYCM_Nxiy73rtlG5Eozt8M4OikaOyNK8qrnxDLdNQ6Z1li2j2WIh5yy5H4u4x8DbwXI3dnj5mV2pCCptr9botHo46SW7KPRbWFvAISFXf2KUs_TnWHmX1j9wPikWspK16D1r9IQqxHjWOQKKjmrKWwxCFUuEIU9varBa8VoLLRbF7cnsSIqpLPnW_vYXhIY-_WwFTRd0VgemXymdWvF---pTlBTDEE8USEOr1zc0MwSYinM8PJBkmxXEaFFxo43LUHP_Sv8LaSN2T0Pq1jmiQ")
                  .addQueryParameter(Constants.KEYP, Constants.KEY);
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public ArrayList<Church> processResults(Response response) {
        ArrayList<Church> churches = new ArrayList<>();

        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject yelpJSON = new JSONObject(jsonData);
                JSONArray placesJSON = yelpJSON.getJSONArray("results");
                for (int i = 0; i < placesJSON.length(); i++) {
                    JSONObject churchJSON = placesJSON.getJSONObject(i);
                    String name = churchJSON.getString("name");
                    double rating = churchJSON.optDouble("rating", 0);
                    String vicinity = churchJSON.getString("vicinity");
                    Church church = new Church(name, rating, vicinity);
                    churches.add(church);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return churches;
    }
}
