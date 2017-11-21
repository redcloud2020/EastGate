package com.uni.easygate.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.uni.easygate.R;
import com.uni.easygate.config.Parameters;
import com.uni.easygate.datalayer.models.Comment;
import com.uni.easygate.datalayer.models.Destination;
import com.uni.easygate.datalayer.models.Event;
import com.uni.easygate.datalayer.models.EventWrapper;
import com.uni.easygate.datalayer.models.Exit;
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
import com.uni.easygate.utilities.Logger;
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

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by sammy on 4/6/2017.
 */

public class ExitTruckFragment extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    Spinner truckNumber;
    EditText driverNumber;
    EditText voucherNumber;
    EditText comment;
    EditText weight;
    EditText exitVoucher;
    EditText exitName;

    TextView exitNameDisplay;
    TextView truckName;
    Spinner driverName;
    TextView entryTime;
    TextView exitTime;

    Spinner spinner;

    CheckBox emptyEntered;

    Button save;

    String truckNumberString;
    String driverNumberString;
    String voucherNumberString;
    String commentString;


    private MyHttpClient myHttpClient;

    //    private Truck truck;
//    private User user;
    private Event event;

    private List<Destination> destinations = new ArrayList<>();

    private Exit exit;

    private LinearLayout voucherLinear;

    ArrayList<String> trucks = new ArrayList<>();

    private List<User> drivers = new ArrayList<>();
    private List<User> driversSorted = new ArrayList<>();

    private boolean clicked = false;

    private boolean akider = false;

    private int hour = 0;
    private int minute = 0;

    private int secondHour = 0;
    private int secondMinute = 0 ;

    private boolean timeSetFirst = false;
    private boolean timeSetSecond = false;

    private boolean firstEdited = false;
    private boolean secondEdited = false;

    private boolean firstSelectedPicker = true;
    private String oldCom = "";
    private EventWrapper events;

    private Logger logger;

    public static ExitTruckFragment newInstance(Event event, Truck truck, User user, Exit exit, EventWrapper events) {

        Bundle args = new Bundle();
        args.putParcelable("Event", event);
        args.putParcelable("Truck", truck);
        args.putParcelable("User", user);
        args.putParcelable("Exit", exit);
        args.putParcelable("Events", events);
        ExitTruckFragment fragment = new ExitTruckFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_exit_truck, container, false);
        if (getActivity() != null)
            ((MainActivity) getActivity()).setTitleToolbar("سجل دخول/خروج");

//
//        layout.setFocusableInTouchMode(true);
//        layout.requestFocus();
//        layout.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                Log.i("", "keyCode: " + keyCode);
//                if( keyCode == KeyEvent.KEYCODE_BACK ) {
//                    if(TextUtils.isEmpty(voucherNumber.getText().toString()) && TextUtils.isEmpty(comment.getText().toString())
//                            &&TextUtils.isEmpty(weight.getText().toString()) &&TextUtils.isEmpty(exitVoucher.getText().toString()))
//                        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    else showAlert();
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });

        logger = Logger.getInstance(getContext());

        truckNumber = (Spinner) layout.findViewById(R.id.truck_number);
        driverNumber = (EditText) layout.findViewById(R.id.driver_number);
        voucherNumber = (EditText) layout.findViewById(R.id.voucher_number);
        comment = (EditText) layout.findViewById(R.id.comment);
        weight = (EditText) layout.findViewById(R.id.weight);

        truckName = (TextView) layout.findViewById(R.id.truck_name);
        driverName = (Spinner) layout.findViewById(R.id.driver_name);
        entryTime = (TextView) layout.findViewById(R.id.entry_time);
        exitName = (EditText) layout.findViewById(R.id.exit_name);
        exitTime = (TextView) layout.findViewById(R.id.exit_time);
        exitVoucher = (EditText) layout.findViewById(R.id.exit_voucher_number);
        exitNameDisplay = (TextView) layout.findViewById(R.id.exit_name_display);
        voucherLinear = (LinearLayout) layout.findViewById(R.id.voucher_linear);

        spinner = (Spinner) layout.findViewById(R.id.spinner);

        emptyEntered = (CheckBox) layout.findViewById(R.id.checkbox);

        save = (Button) layout.findViewById(R.id.save);

        if (!clicked)
            save.setOnClickListener(this);

        layout.findViewById(R.id.next).setOnClickListener(this);
        layout.findViewById(R.id.prev).setOnClickListener(this);


        if (getArguments() != null) {
            events = getArguments().getParcelable("Events");
            event = getArguments().getParcelable("Event");
            List<Exit> exits = Exit.getAll();
            if (exits != null && !exits.isEmpty())
                for (int i = 0; i < exits.size(); i++) {
                    if (exits.get(i) != null && exits.get(i).getEntrance_Id() != null)
                        if (exits.get(i).getEntrance_Id().equals(event.getEvent_Id()))
                            exit = exits.get(i);
                }

            logger.write_line("Read for entry with id " + event.getId() + " at time: " + Methods.getCurrentTimeStampApi().toString());
            if(exit != null){
                logger.write_line("Exit id: " + exit.getId());
                logger.write_line("Exit Voucher Number: " + exit.getEntryExit_voucher_number());
                logger.write_line("Payment Voucher Number: " + exit.getPayment_voucher_number());
            } else {
            }

            final List<Truck> truckList = Truck.getAll();

            for (int i = 0; i < truckList.size(); i++) {
                trucks.add(truckList.get(i).getTruck_number());
            }


            ArrayAdapter<String> truckArray = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, trucks); //selected item will look like a spinner set from XML
            truckArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            truckNumber.setAdapter(truckArray);
            truckNumber.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ufow_spinner_pressed_holo_light));

            truckNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    truckName.setText(
                            getString(R.string.quantity) + " " +
                                    truckList.get(i).getTruck_total_capacity() + " " + getString(R.string.meter));
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                    ((TextView) adapterView.getChildAt(0)).setTextSize(24);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            for (int i = 0; i < truckList.size(); i++) {
                if (truckList.get(i).getTruck_Id().equals(event.getTruck_Id()))
                    truckNumber.setSelection(i);
            }

            drivers = User.getAll();
            ArrayList<String> spinnerArray = new ArrayList<>();
            for (int i = 0; i < drivers.size(); i++) {
                if (drivers.get(i).getRoleId().equals("15")) {
                    spinnerArray.add(drivers.get(i).getFirst_name_ar() + " " + drivers.get(i).getLast_name_ar());
                    driversSorted.add(drivers.get(i));
                }
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            driverName.setAdapter(spinnerArrayAdapter);
            driverName.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ufow_spinner_pressed_holo_light));

            driverName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    driverNumber.setText(driversSorted.get(i).getUsername() + "");
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                    ((TextView) adapterView.getChildAt(0)).setTextSize(24);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            if(event!=null)
            for (int i = 0; i < driversSorted.size(); i++) {
                if (driversSorted.get(i).getUser_Id().equals(event.getDriver_user_Id()))
                    driverName.setSelection(i);
            }


            if (event != null) {
                entryTime.setText(Methods.splitAfter(event.getTimestamp()) + "");
                entryTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firstSelectedPicker = true;
                        showDialog();
                    }
                });


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
                            if (exit == null) {
                                if (oldComments.get(i).getComment_Id().equals(event.getComment_Id())) {
                                    comment.setText(oldComments.get(i).getContent());
                                    oldCom = oldComments.get(i).getTimestamp();
                                }
                            } else if (oldComments.get(i).getComment_Id().equals(exit.getComment_Id())){
                                comment.setText(oldComments.get(i).getContent());
                                oldCom = oldComments.get(i).getTimestamp();}

                }
            }
            if (exit != null) {
                exitName.setText(exit.getPayment_voucher_number());
                exitTime.setText(Methods.splitAfter(exit.getTimestamp()));
                exitVoucher.setText(exit.getEntryExit_voucher_number());
                weight.setText(exit.getTruck_volume());


                for (int i = 0; i < destinations.size(); i++) {
                    if (destinations.get(i).getDestination_Id().equals(exit.getDestination())) {
                        spinner.setSelection(i);
                    }
                }
            } else {
                exitName.setText("");
                if (!timeSetFirst)
                    exitTime.setText(Methods.splitAfter(Methods.getCurrentTimeStampApi()));
                else
                    exitTime.setText(Methods.splitAfter(Methods.getCurrentTimeStampApi(hour, minute)));
                exitVoucher.setText("");
                weight.setText("");
            }
            exitTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firstSelectedPicker = false;
                    showDialog();
                }
            });
            if (event != null) {
                if (event.getTruck_volume().equals("0"))
                    emptyEntered.setChecked(true);
                else emptyEntered.setChecked(false);


                voucherNumber.setText(event.getEntryExit_voucher_number());
            }
        }

        if(event != null) {
            int event_pos = positions_in_wrapper();
            if(event_pos == events.CampEntrance.size() - 1)
            {
                layout.findViewById(R.id.next).setEnabled(false);
                ((Button) layout.findViewById(R.id.next)).setTextColor(Color.GRAY);

            }
            if(event_pos == 0) {

                layout.findViewById(R.id.prev).setEnabled(false);
                ((Button) layout.findViewById(R.id.prev)).setTextColor(Color.GRAY);

            }
        }

        destinations = Destination.getall();
        ArrayList<String> spinnerArray = new ArrayList<>();
        for (int i = 0; i < destinations.size(); i++) {
            spinnerArray.add(destinations.get(i).getDestination_ar());
        }

        if (exit != null)
            exitName.setText(exit.getPayment_voucher_number());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ufow_spinner_pressed_holo_light));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setTextSize(24);
                if (!destinations.get(i).getDisplay()) {
                    exitName.setVisibility(View.GONE);
                    exitNameDisplay.setVisibility(View.GONE);
                    voucherLinear.setVisibility(View.GONE);
                    akider = false;
                } else {
                    exitName.setVisibility(View.VISIBLE);
                    exitNameDisplay.setVisibility(View.VISIBLE);
                    voucherLinear.setVisibility(View.VISIBLE);
                    akider = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (exit != null)
            for (int i = 0; i < destinations.size(); i++) {
                if (destinations.get(i).getDestination_Id().equals(exit.getDestination())) {
                    spinner.setSelection(i);
                }
            }

//        getDestinations();

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                if (Methods.isAutomaticTimeEnabled(getActivity())) {
                    driverNumberString = driverNumber.getText().toString();
                    truckNumberString = trucks.get(truckNumber.getSelectedItemPosition());
                    voucherNumberString = voucherNumber.getText().toString();
                    commentString = comment.getText().toString();
                    if (!clicked)
                        if (akider && TextUtils.isEmpty(exitName.getText().toString()))
                            setError(getString(R.string.fill_akider));
                        else {
                            String payment_voucher = exitName.getText().toString();
                            if (!TextUtils.isEmpty(driverNumberString) &&
                                    !TextUtils.isEmpty(truckNumberString) &&
                                    !TextUtils.isEmpty(voucherNumberString) &&
                                    !TextUtils.isEmpty(weight.getText().toString())
                                    && !TextUtils.isEmpty(exitVoucher.getText().toString())
                                    ) {
                                Truck truc = Truck.getTruckByTruckNumber(truckNumberString);
                                if (Double.parseDouble(weight.getText().toString()) <= Double.parseDouble(truc.getTruck_total_capacity())) {
                                    if (getActivity() != null)
                                        save.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

                                    Truck truck = Truck.getTruckByTruckNumber(truckNumberString);
                                    User user = User.getUserByName(driverNumberString);
                                    JSONArray jsonArray = new JSONArray();
                                    JSONObject commentObject = new JSONObject();

                                    String eventId = null;
                                    if (exit == null)
                                        eventId = UUID.randomUUID().toString();
                                    else eventId = exit.getEvent_Id();
                                    String truckVolume = "0";
                                    if (emptyEntered.isChecked()) {
                                        truckVolume = "0";
                                    } else {

                                        truckVolume = "1";
                                    }
                                    ActiveAndroid.beginTransaction();
                                    try {

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
                                        Comment comment;
                                        String timeone = "";
                                        if (!timeSetFirst)
                                            timeone = Methods.getCurrentTimeStampApi();
                                        else timeone = Methods.getCurrentTimeStampApi(hour, minute);
                                        if (exit == null) {

                                            comment = new Comment(event.getComment_Id(), timeone, commentString,
                                                    SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_ID_FOR_LOG),
                                                    event.getController_user_Id(), "");
                                        } else {

                                            comment = new Comment(exit.getComment_Id(), timeone, commentString,
                                                    SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_ID_FOR_LOG),
                                                    event.getController_user_Id(), "");
                                        }
                                        comment.save();
                                        ActiveAndroid.setTransactionSuccessful();
                                    } finally {
                                        ActiveAndroid.endTransaction();
                                    }
                                    JSONObject myNewEntrance = new JSONObject();
                                    JSONArray myNewEntranceArray = new JSONArray();
                                    ActiveAndroid.beginTransaction();
                                    try {
                                        String eventTime = event.getTimestamp();
                                        if (firstEdited) {
                                            if (!timeSetFirst)
                                                eventTime = Methods.getCurrentTimeStampApi();
                                            else
                                                eventTime = Methods.getCurrentTimeStampApi(hour, minute);
                                        }
                                        String eventUserTime = "";
                                        if(event!=null)
                                            eventUserTime = event.getTime_user();
                                        else eventUserTime = Methods.getCurrentTimeStampApi();
                                        Event eventNew = new Event(event.getEvent_Id(), eventTime, user.getUser_Id(),
                                                truck.getTruck_Id(),
                                                SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_ID_FOR_LOG),
                                                voucherNumberString,
                                                truckVolume, event.getComment_Id(), "0000-00-00 00:00:00", false,
                                                truck.getTruck_number(), user.getFirst_name_ar() + " " + user.getLast_name_ar(),
                                                truck.getTruck_total_capacity(), user.getUsername(),
                                                "0", eventUserTime);
                                        eventNew.save();
                                        List<Event> events = Event.getAll();
                                        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                                        String listString = gson.toJson(
                                                events,
                                                new TypeToken<ArrayList<Event>>() {
                                                }.getType());

                                        try {
                                            myNewEntranceArray = new JSONArray(listString);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        ActiveAndroid.setTransactionSuccessful();
                                    } finally {
                                        ActiveAndroid.endTransaction();
                                    }
                                    ActiveAndroid.beginTransaction();
                                    try {

                                        if (exit != null) {

                                            List<Exit> exits = Exit.getAll();
                                            for (int i = 0; i < exits.size(); i++) {
                                                if (exits.get(i).getEvent_Id().equals(exit.getEvent_Id()))
                                                    Exit.delete(Exit.class, exits.get(i).getId());

                                            }
                                        }
                                        ActiveAndroid.setTransactionSuccessful();
                                    } finally {
                                        ActiveAndroid.endTransaction();
                                    }

                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        if (exit == null)
                                            jsonObject.put("Event_Id", eventId);
                                        else jsonObject.put("Event_Id", exit.getEvent_Id());
                                        jsonObject.put("Driver_user_Id", event.getDriver_user_Id());
                                        jsonObject.put("Controller_user_Id", event.getController_user_Id());
                                        jsonObject.put("Truck_Id", event.getTruck_Id());
                                        jsonObject.put("EntryExit_voucher_number", exitVoucher.getText().toString());
                                        jsonObject.put("Destination", destinations.get(spinner.getSelectedItemPosition()).getDestination_Id());
                                        jsonObject.put("Truck_volume", weight.getText().toString());
                                        jsonObject.put("Comment_Id", event.getComment_Id());
                                        jsonObject.put("Entrance_Id", event.getEvent_Id());
                                        jsonObject.put("Payment_voucher_number", exitName.getText().toString());
                                        if (exit != null && !secondEdited)
                                            jsonObject.put("Timestamp", exit.getTimestamp());
                                        else {
                                            if (!timeSetSecond)
                                                jsonObject.put("Timestamp", Methods.getCurrentTimeStampApi());
                                            else
                                                jsonObject.put("Timestamp", Methods.getCurrentTimeStampApi(secondHour, secondMinute));
                                        }
                                        if (exit != null)
                                            jsonObject.put("Time_user", exit.getTime_user());
                                        else
                                            jsonObject.put("Time_user", Methods.getCurrentTimeStampApi());

                                        if (exit != null)
                                            commentObject.put("Comment_Id", exit.getComment_Id());
                                        else commentObject.put("Comment_Id", event.getComment_Id());

                                        commentObject.put("Content", commentString);
                                        commentObject.put("User_Id_by", SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_ID_FOR_LOG));
                                        commentObject.put("User_Id_about", user.getUser_Id());
                                        commentObject.put("Timestamp", event.getTimestamp());

                                        if (exit != null)
                                            jsonObject.put("Data_edited", "1");
                                        else jsonObject.put("Data_edited", "0");


                                        jsonArray.put(jsonObject);
//                        +destinations.get(spinner.getSelectedItemPosition()).getId()
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Exit exits = new Exit();
                                    if (exit != null)
                                        exits.setEvent_Id(exit.getEvent_Id());
                                    else
                                        exits.setEvent_Id(eventId);
                                    if (exit != null && !secondEdited)
                                        exits.setTimestamp(exit.getTimestamp());
                                    else {
                                        if(!timeSetSecond)
                                        exits.setTimestamp(Methods.getCurrentTimeStampApi());
                                        else
                                        exits.setTimestamp(Methods.getCurrentTimeStampApi(secondHour, secondMinute));
                                    }
                                    exits.setDriver_user_Id(event.getDriver_user_Id());
                                    exits.setController_user_Id(event.getController_user_Id());
                                    exits.setDestination(destinations.get(spinner.getSelectedItemPosition()).getDestination_Id());
                                    exits.setTruck_Id(event.getTruck_Id());
                                    if (exit != null)
                                        exits.setComment_Id(exit.getComment_Id());
                                    else
                                        exits.setComment_Id(event.getComment_Id());


                                    exits.setPayment_voucher_number(payment_voucher);
                                    exits.setEntryExit_voucher_number(exitVoucher.getText().toString());
                                    exits.setEntrance_Id(event.getEvent_Id());
                                    exits.setTruck_volume(weight.getText().toString());


                                    if (exit != null) {
                                        exits.setData_edited("1");
                                        exits.setTime_user(exit.getTime_user());
                                    }
                                    else {exits.setData_edited("0");
                                    exits.setTime_user(Methods.getCurrentTimeStampApi());}
                                    exits.save();

                                    logger.write_line("Save for entry with id " + exits.getEntrance_Id() + " at time: " + Methods.getCurrentTimeStampApi().toString());
                                    logger.write_line("Exit id: " + exits.getId());
                                    logger.write_line("Exit Voucher Number: " + exits.getEntryExit_voucher_number());
                                    logger.write_line("Payment Voucher Number: " + exits.getPayment_voucher_number());
                                    logger.write_line("TextView value: " + payment_voucher);
                                    logger.write_line("\n\n");

                                    JSONArray commentArray = new JSONArray();
                                    commentArray.put(commentObject);

                                    if (isNetworkAvailable()) {
                                        try {
                                            addComment(jsonArray, eventId, commentArray, event.getComment_Id(), myNewEntranceArray);
                                            commentArray = new JSONArray();
                                            commentArray.put(event);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "لا يوجد شبكة، لم تحمل المعلومات", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                    clicked = true;
                                } else
                                    setError(getString(R.string.volume_exceeded));

                            } else
                                setError(getString(R.string.missing));
                        }
                } else ((MainActivity) getActivity()).displayAutomaticTimeEnable();
                break;
            case R.id.next:
                int next_pos = positions_in_wrapper() + 1;
                log.d("emad-d", String.valueOf(next_pos));

                if(next_pos < events.CampEntrance.size()) {
                    log.d("emad-d", "in");
                    Event next_event = events.CampEntrance.get(next_pos);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack(ExitTruckFragment.class.getSimpleName(),
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction().add(R.id.content_layout, ExitTruckFragment.newInstance(
                            next_event, null, null, null, events
                            ), ExitTruckFragment.class.getSimpleName()
                    ).addToBackStack(ExitTruckFragment.class.getSimpleName()).commitAllowingStateLoss();
                }

                break;

            case R.id.prev:
                int prev_pos = positions_in_wrapper() - 1;
                if(prev_pos >= 0) {
                    Event prev_event = events.CampEntrance.get(prev_pos);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack(ExitTruckFragment.class.getSimpleName(),
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction().add(R.id.content_layout, ExitTruckFragment.newInstance(
                            prev_event, null, null, null, events
                            ), ExitTruckFragment.class.getSimpleName()
                    ).addToBackStack(ExitTruckFragment.class.getSimpleName()).commitAllowingStateLoss();
                }

                break;


            default:
                break;
        }
    }

    private void showAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
        builder.setMessage(getString(R.string.data_not_saved))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.proceed), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        getActivity().getSupportFragmentManager().popBackStack();
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

    private int positions_in_wrapper() {
        for(int i = 0; i < events.CampEntrance.size(); i++) {
            if(events.CampEntrance.get(i).getId() == event.getId()) {
                return i;
            }
        }
        return -1;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void addComment(final JSONArray log, final String eventId, final JSONArray commentArray, final String commentId,
                            final JSONArray entranceArray) throws JSONException, UnsupportedEncodingException {
        if (getActivity() != null)
            ((MainActivity) getActivity()).progressBar.setVisibility(View.VISIBLE);

        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(getActivity());
        RequestModel requestModel = requestDataProvider.addComment(
                SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_NUMBER),
                SecurePreferences.getInstance(getActivity()).getString(Parameters.PASSWORD),
                commentArray
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

                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
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

                try {
                    addTruckEntry(entranceArray, commentId, log);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null)
                    ((MainActivity) getActivity()).progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void addTruckEntry(JSONArray jsonObject, final String commentId, final JSONArray entranceArray) throws JSONException, UnsupportedEncodingException {
        if (getActivity() != null)
            ((MainActivity) getActivity()).progressBar.setVisibility(View.VISIBLE);
        myHttpClient = new MyHttpClient();
        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.put(jsonObject);
        RequestDataProvider requestDataProvider = new RequestDataProvider(getActivity());
        RequestModel requestModel = requestDataProvider.addTruckEntry(
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
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
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

                try {
                    sendData(entranceArray);
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
    private void sendData(final JSONArray log) throws JSONException, UnsupportedEncodingException {
        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(getActivity());
        RequestModel requestModel = requestDataProvider.addTruckExit(
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
//                for (int i = 0; i < eventId.size(); i++) {
//                    Event.delete(Event.class, eventId.get(i).getId());
//                }


                if (getActivity() != null) {

                    Toast.makeText(getActivity(), "تم تحميل المعلومات", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                ((MainActivity) getActivity()).progressBar.setVisibility(View.INVISIBLE);

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

    private void setError(String message) {
        if (getActivity() != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        if (firstSelectedPicker) {
            hour = i;
            minute = i1;
            firstEdited = true;
            timeSetFirst = true;
            entryTime.setText(getString(R.string.enter_time) + " " + Methods.splitAfter(Methods.getCurrentTimeStamp(i, i1)));
        } else {
            secondHour = i;
            secondMinute = i1;
            secondEdited = true;
            timeSetSecond = true;
            exitTime.setText(Methods.splitAfter(Methods.getCurrentTimeStamp(i, i1)));
        }
    }
}
