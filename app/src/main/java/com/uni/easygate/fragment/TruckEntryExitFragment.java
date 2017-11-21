package com.uni.easygate.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.google.gson.reflect.TypeToken;
import com.uni.easygate.R;
import com.uni.easygate.adapter.TruckAdapter;
import com.uni.easygate.config.Parameters;
import com.uni.easygate.datalayer.models.Event;
import com.uni.easygate.datalayer.models.EventWrapper;
import com.uni.easygate.datalayer.models.Exit;
import com.uni.easygate.datalayer.models.Truck;
import com.uni.easygate.datalayer.models.User;
import com.uni.easygate.datalayer.server.GetRequestModel;
import com.uni.easygate.datalayer.server.MyHttpClient;
import com.uni.easygate.datalayer.server.RequestDataProvider;
import com.uni.easygate.datalayer.server.ServerResponseHandler;
import com.uni.easygate.security.SecurePreferences;
import com.uni.easygate.ui.MainActivity;
import com.uni.easygate.utilities.Methods;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik;

/**
 * Created by sammy on 2/28/2017.
 */

public class TruckEntryExitFragment extends Fragment implements View.OnClickListener {

    private MyHttpClient myHttpClient;

    private RecyclerView recyclerView;
    private Button add;
    private TextView header;
    private int entranceCount =0;
    public static TruckEntryExitFragment newInstance(Event event) {
        TruckEntryExitFragment fragment = new TruckEntryExitFragment();
        Bundle b = new Bundle();
        b.putParcelable(Parameters.TYPE_MEASUREMENT, event);
        fragment.setArguments(b);
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_truck_exit_entry, container, false);
        if(getActivity()!=null)
            ((MainActivity)getActivity()).setTitleToolbar("جدول الصهاريج");
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        add = (Button) layout.findViewById(R.id.add);
        header = (TextView) layout.findViewById(R.id.header);

        add.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(isNetworkAvailable() && !TextUtils.isEmpty(SecurePreferences.getInstance(getActivity()).getString(Parameters.GET)))
        try {
            getTrucksEntered();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        else getStored();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    private void getTrucksEntered() throws JSONException, UnsupportedEncodingException {
        if(getActivity()!=null)
        ((MainActivity) getActivity()).progressBar.setVisibility(View.VISIBLE);
        myHttpClient = new MyHttpClient();
        RequestDataProvider requestDataProvider = new RequestDataProvider(getActivity());
        GetRequestModel requestModel = requestDataProvider.getRemainingTrucks(
                SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_NUMBER),
                SecurePreferences.getInstance(getActivity()).getString(Parameters.PASSWORD),
                Methods.getCurrentTimeStampApi()
        );
        Type type = new TypeToken<EventWrapper>() {
        }.getType();

        myHttpClient.get(getActivity(), requestModel.getUrl(), requestModel.getParams(), new ServerResponseHandler<EventWrapper>(type) {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onConnectivityError(String message) {
                if(getActivity()!=null)
                ((MainActivity) getActivity()).progressBar.setVisibility(View.GONE);

                getStored();
            }

            @Override
            public void onDataError(String message) {
                if(getActivity()!=null)
                ((MainActivity) getActivity()).progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onServerFailure(String message) {
                if(getActivity()!=null)
                ((MainActivity) getActivity()).progressBar.setVisibility(View.GONE);

            }


            @Override
            public void onServerSuccess(EventWrapper data) {
                if (data != null) {
                   insert(data);
                }else {
                    if(getActivity()!=null)
                    ((MainActivity) getActivity()).progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void getStored(){
        ArrayList<Event> getAllEvents  = (ArrayList<Event>) Event.getAll();
        ArrayList<Exit> getAllExited  = (ArrayList<Exit>) Exit.getAll();
        EventWrapper data = null;
        if(getAllEvents!=null && !getAllEvents.isEmpty()) {
             data = new EventWrapper(getAllEvents, getAllExited);
        }



        ArrayList<User> myUsers = new ArrayList<>();
        if(data!=null && data.getCampEntrance()!=null && !data.getCampEntrance().isEmpty())
            for(int i =0; i<data.getCampEntrance().size(); i++){
                User user = User.getUserById(data.getCampEntrance().get(i).getDriver_user_Id());
                if(user == null)
                myUsers.add(user);
            }

        if(data!=null && data.getCampExit()!=null && !data.getCampExit().isEmpty())
            for(int i =0; i<data.getCampExit().size(); i++){
                User user = User.getUserById(data.getCampExit().get(i).getDriver_user_Id());
                if(user == null)
                myUsers.add(user);
            }

        ArrayList<Truck> myTrucks = new ArrayList<>();
        if(data!=null && data.getCampEntrance()!=null && !data.getCampEntrance().isEmpty())
            for(int i =0; i<data.getCampEntrance().size(); i++){
            Truck truck = Truck.getTruckById(data.getCampEntrance().get(i).getTruck_Id());
                if(truck!=null)
            myTrucks.add(truck);
        }
        if(data!=null && data.getCampExit()!=null && !data.getCampExit().isEmpty())
            for(int i =0; i<data.getCampExit().size(); i++){
            Truck truck = Truck.getTruckById(data.getCampExit().get(i).getTruck_Id());
                if(truck!=null)
            myTrucks.add(truck);
        }
        if(data!=null && data.getCampEntrance()!=null && !data.getCampEntrance().isEmpty())
        for(int i =0; i<data.getCampEntrance().size();i++) {
            for (int j = 0; j < data.getCampExit().size(); j++)
                if (data.getCampEntrance().get(i).getEvent_Id().equals(data.getCampExit().get(j).getEntrance_Id())) {
                    data.getCampEntrance().get(i).setExited(true);
                    data.getCampEntrance().get(i).setExitTime(data.getCampExit().get(j).getTimestamp());
                    break;
                } else {

                    data.getCampEntrance().get(i).setExited(false);
                    data.getCampEntrance().get(i).setExitTime("0000-00-00 00:00:00");
                }

            for(int m =0; m<myUsers.size(); m++){
                if(data.getCampEntrance().get(i).getDriver_user_Id().equals(myUsers.get(m).getUser_Id()))
                    data.getCampEntrance().get(i).setDriverName(myUsers.get(m).getFirst_name_ar()+" "+myUsers.get(m).getLast_name_ar());
                data.getCampEntrance().get(i).setDriverUserNumber(myUsers.get(m).getUsername()+"");
            }

            for(int n=0; n<myTrucks.size(); n++){
                if(data.getCampEntrance().get(i).getTruck_Id().equals(myTrucks.get(n).getTruck_Id()))
                    data.getCampEntrance().get(i).setTruckNumber(myTrucks.get(n).getTruck_number());
                data.getCampEntrance().get(i).setTruckTotalVolume(myTrucks.get(n).getTruck_total_capacity());

            }
        }
        int size = 0;
        if(data!=null && data.getCampEntrance()!=null && !data.getCampEntrance().isEmpty()){
            header.setText(Html.fromHtml("<b>" + getString(R.string.inside_camp) + ":</b> " + data.getCampEntrance().size() + " &#160;  " + "<b>" + getString(R.string.outside) + ":</b> " +
                   "0"));
        }
        if(data!=null && data.getCampEntrance()!=null && !data.getCampEntrance().isEmpty() &&
                data.getCampExit()!=null && !data.getCampExit().isEmpty()) {
             size = data.getCampEntrance().size() - data.getCampExit().size();
            header.setText(Html.fromHtml("<b>" + getString(R.string.inside_camp) + ":</b> " + size + " &#160;  " + "<b>" + getString(R.string.outside) + ":</b> " +
                    data.getCampExit().size()));
        }

        if(data!=null && data.getCampEntrance()!=null && !data.getCampEntrance().isEmpty()) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            final TruckAdapter truckAdapter = new TruckAdapter(getActivity(), data, myUsers, myTrucks);
            recyclerView.setAdapter(truckAdapter);
            recyclerView.setLayoutManager(linearLayoutManager);
            truckAdapter.setOnLongClick(new TruckAdapter.onLongClick() {
                @Override
                public void onLongClick(Event event) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().add(R.id.content_layout, AddTruckFragment.newInstance(
                            event
                            ), AddTruckFragment.class.getSimpleName()
                    ).addToBackStack(AddTruckFragment.class.getSimpleName()).commitAllowingStateLoss();
                }
            });
            truckAdapter.setOnClick(new TruckAdapter.onClick() {
                @Override
                public void OnClick(Event event, Exit exit, Truck truck, User user) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().add(R.id.content_layout, ExitTruckFragment.newInstance(
                            event, truck, user, exit, truckAdapter.myChildren
                            ), ExitTruckFragment.class.getSimpleName()
                    ).addToBackStack(ExitTruckFragment.class.getSimpleName()).commitAllowingStateLoss();
                }
            });
            truckAdapter.sort();
        }
        if(getActivity()!=null)
        ((MainActivity) getActivity()).progressBar.setVisibility(View.GONE);

    }
    private void insert(EventWrapper newData){

//        List<Event> entrance = Event.getAll();
//        for(int i =0; i<entrance.size(); i++){
//            Event.delete(Event.class, entrance.get(i).getId());
//        }
//
//
//        List<Exit> exits = Exit.getAll();
//        for(int i =0; i<exits.size(); i++){
//            Exit.delete(Exit.class, exits.get(i).getId());
//        }
        SecurePreferences.getInstance(getActivity()).put(Parameters.GET, "");
        ActiveAndroid.beginTransaction();
        try {
            if(newData.getCampEntrance()!=null && !newData.getCampEntrance().isEmpty())
            for(int i =0; i<newData.getCampEntrance().size(); i++) {
                Event event = new Event(newData.getCampEntrance().get(i).getEvent_Id(),
                        newData.getCampEntrance().get(i).getTimestamp(),newData.getCampEntrance().get(i).getDriver_user_Id(),
                        newData.getCampEntrance().get(i).getTruck_Id(),
                        newData.getCampEntrance().get(i).getController_user_Id(),
                        newData.getCampEntrance().get(i).getEntryExit_voucher_number(),
                        newData.getCampEntrance().get(i).getTruck_volume(),
                        newData.getCampEntrance().get(i).getComment_Id(), "0000-00-00 00:00:00", false,
                       "", "",
                        "", "", "0", newData.getCampEntrance().get(i).getTime_user());
                event.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }


        ActiveAndroid.beginTransaction();
        try {
            if(newData.getCampExit()!=null && !newData.getCampExit().isEmpty())
                for(int i =0; i<newData.getCampExit().size(); i++) {
                    Exit exit = new Exit(newData.getCampExit().get(i).getEvent_Id(),
                            newData.getCampExit().get(i).getTimestamp(),
                            newData.getCampExit().get(i).getDriver_user_Id(),
                            newData.getCampExit().get(i).getController_user_Id(),
                            newData.getCampExit().get(i).getDestination()
                            , newData.getCampExit().get(i).getTruck_Id(),
                            newData.getCampExit().get(i).getComment_Id(), newData.getCampExit().get(i).getPayment_voucher_number(),
                            newData.getCampExit().get(i).getEntryExit_voucher_number(),
                            newData.getCampExit().get(i).getEntrance_Id(),newData.getCampExit().get(i).getTruck_volume(),
                            "0", newData.getCampExit().get(i).getTime_user());
                    exit.save();
                }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        if(getActivity()!=null)
        ((MainActivity) getActivity()).progressBar.setVisibility(View.GONE);

        ArrayList<Event> getAllEvents  = (ArrayList<Event>) Event.getAll();
        ArrayList<Exit> getAllExited  = (ArrayList<Exit>) Exit.getAll();
        EventWrapper data = null;
        if(getAllEvents!=null && !getAllEvents.isEmpty()) {
            data = new EventWrapper(getAllEvents, getAllExited);
        }

        ArrayList<User> myUsers = new ArrayList<>();
        for(int i =0; i<data.getCampEntrance().size(); i++){
            User user = User.getUserById(data.getCampEntrance().get(i).getDriver_user_Id());
            myUsers.add(user);
        }
        for(int i =0; i<data.getCampExit().size(); i++){
            User user = User.getUserById(data.getCampExit().get(i).getDriver_user_Id());
            myUsers.add(user);
        }
        ArrayList<Truck> entranceTrucks = new ArrayList<>();
        ArrayList<Truck> newTrucks = new ArrayList<>();
        List<Truck> allTrucks = Truck.getAll();
        for(int i =0; i<data.getCampEntrance().size(); i++){
           for(int j =0; j<allTrucks.size(); j++){
               if(allTrucks.get(j).getTruck_Id().equals(data.getCampEntrance().get(i).getTruck_Id()))
                   entranceTrucks.add(i, allTrucks.get(j));
           }

        }
        if(data.getCampExit()!=null && !data.getCampExit().isEmpty())
        for(int i =0; i<data.getCampExit().size(); i++){
            for(int j =0; j<allTrucks.size(); j++) {
                if (allTrucks.get(j).getTruck_Id().equals(data.getCampExit().get(i).getTruck_Id()))
                    newTrucks.add(allTrucks.get(j));
            }

        }
        ArrayList<Truck> myTrucks = new ArrayList<>(entranceTrucks.size()+newTrucks.size());
        myTrucks.addAll(entranceTrucks);
        myTrucks.addAll(newTrucks);
        for(int i =0; i<data.getCampEntrance().size();i++) {
            for (int j = 0; j < data.getCampExit().size(); j++)
                if (data.getCampEntrance().get(i).getEvent_Id().equals(data.getCampExit().get(j).getEntrance_Id())) {
                    data.getCampEntrance().get(i).setExited(true);
                    data.getCampEntrance().get(i).setExitTime(data.getCampExit().get(j).getTimestamp());
                    break;
                } else {

                    data.getCampEntrance().get(i).setExited(false);
                    data.getCampEntrance().get(i).setExitTime("0000-00-00 00:00:00");
                }

            for(int m =0; m<myUsers.size(); m++){
                if(data.getCampEntrance().get(i).getDriver_user_Id().equals(myUsers.get(m).getUser_Id()))
                    data.getCampEntrance().get(i).setDriverName(myUsers.get(m).getFirst_name_ar()+" "+myUsers.get(m).getLast_name_ar());
                    data.getCampEntrance().get(i).setDriverUserNumber(myUsers.get(m).getUsername()+"");
            }

            for(int n=0; n<myTrucks.size(); n++){
                if(data.getCampEntrance().get(i).getTruck_Id().equals(myTrucks.get(n).getTruck_Id()))
                    data.getCampEntrance().get(i).setTruckNumber(myTrucks.get(n).getTruck_number());
                    data.getCampEntrance().get(i).setTruckTotalVolume(myTrucks.get(n).getTruck_total_capacity());

            }
        }


        int size = data.getCampEntrance().size()-data.getCampExit().size();
        header.setText(Html.fromHtml("<b>"+getString(R.string.inside_camp)+":</b> "+size+"&#160;"+"<b>"+getString(R.string.outside)+":</b> "+
        data.getCampExit().size()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        final TruckAdapter truckAdapter = new TruckAdapter(getActivity(), data, myUsers, myTrucks);
        recyclerView.setAdapter(truckAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        truckAdapter.setOnClick(new TruckAdapter.onClick() {
            @Override
            public void OnClick(Event event, Exit exit, Truck truck, User user) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.content_layout, ExitTruckFragment.newInstance(
                        event, truck, user, exit, truckAdapter.myChildren
                ), ExitTruckFragment.class.getSimpleName()
                ).addToBackStack(ExitTruckFragment.class.getSimpleName()).commitAllowingStateLoss();
            }
        });
        truckAdapter.setOnLongClick(new TruckAdapter.onLongClick() {
            @Override
            public void onLongClick(Event event) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.content_layout, AddTruckFragment.newInstance(
                        event
                        ), AddTruckFragment.class.getSimpleName()
                ).addToBackStack(AddTruckFragment.class.getSimpleName()).commitAllowingStateLoss();
            }
        });
        truckAdapter.sort();
        if(getActivity()!=null)
        ((MainActivity) getActivity()).progressBar.setVisibility(View.GONE);
    }

    private void setError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add:
                if(getActivity()!=null)
                if (Methods.isAutomaticTimeEnabled(getActivity())) {
                    ((MainActivity) getActivity()).setTitle("سجل دخول");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.content_layout,new AddTruckFragment(),
                        AddTruckFragment.class.getSimpleName()).addToBackStack(AddTruckFragment.class
                .getSimpleName()).commitAllowingStateLoss();
                } else ((MainActivity) getActivity()).displayAutomaticTimeEnable();
                break;
            default:
                break;
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if(getActivity()!=null && ((MainActivity)getActivity()).progressBar!=null)
//        ((MainActivity) getActivity()).progressBar.setVisibility(View.INVISIBLE);
//
//    }
}
