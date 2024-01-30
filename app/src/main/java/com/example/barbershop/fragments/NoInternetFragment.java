package com.example.barbershop.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.barbershop.R;
import com.example.barbershop.sharedData.DataHolderClass;

public class NoInternetFragment extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_no_internet, container, false);
        Button tryAgainBtn = view.findViewById(R.id.tryAgainBtn);
        tryAgainBtn.setOnClickListener(this::onClick);
        return view;
    }

    public void onClick(View view)
    {
        DataHolderClass.loginActivity.restartWithoutSignOut();
    }
}