package com.farnadsoft.to_do_list.UI;

import com.farnadsoft.to_do_list.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;

import android.preference.Preference.OnPreferenceClickListener;

public class PreferenceActivity extends android.preference.PreferenceActivity {
    Preference cat;
    ProgressDialog mProgressDialog;
    SharedPreferences sp;
    Editor edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        sp = getSharedPreferences("GCM", MODE_PRIVATE);
        edit = sp.edit();
        mProgressDialog = new ProgressDialog(PreferenceActivity.this);
        mProgressDialog.setMessage("Loading...");

        mProgressDialog.setCancelable(false);

        cat = (Preference) findPreference("cat");


        if (getResources().getString(R.string.use_cat).equalsIgnoreCase("true")) {
            cat.setEnabled(true);
            cat.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(PreferenceActivity.this, CategorySelectionActivity.class));
                    return false;
                }
            });
        } else {
            cat.setEnabled(false);
            cat.setSummary("Categories selection is disabled by Admin!");
        }
    }
}
