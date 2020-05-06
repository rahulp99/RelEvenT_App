package com.example.relevent;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {

    private ArrayList<String> eventList = new ArrayList<>();
    private ArrayList<String> venueList = new ArrayList<>();
    private ArrayList<String> dateList = new ArrayList<>();
    private Activity mActivity;
    private OnNoteListener mOnNoteListener;

    public DataAdapter(MainActivity activity, ArrayList<String> eventList, ArrayList<String> venueList, ArrayList<String> dateList, OnNoteListener onNoteListener) {
        this.mActivity = activity;
        this.eventList = eventList;
        this.venueList = venueList;
        this.dateList = dateList;
        this.mOnNoteListener = onNoteListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView eventName, eventVenue, eventDate;

        OnNoteListener onNoteListener;

        public MyViewHolder(View view, OnNoteListener onNoteListener) {
            super(view);
            eventName = (TextView) view.findViewById(R.id.event_title);
            eventVenue = (TextView) view.findViewById(R.id.event_venue);
            eventDate = (TextView) view.findViewById(R.id.event_date);
            this.onNoteListener = onNoteListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_data, parent, false);

        return new MyViewHolder(itemView, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.eventName.setText(eventList.get(position));
        holder.eventVenue.setText(venueList.get(position));
        holder.eventDate.setText(dateList.get(position));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
