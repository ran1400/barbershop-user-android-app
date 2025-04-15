package com.example.barbershop.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.barbershop.MainActivity;
import com.example.barbershop.R;
import com.example.barbershop.alertDialog.SimpleMethod;
import com.example.barbershop.alertDialog.AlertDialog;
import com.example.barbershop.server.Queues;
import com.example.barbershop.server.ServerRequest;
import com.example.barbershop.server.ServerResponseHandle;
import com.example.barbershop.sharedData.DataHolderClass;

import java.util.LinkedList;


public class ChooseQueueFragment extends Fragment
{
    private String selectedQueue;
    private TextView msg;
    private LinkedList<String> selectedHours;
    private String selectedDay;
    private long selectedHour;
    private DropDownListHoursListiner dropDownListHoursListiner = new DropDownListHoursListiner();
    private DropDownListDatesListiner dropDownListDatesListiner = new DropDownListDatesListiner();
    private Spinner datesDropDownList,hoursDropDownList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        DataHolderClass.chooseQueueFragment = this;
        View view =  inflater.inflate(R.layout.fragment_choose_queue, container, false);
        msg = view.findViewById(R.id.msg);
        datesDropDownList = (Spinner)view.findViewById(R.id.dropDownListDates);
        hoursDropDownList = (Spinner)view.findViewById(R.id.dropDownListHours);
        Button okBtn = view.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(this::okBtn);
        Button backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this::backBtn);
        createDatesDropDownLists();
        return view;
    }


    private void createDatesDropDownLists()
    {
        String viewDatesList[] = getDropDownListViewDates(DataHolderClass.emptyQueuesDates);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(DataHolderClass.mainActivity,
                android.R.layout.simple_spinner_item,viewDatesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        datesDropDownList.setAdapter(adapter);
        datesDropDownList.setOnItemSelectedListener(dropDownListDatesListiner);

    }

    private String[] getDropDownListViewDates(LinkedList<String> dates)
    {
        String[] res = new String[dates.size()];
        int i = 0;
        for (String date: dates)
        {
            String dateStr = MainActivity.flipDateString(date) + " " + MainActivity.getDayOfWeekString(date);
            res[i++] = dateStr ;
        }
        return res;
    }

    private void onDateSelected(int id)
    {
        selectedHours = DataHolderClass.emptyQueuesHours.get(id);
        selectedDay = DataHolderClass.emptyQueuesDates.get(id);
        int aviavibleHours = selectedHours.size();
        if (aviavibleHours > 1)
            msg.setText(selectedHours.size() +  " תורים פנויים ב "+ fromDateToString(selectedDay));
        else
            msg.setText(" תור אחד פנוי ב "+ fromDateToString(selectedDay));
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(DataHolderClass.mainActivity,
                android.R.layout.simple_spinner_item,selectedHours);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hoursDropDownList.setAdapter(adapter);
        hoursDropDownList.setOnItemSelectedListener(dropDownListHoursListiner);
    }


    private class DropDownListDatesListiner implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            onDateSelected((int)id);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent){/*do nothing*/}
    }

    private class DropDownListHoursListiner implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            selectedHour = id;
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent){}
    }

    public void okBtn(View view)
    {

        selectedQueue = selectedDay + " " + selectedHours.get((int)selectedHour);
        if (DataHolderClass.mainActivity.updateOrAddQueueCmd  == DataHolderClass.Action.ADD_QUEUE)
        {
            SimpleMethod doIfUserPressOk = () ->
            {
                DataHolderClass.mainActivity.doBeforeServerRequest();
                ServerResponseHandle serverResponseHandle = (String response) -> Queues.addQueueAns(response);
                ServerRequest serverRequest = new ServerRequest(serverResponseHandle);
                serverRequest.addQueue(selectedQueue,DataHolderClass.mainActivity);
            };
            String title = "לקבוע את התור?";
            AlertDialog.showAlertDialog(title,"",doIfUserPressOk,DataHolderClass.mainActivity);
        }
        else // DataHolderClass.mainActivity.updateOrAddQueueCmd  == DataHolderClass.Action.UPDATE_QUEUE
        {
            SimpleMethod doIfUserPressOk = () ->
            {
                DataHolderClass.mainActivity.doBeforeServerRequest();
                ServerResponseHandle serverResponseHandle = (String response) -> Queues.updateQueueAns(response);
                ServerRequest serverRequest = new ServerRequest(serverResponseHandle);
                serverRequest.updateQueue(selectedQueue,DataHolderClass.mainActivity);
            };
            String title = "לעדכן את התור?";
            AlertDialog.showAlertDialog(title,"",doIfUserPressOk,DataHolderClass.mainActivity);
        }
    }

    public void backBtn(View view)
    {
        DataHolderClass.mainActivity.closeChooseQueueWindows();
    }



    public static String fromDateToString (String date) // from 2022-12-28 to 28.12.2022
    {
        String year = date.substring(0,4);
        String month = date.substring(5,7);
        String day = date.substring(8,10);
        return day + "." + month + "." + year;
    }

}