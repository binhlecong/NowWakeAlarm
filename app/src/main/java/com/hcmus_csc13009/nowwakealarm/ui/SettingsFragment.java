package com.hcmus_csc13009.nowwakealarm.ui;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.hcmus_csc13009.nowwakealarm.databinding.FragmentSettingsBinding;
import com.hcmus_csc13009.nowwakealarm.utils.SettingConstant;

public class SettingsFragment extends Fragment {
    final static private int REQUEST_FOR_RINGTONE = 5;

    private FragmentSettingsBinding fragmentSettingsBinding;
    private String tone;
    private Ringtone ringtone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // get data from database

        fragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false);
        View view = fragmentSettingsBinding.getRoot();
        tone = SettingConstant.getDefaultRingtone(getActivity().getApplicationContext());
        if (tone == null)
            tone = RingtoneManager.getActualDefaultRingtoneUri(this.getContext(), RingtoneManager.TYPE_ALARM).toString();
        ringtone = RingtoneManager.getRingtone(getContext(), Uri.parse(tone));
        fragmentSettingsBinding.setToneNameAlarm.setText(ringtone.getTitle(this.getContext()));
        fragmentSettingsBinding.alarmSoundCard.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(tone));
            startActivityForResult(intent, REQUEST_FOR_RINGTONE);
        });
        fragmentSettingsBinding.radius.setText(String.valueOf(SettingConstant.getNearbyRange(getActivity().getApplicationContext())));
        fragmentSettingsBinding.setRadius.setOnClickListener(v -> {
            String radius = fragmentSettingsBinding.radius.getText().toString();
            if (radius.isEmpty()){
                return;
            }
//                SettingConstant.NEARBY_RANGE = Float.parseFloat(radius);
            SettingConstant.updateNearbyRange(getActivity().getApplicationContext(), Float.parseFloat(radius));
            Toast.makeText(getContext().getApplicationContext(), radius, Toast.LENGTH_SHORT).show();

        });

        fragmentSettingsBinding.sendEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + fragmentSettingsBinding.email.getText().toString()));
            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        fragmentSettingsBinding.callPhone.setOnClickListener(v -> {
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
        });
        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_FOR_RINGTONE) {
            if (data == null) return;
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                ringtone = RingtoneManager.getRingtone(this.getContext(), uri);
                tone = uri.toString();
                fragmentSettingsBinding.setToneNameAlarm.setText(ringtone.getTitle(this.getContext()));
                SettingConstant.updateDefaultRingtone(getActivity().getApplicationContext(), tone);
            }
        }
    }
}
