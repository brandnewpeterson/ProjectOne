package com.example.brandnewpeterson.projectone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MasterActivity extends AppCompatActivity {

    private JSONArray moviesArray;
    private GridView gridview;
    private String[] modes = {"Most Popular", "Highest Rated"};
    private ProgressBar gridViewProgress;
    private static int MODE = 0;
    private static int MAX_API_PAGES = 3;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("myJSON", moviesArray.toString());
        outState.putInt("myMODE", MODE);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            moviesArray = new JSONArray( savedInstanceState.getString("myJSON") );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MODE = savedInstanceState.getInt("myMODE");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (moviesArray == null){//Only query API if stored JSON Array not populated from previous session.
            APIFetcher mAPIHelper = new APIFetcher(MODE);
            mAPIHelper.execute();
        }

        gridview = (GridView) findViewById(R.id.gridview);
        gridViewProgress = (ProgressBar) findViewById(R.id.gridViewProgressBar);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Master Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.brandnewpeterson.projectone/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Master Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.brandnewpeterson.projectone/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<String> stringArrayAdapter=
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        modes);
        spinner.setAdapter(stringArrayAdapter); // set the adapter to provide layout of rows and content
        spinner.setSelection(MODE);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item is selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An spinnerItem was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)

                Toast.makeText(getApplicationContext(), "Fetching " + parent.getItemAtPosition(pos).toString() + " movies.", Toast.LENGTH_SHORT).show();
                APIFetcher mAPIHelper = new APIFetcher(pos);
                MODE = pos;
                mAPIHelper.execute();

            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, just another required interface callback
            }

        });
        return super.onCreateOptionsMenu(menu);

    }

    /**
     * Created by brandnewpeterson on 1/23/16.
     */
    public class APIFetcher extends AsyncTask<String, Void, String[]> {
        HttpURLConnection urlConnection;
        Integer MODE;
        String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
        String POPULARITY_FLAG = "sort_by=popularity.desc";
        String RATINGS_FLAG = "sort_by=vote_average.desc&vote_average.gte=1&&vote_count.gte=100";
        String urlString;
        String[] jsonStrings = new String[3];

        public APIFetcher(Integer mode) {
            MODE = mode;
        }

        //System.out.println("MODE is " + MODE);

        @Override
        protected String[] doInBackground(String... args) {

            for (int i = 0; i < MAX_API_PAGES; i++) {//Query multiple pages from API so as to fill screen. (Smarter implementation could re-query on scroll.)

                if (MODE == 0) {
                    urlString = BASE_URL + POPULARITY_FLAG + "&page=" + (i + 1) + "&api_key=" + getResources().getString(R.string.api_key);
                } else {
                    urlString = BASE_URL + RATINGS_FLAG + "&page=" + (i + 1) + "&api_key=" + getResources().getString(R.string.api_key);
                }

                //System.out.println("Input URL: " + urlString);

                StringBuilder result = new StringBuilder();

                try {
                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }

                jsonStrings[i] = result.toString();
            }

            return jsonStrings;
        }

        @Override
        protected void onPostExecute(String[] result) {
            //System.out.println("Results length: " + result.length);
            JSONObject moviesObj = null;
            try {
                moviesObj = new JSONObject(result[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                moviesArray = moviesObj.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 1; i < MAX_API_PAGES; i++) {//Concat page results into one big array.
                try {
                    moviesObj = new JSONObject(result[i]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray moviesArrayAdd = null;
                try {
                    moviesArrayAdd = moviesObj.getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < moviesArrayAdd.length(); j++) {
                    try {
                        moviesArray.put(moviesArrayAdd.getJSONObject(j));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //System.out.println("Movies: " + moviesArray);

                gridview.setAdapter(new ImageAdapter(getApplicationContext()));

                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        Bitmap bitmap = null;
                        ImageView iv = (ImageView) v.findViewById(R.id.posterViewMaster);
                        Drawable drawable = iv.getDrawable();
                        BitmapDrawable d = (BitmapDrawable) drawable;
                        bitmap = d.getBitmap();//Extract bitmap to send to detail activity.

                        Parcelable p = null;

                        try {
                            JSONObject movieObj = moviesArray.getJSONObject(position);
                            p = new MyParcelable(
                                    movieObj.getString("title"),
                                    movieObj.getString("overview"),
                                    movieObj.getString("popularity"),
                                    movieObj.getString("vote_average"),
                                    movieObj.getString("release_date"),
                                    bitmap
                                    );

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent i = new Intent(getBaseContext(), DetailActivity.class);
                        i.putExtra("myData", p); // using the (String name, Parcelable value) overload!
                        startActivity(i); // dataToSend is now passed to the new Activity

                        //Toast.makeText(MasterActivity.this, "" + position,
                          //      Toast.LENGTH_SHORT).show();
                    }
                });

                gridViewProgress.setVisibility(View.GONE);

            }

        }



        public class ImageAdapter extends BaseAdapter {
            String BASE_URL = "http://image.tmdb.org/t/p/w185";
            private Context mContext;
            LayoutInflater inflater;


            public ImageAdapter(Context c) {
                mContext = c;
                inflater = ( LayoutInflater )c.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            public int getCount() {
                return moviesArray.length();
            }

            public Object getItem(int position) {
                return null;
            }

            public long getItemId(int position) {
                return 0;
            }

            // create a new ImageView for each item referenced by the Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                final ViewHolder holder;

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.poster_and_text, null);
                    holder = new ViewHolder();

                    holder.poster = (ImageView) convertView.findViewById(R.id.posterViewMaster);
                    holder.title = (TextView) convertView.findViewById(R.id.titleTextView);
                    holder.rank = (TextView) convertView.findViewById(R.id.rankingTextView);
                    holder.progress = (ProgressBar) convertView.findViewById(R.id.progressBar);

                    convertView.setTag(holder);

                }
                else {
                    holder = (ViewHolder) convertView.getTag();


                }


                holder.poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
                try {
                    JSONObject movieObj = moviesArray.getJSONObject(position);
                    Picasso.with(mContext).
                            load(BASE_URL + movieObj.getString("poster_path")).
                            into(holder.poster, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                holder.progress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                // TODO Auto-generated method stub

                            }
                            });

                    holder.title.setText(movieObj.getString("title"));
                    if (MasterActivity.MODE == 0){
                        holder.rank.setText("Popularity: " + movieObj.getString("popularity"));
                    }else{
                        holder.rank.setText("Avg. Rating: " + movieObj.getString("vote_average"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return convertView;
            }



        }

    }

    static class ViewHolder {//Class for bundling complex view for gridview cells.
        private ImageView poster;
        private TextView title;
        private TextView rank;
        private ProgressBar progress;
    }

}
