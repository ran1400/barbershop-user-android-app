package com.example.barbershop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.barbershop.alertDialog.AlertDialog;
import com.example.barbershop.alertDialog.EditTextAlertDialog;
import com.example.barbershop.alertDialog.EditTextAlertDialogInterface;
import com.example.barbershop.alertDialog.SimpleMethod;
import com.example.barbershop.fragments.ChooseQueueFragment;
import com.example.barbershop.server.Queues;
import com.example.barbershop.server.ServerRequest;
import com.example.barbershop.server.ServerResponseHandle;
import com.example.barbershop.server.UserDetails;
import com.example.barbershop.sharedData.DataHolderClass;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.appcompat.app.AppCompatActivity;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity
{
    public DataHolderClass.Action updateOrAddQueueCmd;
    private static final String[] DATS_OF_WEEK = {"יום ראשון" , "יום שני" , "יום שלישי" ,  "יום רביעי" , "יום חמישי" , "יום שישי" , "יום שבת"};
    private GoogleSignInAccount account;
    private View loadingView;
    private TextView mainText,msgText,settingText;
    private Button addQueueBtn,deleteQueueBtn,updateQueueBtn;
    private ImageButton closeSettingBtn;
    private View chooseQueueLayout,settingWindow,msgWindows,mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (DataHolderClass.userName == null) // there is no connected user
            refresh();
        setContentView(R.layout.activity_main);
        DataHolderClass.mainActivity = this;
        loadingView = findViewById(R.id.loading);
        mainLayout = findViewById(R.id.mainLayout);
        mainText = findViewById(R.id.mainText);
        account = GoogleSignIn.getLastSignedInAccount(this);
        addQueueBtn = findViewById(R.id.addQueueBtn);
        deleteQueueBtn = findViewById(R.id.deleteQueueBtn);
        updateQueueBtn = findViewById(R.id.updateQueueBtn);
        chooseQueueLayout = findViewById(R.id.chooseQueueFragment);
        msgWindows = findViewById(R.id.nsgWindow);
        msgText = findViewById(R.id.msgTextView);
        settingText = findViewById(R.id.settingTextView);
        settingWindow = findViewById(R.id.settingWindow);
        closeSettingBtn = findViewById(R.id.closeSettingBtn);
        changeActivityTitleToDefault();
        inAppMsgHandle(); //show msg from the manager if have
        showUserQueueHandel(); //show the queue to the user handle
    }

    public void doBeforeServerRequest()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadingView.setVisibility(View.VISIBLE);
    }

    public void doWhenServerResponse()
    {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadingView.setVisibility(View.GONE);
    }

    public void showUserQueueHandel()
    {
        if (DataHolderClass.userReservedQueue == null)
        {
            mainText.setText("אין לך תור");
            addQueueBtn.setVisibility(View.VISIBLE);
            deleteQueueBtn.setVisibility(View.INVISIBLE);
            updateQueueBtn.setVisibility(View.INVISIBLE);
        }
        else
        {
            mainText.setText( "יש לך תור :" + "\n\n" + makeDateView(DataHolderClass.userReservedQueue));
            deleteQueueBtn.setVisibility(View.VISIBLE);
            updateQueueBtn.setVisibility(View.VISIBLE);
            addQueueBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void inAppMsgHandle()
    {
        if (DataHolderClass.msg == null)
            msgWindows.setVisibility(View.GONE);
        else
            msgText.setText("\n\n" + DataHolderClass.msg + "\n\n");
    }

    public void changeActivityTitle(String title)
    {
        getSupportActionBar().setTitle(title);
    }


    public void removeUserBtn(View view)
    {
        SimpleMethod doIfUserPressOk = () ->
        {
            ServerResponseHandle serverResponseHandle = (String response) -> UserDetails.removeUserAns(response);
            doBeforeServerRequest();
            ServerRequest serverRequest = new ServerRequest(serverResponseHandle);
            serverRequest.removeUser(this);
        };
        String title = "למחוק את המשתמש?";
        if (DataHolderClass.userReservedQueue == null)
            AlertDialog.showAlertDialog(title,"",doIfUserPressOk,this);
        else
        {
            String msg = "מחיקת החשבון תבטל את התור שלך";
            AlertDialog.showAlertDialog(title,msg,doIfUserPressOk,this);
        }
    }


    public void closeSettingBtn(View view)
    {
        closeSetting();
    }

    public void closeSetting()
    {
        settingWindow.setVisibility(View.GONE);
        closeSettingBtn.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        chooseQueueLayout.setVisibility(View.GONE);
        changeActivityTitleToDefault();
    }

    public void logOutFromAllDevices(View view)
    {
        SimpleMethod doIfUserPressOk = () ->
        {
            doBeforeServerRequest();
            ServerResponseHandle serverResponseHandle = (String response) -> UserDetails.logOutFromAllDevicesAns(response);
            ServerRequest serverRequest = new ServerRequest(serverResponseHandle);
            serverRequest.logOutFromAllDevices(this);
        };
        String title = "להתנתק מכל המכשירים?";
        AlertDialog.showAlertDialog(title,"",doIfUserPressOk,this);
    }

    public void settingBtn(View view)
    {
        showSetting();
    }

    private void showSetting()
    {
        if (settingWindow.getVisibility() == View.VISIBLE)
            closeSetting();
        else
        {
            updatePhoneTextView();
            settingWindow.setVisibility(View.VISIBLE);
            closeSettingBtn.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.INVISIBLE);
            changeActivityTitle("הגדרות");
        }
    }

    public void updatePhoneTextView()
    {
        settingText.setText("מספר הפלאפון שלך הוא :\n\n" + DataHolderClass.userPhone);
    }

    public void refresh(View view)
    {
        refresh();
    }

    public void refresh()
    {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        finish();
        startActivity(loginActivity);
    }

    private static int getDayOfWeek(int year,int month,int day)
    {
        Date date = new GregorianCalendar(year,month-1,day).getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static String getDayOfWeekString(String time) //time is year-month-day hour-mon-sec
    {
        int year = Integer.parseInt(time.substring(0,4));
        int month = Integer.parseInt(time.substring(5,7));
        int day = Integer.parseInt(time.substring(8,10));
        return DATS_OF_WEEK[getDayOfWeek(year,month,day) - 1];
    }

    public static String flipDateString (String input) //input is yyyy-mm-dd res is day.month.year
    {
        String year = input.substring(0,4);
        String month = input.substring(5,7);
        String day = input.substring(8,10);
        return day + "." + month + "." + year;
    }

    public void addQueueBtn(View view)
    {
        showEmptyQueues(DataHolderClass.Action.ADD_QUEUE);
    }

    public void updateQueueBtn(View view)
    {
        showEmptyQueues(DataHolderClass.Action.UPDATE_QUEUE);
    }

    public void deleteQueuesBtn(View view)
    {
        SimpleMethod doIfUserPressOk = () ->
        {
            doBeforeServerRequest();
            ServerResponseHandle serverResponseHandle = (String response) -> Queues.deleteQueueAns(response);
            ServerRequest serverRequest = new ServerRequest(serverResponseHandle);
            serverRequest.deleteQueue(this);
        };
        String title = "האם לבטל את התור?";
        AlertDialog.showAlertDialog(title,"",doIfUserPressOk,this);
    }

    private void showEmptyQueues(DataHolderClass.Action cmd)
    {
        updateOrAddQueueCmd = cmd;
        doBeforeServerRequest();
        ServerRequest serverRequest = new ServerRequest((String response) -> Queues.getEmptyQueuesAns(response));
        serverRequest.getEmptyQueues(this); //continue at showChooseQueueFragment
    }

    public void updateNameBtn(View view)
    {
        updateNameBtn("");
    }

    public void updateNameBtn(String putInEditText)
    {
        EditTextAlertDialogInterface editTextAlertDialogInterface = new EditTextAlertDialogInterface()
        {
            @Override
            public void doIfUserPressOk(String newName)
            {
                doBeforeServerRequest();
                ServerResponseHandle serverResponseHandle = (String response) -> UserDetails.updateNameAns(response,newName);
                ServerRequest serverRequest = new ServerRequest(serverResponseHandle);
                serverRequest.updateName(newName,DataHolderClass.mainActivity);
            }

            @Override
            public boolean inputCheck(String editTextContent)
            {
                if (editTextContent.isEmpty())
                {
                    Toast.makeText(DataHolderClass.mainActivity, "הכנס שם", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        };
        EditTextAlertDialog.showAlertDialog(EditTextAlertDialog.Action.CHANGE_NAME,editTextAlertDialogInterface,putInEditText);
    }

    public void updatePhoneBtn(View view)
    {
        updatePhoneBtn("");
    }

    public void updatePhoneBtn(String putInEditText)
    {
        EditTextAlertDialogInterface editTextAlertDialogInterface = new EditTextAlertDialogInterface()
        {
            @Override
            public void doIfUserPressOk(String newPhone)
            {
                doBeforeServerRequest();
                ServerResponseHandle serverResponseHandle = (String response) -> UserDetails.updatePhoneAns(response,newPhone);
                ServerRequest serverRequest = new ServerRequest(serverResponseHandle);
                serverRequest.updatePhone(newPhone,DataHolderClass.mainActivity);
            }

            @Override
            public boolean inputCheck(String editTextContent)
            {
                if (editTextContent.isEmpty())
                {
                    Toast.makeText(DataHolderClass.mainActivity, "הכנס מספר פלאפון", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        };
        EditTextAlertDialog.showAlertDialog(EditTextAlertDialog.Action.CHANGE_PHONE,editTextAlertDialogInterface,putInEditText);
    }


    public void signOut(View view)
    {
        SimpleMethod doIfUserPressOk = () -> signOut();
        String title = "להתנתק?";
        AlertDialog.showAlertDialog(title,"",doIfUserPressOk,this);
    }


    public void signOut()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task)
            {
                String userMail = account.getEmail();
                String mailName = userMail.substring(0,userMail.indexOf('@'));
                FirebaseMessaging.getInstance().unsubscribeFromTopic(mailName);
                FirebaseMessaging.getInstance().unsubscribeFromTopic("managerMsgs");
                Intent intent = new Intent(MainActivity.this,
                        LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    public void changeActivityTitleToDefault()
    {
        changeActivityTitle("שלום " + DataHolderClass.userName);
    }

    public void showChooseQueueFragment()
    {
        if (DataHolderClass.chooseQueueFragment == null)
            DataHolderClass.chooseQueueFragment = new ChooseQueueFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.chooseQueueFragment,DataHolderClass.chooseQueueFragment).commit();
        mainLayout.setVisibility(View.GONE);
        chooseQueueLayout.setVisibility(View.VISIBLE);
        if (updateOrAddQueueCmd == DataHolderClass.Action.UPDATE_QUEUE)
            changeActivityTitle("עדכון תור");
        else // (updateOrAddQueueCmd == DataHolderClass.Action.ADD_QUEUE)
            changeActivityTitle("קביעת תור");
    }

    public void closeChooseQueueWindows() // btn in choose queue fragment
    {
        getSupportFragmentManager().beginTransaction().remove(DataHolderClass.chooseQueueFragment).commit();
        chooseQueueLayout.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        changeActivityTitleToDefault();
    }


    public static String makeDateView(String date) //yyyy-mm-dd hh:mm:ss
    {
        String res = getDayOfWeekString(date);
        res += "\n" + flipDateString(date.substring(0,10));
        res+= "\n" + date.substring(11,16);
        return res;
    }

}
