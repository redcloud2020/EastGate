package com.uni.easygate.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uni.easygate.R;
import com.uni.easygate.datalayer.models.Event;
import com.uni.easygate.datalayer.models.EventWrapper;
import com.uni.easygate.datalayer.models.Exit;
import com.uni.easygate.datalayer.models.Truck;
import com.uni.easygate.datalayer.models.User;
import com.uni.easygate.utilities.Methods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by sammy on 4/6/2017.
 */

public class TruckAdapter extends RecyclerView.Adapter<TruckAdapter.ViewHolder> {
    final  Drawable arrowDownGrey;
    final  Drawable arrowUpGrey;
    final  Drawable arrowDownBlack;
    final  Drawable arrowUpBlack;
    private EventWrapper myChildren;
    private Context context;
    ArrayList<User> myUsers;
    ArrayList<Truck> myTrucks;
    private onClick onClick;
    private boolean sorted = false;
    private int sorting = -1;
    public void setOnClick(TruckAdapter.onClick onClick) {
        this.onClick = onClick;
    }
    private onLongClick onLongClick;

    public void setOnLongClick(TruckAdapter.onLongClick onLongClick) {
        this.onLongClick = onLongClick;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView truck;
        public TextView driver;
        public TextView enter;
        public TextView exit;
        public TextView date;

        private ImageView upEnter, upExit, downEnter, downExit;

        public ViewHolder(View v) {
            super(v);

            truck = (TextView) v.findViewById(R.id.truck);
            driver = (TextView) v.findViewById(R.id.driver);
            enter = (TextView) v.findViewById(R.id.enter);
            exit = (TextView) v.findViewById(R.id.exit);
            upEnter = (ImageView) v.findViewById(R.id.up_enter);
            upExit = (ImageView) v.findViewById(R.id.up_exit);
            downEnter = (ImageView) v.findViewById(R.id.down_enter);
            downExit = (ImageView) v.findViewById(R.id.down_exit);


        }

    }

    public TruckAdapter(Context context, EventWrapper myDataset, ArrayList<User> myUsers, ArrayList<Truck> myTrucks) {
        this.myChildren = myDataset;
        this.context = context;
        this.myUsers = myUsers;
        this.myTrucks = myTrucks;


          arrowDownGrey = ContextCompat.getDrawable(context, R.drawable.arrow_down_grey).mutate();
          arrowUpGrey = ContextCompat.getDrawable(context, R.drawable.arrow_up_grey).mutate();
          arrowDownBlack = ContextCompat.getDrawable(context, R.drawable.arrow_down_black).mutate();
          arrowUpBlack = ContextCompat.getDrawable(context, R.drawable.arrow_up_black).mutate();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.card_view_expenses, parent, false);
                .inflate(R.layout.row_truck, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(TextUtils.isEmpty(holder.exit.getText().toString()))
                    if(onLongClick!=null)
                        onLongClick.onLongClick(myChildren.getCampEntrance().get(position-1));
                return true;
            }
        });
        if (position == 0) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            holder.truck.setText(context.getString(R.string.truck));
            holder.driver.setText(context.getString(R.string.driver));
            holder.enter.setText(context.getString(R.string.enter));
            holder.exit.setText(context.getString(R.string.exit));


            holder.truck.setTypeface(holder.truck.getTypeface(), Typeface.BOLD);
            holder.driver.setTypeface(holder.driver.getTypeface(), Typeface.BOLD);
            holder.enter.setTypeface(holder.enter.getTypeface(), Typeface.BOLD);
            holder.exit.setTypeface(holder.exit.getTypeface(), Typeface.BOLD);


            holder.downEnter.setVisibility(View.VISIBLE);
            holder.downExit.setVisibility(View.VISIBLE);
            holder.upEnter.setVisibility(View.VISIBLE);
            holder.upExit.setVisibility(View.VISIBLE);

            if(sorting==0) {
                holder.downEnter.setImageDrawable(arrowDownBlack);
                holder.downExit.setImageDrawable(arrowDownGrey);
                holder.upEnter.setImageDrawable(arrowUpGrey);
                holder.upExit.setImageDrawable(arrowUpGrey);
            }else if(sorting==1){
                holder.downEnter.setImageDrawable(arrowDownBlack);
                holder.downExit.setImageDrawable(arrowDownGrey);
                holder.upEnter.setImageDrawable(arrowUpGrey);
                holder.upExit.setImageDrawable(arrowUpGrey);
            }else if (sorting ==2){
                holder.downEnter.setImageDrawable(arrowDownGrey);
                holder.downExit.setImageDrawable(arrowDownGrey);
                holder.upEnter.setImageDrawable(arrowUpBlack);
                holder.upExit.setImageDrawable(arrowUpGrey);
            }else if (sorting ==3){
                holder.downEnter.setImageDrawable(arrowDownGrey);
                holder.downExit.setImageDrawable(arrowDownBlack);
                holder.upEnter.setImageDrawable(arrowUpGrey);
                holder.upExit.setImageDrawable(arrowUpGrey);
            }else if(sorting ==4){
                holder.downEnter.setImageDrawable(arrowDownGrey);
                holder.downExit.setImageDrawable(arrowDownGrey);
                holder.upEnter.setImageDrawable(arrowUpGrey);
                holder.upExit.setImageDrawable(arrowUpBlack);
            }else {
                holder.downEnter.setImageDrawable(arrowDownGrey);
                holder.downExit.setImageDrawable(arrowDownGrey);
                holder.upEnter.setImageDrawable(arrowUpGrey);
                holder.upExit.setImageDrawable(arrowUpGrey);
            }



            holder.downEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sorted=true;
                    sorting=1;
//                    Collections.sort(myChildren.getCampEntrance(), new ChairHeightComparator());


                    for (int m = 0; m < myChildren.getCampEntrance().size(); m++) {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                        holder.exit.setText("");
                        holder.driver.setText("");
                        holder.truck.setText("");
                    }
//                    sorted = true;

                    TableComparator tableComparator = new TableComparator();
                    Collections.sort(myChildren.getCampEntrance(),tableComparator);
                    notifyDataSetChanged();


                }
            });
            holder.upEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sorted=true;
                    sorting=2;
                    for (int m = 0; m < myChildren.getCampEntrance().size(); m++) {
                        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                        holder.exit.setText("");

                    }
                    TableComparator tableComparator = new TableComparator();
                    Collections.sort(myChildren.getCampEntrance(),tableComparator);
                    Collections.reverse(myChildren.getCampEntrance());



                    notifyDataSetChanged();
                }
            });

            holder.downExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sorted=true;
                    sorting=3;
                    if (!myChildren.getCampExit().isEmpty()) {

                        TableComparator tableComparator = new TableComparator();
                        Collections.sort(myChildren.getCampEntrance(),tableComparator);
                        Collections.reverse(myChildren.getCampEntrance());

                        ChairHeightComparator chairHeightComparator = new ChairHeightComparator();
                        Collections.sort(myChildren.getCampEntrance(),chairHeightComparator);

                    }
                    notifyDataSetChanged();
                }
            });
            holder.upExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sorted=true;
                    sorting=4;
                    if (!myChildren.getCampExit().isEmpty()) {
                        TableComparator tableComparator = new TableComparator();
                        Collections.sort(myChildren.getCampEntrance(),tableComparator);
                        Collections.reverse(myChildren.getCampEntrance());

                        ChairHeightComparator chairHeightComparator = new ChairHeightComparator();
                        Collections.sort(myChildren.getCampEntrance(), chairHeightComparator);
                        Collections.reverse(myChildren.getCampEntrance());
                    }
                    notifyDataSetChanged();
                }
            });
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        } else {

            holder.truck.setTypeface(holder.truck.getTypeface(), Typeface.NORMAL);
            holder.driver.setTypeface(holder.driver.getTypeface(), Typeface.NORMAL);
            holder.enter.setTypeface(holder.enter.getTypeface(), Typeface.NORMAL);
            holder.exit.setTypeface(holder.exit.getTypeface(), Typeface.NORMAL);


            holder.downEnter.setVisibility(View.INVISIBLE);
            holder.downExit.setVisibility(View.INVISIBLE);
            holder.upEnter.setVisibility(View.INVISIBLE);
            holder.upExit.setVisibility(View.INVISIBLE);
            if (myChildren.getCampEntrance().get(position-1).getExited()!=null && myChildren.getCampEntrance().get(position-1).getExited()) {
                holder.exit.setText(Methods.splitAfter(myChildren.getCampEntrance().get(position-1).getExitTime())
                        .substring(0, Methods.splitAfter(myChildren.getCampEntrance().get(position-1).getExitTime()).length() - 3));
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_grey));

            } else {
                holder.exit.setText("");
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }

                holder.truck.setText(myChildren.getCampEntrance().get(position-1).getTruckNumber());

//            for(int i =0; i<myTrucks.size();i++) {
//                if(myTrucks.get(i).getTruck_Id().equals(myChildren.getCampEntrance().get(position-1).getTruck_Id())) {
//                    holder.truck.setText(myTrucks.get(position - 1).getTruck_number() + "");
//                    break;
//                }
//                else holder.truck.setText("");
//            }


                holder.driver.setText(myChildren.getCampEntrance().get(position-1).getDriverName());
            if (myChildren.getCampEntrance().get(position - 1) != null) {
                holder.enter.setText(Methods.splitAfter(myChildren.getCampEntrance().get(position - 1).getTimestamp())
                        .substring(0, Methods.splitAfter(myChildren.getCampEntrance().get(position - 1).getTimestamp()).length() - 3));
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(position!=0) {
                        Exit exit = null;
                        if (!myChildren.getCampExit().isEmpty())
                            for (int j = 0; j < myChildren.getCampExit().size(); j++) {
                                if(myChildren.getCampExit().get(j).getEntrance_Id()!=null)
                                if (myChildren.getCampExit().get(j).getEntrance_Id().equals(
                                        myChildren.getCampEntrance().get(position - 1).getEvent_Id()
                                )) {
                                    exit = myChildren.getCampExit().get(j);
                                    break;
                                }
                            }

                        if (onClick != null)
                            onClick.OnClick(myChildren.getCampEntrance().get(position - 1),
                                    exit, myTrucks.get(position - 1), myUsers.get(position - 1));
                    }
                }
            });

        }
    }

    class ChairHeightComparator implements Comparator<Event> {
        public int compare(Event s1, Event s2) {
            if(s1.getExitTime()==null ||s2.getExitTime()==null)
               return -1;
            else
            if (Methods.convertToMilli(s1.getExitTime()) > Methods.convertToMilli(s2.getExitTime()))
                return -1;
            else if (Methods.convertToMilli(s1.getExitTime()) < Methods.convertToMilli(s2.getExitTime()))
                return +1;
            return 0;
        }
    }
    class TableComparator implements Comparator<Event> {
        public int compare(Event s1, Event s2) {
            if(s1.getTimestamp()==null ||s2.getTimestamp()==null)
                return -1;
            else
            if (Methods.convertToMilli(s1.getTimestamp()) > Methods.convertToMilli(s2.getTimestamp()))
                return -1;
            else if (Methods.convertToMilli(s1.getTimestamp()) < Methods.convertToMilli(s2.getTimestamp()))
                return +1;
            return 0;
        }
    }

    public void sort(){
        sorted=true;
        sorting=3;
        if (!myChildren.getCampExit().isEmpty()) {



            TableComparator tableComparator = new TableComparator();
            Collections.sort(myChildren.getCampEntrance(),tableComparator);
            Collections.reverse(myChildren.getCampEntrance());
            ChairHeightComparator chairHeightComparator = new ChairHeightComparator();
            Collections.sort(myChildren.getCampEntrance(),chairHeightComparator);

        }
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return myChildren.getCampEntrance().size() + 1;
    }

    public interface onClick {
        void OnClick(Event event, Exit exit, Truck truck, User user);
    }
    public interface onLongClick {
        void onLongClick(Event event);
    }
    private void sortEnterUp() {

    }
}

