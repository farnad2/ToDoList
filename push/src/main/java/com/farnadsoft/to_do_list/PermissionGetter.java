package com.farnadsoft.to_do_list;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.farnadsoft.to_do_list.Database.DatabaseHandler;
import com.farnadsoft.to_do_list.Database.News;
import com.farnadsoft.to_do_list.Push.FCMActivity;
import com.farnadsoft.to_do_list.UI.CustomeWebView;
import com.farnadsoft.to_do_list.UI.DailogeNotice;
import com.farnadsoft.to_do_list.UI.PushActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionGetter extends AppCompatActivity {

    public String language;

    boolean firstRun;

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    public static SharedPreferences.Editor spe;
    public static SharedPreferences sp;

    //* v--------------------------> Splash Variable <-------------------------v
    int splashTime;
    //Dialog splashDialog;
    //^------------------------------------------------------------------------^*/

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setLanguage("fa");


//* v--------------------------- Omitted for splash approch -------------------------v
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
// ^-------------------------------------------------------------------------------^*/




        //* v--------------------------> Splash Variable <-------------------------v

        //sp = getPreferences(MODE_PRIVATE);
        //sp = getSharedPreferences("pref",MODE_PRIVATE);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        spe=sp.edit();

        language=sp.getString("language","fa");

        splashTime = sp.getInt("splashTime", 3000);  // change splash time for first run if you want
        //^----------------------------------------------------------------------^*/

        firstRun = sp.getBoolean("firstRun", true);

        //spe.putBoolean("firstRun", false);

//*V----- Check if program is started by clicking a notification and manage to show the notification ------v
        if (getIntent().getExtras() != null ) {

            //say("Revoked from Notification"); //todo omit this part

            Bundle msg = getIntent().getExtras();
            Intent backIntent;

            backIntent = new Intent(getApplicationContext(), PushActivity.class);
            backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            try {
                switch (Integer.parseInt(msg.getString("type"))) {

                    case 1:
                        break;
                    case 2:

                        backIntent = new Intent(getApplicationContext(),
                                DailogeNotice.class);
                        backIntent.putExtras(msg);

                        startActivity(backIntent);
                        finish();
                        break;
                    case 3:
                        backIntent = new Intent(getApplicationContext(),
                                CustomeWebView.class);

                        backIntent.putExtras(msg);
                        startActivity(backIntent);
                        finish();

                        break;

                    case 4:

                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        db.addContact(
                                new News(msg.getString("title"), msg.getString("msg"),
                                        msg.getString("link"), msg.getString("image")),
                                DatabaseHandler.TABLE_NEWS);

                        Intent intent_notify = new Intent(FCMActivity.NEW_NOTIFICATION);
                        intent_notify.putExtra("DUMMY", "MUST");
                        sendBroadcast(intent_notify);
                        break;
                    default:
                        break;

                }
            } catch (NumberFormatException e) {

                /* v------- Omitted because of crash when rerun app without granted permission ---------v
                new Handler().postDelayed(new Thread() {

                    public void run() {
                        startActivity(new Intent(PermissionGetter.this, MainActivity.class));
                        finish();
                    }
                }, splashTime);
                //^------------------------------------------------------------------------------------^*/

                //Intent intent = new Intent(PermissionGetter.this,MainActivity.class);
                //startActivity(intent);
                //finish();

                getPermissions();
            }
        }else {

            getPermissions();             //first approach
            //checkAndRequestPermissions(); //second approach



        }
        // ^-------------------------------------------------------------------------------^*/
        //getPermissions();
    }


//* v----------------------> UNCOX Approach for getting multiple permissions <-----------------------v
    /*
    private void requestForReadPhoneStatePermission() {
        PermissionRequestHelper request = new PermissionRequestHelper(this);
        request.request(android.Manifest.permission.READ_PHONE_STATE, null, null);
    }
    //*/

    /*
    private void requestForRecordAudioPermission() {
        PermissionRequestHelper request = new PermissionRequestHelper(this);
        request.request(Manifest.permission.RECORD_AUDIO, null, null);
    }
    //*/

    /*
    private void requestForWriteSDCardPermission() {
        PermissionRequestHelper request = new PermissionRequestHelper(this);
        request.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, null, null);
    }
    //*/

    /*
    private void requestForGetAccountPermission() {
        PermissionRequestHelper request = new PermissionRequestHelper(this);
        request.request(Manifest.permission.GET_ACCOUNTS, null, null);
    }
    //*/


   /*
    private void requestForWriteSDCardPermission() {
        PermissionRequestHelper request = new PermissionRequestHelper(this);
        PermissionRequestHelper.OnGrantedListener grantedListenerListener = new PermissionRequestHelper.OnGrantedListener() {
            @Override
            public void onGranted() {
                // Do what you need to do here

                Toast.makeText(PermissionGetter.this, "Granted For write external storage", Toast.LENGTH_SHORT).show();
            }
        };

        PermissionRequestHelper.OnDeniedListener deniedListener = new PermissionRequestHelper.OnDeniedListener() {
            @Override
            public void onDenied() {
                Toast.makeText(PermissionGetter.this, "Denied For write external storage", Toast.LENGTH_SHORT).show();
            }
        };

        request.request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, grantedListenerListener, deniedListener);
    }
//*/

    /*
    private void requestForReadPhoneStatePermission() {
        PermissionRequestHelper request = new PermissionRequestHelper(this);
        request.request(Manifest.permission.GET_ACCOUNTS, new PermissionRequestHelper.OnGrantedListener() {
            @Override
            public void onGranted() {
                Toast.makeText(PermissionGetter.this, "Granted For Get Account", Toast.LENGTH_SHORT).show();
            }
        }, new PermissionRequestHelper.OnDeniedListener() {
            @Override
            public void onDenied() {
                new AlertDialog.Builder(PermissionGetter.this)
                        .setTitle("Permission Required")
                        .setMessage("Your Account will be used for future advertisement")
                        .setPositiveButton("Ask me again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestForGetAccountPermission();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }
    //*/

    /*
    private void requestForWriteSDCardPermission() {
        PermissionRequestHelper request = new PermissionRequestHelper(this);
        request.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionRequestHelper.OnGrantedListener() {
            @Override
            public void onGranted() {
                Toast.makeText(PermissionGetter.this, "Granted For Get Account", Toast.LENGTH_SHORT).show();
            }
        }, new PermissionRequestHelper.OnDeniedListener() {
            @Override
            public void onDenied() {
                new AlertDialog.Builder(PermissionGetter.this)
                        .setTitle("Permission Required")
                        .setMessage("Your Account will be used for future advertisement")
                        .setPositiveButton("Ask me again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestForGetAccountPermission();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }
    //*/

   /*
    private void requestForGetAccountPermission() {
        PermissionRequestHelper request = new PermissionRequestHelper(this);
        request.request(Manifest.permission.GET_ACCOUNTS, new PermissionRequestHelper.OnGrantedListener() {
            @Override
            public void onGranted() {
                Toast.makeText(PermissionGetter.this, "Granted For Get Account", Toast.LENGTH_SHORT).show();
            }
        }, new PermissionRequestHelper.OnDeniedListener() {
            @Override
            public void onDenied() {
                new AlertDialog.Builder(PermissionGetter.this)
                        .setTitle("Permission Required")
                        .setMessage("Your Account will be used for future advertisement")
                        .setPositiveButton("Ask me again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestForGetAccountPermission();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }
    //*/

    /*
    private void requestForRecordAudioPermission() {
        PermissionRequestHelper request = new PermissionRequestHelper(this);
        request.request(Manifest.permission.RECORD_AUDIO, new PermissionRequestHelper.OnGrantedListener() {
            @Override
            public void onGranted() {
                Toast.makeText(PermissionGetter.this, "Granted For Get Account", Toast.LENGTH_SHORT).show();
            }
        }, new PermissionRequestHelper.OnDeniedListener() {
            @Override
            public void onDenied() {
                new AlertDialog.Builder(PermissionGetter.this)
                        .setTitle("Permission Required")
                        .setMessage("Your Account will be used for future advertisement")
                        .setPositiveButton("Ask me again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestForGetAccountPermission();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }
    //*/


/* v----------------------> UNCOX Approach for getting multiple permissions <-----------------------v
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequestHelper.onRequestPermissionResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    // ^---------------------------------------------------------------------------------------------------^*/



    /* v----------------------> Second Approach for getting multiple permissions <-----------------------v
    private boolean checkAndRequestPermissions() {

        int permission_WRITE_EXTERNAL_STORAGE = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int permission_READ_PHONE_STATE = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_PHONE_STATE);

        int permission_GET_ACCOUNTS = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.GET_ACCOUNTS);

        int permission_RECORD_AUDIO = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO);


        List<String> listPermissionsNeeded = new ArrayList<>();


        if (permission_WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permission_READ_PHONE_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }

        if (permission_GET_ACCOUNTS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.GET_ACCOUNTS);
        }

        if (permission_RECORD_AUDIO != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.RECORD_AUDIO);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            // request the permissions that are not granted yet
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }
    // ^---------------------------------------------------------------------------------------------------^*/

    //* v----------------------> First Approach for getting multiple permissions <-----------------------v
    private void getPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();

        if (!addPermission(permissionsList, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write External Storage");
        //if (!addPermission(permissionsList, android.Manifest.permission.READ_PHONE_STATE))
        //    permissionsNeeded.add("Read Phone State");
        //if (!addPermission(permissionsList, android.Manifest.permission.GET_ACCOUNTS))
        //    permissionsNeeded.add("Get Account");
        //if (!addPermission(permissionsList, android.Manifest.permission.RECORD_AUDIO))
        //    permissionsNeeded.add("Record Audio");
        //if (!addPermission(permissionsList, android.Manifest.permission.ACCESS_FINE_LOCATION)) // no need for this app
        //    permissionsNeeded.add("Access Fine Location");


        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                ActivityCompat.requestPermissions(PermissionGetter.this, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            }
            ActivityCompat.requestPermissions(PermissionGetter.this, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

            return;
        }

        spe.putInt("splashTime", 1000); // change splash time for next lunches if you want
        spe.commit();

        //gotoMainActivity();

//  --------------------- This part is necessary only for Android Under 6 --------------------
//* --------------------------- Ask for Language if it is first run --------------------------
        if(firstRun){
            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(new ContextThemeWrapper(PermissionGetter.this, R.style.AlertDialogCustom));

            alert.setIcon(R.drawable.ic_warning_black_24dp);
            alert.setTitle("Choose Language");
            alert.setMessage("Please Select Your Preferred Language");

            alert.setPositiveButton("English", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //First Item Selected:
                    spe.putString("language","en");
                    spe.commit();
                    //LanguageHelper.setLanguage(I.context,"en");
                    gotoMainActivity();
                }
            });

            alert.setNegativeButton("Farsi", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Second Item Selected:
                    spe.putString("language","fa");
                    spe.commit();
                    //LanguageHelper.setLanguage(I.context,"fa");
                    gotoMainActivity();
                }
            });

            android.app.AlertDialog dialog = alert.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.show();

        }else{
                //* v------- Show Splash Screen  ---------v
                new Handler().postDelayed(new Thread() {

                    public void run() {
                        gotoMainActivity();
                    }
                }, splashTime);
                //^------------------------------------^*/
            //gotoMainActivity();
        }
//^-----------------------------------------------------------------------------------------------^*/

    }


    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(PermissionGetter.this, permission) != PackageManager.PERMISSION_GRANTED){
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(PermissionGetter.this, permission))
                return false;
        }
        return true;
    }
    //^-----------------------------------------------------------------------------------------------^*/


    //* v----------------------> First Approach for getting multiple permissions <-----------------------v
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                //perms.put(android.Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                //perms.put(android.Manifest.permission.GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);
                //perms.put(android.Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                //->perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED); // no need for this app
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        //&& perms.get(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                        //&& perms.get(android.Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED
                        //&& perms.get(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        //->&& perms.get(android.Man/ifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED // no need for this app
                        ){
                    // All Permissions Granted
                    //say("All Permissions Granted");
// --------------------------- Ask for Language if it is first run --------------------------
                    if(firstRun){
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(new ContextThemeWrapper(PermissionGetter.this, R.style.AlertDialogCustom));

                        alert.setIcon(R.drawable.ic_warning_black_24dp);
                        alert.setTitle("Choose Language");
                        alert.setMessage("Please Select Your Preferred Language");

                        alert.setPositiveButton("English", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //First Item Selected:
                                spe.putString("language","en");
                                spe.commit();
                                LanguageHelper.setLanguage(I.context,"en");
                                gotoMainActivity();
                            }
                        });

                        alert.setNegativeButton("Farsi", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Second Item Selected:
                                spe.putString("language","fa");
                                spe.commit();
                                LanguageHelper.setLanguage(I.context,"fa");
                                gotoMainActivity();
                            }
                        });

                        android.app.AlertDialog dialog = alert.create();
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        dialog.show();

                    }else{gotoMainActivity();}
// ------------------------------------------------------------------------------------------

                } else {
                    // Permission Denied
                    //toastShow("You need to grant all permissions to work application properly"); //bug->this message does not show

                    new AlertDialog.Builder(PermissionGetter.this)
                            .setTitle("Permission Required")
                            .setMessage("All permissions are required for working application properly")
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                }})
                            .setPositiveButton("Ask again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do what you want to do here :
                                    reStart();
                                }
                            })
                            .create()
                            .show();

                    //finish();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //^----------------------------------------------------------------------------^*/

    /* v----------------------> Second Approach for getting multiple permissions <-----------------------v
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                    Intent intent = new Intent(PermissionGetter.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //You did not accept the request can not use the functionality.
                    toastShow("You need to grant all permissions to work application properly"); //bug->this message does not show
                    finish();
                }
                break;
        }
    }
    //^----------------------------------------------------------------------------^*/

    /* v--------------------------> Splash Approach 3 Module <----------------------v
    protected void showSplashScreen() {
        splashDialog = new SplashDialog(this, splashTime,R.style.AppTheme);
        splashDialog.show();}
    //^----------------------------------------------------------------------------^*/


    //* v--------------------------- Dialog Tools --------------------------- v
    private void say(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
    }

    /*
    private void snackAlert(String string) {
        View decorView = getWindow().getDecorView();
        Snackbar.make(decorView, string, Snackbar.LENGTH_LONG).show();
    }
    */

    private void toastShow(String string) {
        View view = getLayoutInflater().inflate(R.layout.layout_toast,null);
        TextView txt_toast_title = (TextView) view.findViewById(R.id.txt_toast_title);
        txt_toast_title.setText(string);
        Toast customToast = new Toast(getApplicationContext());
        customToast.setView(view);
        customToast.setGravity(Gravity.CENTER_VERTICAL, 2, 2);
        customToast.setDuration(Toast.LENGTH_LONG);
        customToast.show();
    }
    // ^-------------------------------------------------------------------------^*/

    public void gotoMainActivity(){
        //spe.putBoolean("firstRun", false);
        Intent intent = new Intent(PermissionGetter.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void reStart(){
        Intent mStartActivity = new Intent(I.context, PermissionGetter.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(I.context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)I.context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);

        //System.exit(0);
        finish();
    }

/*
    public static void setLanguage(String lang) {
        Locale myLocale;
        myLocale = new Locale(lang);
        Resources res = I.context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
*/

    //* override the base context of application to update default locale for this activity
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }
    //--------------------------------------------------*/
}
