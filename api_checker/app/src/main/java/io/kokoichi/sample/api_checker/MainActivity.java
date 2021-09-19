package io.kokoichi.sample.api_checker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //レイアウト要素
    Button button;
    TextView titleView;
    TextView dateView;
    TextView descriptionView;

    //API
    private final String API_URL_PREFIX = "www.googleapis.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        titleView = findViewById(R.id.titleView);
        dateView = findViewById(R.id.dateView);
        descriptionView = findViewById(R.id.descriptionView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MyAsync async = new MyAsync(titleView, dateView, descriptionView);
                FaceDetect async = new FaceDetect(titleView, dateView, descriptionView, getResources());
                async.execute();
            }
        });
    }
    // ここにAPI通信処理を書きます。
}