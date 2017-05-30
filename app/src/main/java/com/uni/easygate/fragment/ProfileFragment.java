package com.uni.easygate.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uni.easygate.R;
import com.uni.easygate.config.Parameters;
import com.uni.easygate.security.SecurePreferences;
import com.uni.easygate.ui.MainActivity;

/**
 * Created by sammy on 3/2/2017.
 */

public class ProfileFragment extends Fragment {


    private TextView username;
//    private TextView truckname;
    private TextView name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_profile, container, false);
        if(getActivity()!=null)
            ((MainActivity)getActivity()).setTitleToolbar("الملف الشخصي");
        setHasOptionsMenu(false);

        username = (TextView) layout.findViewById(R.id.username);
//        truckname = (TextView) layout.findViewById(R.id.truck_name);
        name = (TextView) layout.findViewById(R.id.name);

        username.setText(SecurePreferences.getInstance(getActivity()).getString(Parameters.USER_NUMBER));
        name.setText(SecurePreferences.getInstance(getActivity()).getString(Parameters.USERNAME));
//        truckname.setText(SecurePreferences.getInstance(getActivity()).getString(Parameters.VEHICLE_NUMBER));

        return layout;
    }
}