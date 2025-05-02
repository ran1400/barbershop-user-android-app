package com.example.barbershop.server;


import android.util.Log;
import android.widget.Toast;
import com.example.barbershop.sharedData.DataHolderClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class Queues
{
    public static void addQueueAns(String response)
    {
        DataHolderClass.mainActivity.doWhenServerResponse();
        if (response == ServerRequest.ERROR_RESPONSE)
        {
            Toast.makeText(DataHolderClass.mainActivity, "התחברות לשרת נכשלה", Toast.LENGTH_SHORT).show();
            return;
        }
        String error,newQueue = null;
        try
        {
            JSONObject serverResponse = new JSONObject(response);
            error = serverResponse.optString("error",null);
            if (error == null)
                newQueue = serverResponse.getString("newQueue");
        }
        catch (Exception e){error = "yes";}
        if(error == null)
        {
            DataHolderClass.userReservedQueue = newQueue;
            Toast.makeText(DataHolderClass.mainActivity, "התור נקבע", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.closeChooseQueueWindows();
            DataHolderClass.mainActivity.showUserQueueHandel();
        }
        else if (error.equals("permission problem"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.signOut();
        }
        else if (error.equals("manager block the system"))
            Toast.makeText(DataHolderClass.mainActivity, "המנהל חסם את ההאופציה לקביעת תורים - נסה מאוחר יותר", Toast.LENGTH_SHORT).show();
        else // error is : yes || cmd failed || sql connection failed
        {
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.closeChooseQueueWindows();
        }
    }

    public static void updateQueueAns(String response)
    {
        DataHolderClass.mainActivity.doWhenServerResponse();
        if (response == ServerRequest.ERROR_RESPONSE)
        {
            Toast.makeText(DataHolderClass.mainActivity, "התחברות לשרת נכשלה", Toast.LENGTH_SHORT).show();
            return;
        }
        String error,newQueue = null;
        try
        {
            JSONObject serverResponse = new JSONObject(response);
            error = serverResponse.optString("error",null);
            if (error == null)
                newQueue = serverResponse.getString("newQueue");
        }
        catch (Exception e){error = "yes";}
        if(error == null)
        {
            DataHolderClass.userReservedQueue = newQueue;
            Toast.makeText(DataHolderClass.mainActivity, "התור נקבע", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.closeChooseQueueWindows();
            DataHolderClass.mainActivity.showUserQueueHandel();
        }
        else if (error.equals("permission problem"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.signOut();
        }
        else if (error.equals("manager block the system"))
            Toast.makeText(DataHolderClass.mainActivity, "המנהל חסם את ההאופציה לקביעת תורים - נסה מאוחר יותר", Toast.LENGTH_SHORT).show();
        else // error is : yes || cmd failed || sql connection failed
        {
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.closeChooseQueueWindows();
        }
    }

    public static void deleteQueueAns(String response)
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
            JSONObject serverResponse = new JSONObject(response);
            error = serverResponse.optString("error","yes");
        }
        catch (Exception e){error = "yes";}
        if(error.equals("no"))
        {
            Toast.makeText(DataHolderClass.mainActivity , "התור בוטל", Toast.LENGTH_SHORT).show();
            DataHolderClass.userReservedQueue = null;
            DataHolderClass.mainActivity.showUserQueueHandel();
        }
        else if (error.equals("permission problem"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.signOut();
        }
        else // error is : yes || cmd failed || sql connection failed
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
    }


    public static void getEmptyQueuesAns(String response)
    {
        DataHolderClass.mainActivity.doWhenServerResponse();
        if (response == ServerRequest.ERROR_RESPONSE)
        {
            Toast.makeText(DataHolderClass.mainActivity, "התחברות לשרת נכשלה", Toast.LENGTH_SHORT).show();
            return;
        }
        String error;
        JSONObject serverResponse;
        JSONArray queues = null;
        try
        {
            serverResponse = new JSONObject(response);
            error = serverResponse.optString("error", null);
            if (error == null)
                queues = serverResponse.getJSONArray("queues");
        }
        catch (Exception e) {error = "yes";}
        if (error == null)
        {
            if (getEmptyQueuesAnsHelper(queues) == false)
                Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
        }
        else if (error.equals("permission problem"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.signOut();
        }
        else if (error.equals("manager block the system"))
            Toast.makeText(DataHolderClass.mainActivity, "המנהל חסם את ההאופציה לקביעת תורים - נסה מאוחר יותר", Toast.LENGTH_SHORT).show();
        else // error is : yes || cmd failed || sql connection failed
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
    }

    private static boolean getEmptyQueuesAnsHelper(JSONArray queues)
    {
        int queuesAmount = queues.length();
        if (queuesAmount == 0)
        {
            Toast.makeText(DataHolderClass.mainActivity, "אין תורים פנויים", Toast.LENGTH_SHORT).show();
            return true;
        }
        LinkedList<String> dates = new LinkedList<>();
        LinkedList<LinkedList<String>> hoursList = new LinkedList<>();
        try
        {
            LinkedList<String> dateHours = new LinkedList<>();
            String prevDate = queues.getString(0).substring(0,10);
            dates.add(prevDate);
            for (int i = 0 ; i < queuesAmount ; i++)
            {
                String crntQueue = queues.getString(i);
                String crntDate = crntQueue.substring(0,10);
                String crntHour = crntQueue.substring(11,16);
                if (crntDate.equals(prevDate))
                    dateHours.add(crntHour);
                else
                {
                    dates.add(crntDate);
                    prevDate = crntDate ;
                    hoursList.add(dateHours);
                    dateHours = new LinkedList<>();
                    dateHours.add(crntHour);
                }
            }
            hoursList.add(dateHours);
        }
        catch (JSONException e) {return false;}
        DataHolderClass.emptyQueuesHours = hoursList;
        DataHolderClass.emptyQueuesDates = dates;
        DataHolderClass.mainActivity.showChooseQueueFragment();
        return true;
    }


}
