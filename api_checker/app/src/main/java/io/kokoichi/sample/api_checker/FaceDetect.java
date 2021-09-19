package io.kokoichi.sample.api_checker;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


class FaceDetect extends AsyncTask<String, Void, String> {

    private final WeakReference<TextView> titleViewReference;
    private final WeakReference<TextView> dateViewReference;
    private final WeakReference<TextView> descViewReference;

    private final Resources res;

    private final String PATH = "/api/android/facedetect";
//    private final String PATH = "/api/rank";

    private final String API_URL_PREFIX = "test-flask-and-react.herokuapp.com";

    public FaceDetect(TextView titleView, TextView dateView, TextView descriptionView, Resources resources) {
        titleViewReference = new WeakReference<TextView>(titleView);
        dateViewReference = new WeakReference<TextView>(dateView);
        descViewReference = new WeakReference<TextView>(descriptionView);

        res = resources;
    }


    @Override
    protected String doInBackground(String... params) {

        String attachmentName = "bitmap";
        String attachmentFileName = "bitmap.bmp";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        final StringBuilder result = new StringBuilder();
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority(API_URL_PREFIX);
        uriBuilder.path(PATH);
//        uriBuilder.appendQueryParameter("q", "夏目漱石");
        final String uriStr = uriBuilder.build().toString();

        Log.d("hoge", "uriStr: " + uriStr);

        URL url = null;
        try {
            url = new URL(uriStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection con = null;

        try {

//            Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.ayamen);
//            /// POST送信するデータをバイト配列で用意
//            StringBuilder postData = new StringBuilder();
//            for (Map.Entry<String, Object> entry : bitmap) {
//                if (postData.length() != 0) postData.append('&');
//                postData.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
//                        .append('=')
//                        .append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
//            }
//            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
//

            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
//            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Cache-Control", "no-cache");
//            con.setRequestProperty("Content-Type", "multipart/form-data");
            con.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);
            con.setUseCaches(false);
            con.setDoInput(true);   // データを書き込む, include a request body


            Log.d("hoge", "initial set finish");




//
//            Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.ayamen);
//            OutputStream out = new BufferedOutputStream(con.getOutputStream());
////            writeStream(out);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
//
////            InputStream in = new BufferedInputStream(con.getInputStream());
////            readStream(in);




            Log.d("hoge","connect");


            con.connect(); //HTTP接続





            // CONTENT
            // ====================================
//            Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.ayamen);
//            try(OutputStream outStream = con.getOutputStream()) {
////                outStream.write( word.getBytes(StandardCharsets.UTF_8));
////                FileOutputStream outFile = Outp(con.getOutputStream());
//                bitmap.compress(Bitmap.CompressFormat.PNG, 50, outStream);
//                Log.d("hoge", String.valueOf(outStream));
//                outStream.flush();
//                Log.d("hoge","flush");
//                Log.d("hoge", String.valueOf(outStream));
//            } catch (Exception e) {
//                // POST送信エラー
//                e.printStackTrace();
//                Log.d("hoge","POST送信エラー");
//            }

            // ====================================


//            final int status = con.getResponseCode();
//            if (status == HttpURLConnection.HTTP_OK) {
//                // レスポンスを受け取る処理等
//            }
//            else{
//            }
//            Log.d("hoge", res);
//










            // Content PART 2
            // ------------------------------------------------------------------
            DataOutputStream request = new DataOutputStream(
                    con.getOutputStream());

            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + crlf);
            request.writeBytes(crlf);

            // とりあえず、固定の画像を送ってみる
            Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.ayamen);

            Log.d("hoge", "bitmap width: " + bitmap.getWidth());
            Log.d("hoge", "bitmap height: " + bitmap.getHeight());

            //I want to send only 8 bit black & white bitmaps
            byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
            for (int i = 0; i < bitmap.getWidth(); ++i) {
                for (int j = 0; j < bitmap.getHeight(); ++j) {
                    //we're interested only in the MSB of the first byte,
                    //since the other 3 bytes are identical for B&W images
                    pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                }
            }


            request.write(pixels);

            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

            // ------------------------------------------------------------------


            Log.d("hoge", "input proceeding");
            final InputStream in = con.getInputStream();
            final InputStreamReader inReader = new InputStreamReader(in);
            final BufferedReader bufReader = new BufferedReader(inReader);

            Log.d("hoge", "==== in ====");
            Log.d("hoge", String.valueOf(in));

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
            Log.d("hoge", String.valueOf(e));
            Log.e("button", e.getMessage());
            Log.d("hoge", "error while posting");
        }
        finally {
            con.disconnect();
        }

        Log.d("hoge", "result: " + result);

        return result.toString(); //onPostExecuteへreturn
    }

    @Override
    protected void onPostExecute(String result) { //doInBackgroundが終わると呼び出される
        Log.d("hoge", "onPostExecute is called");
        Log.d("hoge", result);
        try {
            JSONObject json = new JSONObject(result);

            Log.d("hoge", String.valueOf(json));


//            String name = json.getString("name");
//            int mi_age = json.getInt("age");
//
//            Log.d("hoge", name);
//            Log.d("hoge", String.valueOf(mi_age));

            TextView titleView = titleViewReference.get();
            TextView dateView = dateViewReference.get();
            TextView descView = descViewReference.get();

//            titleView.setText(name);
////            dateView.setText(publishedDate);
//            dateView.setText(String.valueOf(mi_age));


            descView.setText(json.toString());


        } catch (JSONException e) {
            Log.d("hoge", "onPostExecute ERROR");
            e.printStackTrace();
        }

    }
}