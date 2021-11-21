package com.hcmus_csc13009.nowwakealarm.ui;

import android.app.SearchManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.adapter.AlarmAdapter;
import com.hcmus_csc13009.nowwakealarm.models.Alarm;
import com.hcmus_csc13009.nowwakealarm.utils.DatabaseHelper;
import com.hcmus_csc13009.nowwakealarm.viewmodel.AlarmViewModel;

public class AlarmsFragment extends Fragment implements DatabaseHelper {
    private AlarmViewModel alarmViewModel = null;
    private AlarmAdapter adapter;
    private SearchView searchView;
    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new AlarmAdapter(this.getContext(), this);
        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        alarmViewModel.getAllAlarms().observe(this, adapter::setAlarms);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_alarms, container, false);


        recyclerView = view.findViewById(R.id.listAlarmRecyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        addSwipAndDragItem();

        searchView = view.findViewById(R.id.search_recipe);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }


    private void addSwipAndDragItem() {
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                                                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        adapter.moveAlarm(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        return true;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Alarm alarm = adapter.getAlarmAt(position);
                        Toast.makeText(AlarmsFragment.this.getContext(), "Deleted " + alarm.getTitle(), Toast.LENGTH_SHORT).show();
                        onDelete(alarm);
                    }
                }
        );
        helper.attachToRecyclerView(this.recyclerView);
    }

    @Override
    public void onToggle(Alarm alarm) {
        // switch turn on/off alarm
        if (alarm.isEnable()) {
            alarm.setEnable(false);
        } else {
            alarm.setEnable(true);
        }
        // TODO: DB
        alarmViewModel.update(alarm);
    }

    @Override
    public void onDelete(Alarm alarm) {
        // TODO: DB
        alarmViewModel.delete(alarm);
    }

    public void onUpdate(Alarm alarm) {
        alarmViewModel.update(alarm);
    }

    // TODO - Binh add cancel alarm service listener
}
