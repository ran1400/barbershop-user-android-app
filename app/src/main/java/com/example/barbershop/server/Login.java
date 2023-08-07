package com.example.barbershop.server;


import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.barbershop.sharedData.DataHolderClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;

public class Login
{

    static public void checkGoogleSignInAns(String response,String userMail)
    {
        if (response.equals("X"))
        {
            Toast.makeText(DataHolderClass.loginActivity, "התחברות נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
            DataHolderClass.loginActivity.restart();
        }
        else if (response.equals("connection failed") || response.equals("cmd failed") || response.equals(ServerRequest.ERROR_RESPONSE))
        {
            Toast.makeText(DataHolderClass.loginActivity, "פניה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
            DataHolderClass.loginActivity.restart();
        }
        else //response is secretKey
        {
            DataHolderClass.loginActivity.writeKeyToMemory(response);
            DataHolderClass.userSecretKey = response;
            ServerRequest serverRequest = new ServerRequest((String serverResponse)->Login.getUserDetailsAns(serverResponse,userMail));
            serverRequest.getUserDetails(userMail,response,DataHolderClass.loginActivity);
        }
    }


    public static void getUserDetailsAns(String response,String userMail)
    {
        if (response.equals("connection failed") || response.equals(ServerRequest.ERROR_RESPONSE) || response.equals("cmd failed"))
        {
            DataHolderClass.loginActivity.showNoInternetFragment();
            DataHolderClass.loginActivity.doWhenServerResponse();
            DataHolderClass.loginActivity.makeLoginBtnInvisible();
        }
        else if (response.equals("permission problem"))
        {
            Toast.makeText(DataHolderClass.loginActivity, "בעיית הרשאות, התחבר שוב", Toast.LENGTH_LONG).show();
            DataHolderClass.loginActivity.restart();
        }
        else if (response.equals("block user"))
        {
            Toast.makeText(DataHolderClass.loginActivity, "המשתמש נחסם על ידי המנהל", Toast.LENGTH_LONG).show();
            DataHolderClass.loginActivity.restart();
        }
        else
        {
            String responseSplit[];
            responseSplit = response.split("<");
            DataHolderClass.userMail = userMail;
            DataHolderClass.userName = responseSplit[0];
            DataHolderClass.userPhone = responseSplit[1];
            DataHolderClass.msg = responseSplit[2];
            DataHolderClass.userReservedQueue = responseSplit[3];
            FirebaseMessaging.getInstance().subscribeToTopic(userMail.split("@")[0]);
            DataHolderClass.loginActivity.moveToMainActivity();
        }
    }

    public static void checkIfNewUserAns(String response) //called when tmpSecretKey is null
    {
        if (response.equals("yes"))
        {
            DataHolderClass.loginActivity.firstLogin();
            DataHolderClass.loginActivity.doWhenServerResponse();
        }
        else if(response.equals("no"))
        {
            String userMail = DataHolderClass.loginActivity.userMail;
            ServerRequest serverRequest = new ServerRequest((String serverResponse) -> Login.checkGoogleSignInAns(serverResponse,userMail));
            serverRequest.checkGoogleLogIn(DataHolderClass.loginActivity.idToken,DataHolderClass.loginActivity);
        }
        else // (response.equals("connection failed") || response.equals(ServerRequest.ERROR_RESPONSE) || response.equals("cmd failed"))
        {
            DataHolderClass.loginActivity.showNoInternetFragment();
            DataHolderClass.loginActivity.doWhenServerResponse();
        }

    }

    public static void firstLoginAns(String response,String userMail) //response is the secret key
    {
        if (response.equals("X"))
            DataHolderClass.loginActivity.restart();
        else if (response.equals("connection failed") || response.equals(ServerRequest.ERROR_RESPONSE) || response.equals("cmd failed"))
        {
            DataHolderClass.loginActivity.doWhenServerResponse();
            Toast.makeText(DataHolderClass.loginActivity, "פניה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(DataHolderClass.loginActivity, "נרשמת בהצלחה", Toast.LENGTH_SHORT).show();
            DataHolderClass.loginActivity.writeKeyToMemory(response);
            DataHolderClass.userSecretKey = response;
            ServerRequest serverRequest = new ServerRequest((String serverResponse)->Login.getUserDetailsAns(serverResponse,userMail));
            serverRequest.getUserDetails(userMail,response,DataHolderClass.loginActivity);
        }
    }
}