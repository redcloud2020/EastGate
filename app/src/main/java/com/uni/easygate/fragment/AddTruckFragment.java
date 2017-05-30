package com.uni.easygate.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.google.gson.reflect.TypeToken;
import com.uni.easygate.R;
import com.uni.easygate.config.Parameters;
import com.uni.easygate.datalayer.models.Comment;
import com.uni.easygate.datalayer.models.Event;
import com.uni.easygate.datalayer.models.Truck;
import com.uni.easygate.datalayer.models.User;
import com.uni.easygate.datalayer.models.UserWrapperModel;
import com.uni.easygate.datalayer.server.GetRequestModel;
import com.uni.easygate.datalayer.server.MyHttpClient;
import com.uni.easygate.datalayer.server.RequestDataProvider;
import com.uni.easygate.datalayer.server.RequestModel;
import com.uni.easygate.datalayer.server.ServerResponseHandler;
import com.uni.easygate.security.SecurePreferences;
import com.uni.easygate.ui.MainActivity;
import com.uni.easygate.utilities.Methods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by sammy on 4/6/2017.
 */

public class AddTruckFragment extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    Spinner truckNumber;
    EditText driverNumber;
    EditText voucherNumber;
    EditText comment;

    TextView truckName;
    Spinner driverName;
    TextView entryTime;

    CheckBox emptyEntered;

    Button save;

    String truckNumberString;
    String driverNumberString;
    String voucherNumberString;
    String commentString;

    ArrayList<String> trucks = new ArrayList<>();
    private MyHttpClient myHttpClient;

    private List<User> drivers = new ArrayList<>();
    private List<User> driversSorted = new ArrayList<>();

    private String truckVolume;

    private Boolean clicked = false;

    private Boolean edited = false;

            private int hour;
    private int minute;
    private Event event;
    private boolean timeSet = false;
    String oldCom = "";
    public static AddTruckFragment newInstance(Event event) {

        Bundle args = new Bundle();
        args.putParcelable("Event", event);
        AddTruckFragment fragment = new AddTruckFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_add_truck, container, false);
        if (getActivity() != null)
            ((MainActivity) getActivity()).setTitleToolbar("سجل دخول");


        if (getArguments() != null)
            event = getArguments().getParcelable("Event");
//        layout.setFocusableInTouchMode(true);
//        layout.requestFocus();
//        layout.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                Log.i("", "keyCode: " + keyCode);
//                if( keyCode == KeyEvent.KEYCODE_BACK ) {
//                    if(TextUtils.isEmpty(voucherNumber.getText().toString()) && TextUtils.isEmpty(comment.getText().toString()))
//                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    else showAlert();
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });

        truckNumber = (Spinner) layout.findViewById(R.id.truck_number);
        driverNumber = (EditText) layout.findViewById(R.id.driver_number);
        voucherNumber = (EditText) layout.findViewById(R.id.voucher_number);
        comment = (EditText) layout.findViewById(R.id.comment);


        truckName = (TextView) layout.findViewById(R.id.truck_name);
        driverName = (Spinner) layout.findViewById(R.id.driver_name);
        entryTime = (TextView) layout.findViewById(R.id.entry_time);

        emptyEntered = (CheckBox) layout.findViewById(R.id.checkbox);

        if (event != null) {
            driverNumber.setText(event.getDriverUserNumber());
            voucherNumber.setText(event.getEntryExit_voucher_number());
            entryTime.setText(Methods.splitAfter(event.getTimestamp()));

        }


        save = (Button) layout.findViewById(R.id.save);

        drivers = User.getAll();
        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("");
        for (int i = 0; i < drivers.size(); i++) {
            if (drivers.get(i).getRoleId().equals("15")) {
                spinnerArray.add(drivers.get(i).getFirst_name_ar() + " " + drivers.get(i).getLast_name_ar());
                driversSorted.add(drivers.get(i));
            }
        }
//        Locale locale = new Locale("ar");
//        final Collator collator = java.text.Collator.getInstance(locale);
//
//        Collections.sort(spinnerArray, new Comparator<String>() {
//            @Override
//            public int compare(String obj1, String obj2) {
//                return collator.compare(obj1, obj2);
//            }
//        });
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverName.setAdapter(spinnerArrayAdapter);
        driverName.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ufow_spinner_pressed_holo_light));

        final List<Truck> truckList = Truck.getAll();
        trucks.add("");
        for (int i = 0; i < truckList.size(); i++) {
            trucks.add(truckList.get(i).getTruck_number());
        }


        ArrayAdapter<String> truckArray = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, trucks); //selected item will look like a spinner set from XML
        truckArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        truckNumber.setAdapter(truckArray);
        truckNumber.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ufow_spinner_pressed_holo_light));


        driverName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0)
                    driverNumber.setText(driversSorted.get(i - 1).getUsername() + "");
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setTextSize(24);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//        driverNumber.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if(editable.toString().length()!=0) {
//                    User user = User.getUserByName(editable.toString());
//                    if (user != null) {
//                        for(int i =0; i<drivers.size();i++){
//                            if(drivers.get(i).getId().equals(user.getId()))
//                                driverName.setSelection(i);
//                        }
//                    }
//                }
//            }
//        });
        driverNumber.setVisibility(View.GONE);


        truckNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0)
                    truckName.setText(
                            getString(R.string.quantity) + " " +
                                    truckList.get(i-1).getTruck_total_capacity() + " " + getString(R.string.meter));
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setTextSize(24);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (event == null) {
            if (!timeSet)
                entryTime.setText(getString(R.string.enter_time) + " " + Methods.splitAfter(Methods.getCurrentTimeStamp()));
            else
                entryTime.setText(getString(R.string.enter_time) + " " + Methods.splitAfter(Methods.getCurrentTimeStamp(hour, minute)));

        }
        entryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        if (!clicked)
            save.setOnClickListener(this);


        if (event != null) {
            if (event.getTruck_volume().equals("0"))
                emptyEntered.setChecked(true);
            else emptyEntered.setChecked(false);
            for (int i = 0; i < truckList.size(); i++) {
                if (truckList.get(i).getTruck_Id().equals(event.getTruck_Id()))
                    truckNumber.setSelection(i + 1);
            }
            for (int i = 0; i < driversSorted.size(); i++) {
                if (driversSorted.get(i).getUser_Id().equals(event.getDriver_user_Id()))
                    driverName.setSelection(i + 1);
            }

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(event.getComment_Id());
            if (isNetworkAvailable())
                try {
                    getComment(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                List<Comment> oldComments = Comment.getAll();
                if (oldComments != null && !oldComments.isEmpty())
                    for (int i = 0; i < oldComments.size(); i++) {
                        if (event == null) {
                            if (oldComments.get(i).getComment_Id().equals(event.getComment_Id())) {
                                comment.setText(oldComments.get(i).getContent());
                                oldCom = oldComments.get(i).getTimestamp();
                            }
                        } else if (oldComments.get(i).getComment_Id().equals(event.getComment_Id())) {
                            comment.setText(oldComments.get(i).getContent());
                            oldCom = oldComments.get(i).getTimestamp();

                    }
            }

        }
        return layout;
    }

    private void getComment(JSONArray jsonArray) throws JSONException, UnsupportedEncodingException {

        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(getActivity());
        GetRequestModel requestModel = requestDataProvider.getComment(SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_NUMBER),
                SecurePreferences.getInstance(getActivity()).getString(Parameters.PASSWORD),
                jsonArray);
        Type type = new TypeToken<ArrayList<Comment>>() {
        }.getType();

        myHttpClient.get(getActivity(), requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<ArrayList<Comment>>(type) {

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
            public void onServerSuccess(ArrayList<Comment> data) {
                if (data != null) {
                    if (!data.isEmpty()) {
                        if(!TextUtils.isEmpty(oldCom)) {
                            if (Methods.convertToMilli(data.get(0).getTimestamp()) > Methods.convertToMilli(oldCom))
                                if(!TextUtils.isEmpty(data.get(0).getContent()))
                                    comment.setText(data.get(0).getContent() + "");
                        }else
                        if(!TextUtils.isEmpty(data.get(0).getContent()))
                            comment.setText(data.get(0).getContent() + "");
                    }
                }

            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void showDialog() {
        final Calendar calendar = Calendar.getInstance();
        // Get the current hour and minute
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog tpd;
        if (getActivity() != null) {
            tpd = new TimePickerDialog(getActivity(), this, hour, minute, false);
            tpd.show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                if (Methods.isAutomaticTimeEnabled(getActivity())) {
                    if (driverName.getSelectedItemPosition() != 0) {
                        if (truckNumber.getSelectedItemPosition() != 0) {
                            driverNumberString = driverNumber.getText().toString();
                            truckNumberString =
                                    trucks.get(truckNumber.getSelectedItemPosition());
                            voucherNumberString = voucherNumber.getText().toString();
                            commentString = comment.getText().toString();
                            if (!clicked)
                                if (!TextUtils.isEmpty(driverNumberString) &&
                                        !TextUtils.isEmpty(truckNumberString) &&
                                        !TextUtils.isEmpty(voucherNumberString)) {
                                    if (getActivity() != null)
                                        save.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

                                    Truck truck = Truck.getTruckByTruckNumber(truckNumberString);
                                    User user = User.getUserByName(driverNumberString);
                                    JSONArray jsonArray = new JSONArray();
                                    JSONObject commentObject = new JSONObject();
                                    String eventID = UUID.randomUUID().toString();
                                    String commentstr = UUID.randomUUID().toString();


                                    try {


                                        if (event != null) {
                                            List<Comment> comments = Comment.getAll();
                                            for (int i = 0; i < comments.size(); i++) {
                                                if (comments.get(i).getComment_Id().equals(event.getComment_Id()))
                                                    Comment.delete(Comment.class, comments.get(i).getId());
                                            }
                                            List<Event> events = Event.getAll();
                                            for (int i = 0; i < events.size(); i++) {
                                                if (events.get(i).getEvent_Id().equals(event.getEvent_Id()))
                                                    Event.delete(Event.class, events.get(i).getId());
                                            }
                                        }
                                        JSONObject jsonObject = new JSONObject();
                                        if (event != null)
                                            jsonObject.put("Event_Id", event.getEvent_Id());
                                        else
                                            jsonObject.put("Event_Id", eventID);
                                        jsonObject.put("Driver_user_Id", user.getUser_Id());
                                        if (emptyEntered.isChecked()) {
                                            jsonObject.put("Truck_volume", "0");
                                            truckVolume = "0";
                                        } else {
                                            jsonObject.put("Truck_volume", "1");
                                            truckVolume = "1";
                                        }
                                        jsonObject.put("Controller_user_Id", SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_ID_FOR_LOG));
                                        if (truck != null)
                                            jsonObject.put("Truck_Id", truck.getTruck_Id());
                                        jsonObject.put("EntryExit_voucher_number", voucherNumberString);
                                        jsonObject.put("Data_edited", "0");

                                        jsonObject.put("Comment_Id", commentstr);
                                        if (event != null && !edited)
                                            jsonObject.put("Timestamp", event.getTimestamp());
                                        else {
                                            if (!timeSet)
                                                jsonObject.put("Timestamp", Methods.getCurrentTimeStamp());
                                            else
                                                jsonObject.put("Timestamp", Methods.getCurrentTimeStamp(hour, minute));
                                        }
                                        if (event != null)
                                            jsonObject.put("Time_user", event.getTime_user());
                                        else
                                            jsonObject.put("Time_user", Methods.getCurrentTimeStampApi());

                                        jsonArray.put(jsonObject);


                                        commentObject.put("Comment_Id", commentstr);
                                        commentObject.put("Content", commentString);
                                        commentObject.put("User_Id_by", SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_ID_FOR_LOG));
                                        commentObject.put("User_Id_about", user.getUser_Id());
                                        if (!timeSet)
                                            commentObject.put("Timestamp", Methods.getCurrentTimeStamp());
                                        else
                                            commentObject.put("Timestamp", Methods.getCurrentTimeStamp(hour, minute));


                                        ActiveAndroid.beginTransaction();
                                        try {
                                            String time = "";
                                            if (!timeSet)
                                                time = Methods.getCurrentTimeStampApi();
                                            else
                                                time = Methods.getCurrentTimeStampApi(hour, minute);

                                            Comment comment = new Comment(commentstr, time, commentString,
                                                    SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_ID_FOR_LOG),
                                                    user.getUser_Id(), "");
                                            comment.save();
                                            ActiveAndroid.setTransactionSuccessful();
                                        } finally {
                                            ActiveAndroid.endTransaction();
                                        }
                                        ActiveAndroid.beginTransaction();
                                        try {
                                            String eventIDNew;
                                            if (event != null)
                                                eventIDNew = event.getEvent_Id();
                                            else eventIDNew = eventID;
                                            String eventTime = "";
                                            if (!timeSet)
                                                eventTime = Methods.getCurrentTimeStampApi(hour, minute);
                                            else
                                                eventTime = Methods.getCurrentTimeStampApi();

                                            String eventUserTime = "";
                                            if (event != null && !edited) {
                                                eventTime = event.getTimestamp();
                                                eventUserTime = event.getTime_user();
                                            }
                                            else {
                                                eventUserTime = Methods.getCurrentTimeStampApi();
                                                if (!timeSet)
                                                    eventTime = Methods.getCurrentTimeStampApi();
                                                else
                                                    eventTime = Methods.getCurrentTimeStampApi(hour, minute);
                                            }
                                            Event event = new Event(eventIDNew, eventTime, user.getUser_Id(),
                                                    truck.getTruck_Id(),
                                                    SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_ID_FOR_LOG),
                                                    voucherNumberString,
                                                    truckVolume, commentstr, "0000-00-00 00:00:00", false,
                                                    truck.getTruck_number(), user.getFirst_name_ar() + " " + user.getLast_name_ar(),
                                                    truck.getTruck_total_capacity(), user.getUsername(),
                                                    "0", eventUserTime);
                                            event.save();
                                            ActiveAndroid.setTransactionSuccessful();
                                        } finally {
                                            ActiveAndroid.endTransaction();
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    JSONArray json = new JSONArray();
                                    json.put(commentObject);

                                    if (isNetworkAvailable())
                                        try {
                                            sendComment(json, jsonArray, commentstr, eventID);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    else {
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }

                                    clicked = true;
                                } else
                                    Toast.makeText(getActivity(), getString(R.string.missing), Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(getActivity(), getString(R.string.missing), Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(getActivity(), getString(R.string.missing), Toast.LENGTH_LONG).show();

                } else ((MainActivity) getActivity()).displayAutomaticTimeEnable();

                break;
            default:
                break;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void sendComment(JSONArray jsonObject, final JSONArray jsonArray, final String commentId, final String eventId) throws JSONException, UnsupportedEncodingException {
        if (getActivity() != null)
            ((MainActivity) getActivity()).progressBar.setVisibility(View.VISIBLE);
        myHttpClient = new MyHttpClient();
        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.put(jsonObject);
        RequestDataProvider requestDataProvider = new RequestDataProvider(getActivity());
        RequestModel requestModel = requestDataProvider.addComment(
                SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_NUMBER),
                SecurePreferences.getInstance(getActivity()).getString(Parameters.PASSWORD),
                jsonObject
        );
        Type type = new TypeToken<Comment>() {
        }.getType();

        myHttpClient.post(getActivity(), requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<Comment>(type) {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onConnectivityError(String message) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
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
            public void onServerSuccess(Comment data) {

                if (isNetworkAvailable())
                    try {
                        sendDataToServer(jsonArray, eventId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                else {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void sendDataToServer(final JSONArray log, final String eventId) throws JSONException, UnsupportedEncodingException {
        if (getActivity() != null)
            ((MainActivity) getActivity()).progressBar.setVisibility(View.VISIBLE);
        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(getActivity());
        RequestModel requestModel = requestDataProvider.addTruckEntry(
                SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_NUMBER),
                SecurePreferences.getInstance(getActivity()).getString(Parameters.PASSWORD),
                log
        );
        Type type = new TypeToken<UserWrapperModel>() {
        }.getType();

        myHttpClient.post(getActivity(), requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<UserWrapperModel>(type) {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onConnectivityError(String message) {


                Intent intent = new Intent(getActivity(), MainActivity.class);
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


                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null)
                    ((MainActivity) getActivity()).progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void setError(String message) {
        Toast.makeText(getActivity(), message + "", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hour = i;
        minute = i1;
        edited = true;
        timeSet = true;
        entryTime.setText(getString(R.string.enter_time) + " " + Methods.splitAfter(Methods.getCurrentTimeStamp(i, i1)));
    }
}
