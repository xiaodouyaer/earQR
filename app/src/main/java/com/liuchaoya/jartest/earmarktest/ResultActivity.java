package com.liuchaoya.jartest.earmarktest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Objects;

public class ResultActivity extends AppCompatActivity {
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(cn.ac.ict.earmarktest.R.layout.activity_result);
        Bundle localBundle = getIntent().getExtras();
        StringBuilder builder = new StringBuilder();
        builder.append("");
        try{
            builder.append(Objects.requireNonNull(localBundle).getString("Tips"));
            builder.append("\n");
            String str = paramBundle.toString();
            if (localBundle.getString("Content") != null) {
                builder = new StringBuilder();
                builder.append(str);
                builder.append(localBundle.getString("Content"));
                builder.append("\n");;
            }
            ((TextView) findViewById(cn.ac.ict.earmarktest.R.id.textView)).setText(builder.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}