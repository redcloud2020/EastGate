package com.uni.easygate.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.uni.easygate.R;
import com.uni.easygate.config.Parameters;
import com.uni.easygate.datalayer.models.Activity;
import com.uni.easygate.datalayer.models.Comment;
import com.uni.easygate.datalayer.models.Event;
import com.uni.easygate.datalayer.models.Exit;
import com.uni.easygate.datalayer.models.GpsLog;
import com.uni.easygate.datalayer.models.UserWrapperModel;
import com.uni.easygate.datalayer.server.MyHttpClient;
import com.uni.easygate.datalayer.server.RequestDataProvider;
import com.uni.easygate.datalayer.server.RequestModel;
import com.uni.easygate.datalayer.server.ServerResponseHandler;
import com.uni.easygate.fragment.AddTruckFragment;
import com.uni.easygate.fragment.ExitTruckFragment;
import com.uni.easygate.fragment.ProfileFragment;
import com.uni.easygate.fragment.TruckEntryExitFragment;
import com.uni.easygate.security.SecurePreferences;
import com.uni.easygate.utilities.Methods;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String[] drawerItemsArray;

    private int position = 0;

    private Fragment fragment;

    private MyHttpClient myHttpClient;

    private Intent intent;

    private List<GpsLog> gpsLog;
    private List<Event> measureLog;
    private List<Comment> commentLog;
    private List<Activity> activityLog;

    public ProgressBar progressBar;

    private boolean isMap = true;

    private boolean added = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActiveAndroid.initialize(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        drawerItemsArray = getResources().getStringArray(R.array.drawer_items_array);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        getSupportActionBar().setTitle("جدول الصهاريج");

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = new TruckEntryExitFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_layout, fragment, "")
                .commit();

    }

    public void setTitleToolbar(String text) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(text);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Bundle args = new Bundle();
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (id) {

            case R.id.nav_home:
                position = 0;

                if (Methods.isAutomaticTimeEnabled(this)) {
                    fragment = new TruckEntryExitFragment();
                } else displayAutomaticTimeEnable();

                break;
            case R.id.nav_profile:
                position = 1;
                if (Methods.isAutomaticTimeEnabled(this)) {
                    fragment = new ProfileFragment();
                } else displayAutomaticTimeEnable();

                break;
            case R.id.nav_logout:
                position = 2;
                showLogoutDialog();
                break;

            default:
                fragment = new TruckEntryExitFragment();
                break;
        }

        if (position != 2) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_layout, fragment, "")
                    .commit();
//            getSupportActionBar().setTitle(drawerItemsArray[position]);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder((new ContextThemeWrapper(this, R.style.AlertDialogCustom)));
        alertDialogBuilder.setTitle(getResources().getString(R.string.are_you_logout));
        alertDialogBuilder
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activityLog = Activity.getAll();
                        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                        String listString = gson.toJson(
                                activityLog,
                                new TypeToken<ArrayList<Activity>>() {
                                }.getType());

                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(listString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            uploadActivityLog(jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    private void setError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    private void uploadActivityLog(final JSONArray log) throws JSONException, UnsupportedEncodingException {

        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(MainActivity.this);
        RequestModel requestModel = requestDataProvider.addUserActivity(
                SecurePreferences.getInstance(MainActivity.this).getString(Parameters.USER_NUMBER),
                SecurePreferences.getInstance(MainActivity.this).getString(Parameters.PASSWORD),
                log
        );
        Type type = new TypeToken<UserWrapperModel>() {
        }.getType();

        myHttpClient.post(MainActivity.this, requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<UserWrapperModel>(type) {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onConnectivityError(String message) {
                logout();
            }

            @Override
            public void onDataError(String message) {
                setError(message);
                logout();
            }

            @Override
            public void onServerFailure(String message) {
                setError(message);
                logout();
            }


            @Override
            public void onServerSuccess(UserWrapperModel data) {
                for (int i = 0; i < activityLog.size(); i++) {
                    Activity.delete(Activity.class, activityLog.get(i).getId());
                }
                List<Comment> comments = Comment.getAll();
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                String listString = gson.toJson(
                        comments,
                        new TypeToken<ArrayList<Comment>>() {
                        }.getType());

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(listString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    sendComment(jsonArray, comments);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();

            }
        });
    }

    private void sendComment(JSONArray jsonObject, final List<Comment> commentLog) throws JSONException, UnsupportedEncodingException {
        ((MainActivity) MainActivity.this).progressBar.setVisibility(View.VISIBLE);
        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(MainActivity.this);
        RequestModel requestModel = requestDataProvider.addComment(
                SecurePreferences.getInstance(MainActivity.this).getString(Parameters.USER_NUMBER),
                SecurePreferences.getInstance(MainActivity.this).getString(Parameters.PASSWORD),
                jsonObject
        );
        Type type = new TypeToken<UserWrapperModel>() {
        }.getType();

        myHttpClient.post(MainActivity.this, requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<UserWrapperModel>(type) {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onConnectivityError(String message) {

            }

            @Override
            public void onDataError(String message) {
                setError(message);
            }

            @Override
            public void onServerFailure(String message) {
                setError(message);

            }


            @Override
            public void onServerSuccess(UserWrapperModel data) {
                for (int i = 0; i < commentLog.size(); i++) {
                    Comment.delete(Comment.class, commentLog.get(i).getId());
                }
                List<Event> events = Event.getAll();
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                String listString = gson.toJson(
                        events,
                        new TypeToken<ArrayList<Event>>() {
                        }.getType());

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(listString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    sendDataToServer(jsonArray, events);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void sendDataToServer(final JSONArray log, final List<Event> eventId) throws JSONException, UnsupportedEncodingException {
        ((MainActivity) MainActivity.this).progressBar.setVisibility(View.VISIBLE);
        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(MainActivity.this);
        RequestModel requestModel = requestDataProvider.addTruckEntry(
                SecurePreferences.getInstance(MainActivity.this).getString(Parameters.USER_NUMBER),
                SecurePreferences.getInstance(MainActivity.this).getString(Parameters.PASSWORD),
                log
        );
        Type type = new TypeToken<UserWrapperModel>() {
        }.getType();

        myHttpClient.post(MainActivity.this, requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<UserWrapperModel>(type) {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onConnectivityError(String message) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onDataError(String message) {
                setError(message);
            }

            @Override
            public void onServerFailure(String message) {
                setError(message);
            }


            @Override
            public void onServerSuccess(UserWrapperModel data) {
                for (int i = 0; i < eventId.size(); i++) {
                    Event.delete(Event.class, eventId.get(i).getId());
                }
                List<Exit> exits = Exit.getAll();
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                String listString = gson.toJson(
                        exits,
                        new TypeToken<ArrayList<Exit>>() {
                        }.getType());

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(listString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    sendDataToServerExit(jsonArray, exits);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFinish() {
                super.onFinish();
                ((MainActivity) MainActivity.this).progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void sendDataToServerExit(final JSONArray log, final List<Exit> eventId) throws JSONException, UnsupportedEncodingException {
        ((MainActivity) MainActivity.this).progressBar.setVisibility(View.VISIBLE);

        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(MainActivity.this);
        RequestModel requestModel = requestDataProvider.addTruckExit(
                SecurePreferences.getInstance(MainActivity.this).getString(Parameters.USER_NUMBER),
                SecurePreferences.getInstance(MainActivity.this).getString(Parameters.PASSWORD),
                log
        );
        Type type = new TypeToken<UserWrapperModel>() {
        }.getType();

        myHttpClient.post(MainActivity.this, requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<UserWrapperModel>(type) {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onConnectivityError(String message) {

            }

            @Override
            public void onDataError(String message) {
                setError(message);
            }

            @Override
            public void onServerFailure(String message) {
                setError(message);
            }


            @Override
            public void onServerSuccess(UserWrapperModel data) {
                for (int i = 0; i < eventId.size(); i++) {
                    Exit.delete(Exit.class, eventId.get(i).getId());
                }

               logout();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                ((MainActivity) MainActivity.this).progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void logout() {
        progressBar.setVisibility(View.GONE);

        ActiveAndroid.beginTransaction();
        try {

            Activity activity = new Activity(UUID.randomUUID().toString(),
                    SecurePreferences.getInstance(MainActivity.this).getString(Parameters.USER_ID_FOR_LOG)
                    ,
                    Parameters.ACTIVITY_LOG_OUT, Methods.getCurrentTimeStamp());
            activity.save();
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

        SecurePreferences.getInstance(MainActivity.this).put("b", SecurePreferences.getInstance(MainActivity.this).getString(Parameters.PASSWORD));
        SecurePreferences.getInstance(MainActivity.this).put(Parameters.DRIVER_NUMBER, "");
        SecurePreferences.getInstance(MainActivity.this).put(Parameters.VEHICLE_NUMBER, "");
        SecurePreferences.getInstance(MainActivity.this).put(Parameters.LOGGED, "");
        SecurePreferences.getInstance(MainActivity.this).put(Parameters.FIRST_TIME, "");
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setMessage(getString(R.string.gps_inactive))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.enable), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void displayAutomaticTimeEnable() {
        ActiveAndroid.beginTransaction();
        try {

            Activity activity = new Activity(UUID.randomUUID().toString(),
                    SecurePreferences.getInstance(MainActivity.this).getString(Parameters.USER_ID_FOR_LOG)
                    ,
                    Parameters.ACTIVITY_AUTOMATIC_OFF, Methods.getCurrentTimeStamp());
            activity.save();
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }


        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setMessage(getString(R.string.automatic_time_inactive))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.enable), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        ActiveAndroid.beginTransaction();
                        try {

                            Activity activity = new Activity(UUID.randomUUID().toString(),
                                    SecurePreferences.getInstance(MainActivity.this).getString(Parameters.USER_ID_FOR_LOG)
                                    ,
                                    Parameters.ACTIVITY_AUTOMATIC_ON, Methods.getCurrentTimeStamp());
                            activity.save();
                            ActiveAndroid.setTransactionSuccessful();
                        } finally {
                            ActiveAndroid.endTransaction();
                        }
                        startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setMessage(getString(R.string.data_not_saved))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.proceed), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        getSupportFragmentManager().popBackStack();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment addFragment = fragmentManager.findFragmentByTag(AddTruckFragment.class.getSimpleName());
        Fragment exitFragment = fragmentManager.findFragmentByTag(ExitTruckFragment.class.getSimpleName());
        if (addFragment == null && exitFragment == null) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "اضفط على مفتاح العودة مرة اخرى من اجل الخروج من التطبيق", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
     else showAlert();


}


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myHttpClient != null)
            myHttpClient.cancelAllRequests(true);
    }
}
