package com.farnadsoft.to_do_list.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.farnadsoft.to_do_list.Database.Categeries;
import com.farnadsoft.to_do_list.R;
import com.farnadsoft.to_do_list.Server.Server;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CategorySelectionActivity extends AppCompatActivity {

    ListView listView;

    CategoryAdapter adapter;
    ProgressDialog progressDialog;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    JSONObject savedCat;
    private DisplayImageOptions options;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);



        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(options).build();
        ImageLoader.getInstance().init(config);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        try {
            savedCat = new JSONObject(sp.getString("CAT", "{}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView = (ListView) findViewById(R.id.listView);
        RequestParams param = new RequestParams();
        param.put("email",
                PushActivity.getRegistrationEmail(this));
        adapter = new CategoryAdapter(this);
        listView.setAdapter(adapter);
        Server.get("/user/categories", param, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressDialog = new ProgressDialog(CategorySelectionActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

            }


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                JSONArray jsonArray= null;
                try {
                    jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Categeries categeries = new Categeries();
                        try {
                            categeries.setId(jsonArray.getJSONObject(i).getString("id"));
                            categeries.setName(jsonArray.getJSONObject(i).getString("cat_name"));
                            categeries.setSlug(jsonArray.getJSONObject(i).getString("cat_slug"));
                            categeries.setImage(jsonArray.getJSONObject(i).getString("image"));
                            adapter.add(categeries);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressDialog.cancel();
            }

        });

    }


    public void addCategory(String cat) {
        try {
            savedCat.put(cat, true);
            saveCat();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveCat() {
        editor.putString("CAT", savedCat.toString());
        editor.commit();
    }

    public void removeCat(String cat) {
        if (savedCat.has(cat)) {
            savedCat.remove(cat);
            saveCat();
        }
    }

    public class CategoryAdapter extends ArrayAdapter<Categeries> {

        private final LayoutInflater inflater;

        public CategoryAdapter(Context context) {
            super(context, 0);
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.category_item, parent, false);
            }
            final CheckBox chk = (CheckBox) convertView.findViewById(R.id.checkBox); // title
            final ImageView image= (ImageView) convertView.findViewById(R.id.image); // title
            if (savedCat.has(getItem(position).getSlug())) {
                chk.setChecked(true);
            } else {
                chk.setChecked(false);
            }
            chk.setText(getItem(position).getName());
            chk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chk.isChecked()) {
                        addCategory(getItem(position).getSlug());
                        // --> Set the category active
                        FirebaseMessaging.getInstance().subscribeToTopic(getItem(position).getSlug());
                    } else {
                        removeCat(getItem(position).getSlug());
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(getItem(position).getSlug());
                    }
                }
            });

            ImageLoader.getInstance().displayImage(getItem(position).getImage(),image);
            return convertView;

        }

    }

}
