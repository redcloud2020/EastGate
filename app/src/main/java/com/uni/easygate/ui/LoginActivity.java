package com.uni.easygate.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.uni.easygate.datalayer.models.Destination;
import com.uni.easygate.datalayer.models.DestinationWrapper;
import com.uni.easygate.datalayer.models.Event;
import com.uni.easygate.datalayer.models.Exit;
import com.uni.easygate.datalayer.models.Tank;
import com.uni.easygate.datalayer.models.Truck;
import com.uni.easygate.datalayer.models.TruckWrapperModel;
import com.uni.easygate.datalayer.models.User;
import com.uni.easygate.datalayer.models.UserWrapperModel;
import com.uni.easygate.datalayer.server.GetRequestModel;
import com.uni.easygate.datalayer.server.MyHttpClient;
import com.uni.easygate.datalayer.server.RequestDataProvider;
import com.uni.easygate.datalayer.server.RequestModel;
import com.uni.easygate.datalayer.server.ServerResponseHandler;
import com.uni.easygate.security.SecurePreferences;
import com.uni.easygate.utilities.Methods;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by sammy on 2/28/2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userNumberEt;
    private EditText passwordEt;

    private Button login;

    private String userNumber;
    private String password;

    private MyHttpClient myHttpClient;

    private ProgressBar progressBar;


    private List<Event> measureLog;
    private List<Comment> commentLog;
    private ImageView rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        userNumberEt = (EditText) findViewById(R.id.user_number_edit_text);
        passwordEt = (EditText) findViewById(R.id.password_edit_text);
        rotate = (ImageView) findViewById(R.id.rotate);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        login = (Button) findViewById(R.id.login);
        rotate.bringToFront();
        login.setOnClickListener(this);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = SecurePreferences.getInstance(LoginActivity.this).getString("b");
                userNumber = SecurePreferences.getInstance(LoginActivity.this).getString(Parameters.USER_NUMBER);
                if (!TextUtils.isEmpty(userNumber) && !TextUtils.isEmpty(password)) {

                    progressBar.setVisibility(View.VISIBLE);
                    List<Activity> activityLog = Activity.getAll();
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
                        uploadActivityLog(jsonArray, activityLog);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else setError(getString(R.string.missing));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:

                userNumber = userNumberEt.getText().toString();
                password = passwordEt.getText().toString();

                if (!TextUtils.isEmpty(password))
                    if (!TextUtils.isEmpty(userNumber)) {
                        if (Methods.isAutomaticTimeEnabled(LoginActivity.this)) {
                            try {
                                if (isNetworkAvailable())
                                    if (TextUtils.isEmpty(SecurePreferences.getInstance(this).getString(Parameters.FIRST_TIME)))
                                        getUsers(userNumber, Methods.md5(password), Parameters.DUMMY_TIMESTAMP);
                                    else {
                                        User lastUser = User.getOldDate();
                                        if(lastUser!=null && lastUser.getUpdated_at()!=null)
                                        getUsers(userNumber, Methods.md5(password), lastUser.getUpdated_at());
                                        else
                                        getUsers(userNumber, Methods.md5(password),Parameters.DUMMY_TIMESTAMP);
                                    }
                                else proceedToLogin();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else displayAutomaticTimeEnable();
                    } else displayUserNumberMissingDialog();
                else displayPasswordMissingDialog();


                break;
        }
    }

    private void proceedToLogin() {

        progressBar.setVisibility(View.GONE);
        userNumber = userNumberEt.getText().toString();
        password = passwordEt.getText().toString();
        User myUser = null;
        if (!TextUtils.isEmpty(userNumber) && !TextUtils.isEmpty(password))
            myUser = User.login(userNumber, Methods.md5(password));
        if (myUser != null && myUser.getEast_gate_app().equals("1")) {
            SecurePreferences.getInstance(LoginActivity.this).put(Parameters.USER_NUMBER, userNumber);
            SecurePreferences.getInstance(LoginActivity.this).put(Parameters.USERNAME, myUser.getFirst_name_ar() + " " +
                    myUser.getLast_name_ar());
            SecurePreferences.getInstance(LoginActivity.this).put(Parameters.USER_ID_FOR_LOG, myUser.getUser_Id());
            SecurePreferences.getInstance(LoginActivity.this).put(Parameters.USER_ID_ABOUT, myUser.getEmployer_Id());
            SecurePreferences.getInstance(LoginActivity.this).put(Parameters.PASSWORD, Methods.md5(password));
            SecurePreferences.getInstance(this).put(Parameters.FIRST_TIME, Parameters.FIRST_TIME);
            SecurePreferences.getInstance(this).put(Parameters.LOGGED, Parameters.LOGGED);
            SecurePreferences.getInstance(this).put(Parameters.GET, Parameters.GET);

            ActiveAndroid.beginTransaction();
            try {

                Activity activity = new Activity(UUID.randomUUID().toString(), myUser.getUser_Id(),
                        Parameters.ACTIVITY_LOG_IN, Methods.getCurrentTimeStamp());
                activity.save();
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }

            Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
        } else displayWrongCredentials();

    }

    private void displayPasswordMissingDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setMessage(getString(R.string.please_enter_password))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void displayDriverMissingDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setMessage(getString(R.string.please_enter_driver_number))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void displayVehicleNumberDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setMessage(getString(R.string.input_vehicle_number))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void displayUserNumberMissingDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setMessage(getString(R.string.please_input_user_number))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean displayGPSNotEnabledDialog() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }


    private void displayAutomaticTimeEnable() {
        ActiveAndroid.beginTransaction();
        try {

            Activity activity = new Activity(UUID.randomUUID().toString(),
                    SecurePreferences.getInstance(LoginActivity.this).getString(Parameters.USER_ID_FOR_LOG)
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
                                    SecurePreferences.getInstance(LoginActivity.this).getString(Parameters.USER_ID_FOR_LOG)
                                    ,
                                    Parameters.ACTIVITY_AUTOMATIC_ON, Methods.getCurrentTimeStamp());
                            activity.save();
                            ActiveAndroid.setTransactionSuccessful();
                        } finally {
                            ActiveAndroid.endTransaction();
                        }
                        startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
                    }
                });
//                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        dialog.cancel();
//                    }
//                }

        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void displayWrongCredentials() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setMessage(getString(R.string.wrong_information))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void getUsers(final String username, final String password, final String timestamp) throws JSONException, UnsupportedEncodingException {
        progressBar.setVisibility(View.VISIBLE);
        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(this);
        GetRequestModel requestModel = requestDataProvider.getUsers(username, password, timestamp);
        Type type = new TypeToken<UserWrapperModel>() {
        }.getType();

        myHttpClient.get(this, requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<UserWrapperModel>(type) {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onConnectivityError(String message) {
                proceedToLogin();

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onDataError(String message) {
                setError(message);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onServerFailure(String message) {
                setError(message);
                progressBar.setVisibility(View.GONE);

            }


            @Override
            public void onServerSuccess(UserWrapperModel data) {
                if (data != null) {
                    insertUsers(data, username, password, timestamp);
                }

            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void setError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void insertUsers(UserWrapperModel data, String userNumber, String password, String timestamp) {

        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < data.getNewUpdatedData().size(); i++) {
                User user = new User(data.getNewUpdatedData().get(i).getUser_Id(), data.getNewUpdatedData().get(i).getFirst_name_ar()
                        , data.getNewUpdatedData().get(i).getLast_name_ar(), data.getNewUpdatedData().get(i).getFirst_name_en(),
                        data.getNewUpdatedData().get(i).getLast_name_en(),
                        data.getNewUpdatedData().get(i).getPhone_number(), data.getNewUpdatedData().get(i).getUsername()
                        , data.getNewUpdatedData().get(i).getPassword(), data.getNewUpdatedData().get(i).getEmployer_Id(),
                        data.getNewUpdatedData().get(i).getRating(), data.getNewUpdatedData().get(i).getCreated_at(),
                        data.getNewUpdatedData().get(i).getUpdated_at(), data.getNewUpdatedData().get(i).getIsDeleted(),
                        data.getNewUpdatedData().get(i).getRoleId(), data.getNewUpdatedData().get(i).getEast_gate_app());
                user.save();
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }


        if (data.getDeletedData() != null && !data.getDeletedData().isEmpty()) {

            ActiveAndroid.beginTransaction();
            try {
                for (int i = 0; i < data.getDeletedData().size(); i++) {
                    User.delete(User.class, data.getDeletedData().get(i).getId());
                }

                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }
        if (TextUtils.isEmpty(SecurePreferences.getInstance(this).getString(Parameters.FIRST_TIME)))
            try {
                getTrucks(userNumber, password, Parameters.DUMMY_TIMESTAMP);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        else {
            Truck lastTruck = Truck.getOldDate();
            try {
                if(lastTruck!=null && lastTruck.getUpdated_at()!=null)
                getTrucks(userNumber, password, lastTruck.getUpdated_at());
                else
                getTrucks(userNumber, password,Parameters.DUMMY_TIMESTAMP);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    private void getTrucks(final String username, final String password, final String timestamp) throws JSONException, UnsupportedEncodingException {

        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(this);
        GetRequestModel requestModel = requestDataProvider.getTrucks(username, password, timestamp);
        Type type = new TypeToken<TruckWrapperModel>() {
        }.getType();

        myHttpClient.get(this, requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<TruckWrapperModel>(type) {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onConnectivityError(String message) {
                proceedToLogin();
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
            public void onServerSuccess(TruckWrapperModel data) {
                if (data != null) {
                    insertTrucks(data, username, password, timestamp);
                }

            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void getDestinations(final String username, final String password, final String timestamp) throws JSONException, UnsupportedEncodingException {

        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(this);
        GetRequestModel requestModel = requestDataProvider.getDestinations(username, password, timestamp);
        Type type = new TypeToken<DestinationWrapper>() {
        }.getType();

        myHttpClient.get(this, requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<DestinationWrapper>(type) {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onConnectivityError(String message) {
                proceedToLogin();
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
            public void onServerSuccess(DestinationWrapper data) {
                if (data != null) {
                    insertDestinations(data, username, password, timestamp);
                }

            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }


    private void insertDestinations(DestinationWrapper data, String userNumber, String password, String timestamp) {

        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < data.getNewUpdatedData().size(); i++) {
                Destination destination = new Destination(data.getNewUpdatedData().get(i).getDestination_Id(), data.getNewUpdatedData().get(i).getDestination_ar()
                        , data.getNewUpdatedData().get(i).getDestination_en(), data.getNewUpdatedData().get(i).getAr_short()
                        , data.getNewUpdatedData().get(i).getCreated_at(), data.getNewUpdatedData().get(i).getUpdated_at(),
                        data.getNewUpdatedData().get(i).getDisplay());
                destination.save();
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        if (data.getDeletedData() != null && !data.getDeletedData().isEmpty()) {

            ActiveAndroid.beginTransaction();
            try {
                for (int i = 0; i < data.getDeletedData().size(); i++) {
                    Destination.delete(Tank.class, data.getDeletedData().get(i).getId());
                }

                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }

        proceedToLogin();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_menu, menu); // set your file name
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void uploadActivityLog(final JSONArray log, final List<Activity> activityLog) throws JSONException, UnsupportedEncodingException {

        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(LoginActivity.this);
        RequestModel requestModel = requestDataProvider.addUserActivity(
                userNumber,
                password,
                log
        );
        Type type = new TypeToken<UserWrapperModel>() {
        }.getType();

        myHttpClient.post(LoginActivity.this, requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<UserWrapperModel>(type) {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onConnectivityError(String message) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDataError(String message) {
                setError(message);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onServerFailure(String message) {
                setError(message);
                progressBar.setVisibility(View.GONE);
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
        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(LoginActivity.this);
        RequestModel requestModel = requestDataProvider.addComment(
                userNumber,
                password,
                jsonObject
        );
        Type type = new TypeToken<UserWrapperModel>() {
        }.getType();

        myHttpClient.post(LoginActivity.this, requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<UserWrapperModel>(type) {

            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onConnectivityError(String message) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDataError(String message) {
                setError(message);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onServerFailure(String message) {
                setError(message);
                progressBar.setVisibility(View.GONE);

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
        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(LoginActivity.this);
        RequestModel requestModel = requestDataProvider.addTruckEntry(
                userNumber,
                password,
                log
        );
        Type type = new TypeToken<UserWrapperModel>() {
        }.getType();

        myHttpClient.post(LoginActivity.this, requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<UserWrapperModel>(type) {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onConnectivityError(String message) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDataError(String message) {
                setError(message);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onServerFailure(String message) {
                progressBar.setVisibility(View.GONE);
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

            }
        });
    }

    private void sendDataToServerExit(final JSONArray log, final List<Exit> eventId) throws JSONException, UnsupportedEncodingException {

        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(LoginActivity.this);
        RequestModel requestModel = requestDataProvider.addTruckExit(
                userNumber,
                password,
                log
        );
        Type type = new TypeToken<UserWrapperModel>() {
        }.getType();

        myHttpClient.post(LoginActivity.this, requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<UserWrapperModel>(type) {

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

            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void insertTrucks(TruckWrapperModel data, String userNumber, String password, String timestamp) {

        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < data.getNewUpdatedData().size(); i++) {
                Truck truck = new Truck(data.getNewUpdatedData().get(i).getTruck_Id(), data.getNewUpdatedData().get(i).getTruck_number()
                        , data.getNewUpdatedData().get(i).getEmployer_Id(), data.getNewUpdatedData().get(i).getGreen_plate_number(),
                        data.getNewUpdatedData().get(i).getYellow_plate_number(),
                        data.getNewUpdatedData().get(i).getYear_of_make(), data.getNewUpdatedData().get(i).getTruck_total_capacity()
                        , data.getNewUpdatedData().get(i).getHose_length(), data.getNewUpdatedData().get(i).getIsDeleted(),
                        data.getNewUpdatedData().get(i).getCreated_at(), data.getNewUpdatedData().get(i).getUpdated_at());
                truck.save();
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        if (data.getDeletedData() != null && !data.getDeletedData().isEmpty()) {

            ActiveAndroid.beginTransaction();
            try {
                for (int i = 0; i < data.getDeletedData().size(); i++) {
                    Truck.delete(Truck.class, data.getDeletedData().get(i).getId());
                }

                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }
        if (TextUtils.isEmpty(SecurePreferences.getInstance(this).getString(Parameters.FIRST_TIME)))
            try {
                getDestinations(userNumber, password, Parameters.DUMMY_TIMESTAMP);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        else {
            Destination destination = Destination.getOldDate();
            try {
                if(destination!=null && destination.getUpdated_at()!=null)
                getDestinations(userNumber, password, destination.getUpdated_at());
                else
                getDestinations(userNumber, password, Parameters.DUMMY_TIMESTAMP);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myHttpClient != null)
            myHttpClient.cancelAllRequests(true);
    }
}
