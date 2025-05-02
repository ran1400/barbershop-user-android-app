package com.example.barbershop.fragments;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.barbershop.R;
import com.example.barbershop.server.Login;
import com.example.barbershop.server.ServerRequest;
import com.example.barbershop.sharedData.DataHolderClass;


public class FirstLoginFragment extends Fragment implements View.OnClickListener
{
    private EditText userNameEditText ;
    private EditText phoneEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_first_login, container, false);
        userNameEditText = view.findViewById(R.id.userName);
        phoneEditText = view.findViewById(R.id.phone);
        Button loginButton = view.findViewById(R.id.okBtn);
        Button backButton = view.findViewById(R.id.backBtn);
        loginButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        if (DataHolderClass.loginActivity.account.getGivenName() != null) // if not null suggest name to user in first login
        {
            String nameSuggestion = DataHolderClass.loginActivity.account.getGivenName();
            if (DataHolderClass.loginActivity.account.getFamilyName() != null)
                nameSuggestion += " " + DataHolderClass.loginActivity.account.getFamilyName();
            userNameEditText.setText(nameSuggestion);
        }
        return view;
    }


    @Override
    public void onClick(View view)
    {
      if (view.getId() == R.id.okBtn)
          okBtn();
      else // (view.getId() == R.id.backBtn)
          backBtn();
    }

    private void backBtn()
    {
        DataHolderClass.loginActivity.dismissFirstLoginFragment();
        DataHolderClass.loginActivity.makeLoginBtnVisible();
    }

    private void okBtn()
    {
        String userName = userNameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        if ( userName.isEmpty() )
            Toast.makeText(DataHolderClass.loginActivity.getApplicationContext(), "הכנס שם תקין", Toast.LENGTH_SHORT).show();
        else  if (phone.isEmpty() )
            Toast.makeText(DataHolderClass.loginActivity.getApplicationContext(), "הכנס מספר פלאפון תקין", Toast.LENGTH_SHORT).show();
        else if (userName.contains("<") || userName.contains("'"))
            Toast.makeText(DataHolderClass.loginActivity.getApplicationContext(), "תו לא חוקי בשם", Toast.LENGTH_SHORT).show();
        else
        {
            DataHolderClass.loginActivity.doBeforeServerRequest();
            ServerRequest serverRequest = new ServerRequest((String response) -> Login.firstLoginAns(response));
            serverRequest.firstLogin(userName,phone,DataHolderClass.loginActivity.idToken,DataHolderClass.loginActivity.userMail,DataHolderClass.loginActivity);
        }
    }

}