package com.hcmus_csc13009.nowwakealarm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.models.Alarm;
import com.hcmus_csc13009.nowwakealarm.utils.AlarmUtils;

import java.util.Collections;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    final private LayoutInflater layoutInflater;
    private List<Alarm> alarms;

    public AlarmAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public AlarmAdapter(Context context, List<Alarm> alarms) {
        this(context);
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
                holder.repeat.setText("Once off");
            }
            holder.isStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    currentAlarm.setEnable(b);
                }
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
