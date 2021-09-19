package io.kokoichi.sample.api_checker;

import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

class MyAsync extends AsyncTask<String, Void, String> {

    private final WeakReference<TextView> titleViewReference;
    private final WeakReference<TextView> dateViewReference;
    private final WeakReference<TextView> descViewReference;

    private final String API_URL_PREFIX = "test-flask-and-react.herokuapp.com";

    public MyAsync(TextView titleView, TextView dateView, TextView descriptionView) {
        titleViewReference = new WeakReference<TextView>(titleView);
        dateViewReference = new WeakReference<TextView>(dateView);
        descViewReference = new WeakReference<TextView>(descriptionView);
    }


    @Override
    protected String doInBackground(String... params) {
        final StringBuilder result = new StringBuilder();
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority(API_URL_PREFIX);
        uriBuilder.path("/api/me");
//        uriBuilder.appendQueryParameter("q", "夏目漱石");
        final String uriStr = uriBuilder.build().toString();

        try {
            URL url = new URL(uriStr);
            HttpURLConnection con = null;
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect(); //HTTP接続

            final InputStream in = con.getInputStream();
            final InputStreamReader inReader = new InputStreamReader(in);
            final BufferedReader bufReader = new BufferedReader(inReader);

            String line = null;
            while((line = bufReader.readLine()) != null) {
                result.append(line);
                Log.d("hoge", "read !!");
            }
            Log.e("but", result.toString());
            bufReader.close();
            inReader.close();
            in.close();
        }

        catch(Exception e) { //エラー
            Log.e("button", e.getMessage());
            Log.d("hoge", "error while creating Url");
        }

        Log.d("hoge", "Url: " + result);

        return result.toString(); //onPostExecuteへreturn
    }

    @Override
    protected void onPostExecute(String result) { //doInBackgroundが終わると呼び出される
        Log.d("hoge", "onPostExecute is called");
        Log.d("hoge", result);
        try {
            JSONObject json = new JSONObject(result);
//            String items = json.getString("items");
//            JSONArray itemsArray = new JSONArray(items);
//            JSONObject bookInfo = itemsArray.getJSONObject(0).getJSONObject("volumeInfo");

//            JSONObject rankInfo = itemsArray.getJSONObject(0);

            Log.d("hoge", String.valueOf(json));

//            Log.e("hoge", "type of rankInfo is " + rankInfo);

            String name = json.getString("name");
//            String publishedDate = rankInfo.getString("amount");
            int mi_age = json.getInt("age");

            Log.d("hoge", name);
            Log.d("hoge", String.valueOf(mi_age));

            TextView titleView = titleViewReference.get();
            TextView dateView = dateViewReference.get();
            TextView descView = descViewReference.get();

            titleView.setText(name);
//            dateView.setText(publishedDate);
            dateView.setText(String.valueOf(mi_age));
            descView.setText(json.toString());

        } catch (JSONException e) {
            Log.d("hoge", "onPostExecute ERROR");
            e.printStackTrace();
        }

    }
}