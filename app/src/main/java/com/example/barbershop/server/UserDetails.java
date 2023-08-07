package com.example.barbershop.server;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.example.barbershop.sharedData.DataHolderClass;


public class UserDetails
{
    public static void removeUserAns(String response)
    {
        DataHolderClass.mainActivity.doWhenServerResponse();
        if (response.equals("V"))
        {
            DataHolderClass.mainActivity.signOut();
            Toast.makeText(DataHolderClass.mainActivity, "המשתמש נמחק", Toast.LENGTH_SHORT).show();
            DataHolderClass.loginActivity.writeKeyToMemory(null);
        }
        else if (response.equals("permission problem"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
        }
        else // (response.equals("cmd failed") || response.equals("connection failed") || response.equals(ServerRequest.ERROR_RESPONSE))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
        }

    }

    public static void logOutFromAllDevicesAns(String response)
    {
        DataHolderClass.mainActivity.doWhenServerResponse();
        if(response.equals("V"))
        {
            SharedPreferences sharedPreferences = DataHolderClass.mainActivity.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(DataHolderClass.userMail,null);
            editor.commit();
            DataHolderClass.mainActivity.signOut();
        }
        else if (response.equals("permission problem"))
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
        else // (response.equals("cmd failed") || response.equals("connection failed") || response.equals(ServerRequest.ERROR_RESPONSE))
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
    }

    public static void updatePhoneAns(String response,String newPhone)
    {
        DataHolderClass.mainActivity.doWhenServerResponse();
        if (response.equals("V"))
        {
            DataHolderClass.userPhone = newPhone;
            DataHolderClass.mainActivity.updatePhone();
            Toast.makeText(DataHolderClass.mainActivity, "מספר הפלאפון שונה בהצלחה", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (response.equals("permission problem"))
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
        else // (response.equals("cmd failed") || response.equals("connection failed") || response.equals(ServerRequest.ERROR_RESPONSE))
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
        DataHolderClass.mainActivity.updatePhone(newPhone); // call again to the change phone alert dialog
    }

    public static void updateNameAns(String response,String newName)
    {
        DataHolderClass.mainActivity.doWhenServerResponse();
        if(response.equals("V"))
        {
            DataHolderClass.userName = newName;
            DataHolderClass.mainActivity.changeActivityTitle();
            Toast.makeText(DataHolderClass.mainActivity,"השם שונה בהצלחה" , Toast.LENGTH_SHORT).show();
            return;
        }
        else if (response.equals("permission problem"))
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
        else // (response.equals("cmd failed") || response.equals("connection failed") || response.equals(ServerRequest.ERROR_RESPONSE))
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
        DataHolderClass.mainActivity.updateName(newName); // call again to the change name alert dialog
    }

}
