package com.example.barbershop.server;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.example.barbershop.sharedData.DataHolderClass;

import org.json.JSONObject;


public class UserDetails
{
    public static void removeUserAns(String response)
    {

        DataHolderClass.mainActivity.doWhenServerResponse();
        if (response == ServerRequest.ERROR_RESPONSE)
        {
            Toast.makeText(DataHolderClass.mainActivity, "התחברות לשרת נכשלה", Toast.LENGTH_SHORT).show();
            return;
        }
        String error;
        try
        {
            JSONObject userDetails = new JSONObject(response);
            error = userDetails.optString("error","yes");
        }
        catch (Exception e){error = "yes";}
        if(error.equals("no"))
        {
            DataHolderClass.mainActivity.signOut();
            Toast.makeText(DataHolderClass.mainActivity, "המשתמש נמחק", Toast.LENGTH_SHORT).show();
            DataHolderClass.loginActivity.writeKeyToMemory(null);
        }
        else if (error.equals("permission problem"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.signOut();
        }
        else // error is : yes || start with cmd failed || sql connection failed
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
    }

    public static void logOutFromAllDevicesAns(String response)
    {
        DataHolderClass.mainActivity.doWhenServerResponse();
        if (response == ServerRequest.ERROR_RESPONSE)
        {
            Toast.makeText(DataHolderClass.mainActivity, "התחברות לשרת נכשלה", Toast.LENGTH_SHORT).show();
            return;
        }
        String error;
        try
        {
            JSONObject userDetails = new JSONObject(response);
            error = userDetails.optString("error","yes");
        }
        catch (Exception e){error = "yes";}
        if(error.equals("no"))
            DataHolderClass.mainActivity.signOut();
        else if (error.equals("permission problem"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.signOut();
        }
        else // error is : yes || cmd failed || sql connection failed
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
    }

    public static void updatePhoneAns(String response,String userInput)
    {
        DataHolderClass.mainActivity.doWhenServerResponse();
        if (response == ServerRequest.ERROR_RESPONSE)
        {
            Toast.makeText(DataHolderClass.mainActivity, "התחברות לשרת נכשלה", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.updatePhoneBtn(userInput); // call again to the change phone alert dialog
            return;
        }
        String error,newPhoneNumber = null;
        try
        {
            JSONObject serverResponse = new JSONObject(response);
            error = serverResponse.optString("error",null);
            if (error == null)
                newPhoneNumber = serverResponse.getString("userPhone");
        }
        catch (Exception e){error = "yes";}
        if (error == null)
        {
            DataHolderClass.userPhone = newPhoneNumber;
            Toast.makeText(DataHolderClass.mainActivity, "מספר הפלאפון שונה בהצלחה", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.updatePhoneTextView();
        }
        else if (error.equals("permission problem"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.signOut();
        }
        else // error is : yes || cmd failed || sql connection failed
        {
            DataHolderClass.mainActivity.updatePhoneBtn(userInput); // call again to the change phone alert dialog
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
        }
    }

    public static void updateNameAns(String response,String userInput)
    {
        DataHolderClass.mainActivity.doWhenServerResponse();
        if (response == ServerRequest.ERROR_RESPONSE)
        {
            DataHolderClass.mainActivity.updateNameBtn(userInput); // call again to the change name alert dialog
            Toast.makeText(DataHolderClass.mainActivity, "התחברות לשרת נכשלה", Toast.LENGTH_SHORT).show();
            return;
        }
        String error,newName = null;
        try
        {
            JSONObject serverResponse = new JSONObject(response);
            error = serverResponse.optString("error",null);
            if (error == null)
                newName = serverResponse.getString("userName");
        }
        catch (Exception e){error = "yes";}
        if (error == null)
        {
            DataHolderClass.userName = newName;
            Toast.makeText(DataHolderClass.mainActivity , "השם שונה בהצלחה", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.changeActivityTitleToDefault();
        }
        else if (error.equals("permission problem"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.signOut();
        }
        else // error is : yes || cmd failed || sql connection failed
        {
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.updateNameBtn(userInput); // call again to the change name alert dialog
        }
    }
}
