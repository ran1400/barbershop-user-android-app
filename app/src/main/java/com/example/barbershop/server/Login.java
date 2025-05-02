package com.example.barbershop.server;

import android.util.Log;
import android.widget.Toast;

import com.example.barbershop.sharedData.DataHolderClass;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;


public class Login
{

    public static void getUserDetailsHelper(JSONObject userDetails)
    {
        DataHolderClass.userMail = userDetails.optString("userMail",null);
        DataHolderClass.userName = userDetails.optString("userName",null);
        DataHolderClass.userPhone = userDetails.optString("userPhone",null);
        DataHolderClass.msg = userDetails.optString("managerMsg",null);
        if (DataHolderClass.msg.equals("NULL"))
            DataHolderClass.msg = null;
        DataHolderClass.userReservedQueue = userDetails.optString("userQueue",null);
        firebaseMessagingSubscribe(DataHolderClass.userMail);
        DataHolderClass.loginActivity.moveToMainActivity();
    }

    public static void firebaseMessagingSubscribe(String userMail)
    {
        FirebaseMessaging.getInstance().subscribeToTopic(userMail.substring(0,userMail.indexOf('@')));
        FirebaseMessaging.getInstance().subscribeToTopic("managerMsgs");
    }

    public static void getUserDetailsAns(String response)
    {
        if (response == ServerRequest.ERROR_RESPONSE)
        {
            Toast.makeText(DataHolderClass.loginActivity, "גישה לנתונים נכשלה, נסה שוב", Toast.LENGTH_LONG).show();
            DataHolderClass.loginActivity.signOut();
            DataHolderClass.loginActivity.makeLoginBtnVisible();
            DataHolderClass.loginActivity.doWhenServerResponse();
            return;
        }
        String blockedUser = null;
        String error;
        JSONObject userDetails = null;
        try
        {
            userDetails = new JSONObject(response);
            error = userDetails.optString("error",null);
            blockedUser = userDetails.optString("blockedUser",null);
        }
        catch (Exception e){error = "yes";}
        if (error != null)
        {
            Toast.makeText(DataHolderClass.loginActivity, "גישה לנתונים נכשלה, התחבר שוב", Toast.LENGTH_LONG).show();
            DataHolderClass.loginActivity.signOut();
            DataHolderClass.loginActivity.makeLoginBtnVisible();
            DataHolderClass.loginActivity.doWhenServerResponse();
        }
        else if (blockedUser != null)
        {
            Toast.makeText(DataHolderClass.loginActivity, "המשתמש נחסם על ידי המנהל", Toast.LENGTH_LONG).show();
            DataHolderClass.loginActivity.makeLoginBtnVisible();
            DataHolderClass.loginActivity.signOut();
            DataHolderClass.loginActivity.doWhenServerResponse();
        }
        else
            getUserDetailsHelper(userDetails);
    }

    public static void checkGoogleLogin(String response) //called when tmpSecretKey is null
    {
        if (response == ServerRequest.ERROR_RESPONSE)
        {
            Toast.makeText(DataHolderClass.loginActivity, "גישה לנתונים נכשלה, התחבר שוב", Toast.LENGTH_LONG).show();
            DataHolderClass.loginActivity.signOut();
            DataHolderClass.loginActivity.makeLoginBtnVisible();
            DataHolderClass.loginActivity.doWhenServerResponse();
            return;
        }
        String error,newUser = null,secretKey = null,blockedUser = null;
        JSONObject userDetails = null;
        try
        {
            userDetails = new JSONObject(response);
            error = userDetails.optString("error",null);
            newUser = userDetails.optString("newUser",null);
            secretKey = userDetails.optString("userSecretKey",null);
            blockedUser = userDetails.optString("blockedUser",null);
        }
        catch (Exception e) {error = "yes";}
        if (error != null)
        {
            Toast.makeText(DataHolderClass.loginActivity, "גישה לנתונים נכשלה, התחבר שוב", Toast.LENGTH_LONG).show();
            DataHolderClass.loginActivity.signOut();
            DataHolderClass.loginActivity.makeLoginBtnVisible();
            DataHolderClass.loginActivity.doWhenServerResponse();
            return;
        }
        if (newUser != null) //its new user
        {
            DataHolderClass.loginActivity.firstLogin();
            DataHolderClass.loginActivity.doWhenServerResponse();
            return;
        }
        if (blockedUser != null)
        {
            Toast.makeText(DataHolderClass.loginActivity, "המשתמש נחסם על ידי המנהל", Toast.LENGTH_LONG).show();
            DataHolderClass.loginActivity.signOut();
            DataHolderClass.loginActivity.makeLoginBtnVisible();
            DataHolderClass.loginActivity.doWhenServerResponse();
            return;
        }
        DataHolderClass.userSecretKey = secretKey;
        DataHolderClass.loginActivity.writeKeyToMemory(secretKey);
        getUserDetailsHelper(userDetails);
    }


    public static void firstLoginAns(String response) //response is the secret key
    {
        if (response.equals(ServerRequest.ERROR_RESPONSE))
        {
            DataHolderClass.loginActivity.doWhenServerResponse();
            Toast.makeText(DataHolderClass.loginActivity, "פניה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
            return;
        }
        String error,secretKey = null;
        JSONObject userDetails = null;
        try
        {
            userDetails = new JSONObject(response);
            error = userDetails.optString("error",null);
            if (error == null)
                secretKey = userDetails.getString("secretKey");
        }
        catch (Exception e){error = "yes";}
        if (error != null)
        {
            if (error.equals("google check failed"))
            {
                Toast.makeText(DataHolderClass.loginActivity, "אימות משתמש נכשל - נסה שוב", Toast.LENGTH_SHORT).show();
                DataHolderClass.loginActivity.dismissFirstLoginFragment();
                DataHolderClass.loginActivity.makeLoginBtnVisible();
                DataHolderClass.loginActivity.signOut();
                return;
            }
            DataHolderClass.loginActivity.doWhenServerResponse();
            Toast.makeText(DataHolderClass.loginActivity, "פניה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(DataHolderClass.loginActivity, "נרשמת בהצלחה", Toast.LENGTH_SHORT).show();
            DataHolderClass.loginActivity.writeKeyToMemory(secretKey);
            DataHolderClass.userSecretKey = secretKey;
            getUserDetailsHelper(userDetails);
        }
    }
}
