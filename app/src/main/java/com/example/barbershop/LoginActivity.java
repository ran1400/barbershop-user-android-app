package com.example.barbershop;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.barbershop.fragments.FirstLoginFragment;
import com.example.barbershop.fragments.NoInternetFragment;
import com.example.barbershop.server.Login;
import com.example.barbershop.server.ServerRequest;
import com.example.barbershop.sharedData.DataHolderClass;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity implements  View.OnClickListener
{

    public String userMail, idToken;
    public GoogleSignInAccount account;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int GOOGLE_LOGIN_REQUEST_CODE = 1000;
    private Intent toMainActivityIntent;
    private SignInButton googleLoginBtn;
    private ProgressBar loadingView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DataHolderClass.loginActivity = this; // important - first put in sharedDate the loginActivity
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (checkIfFirstEnter())
            doOnFirstEnterToTheApp();
        toMainActivityIntent = new Intent(LoginActivity.this,
                MainActivity.class);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);
        setContentView(R.layout.activity_login);//define views only after this line
        googleLoginBtn = findViewById(R.id.googleBtn);
        loadingView = findViewById(R.id.loading);
        googleLoginBtn.setOnClickListener(this);
        if (account != null) // there are user sign in
             doIfHasLogInUser();
        else
        {
            writeKeyToMemory(null);
            googleLoginBtn.setVisibility(View.VISIBLE);
        }
    }

    private void doIfHasLogInUser()
    {
        userMail = account.getEmail();
        DataHolderClass.userSecretKey = getKeyFromMemory();
        if (DataHolderClass.userSecretKey != null)
        {
            doBeforeServerRequest();
            ServerRequest serverRequest = new ServerRequest((String response)->Login.getUserDetailsAns(response,userMail));
            serverRequest.getUserDetails(userMail,DataHolderClass.userSecretKey, DataHolderClass.loginActivity);
        }
        else
            restart();
    }

    private void doOnFirstEnterToTheApp()
    {
        createNotificationChannels();
        FirebaseMessaging.getInstance().subscribeToTopic("managerMsgs");
    }

    private void createNotificationChannels()
    {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel;
            channel = new NotificationChannel("queuesUpdates", "שינוי תורים על ידי המנהל", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            channel = new NotificationChannel("quietManagerMsg", "הודעות מהמנהל - ללא צליל", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
            channel = new NotificationChannel("managerMsg", "הודעות מהמנהל", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            channel = new NotificationChannel("other", "חסימות ומחיקות חשבון", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
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

    public void makeLoginBtnVisible()
    {
        googleLoginBtn.setVisibility(View.VISIBLE);
        googleLoginBtn.setEnabled(true);
    }
    public void makeLoginBtnInvisible()
    {
        googleLoginBtn.setVisibility(View.GONE);

    }
    public void showNoInternetFragment()
    {
        NoInternetFragment noInternetFragment = new NoInternetFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout,noInternetFragment);
        fragmentTransaction.commit();
    }

    private boolean checkIfFirstEnter()
    {
        if (sharedPreferences.getBoolean("firstEnter",true))
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstEnter",false);
            editor.commit();
            return true;
        }
        else
            return false;
    }

    public String getKeyFromMemory()
    {
        return sharedPreferences.getString("secretKey",null); //null is if userId not exist
    }

    public void writeKeyToMemory(String secretKey)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("secretKey",secretKey);
        editor.commit();
    }

    @Override
    public void onClick(View view)
    {
        loginBtnFun(); //onclick listen only to loginBtn
    }

    public void restart()
    {
        signOut();
        Intent intent = new Intent(LoginActivity.this,
                LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void restartWithoutSignOut() // for use of first login fragment or send mail fragment
    {
        Intent intent = new Intent(LoginActivity.this,
                LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginBtnFun() //move to googleLoginAns after user log in
    {
        loadingView.setVisibility(View.VISIBLE);
        googleLoginBtn.setEnabled(false);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,GOOGLE_LOGIN_REQUEST_CODE); //move to googleLoginAns
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try
        {
            account = task.getResult(ApiException.class);
            googleLoginAns(account);
        }
        catch (ApiException e)
        {
            googleLoginAns(null);
        }
    }

    private void googleLoginAns (GoogleSignInAccount account)
    {
        loadingView.setVisibility(View.GONE);
        if (account != null) //success google login
            doAfterSuccessfulGoogleLogin(); // if have secret key go to checkIfNewUserAns else go to getUserDetailsAns
        else
        {
            String msg = "התחברות דרך גוגל נכשלה\n           נסה שוב";
            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
            googleLoginBtn.setEnabled(true);
        }
    }


    private void doAfterSuccessfulGoogleLogin()
    {
        userMail = account.getEmail();
        String secretKey = getKeyFromMemory();
        idToken = account.getIdToken();
        DataHolderClass.loginActivity.doBeforeServerRequest();
        if (secretKey != null)
        {
            DataHolderClass.userSecretKey = secretKey;
            doBeforeServerRequest();
            ServerRequest serverRequest = new ServerRequest((String response) -> Login.getUserDetailsAns(response,userMail));
            serverRequest.getUserDetails(userMail, secretKey, DataHolderClass.loginActivity);
        }
        else
        {
            doBeforeServerRequest();
            ServerRequest serverRequest = new ServerRequest((String response)-> Login.checkIfNewUserAns(response));
            serverRequest.checkIfNewUser(userMail,DataHolderClass.loginActivity);
        }
    }


    public void moveToMainActivity()
    {
        startActivity(toMainActivityIntent);
        finish();
    }

    private void signOut()
    {
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(Task<Void> task)
            {
                String mailName = DataHolderClass.userMail.substring(0,DataHolderClass.userMail.indexOf('@'));
                FirebaseMessaging.getInstance().unsubscribeFromTopic(mailName);
                loadingView.setVisibility(View.GONE);
                makeLoginBtnVisible();
            }
        });
    }

    public void firstLogin()
    {
        googleLoginBtn.setEnabled(false);
        FirstLoginFragment loginFragment = new FirstLoginFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, loginFragment);
        fragmentTransaction.commit();
    }

}
