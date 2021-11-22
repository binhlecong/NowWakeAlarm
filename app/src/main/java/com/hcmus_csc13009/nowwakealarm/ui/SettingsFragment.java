package com.hcmus_csc13009.nowwakealarm.ui;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.hcmus_csc13009.nowwakealarm.R;
import com.hcmus_csc13009.nowwakealarm.databinding.ActivityAddAlarmBinding;
import com.hcmus_csc13009.nowwakealarm.databinding.FragmentSettingsBinding;
import com.hcmus_csc13009.nowwakealarm.utils.SettingConstant;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding fragmentSettingsBinding;
    private String tone;
    private Ringtone ringtone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false);
        View view = fragmentSettingsBinding.getRoot();
        tone = RingtoneManager.getActualDefaultRingtoneUri(this.getContext(), RingtoneManager.TYPE_ALARM).toString();
        ringtone = RingtoneManager.getRingtone(getContext(), Uri.parse(tone));
        fragmentSettingsBinding.alarmSoundCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) Uri.parse(tone));
                startActivityForResult(intent, 5);
            }
        });
        fragmentSettingsBinding.setRadius.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String radius = fragmentSettingsBinding.radius.getText().toString();
                if (radius.isEmpty()){
                    return;
                }
                SettingConstant.NEARBY_RANGE = Float.parseFloat(radius);
                Toast.makeText(getContext().getApplicationContext(), radius, Toast.LENGTH_SHORT).show();

            }
        });

        fragmentSettingsBinding.sendEmail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + fragmentSettingsBinding.email.getText().toString()));
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        fragmentSettingsBinding.callPhone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String number = ("tel:" + fragmentSettingsBinding.phoneNumber.getText().toString());
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(number));

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            1);

                } else {
                    try {
                        startActivity(callIntent);
                    } catch(SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return view;

    }
}
