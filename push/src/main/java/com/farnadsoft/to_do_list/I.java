package com.farnadsoft.to_do_list;

import android.app.Application;
import android.content.Context;

import android.view.LayoutInflater;

/*---------------- omitted for adding permissions for Android 6 ------------------
import android.database.sqlite.SQLiteDatabase;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
//-------------------------------------------------------------------------------*/

public class I extends Application {

    //public static boolean vocab504 = false; // آیا کاربر برنامه رو فعالسازی کرده یا نه؟
    //public static boolean addVocab  = false; // آیا کاربر برنامه رو فعالسازی کرده یا نه؟
/*
    public static final String DIR_SDCARD           = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DIR_APP              = DIR_SDCARD + "/EssentialWords";
    public static final String DIR_DATABASE_504              = DIR_SDCARD + "/ ";
    public static final String DIR_DATABASE_PERSONAL_VOCAB         = DIR_APP + "/Database";
    public static final String DIR_PICTURES         = DIR_APP + "/Pictures/";
    public static final String DIR_SOUNDS         = DIR_APP + "/Sounds/";
*/
/*---------------- omitted for adding permissions for Android 6 ------------------
    public DataBaseConnection504 dbConnection504;
    public DataBaseConnection dbConnectionPersonalVocab;
    public static SQLiteDatabase db504;
    public static SQLiteDatabase db;
//-------------------------------------------------------------------------------*/

    public static LayoutInflater layoutInflater;
    public static Context        context;


    @Override
    public void onCreate() {
        super.onCreate();

/*v--------- moved to MainActivity for execution after getting permissions -----------v
        new File(DIR_DATABASE_504).mkdirs();
        new File(DIR_APP).mkdirs();
        new File(DIR_DATABASE_PERSONAL_VOCAB).mkdirs();
        new File(DIR_PICTURES).mkdirs();
        new File(DIR_SOUNDS).mkdirs();
//^------------------------------------------------------------------------------------^*/

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = getApplicationContext();

/*---------------- omitted for adding permissions for Android 6 ------------------
        dbConnection504 = new DataBaseConnection504(context);
        dbConnectionPersonalVocab = new DataBaseConnection(context);
        db504=dbConnection504.getWritableDatabase();
        db= dbConnectionPersonalVocab.getWritableDatabase();
//-------------------------------------------------------------------------------*/
    }

/*---------------- omitted for adding permissions for Android 6 ------------------
    public class DataBaseConnection504 extends SQLiteAssetHelper {

        private static final String DATABASE_NAME_504    = "v.db";
        private static final int    DATABASE_504_VERSION = 1;

        public DataBaseConnection504(Context context) {
            super(context, DATABASE_NAME_504, DIR_DATABASE_504, null, DATABASE_504_VERSION);
        }
    }

    public class DataBaseConnection extends SQLiteAssetHelper {

        private static final String DATABASE_NAME_PERSONAL_VOCAB    = "pv.db";
        private static final int    DATABASE_PERSONAL_VOCAB_VERSION = 1;

        public DataBaseConnection(Context context) {
            super(context, DATABASE_NAME_PERSONAL_VOCAB, DIR_DATABASE_PERSONAL_VOCAB, null, DATABASE_PERSONAL_VOCAB_VERSION);
        }
    }
//-------------------------------------------------------------------------------*/
}
