package com.farnadsoft.to_do_list;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.chiralcode.colorpicker.ColorPicker;
import com.farnadsoft.to_do_list.Database.DatabaseHandler;
import com.farnadsoft.to_do_list.Database.ListViewSwipeGesture;
import com.farnadsoft.to_do_list.Database.News;
import com.farnadsoft.to_do_list.Push.FCMActivity;
import com.farnadsoft.to_do_list.UI.CustomeWebView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.farnadsoft.to_do_list.R.id.list_push;

//* -------- Moved here from class I for Android 6 permission management approach -------
// ------------------------------------------------------------------------------------*/


/* v--------------------------------> History of Changes <------------------------------v
2017-09-17:-Change location of some notification modules from Bazar modules to outside of it (for regularity of the codes)
           -Omitting location permission request from FCMActivity
2017-09-16:-Designing Multiple permissions & adding lunching activity (PermissionGetter Activity)
            -Extending ActivityAddVocab from AppCompatActivity caused crash when recalling onCreate(null) in the body that is fixed
            -moving some part of push codes to lunch activity (PermissionGetter)
            -location ability and its permission omitted from push modules
 Adding notification ability
 Final Changes: using of 2 database
// v----------------------------------------------------------------------------------v*/

/* v--------------------------------------> Notes <--------------------------------------v
در صورتی که از برنامه خارج شده باشیم و نوتیفیکیشن تصویری بفرستیم برای اینکه به درستی نمایش داده شود میبایست اکتیویتی اصلی از FCMActivity اکستند شده باشد.-
//? THere is a question that why AppCompatActivity works instead of FCMActivity
دقت شود هنگام تغییر نام پکیج، کد زیر نیز میبایست آپدیت گردد
File mFile = new File("/data/data/com.farnadsoft.essential_words_with_images_unlimited_gp/files/" + tempFile);
// v----------------------------------------------------------------------------------v*/

//public class MainActivity extends AppCompatActivity { //	در صورتی که از برنامه خارج شده باشیم و نوتیفیکیشن تصویری بفرستیم برای اینکه به درستی نمایش داده شود میبایست اکتیویتی اصلی از FCMActivity اکستند شده باشد.
public class MainActivity extends FCMActivity {

    //* v--------------------------- Speak to Text Variables ----------------------v
    private static final int REQUEST_CODE = 1234;
    ImageView iv_talk;
    String language;
    ArrayList<String> matches_text;
    // ^-------------------------------------------------------------------------^*/

    //* v--------------------------- To Do List Variables ----------------------v
    EditText et_item;
    EditText et_item2;

    boolean isView1=true;
    ViewFlipper viewFlipper;
    Button btn_change_task;
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    static ArrayList<String> listItems1 = new ArrayList<String>();
    static ArrayList<String> listItems2 = new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    static ArrayAdapter<String> adapter1;
    static ArrayAdapter<String> adapter2;
    // ^-----------------------------------------------------------------------^ */

    //* v------------- Notification Variables ------------v
    private DisplayImageOptions options;

    DatabaseHandler push_db;

    List<News> contacts;
    MainActivity.SampleAdapter adapter;
    ListView list;
    LayoutInflater inflater;
    // ^-------------------------------------------------^ */

    private ColorPicker colorPicker;

    ImageView iv_menu, iv_logo;
    RelativeLayout rl_main;
    Button  btn_background;
    Button btn_background_wood, btn_background_metal, btn_background_wood0, btn_background_blue, btn_background_green,btn_background_personal;

    private AlertDialog menuRightAlertDialog, menuBackgroundAlertDialog;

    public final short METAL_BACKGROUND = 1, WOOD_BACKGROUND = 2, WOOD0_BACKGROUND = 3, BLUE_BACKGROUND = 4, GREEN_BACKGROUND = 5,PERSONAL_BACKGROUND=6;

    short background,background2;
    public int personalBackgroundColor,personalBackgroundColor2;

    public static SharedPreferences.Editor spe;
    public static SharedPreferences sp;

    //boolean firstRun,language_english, language_farsi,vocabIsPlaying=false;
    boolean firstRun;
    boolean language_english, language_farsi;
    //boolean firstRun;

    //TextToSpeech tts;

    SlidingMenu menu;

    Context context = this;
    CheckBox chb_english, chb_farsi;

    long back_pressed ;

    /* v--------------------------> Splash Variables <-------------------------v
    //int splashTime;
     Dialog splashDialog;
    //^------------------------------------------------------------------------^*/

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setLanguage("fa");
        //* v--------------------> prepare shared preferences variables <-------------------v
        //spe = getPreferences(MODE_PRIVATE).edit();
        //sp = getPreferences(MODE_PRIVATE);
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        spe=sp.edit();

        language_english = sp.getBoolean("language_english", false);
        language_farsi = sp.getBoolean("language_farsi", true);
        language=sp.getString("language","fa"); //todo for english version change to "en"

// ^-------------------------------------------------------------------------------^*/

        //if (language_english) setLanguage("en");else if(language_farsi)setLanguage("fa");
        LanguageHelper.setLanguage(I.context,language);

/* v--------------------------- adjusting how the layout must be shown -------------------------v
        if (Build.VERSION.SDK_INT > 20) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
// ^-------------------------------------------------------------------------------------------^*/

        setContentView(R.layout.activity_main);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
// ^-------------------------------------------------------------------------------------------^*/

        final Bundle extras = getIntent().getExtras(); //check if MainActivity is called by user, if so show the splash screen

//* v----------------> Specify category of app for notification <-------------------v
        // todo change Topic to To_Do_List_Bazar_ before issue
        FirebaseMessaging.getInstance().subscribeToTopic("To_Do_List_GP");
        //FirebaseMessaging.getInstance().subscribeToTopic("FCM");say("FCM Test Mode");
// ^-------------------------------------------------------------------------------^*/

//* v----------------------- Notification Modules (Came from UI.PushActivity) ---------------------v
        list = (ListView) findViewById(list_push);
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(options).build();
        ImageLoader.getInstance().init(config);
        push_db = new DatabaseHandler(this);
        // ---------------------------------
        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adapter = new MainActivity.SampleAdapter(this);
        push_db = new DatabaseHandler(this);
        contacts = push_db.getAllContacts(DatabaseHandler.TABLE_NEWS);

        if (contacts.size() != 0) {
            ListViewSwipeGesture touchListener = new ListViewSwipeGesture(list,
                    swipeListener, this);
            touchListener.SwipeType = ListViewSwipeGesture.Dismiss;
            touchListener.HalfDrawable = getResources().getDrawable(R.drawable.ic_delete);
            list.setOnTouchListener(touchListener);
        } else {
            adapter.add(new News(
                    -1,
                    "No Notifications",
                    "-",
                    "http://dummyimage.com/qvga/CCC/000.png&text=No+Notifications",
                    "-1", "-1"));
        }

        //-->list.setAdapter(adapter);   //<-- caused null pointer exception error

// ^--------------------------------------------------------------------------------^*/


        firstRun = sp.getBoolean("firstRun", true);

        background = (short)sp.getInt("background", METAL_BACKGROUND);
        background2 = (short)sp.getInt("background2", WOOD_BACKGROUND);

        if (background==6){personalBackgroundColor=sp.getInt("personalBackgroundColor", 0);}
        if (background2==6){personalBackgroundColor2=sp.getInt("personalBackgroundColor2", 0);}

//---------------------- Sliding Menu ---------------------
        menu = new SlidingMenu(this);

//        menu.setMode(SlidingMenu.SLIDING_WINDOW);
        //menu.setMode(SlidingMenu.LEFT_RIGHT);
        menu.setMode(SlidingMenu.RIGHT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setBehindOffset(100);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

        //View view = I.layoutInflater.inflate(R.layout.menu_left, null);
        //menu.setMenu(view);
        menu.setSecondaryMenu(R.layout.menu_right);

        if (firstRun) {
            menu.showSecondaryMenu();
            spe.putBoolean("firstRun", false);
            spe.commit();
        }


        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        iv_menu = (ImageView) findViewById(R.id.iv_menu);

        rl_main = (RelativeLayout) findViewById(R.id.rl_main);

        iv_logo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                menu.showSecondaryMenu();
            }
        });


//---------------- Right Menu -----------------
        iv_menu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                menuRightAlertDialog.show();

                btn_background.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        menuBackgroundAlertDialog.show();
                        if(personalBackgroundColor!=0) btn_background_personal.setBackgroundColor(personalBackgroundColor);

                        btn_background_metal.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {

                                //if(isView1)background=METAL_BACKGROUND;else background2=METAL_BACKGROUND;
                                setBackground(METAL_BACKGROUND);
                                //spe.putInt("background", METAL_BACKGROUND);

                                if (isView1) {spe.putInt("background", METAL_BACKGROUND);background=METAL_BACKGROUND;}
                                else {spe.putInt("background2", METAL_BACKGROUND);background2=METAL_BACKGROUND;}

                                spe.commit();
                                menuRightAlertDialog.dismiss();
                                menuBackgroundAlertDialog.dismiss();
                            }
                        });

                        btn_background_wood.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //if(isView1)background=WOOD_BACKGROUND;else background2=WOOD_BACKGROUND;
                                setBackground(WOOD_BACKGROUND);
                                if (isView1) {spe.putInt("background", WOOD_BACKGROUND);background=WOOD_BACKGROUND;}
                                else {spe.putInt("background2", WOOD_BACKGROUND);background2=WOOD_BACKGROUND;}
                                spe.commit();
                                menuRightAlertDialog.dismiss();
                                menuBackgroundAlertDialog.dismiss();
                            }
                        });


                        btn_background_wood0.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                setBackground(WOOD0_BACKGROUND);
                                if (isView1) {spe.putInt("background", WOOD0_BACKGROUND);background=WOOD0_BACKGROUND;}
                                else {spe.putInt("background2", WOOD0_BACKGROUND);background2=WOOD0_BACKGROUND;}
                                spe.commit();
                                menuRightAlertDialog.dismiss();
                                menuBackgroundAlertDialog.dismiss();
                            }
                        });

                        btn_background_green.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //if(isView1)background=GREEN_BACKGROUND;else background2=GREEN_BACKGROUND;
                                setBackground(GREEN_BACKGROUND);
                                if (isView1) {spe.putInt("background", GREEN_BACKGROUND);background=GREEN_BACKGROUND;}
                                else {spe.putInt("background2", GREEN_BACKGROUND);background2=GREEN_BACKGROUND;}
                                spe.putInt("background", GREEN_BACKGROUND);
                                spe.commit();
                                menuRightAlertDialog.dismiss();
                                menuBackgroundAlertDialog.dismiss();
                            }
                        });

                        btn_background_blue.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //if(isView1)background=BLUE_BACKGROUND;else background2=BLUE_BACKGROUND;
                                setBackground(BLUE_BACKGROUND);
                                if (isView1) {spe.putInt("background", BLUE_BACKGROUND);background=BLUE_BACKGROUND;}
                                else {spe.putInt("background2", BLUE_BACKGROUND);background2=BLUE_BACKGROUND;}
                                spe.putInt("background", BLUE_BACKGROUND);
                                spe.commit();
                                menuRightAlertDialog.dismiss();
                                menuBackgroundAlertDialog.dismiss();
                            }
                        });

                        btn_background_personal.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {

                                //if(isView1)background=PERSONAL_BACKGROUND;else background2=PERSONAL_BACKGROUND;

                                if (isView1) {
                                    personalBackgroundColor = colorPicker.getColor();
                                    btn_background_personal.setBackgroundColor(personalBackgroundColor);
                                    spe.putInt("background", PERSONAL_BACKGROUND);
                                    spe.putInt("personalBackgroundColor", personalBackgroundColor);
                                    background=PERSONAL_BACKGROUND;
                                }
                                else {
                                    personalBackgroundColor2 = colorPicker.getColor();
                                    btn_background_personal.setBackgroundColor(personalBackgroundColor2);
                                    spe.putInt("background2", PERSONAL_BACKGROUND);
                                    spe.putInt("personalBackgroundColor2", personalBackgroundColor2);
                                    background2=PERSONAL_BACKGROUND;
                                }

                                setBackground(PERSONAL_BACKGROUND);

                                //spe.putInt("background", PERSONAL_BACKGROUND);
                                //spe.putInt("personalBackgroundColor", personalBackgroundColor);
                                spe.commit();
                                menuRightAlertDialog.dismiss();
                                menuBackgroundAlertDialog.dismiss();
                            }
                        });


                    }
                });

                chb_english.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        language_english = b;
                        language_farsi = !b;
                        chb_farsi.setChecked(!b);
                        spe.putBoolean("language_english", b);
                        spe.putBoolean("language_farsi", !b);
                        if(language_farsi)spe.putString("language","fa");else spe.putString("language","en");
                        spe.commit();

                        if (language_english) {
                            //searchVocabs=" AND personal is null";
                            //setForegroundTextTo504();
                            //setLanguage("en");
                            LanguageHelper.setLanguage(I.context,"en");
                            recreate();

                        } else if (language_farsi) {
                            //searchVocabs=" AND personal not null";
                            //setForegroundTextToPersonal();
                            //setLanguage("fa");
                            LanguageHelper.setLanguage(I.context,"fa");
                            recreate();

                        }
                    }
                });

                chb_farsi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        language_farsi = b;
                        language_english = !b;
                        chb_english.setChecked(!b);
                    }
                });

            }
        });
//----------------------------------------------

        if (isView1) setBackground(background); else setBackground(background2);

        LayoutInflater inflateReviewDateList = MainActivity.this.getLayoutInflater();

        View menu_right = inflateReviewDateList.inflate(R.layout.menu_option, null);
        View menu_background = inflateReviewDateList.inflate(R.layout.menu_background, null);
        btn_background_metal = (Button) menu_background.findViewById(R.id.btn_background_metal);
        btn_background_wood = (Button) menu_background.findViewById(R.id.btn_background_wood);
        btn_background_wood0 = (Button) menu_background.findViewById(R.id.btn_background_wood0);
        btn_background_green = (Button) menu_background.findViewById(R.id.btn_background_green);
        btn_background_blue = (Button) menu_background.findViewById(R.id.btn_background_blue);
        btn_background_personal = (Button) menu_background.findViewById(R.id.btn_background_personal);
        colorPicker = (ColorPicker) menu_background.findViewById(R.id.colorPicker);

        btn_background = (Button) menu_right.findViewById(R.id.btn_background);

        chb_english = (CheckBox) menu_right.findViewById(R.id.chb_english);
        chb_farsi = (CheckBox) menu_right.findViewById(R.id.chb_farsi);
        chb_english.setChecked(language_english);
        chb_farsi.setChecked(language_farsi);

        AlertDialog.Builder menuRightAlertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        menuRightAlertDialogBuilder.setView(menu_right);
        menuRightAlertDialog = menuRightAlertDialogBuilder.create();

        AlertDialog.Builder menuBackgroundAlertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        menuBackgroundAlertDialogBuilder.setView(menu_background);
        menuBackgroundAlertDialogBuilder.setMessage(getResources().getString(R.string.choose_background_color_title));
        menuBackgroundAlertDialog = menuBackgroundAlertDialogBuilder.create();

        viewFlipper=(ViewFlipper)findViewById(R.id.view_flipper);

        btn_change_task=(Button)findViewById(R.id.btn_change_task);

        btn_change_task.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                isView1=!isView1;
                viewFlipper.showNext();
                if(isView1){btn_change_task.setText(getResources().getString(R.string.to_do_list));setBackground(background);}
                else{
                    btn_change_task.setText(getResources().getString(R.string.shopping_basket));
                    setBackground(background2);
                }

            }
        });

        if(readList1(this)!=null){
            listItems1=readList1(this);
        }

        if(readList2(this)!=null){
            listItems2=readList2(this);
        }

        final ListView lstView1 = (ListView) findViewById(R.id.list1);
        final ListView lstView2 = (ListView) findViewById(R.id.list2);
        lstView1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lstView1.setTextFilterEnabled(true);
        lstView2.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lstView2.setTextFilterEnabled(true);

        adapter1=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked,listItems1);
        adapter2=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked,listItems2);
        lstView1.setAdapter(adapter1);
        lstView2.setAdapter(adapter2);

        et_item=(EditText) findViewById(R.id.et_item);
        ImageButton ib_add=(ImageButton) findViewById(R.id.ib_add);
        et_item2=(EditText) findViewById(R.id.et_item2);
        ImageButton ib_add2=(ImageButton) findViewById(R.id.ib_add2);

        ImageView iv_delete2=(ImageView) findViewById(R.id.iv_delete2);

        iv_delete2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int listSize;
                if (isView1) {
                    listSize = lstView1.getCount();
                    for (int i = listSize; i > -1; i--) {
                        if (lstView1.isItemChecked(i)) {
                            lstView1.setItemChecked(i, false);
                            listItems1.remove(i);
                        }
                    }
                    lstView1.invalidateViews();
                    writeList1(getBaseContext(), listItems1);
                }else{
                        listSize=lstView2.getCount();
                        for (int i=listSize; i>-1; i--) {
                            if (lstView2.isItemChecked(i)) {
                                lstView2.setItemChecked(i, false);
                                listItems2.remove(i);
                            }
                        }
                        lstView2.invalidateViews();
                        writeList2(getBaseContext(),listItems2);
                }
            }
        });

        ib_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!et_item.getText().toString().matches("")){
                    listItems1.add(et_item.getText().toString());
                    adapter1.notifyDataSetChanged();
                    et_item.setText("");
                    writeList1(getBaseContext(),listItems1);
                }
            }
        });

        ib_add2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!et_item2.getText().toString().matches("")){
                    listItems2.add(et_item2.getText().toString());
                    adapter2.notifyDataSetChanged();
                    et_item2.setText("");
                    writeList2(getBaseContext(),listItems2);
                }
            }
        });



//* v-------------------------------- Speak to Text Codes ----------------------------v
//معرفی ابزار ها
        iv_talk = (ImageView) findViewById(R.id.iv_talk);
        //->Speech = (TextView) findViewById(R.id.speech);

//ایجاد یک لیسنر برای دکمه استارت
        iv_talk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
//استفاده از اینتنت برای فراخوانی و بررسی دسترسی گوشی به اینترنت
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    //        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_LONG).show();
                }
            }
        });
// ^-----------------------------------------------------------------------------------^*/

    }

//* v------------------------------> Notification Module <-----------------------------v
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            contacts.clear();
            contacts = push_db.getAllContacts(DatabaseHandler.TABLE_NEWS);
            adapter.notifyDataSetChanged();


        }
    };
//^----------------------------------------------------------------------------------^*/


    //* v-------------------------------- Speak to Text Modules ----------------------------v
    public boolean isConnected() {
//استفاده از کانکت منیجر برای مطمن شدن از دسترسی و عدم دسترسی به اینترنت
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net != null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
//برای استفاده از کانکت منیجر باید دسترسی اون رو در منی فست فعال کنیم.
    }

    //استفاده از اکتیوتی رسولت برای نتیجه گیری از دریافت اطلاعات از google voice و  نمایش ان بروی لیست ویو
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            matches_text = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (isView1) {
                et_item.setText(matches_text.get(0));
            }else {
                et_item2.setText(matches_text.get(0));
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
// ^--------------------------------------------------------------------------------------^*/



//* v-------------------------------- To Do List Modules ----------------------------v
    public static ArrayList<String> readList1(Context c){
        try{
            FileInputStream fis = c.openFileInput("todolist");
            ObjectInputStream is = new ObjectInputStream(fis);
            ArrayList<String> list = (ArrayList<String>) is.readObject();
            is.close();
            return list;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> readList2(Context c){
        try{
            FileInputStream fis = c.openFileInput("shoppinglist");
            ObjectInputStream is = new ObjectInputStream(fis);
            ArrayList<String> list = (ArrayList<String>) is.readObject();
            is.close();
            return list;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static void writeList1(Context c, ArrayList<String> list){
        try{
            FileOutputStream fos = c.openFileOutput("todolist", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(list);
            os.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void writeList2(Context c, ArrayList<String> list){
        try{
            FileOutputStream fos = c.openFileOutput("shoppinglist", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(list);
            os.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    public void onListItemClick(
            ListView parent, View v, int position, long id)
    {
//        Toast.makeText(this,"You have selected " + items[position],Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //---save whatever you need to persist---
        if (!isView1){outState.putString("viewState", "view2");}else{outState.putString("viewState", "view1");}
        super.onSaveInstanceState(outState);
    }

    @ Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //---retrieve the information persisted earlier---
        String viewState = savedInstanceState.getString("viewState");
        if (viewState.equals("view2")){
            viewFlipper.showNext();
            //btn_change_task.setText("Shopping Basket");
            btn_change_task.setText(getResources().getString(R.string.shopping_basket));
            isView1=false;
        }
        else{isView1=true;}
    }
//^--------------------------- To Do List Modules -------------------------^*/

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


//^========================================================================================^

    @Override
    public void onDestroy() {
        super.onDestroy();

//* v------------------------------> Notification Module <-----------------------------v
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
//^----------------------------------------------------------------------------------^*/


    }

    //* v------------------------------> Notification Module <-----------------------------v
    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(receiver, new IntentFilter(FCMActivity.NEW_NOTIFICATION));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //^----------------------------------------------------------------------------------^*/

    private void setBackground(short background) {
        switch (background) {
            case METAL_BACKGROUND:
                rl_main.setBackgroundResource(R.drawable.metal7);
                break;
            case WOOD_BACKGROUND:
                rl_main.setBackgroundResource(R.drawable.wood);
                break;
            case WOOD0_BACKGROUND:
                rl_main.setBackgroundResource(R.drawable.wood0);
                break;
            case BLUE_BACKGROUND:
                rl_main.setBackgroundColor(0xff77aadd);
                break;
            case GREEN_BACKGROUND:
                rl_main.setBackgroundColor(0xff99bb99);
                break;
            case PERSONAL_BACKGROUND:
                if (isView1) rl_main.setBackgroundColor(personalBackgroundColor); else rl_main.setBackgroundColor(personalBackgroundColor2);
                break;
        }
    }


//*v---------------------------- onBackPressed -----------------------------v
    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis()<back_pressed + 2000 ) {
            super.onBackPressed();
            System.exit(0);
        }
        else {
            //Toast.makeText(context, "برای خروج از برنامه یکبار دیگر کلید بازگشت را فشار دهید", Toast.LENGTH_LONG).show();
            toastShow("برای خروج از برنامه یکبار دیگر کلید بازگشت را فشار دهید");
            back_pressed = System.currentTimeMillis();
        }

/*        stopPlaying();
        super.onBackPressed();
*/
    }
//^-----------------------------------------------------------------------^*/

//* v------------------------------> Notification Modules <-----------------------------v
    ListViewSwipeGesture.TouchCallbacks swipeListener = new ListViewSwipeGesture.TouchCallbacks() {

        @Override
        public void FullSwipeListView(int position) {
            // Call back function for second action
        }

        @Override
        public void HalfSwipeListView(int position) {

        }

        @Override
        public void LoadDataForScroll(int count) {
            // call back function to load more data in listview (Continuous
            // scroll)

        }

        @Override
        public void onDismiss(ListView listView, int[] reverseSortedPositions) {

            try {
                for (int i : reverseSortedPositions) {
                    if (contacts.get(i).getID() != -1) {

                        push_db.deleteContact(contacts.get(i));
                        adapter.remove(contacts.get(i));
                        adapter.notifyDataSetChanged();
                        contacts.remove(i);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void OnClickListView(int position) {
            try {
                if (contacts.get(position).getID() != -1) {

                    Intent intent = new Intent(MainActivity.this,
                            CustomeWebView.class);
                    intent.putExtra("link", contacts.get(position).getLink());

                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };
    //--------------------------------------
    public class SampleAdapter extends ArrayAdapter<News> {

        public SampleAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public int getCount() {
            return contacts.size();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.cat_swipe_layout, parent,
                    false);
            if (convertView == null) {
                inflater.inflate(R.layout.cat_swipe_layout, parent, false);
            }
            TextView title = (TextView) convertView.findViewById(R.id.date); // title
            TextView branch = (TextView) convertView.findViewById(R.id.msg); // title
            ImageView v = (ImageView) convertView.findViewById(R.id.img);
            try {
                title.setText(Html.fromHtml((contacts.get(position).getName())));
                branch.setText(Html.fromHtml(contacts.get(position).getDate()));
                // Log.d("image", getItem(position).seen);

                if ((contacts.get(position).getImage().trim().length() <= 0)) {

                } else {
                    ImageLoader.getInstance().displayImage(
                            contacts.get(position).getImage(), v, options);
                }

                branch.setSelected(true);
                branch.requestFocus();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return convertView;

        }

    }
//^----------------------------------------------------------------------------------^*/

    /* v--------------------------> Splash Approach 3 Module <----------------------v
    protected void showSplashScreen(int splashTime) {
    splashDialog = new SplashDialog(this, splashTime,R.style.AppTheme);
    splashDialog.show();}
    //^----------------------------------------------------------------------------^*/


/* v-----> Old approach (Does not work for Android 8 & 9) <-------v
    public static void setLanguage(String lang) {
        Locale myLocale;
        myLocale = new Locale(lang);
        Resources res = I.context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
//^----------------------------------------------------------------^*/

    //* v-------> New approach (to work on Android 8 & 9 also) <---------v
    public static void setLanguage(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        I.context.getApplicationContext().getResources().updateConfiguration(config,
                I.context.getApplicationContext().getResources().getDisplayMetrics());
    }
    //^-----------------------------------------------------------------^*/


    //* override the base context of application to update default locale for this activity
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }
    //--------------------------------------------------*/

}