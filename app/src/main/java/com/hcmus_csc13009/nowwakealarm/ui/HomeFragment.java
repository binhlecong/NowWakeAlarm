package com.hcmus_csc13009.nowwakealarm.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hcmus_csc13009.nowwakealarm.R;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView appName = (TextView) view.findViewById(R.id.appName);
        Typeface face = Typeface.createFromAsset(getContext().getAssets(),
                "coconutz.ttf");
        appName.setTypeface(face);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
