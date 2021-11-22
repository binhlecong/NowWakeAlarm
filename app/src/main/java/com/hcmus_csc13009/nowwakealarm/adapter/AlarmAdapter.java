package com.hcmus_csc13009.nowwakealarm.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import java.util.Collections;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> implements Filterable {
    final static public String ALARM_OBJECT_DATA = "ALARM_OBJECT_DATA";

    final private LayoutInflater layoutInflater;
    private DatabaseHelper databaseHelper;

    private List<Alarm> alarms;
    private List<Alarm> alarmsOld;

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
            Log.i("@@@ ID = ", "" + currentAlarm.getID());
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
        this.alarmsOld = this.alarms;
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

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchStr = charSequence.toString().toLowerCase();
                if (searchStr.isEmpty()) {
                    alarms = alarmsOld;
                } else {
                    List<Alarm> list = new ArrayList<>();
                    for (Alarm alarm : alarmsOld) {
                        if (alarm.getTitle().toLowerCase().contains(searchStr) ||
                            alarm.getDescription().toLowerCase().contains(searchStr)) {
                            list.add(alarm);
                        }
                    }
                    alarms = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = alarms;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                alarms = (List<Alarm>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


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
