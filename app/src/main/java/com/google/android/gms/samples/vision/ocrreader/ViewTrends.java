package com.google.android.gms.samples.vision.ocrreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ViewTrends extends AppCompatActivity {
    TrendAnalyzer trends = new TrendAnalyzer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trends);
    }
}
