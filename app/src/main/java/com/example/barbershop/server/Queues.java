package com.example.barbershop.server;


import android.widget.Toast;
import com.example.barbershop.sharedData.DataHolderClass;
import java.util.LinkedList;

public class Queues
{

    public static void addQueueAns(String response)
    {
        DataHolderClass.mainActivity.doWhenServerResponse();
        if (response.equals("V"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "התור נקבע", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.refresh();
        }
        else if (response.equals("cmd failed"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.refresh();
        }
        else if (response.equals("managerBlockSystem"))
            Toast.makeText(DataHolderClass.mainActivity, "המנהל חסם את ההאופציה לקביעת תורים - נסה מאוחר יותר", Toast.LENGTH_SHORT).show();
        else if (response.equals("permission problem"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.signOut();
        }
        else // ( response.equals("connection failed") || response.equals(ServerRequest.ERROR_RESPONSE))
            Toast.makeText(DataHolderClass.mainActivity, "התחברות לשרת נכשלה", Toast.LENGTH_SHORT).show();
    }

    public static void updateQueueAns(String response)
    {
        DataHolderClass.mainActivity.doWhenServerResponse();
        if(response.equals("V"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "התור עודכן", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.refresh();
        }
        else if (response.equals("cmd failed"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.refresh();
        }
        else if (response.equals("managerBlockSystem"))
            Toast.makeText(DataHolderClass.mainActivity, "המנהל חסם את ההאופציה לקביעת תורים - נסה מאוחר יותר", Toast.LENGTH_SHORT).show();
        else if (response.equals("permission problem"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.signOut();
        }
        else // ( response.equals("connection failed") || response.equals(ServerRequest.ERROR_RESPONSE))
            Toast.makeText(DataHolderClass.mainActivity, "התחברות לשרת נכשלה", Toast.LENGTH_SHORT).show();
    }

    public static void deleteQueueAns(String response)
    {
        DataHolderClass.mainActivity.doWhenServerResponse();
        if(response.equals("V"))
        {
            Toast.makeText(DataHolderClass.mainActivity , "התור בוטל", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.refresh();
        }
        else if (response.equals("cmd failed"))
        {
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
            DataHolderClass.mainActivity.refresh();
        }
        else if (response.equals("permission problem"))
        {
            DataHolderClass.mainActivity.signOut();
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשאות", Toast.LENGTH_SHORT).show();
        }
        else // ( response.equals("connection failed") || response.equals(ServerRequest.ERROR_RESPONSE))
            Toast.makeText(DataHolderClass.mainActivity, "התחברות לשרת נכשלה", Toast.LENGTH_SHORT).show();
    }


    public static void getEmptyQueuesAns(String response)
    {
        DataHolderClass.mainActivity.doWhenServerResponse();
        if(response.equals("managerBlockSystem"))
            Toast.makeText(DataHolderClass.mainActivity, "המנהל חסם את האופציה לקביעת / שינוי תורים", Toast.LENGTH_SHORT).show();
        else if (response.equals("cmd failed") || response.equals("connection failed") || response.equals(ServerRequest.ERROR_RESPONSE))
            Toast.makeText(DataHolderClass.mainActivity, "בקשה לשרת נכשלה - נסה שוב", Toast.LENGTH_SHORT).show();
        else if (response.equals("permission problem"))
            Toast.makeText(DataHolderClass.mainActivity, "בעיית הרשות", Toast.LENGTH_SHORT).show();
        else if (response.length() == 0)
            Toast.makeText(DataHolderClass.mainActivity,"אין כרגע תורים פנויים", Toast.LENGTH_SHORT).show();
        else
            getEmptyQueuesAns_helper(response);
    }


    private static void getEmptyQueuesAns_helper(String response)
    {
        int responseLength = response.length();
        LinkedList<String> dates = new LinkedList<>();
        LinkedList < LinkedList<String> > hoursList = new LinkedList<>();
        String prevDate = response.substring(0,11);
        dates.add(prevDate);
        LinkedList<String> dateHours = new LinkedList<>();
        for(int i = 0 ; i < responseLength ;  i+=19) ////19 is serverTime (year-month-day hour-min-sec)
        {
            String crntDate = response.substring(i,i+11);
            String crntHour = response.substring(i+11,i+16);
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
        DataHolderClass.emptyQueuesHours = hoursList;
        DataHolderClass.emptyQueuesDates = dates;
        DataHolderClass.mainActivity.showChooseQueueFragment();
    }

}
