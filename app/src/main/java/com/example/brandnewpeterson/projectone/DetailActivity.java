package com.example.brandnewpeterson.projectone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MyParcelable myData = (MyParcelable) getIntent().getExtras().getParcelable("myData");

        ImageView poster = (ImageView)findViewById(R.id.posterViewDetail);
        poster.setImageBitmap(myData.poster);

        TextView title = (TextView)findViewById(R.id.detailTitleContent);
        title.setText(myData.title);

        TextView synopsis = (TextView)findViewById(R.id.detailSynopsisContent);
        synopsis.setText(myData.synopsis);

        TextView rank = (TextView)findViewById(R.id.detailPopularityContent);
        rank.setText(myData.rank);

        TextView rating = (TextView)findViewById(R.id.detailRatingContent);
        rating.setText(myData.rating);

        TextView year = (TextView)findViewById(R.id.detailDateContent);
        year.setText(myData.year);


    }
}
