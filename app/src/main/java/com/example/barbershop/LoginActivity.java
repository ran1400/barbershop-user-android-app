package com.example.barbershop;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.barbershop.fragments.FirstLoginFragment;
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



public class LoginActivity extends AppCompatActivity
{

    public String userMail,idToken;
    public GoogleSignInAccount account;
    private GoogleSignInClient mGoogleSignInClient;
    private Intent toMainActivityIntent;
    private SignInButton googleLoginBtn;
    private ProgressBar loadingView;
    private SharedPreferences sharedPreferences;
    private FirstLoginFragment firstLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DataHolderClass.loginActivity = this;
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (checkIfFirstEnter())
            createNotificationChannels();
        toMainActivityIntent = new Intent(LoginActivity.this,
                MainActivity.class);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);
        googleLoginBtn = findViewById(R.id.googleBtn);
        googleLoginBtn.setOnClickListener((View) -> loginBtnFun());
        loadingView = findViewById(R.id.loading);
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
            ServerRequest serverRequest = new ServerRequest((String response)->Login.getUserDetailsAns(response));
            serverRequest.getUserDetails(userMail,DataHolderClass.userSecretKey, DataHolderClass.loginActivity);
        }
        else
        {
            signOut();
            makeLoginBtnVisible();
        }
    }



    private void createNotificationChannels()
    {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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


    public void dismissFirstLoginFragment()
    {
        getSupportFragmentManager().beginTransaction()
                .remove(firstLoginFragment)
                .commit();
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
        try
        {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    this,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            return sharedPreferences.getString("secretKey", null);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void writeKeyToMemory(String secretKey)
    {
        try
        {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    this,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            sharedPreferences.edit().putString("secretKey",secretKey).apply();

        } catch (Exception e) {e.printStackTrace();}
    }


    private void loginBtnFun()  //move to googleLoginAns after user log in
    {
        loadingView.setVisibility(View.VISIBLE);
        googleLoginBtn.setEnabled(false);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,0); //move to googleLoginAns
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) // move to googleLoginAns
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
            ServerRequest serverRequest = new ServerRequest((String response) -> Login.getUserDetailsAns(response));
            serverRequest.getUserDetails(userMail, secretKey, DataHolderClass.loginActivity);
        }
        else //secretKey is null,optional new user
        {
            doBeforeServerRequest();
            ServerRequest serverRequest = new ServerRequest((String response)-> Login.checkGoogleLogin(response));
            serverRequest.checkGoogleLogin(idToken,userMail,DataHolderClass.loginActivity);
        }
    }


    public void moveToMainActivity()
    {
        startActivity(toMainActivityIntent);
        finish();
    }

    public void signOut()
    {
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(Task<Void> task)
            {
                writeKeyToMemory(null);
                String mailName = userMail.substring(0,userMail.indexOf('@'));
                FirebaseMessaging.getInstance().unsubscribeFromTopic(mailName);
                FirebaseMessaging.getInstance().subscribeToTopic("managerMsgs");
            }
        });
    }

    public void firstLogin()
    {
        googleLoginBtn.setVisibility(View.INVISIBLE);
        firstLoginFragment = new FirstLoginFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout,firstLoginFragment).commit();
    }

}
