package com.hcmus_csc13009.nowwakealarm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.models.Alarm;
import com.hcmus_csc13009.nowwakealarm.ui.AddAlarmActivity;
import com.hcmus_csc13009.nowwakealarm.utils.AlarmUtils;
import com.hcmus_csc13009.nowwakealarm.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> implements Filterable {
    final static public String ALARM_OBJECT_DATA = "ALARM_OBJECT_DATA";

    final private LayoutInflater layoutInflater;
    private List<Alarm> alarms;
    private List<Alarm> filteredDataAlarm;
    private DatabaseHelper databaseHelper;

    public AlarmAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public AlarmAdapter(Context context, DatabaseHelper databaseHelper) {
        this(context);
        this.databaseHelper = databaseHelper;
    }

    public AlarmAdapter(Context context, DatabaseHelper databaseHelper, List<Alarm> alarms) {
        this(context, databaseHelper);
        this.alarms = alarms;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        if (alarms != null) {
            Alarm currentAlarm = alarms.get(position);
            holder.isStart.setChecked(currentAlarm.isEnable());
            holder.title.setText(currentAlarm.getTitle());
            holder.time.setText(AlarmUtils.getHourMinute(currentAlarm.getTime()));
            if (currentAlarm.isRepeatMode()) {
                holder.repeat.setText(AlarmUtils.getDaysInWeek(currentAlarm.getDaysInWeek()));
            } else {
                holder.repeat.setText(R.string.non_recur_alarm);
            }
            holder.isStart.setOnCheckedChangeListener((compoundButton, b) -> {
                if (compoundButton.isShown() || compoundButton.isPressed()) {
                    databaseHelper.onToggle(currentAlarm);
                }
            });
            holder.itemView.setOnClickListener(view -> {
                Context context = view.getContext();
                Intent intent = new Intent(context, AddAlarmActivity.class);
                intent.putExtra(ALARM_OBJECT_DATA, currentAlarm);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return alarms == null ? 0 : alarms.size();
    }

    public Alarm getAlarmAt(int position) {
        return alarms.get(position);
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    public void moveAlarm(int fromPosition, int toPosition) {
        Collections.swap(alarms, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
//        databaseHelper.onUpdate(alarms.get(fromPosition));
//        databaseHelper.onUpdate(alarms.get(toPosition));
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {

        //Run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Alarm> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filteredList = alarms;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Alarm alarm : alarms) {
                    if (alarm.getTitle().toLowerCase().contains(filterPattern)){
                        filteredList.add(alarm);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        //Run on ui thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            alarms.clear();
            alarms.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        final private TextView time;
        final private TextView title;
        final private TextView repeat;
        final private SwitchCompat isStart;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.timeAlarmItem);
            title = itemView.findViewById(R.id.titleAlarmItem);
            repeat = itemView.findViewById(R.id.recurDayAlarmItem);
            isStart = itemView.findViewById(R.id.startAlarmItem);
        }
    }
}
