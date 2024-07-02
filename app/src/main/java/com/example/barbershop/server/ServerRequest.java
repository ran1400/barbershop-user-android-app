package com.example.barbershop.server;


import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.barbershop.sharedData.DataHolderClass;
import java.util.HashMap;
import java.util.Map;

public class ServerRequest
{
    public static final String ERROR_RESPONSE = "requestError";
    private  Map map;
    private String url;
    private ServerResponseHandle responseHandle; // method that called when get response from the server

    public ServerRequest(ServerResponseHandle responseHandle)
    {
        map = new HashMap<String,String>();
        this.responseHandle = responseHandle;
    }

    public void firstLogin(String name,String phone,String idToken,Context context)
    {
        url = "https://ran-yehezkel.online/barbershop/commands/user/add_new_user.php";
        map.put("name",name);
        map.put("phone",phone);
        map.put("idToken",idToken);
        sendRequest(context);
    }

    public void checkIfNewUser(String userMail,Context context)
    {
        url = "https://ran-yehezkel.online/barbershop/commands/user/check_if_new_user.php";
        map.put("userMail",userMail);
        sendRequest(context);
    }

    public void getUserDetails(String userMail,String secretKey,Context context)
    {
        url = "https://ran-yehezkel.online/barbershop/commands/user/get_user_details.php";
        map.put("userMail",userMail);
        map.put("secretKey",secretKey);
        sendRequest(context);
    }

    public void checkGoogleLogIn(String idToken, Context context)
    {
        url = "https://ran-yehezkel.online/barbershop/commands/user/check_google_login.php";
        map.put("idToken",idToken);
        sendRequest(context);
    }

    public void deleteQueue(Context context)
    {
        url = "https://ran-yehezkel.online/barbershop/commands/user/remove_reserved_queue_and_add_empty_queue.php";
        map.put("userMail",DataHolderClass.userMail);
        map.put("userName", DataHolderClass.userName);
        map.put("secretKey",DataHolderClass.userSecretKey);
        map.put("date",DataHolderClass.userReservedQueue);
        sendRequest(context);
    }

    public void logOutFromAllDevices(Context context)
    {
        url = "https://ran-yehezkel.online/barbershop/commands/user/make_new_uuid.php";
        map.put("userMail",DataHolderClass.userMail);
        map.put("secretKey",DataHolderClass.userSecretKey);
        sendRequest(context);
    }

    public void addQueue(String newDate,Context context)
    {
        url = "https://ran-yehezkel.online/barbershop/commands/user/add_reserved_queue.php";
        map.put("userMail",DataHolderClass.userMail);
        map.put("userName",DataHolderClass.userName);
        map.put("secretKey",DataHolderClass.userSecretKey);
        map.put("newDate", newDate);
        sendRequest(context);
    }


    public void getEmptyQueues(Context context)
    {
        url = "https://ran-yehezkel.online/barbershop/commands/user/ask_for_empty_queues.php";
        map.put("userMail",DataHolderClass.userMail);
        map.put("secretKey",DataHolderClass.userSecretKey);
        sendRequest(context);
    }

    public void updateName(String name,Context context)
    {
        url = "https://ran-yehezkel.online/barbershop/commands/user/update_name.php";
        map.put("userMail",DataHolderClass.userMail);
        map.put("secretKey",DataHolderClass.userSecretKey);
        map.put("name", name);
        sendRequest(context);
    }

    public void updatePhone(String phone,Context context)
    {
        url = "https://ran-yehezkel.online/barbershop/commands/user/update_phone.php";
        map.put("userMail",DataHolderClass.userMail);
        map.put("secretKey",DataHolderClass.userSecretKey);
        map.put("phone", phone);
        sendRequest(context);
    }

    public void updateQueue(String newDate,Context context)
    {
        url = "https://ran-yehezkel.online/barbershop/commands/user/update_queue.php";
        map.put("userMail",DataHolderClass.userMail);
        map.put("userName",DataHolderClass.userName);
        map.put("secretKey",DataHolderClass.userSecretKey);
        map.put("newDate", newDate);
        sendRequest(context);
    }

    public void removeUser(Context context)
    {;
        url = "https://ran-yehezkel.online/barbershop/commands/user/remove_user.php";
        map.put("userMail",DataHolderClass.userMail);
        map.put("userName",DataHolderClass.userName);
        map.put("secretKey",DataHolderClass.userSecretKey);
        sendRequest(context);
    }

    private void sendRequest(Context context)
    {
        StringRequest sr = new StringRequest(1, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        responseHandle.doWhenGetResponseFromTheServer(response);
                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                responseHandle.doWhenGetResponseFromTheServer(ServerRequest.ERROR_RESPONSE);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                return map;
            }
        };
        RequestQueue rq = Volley.newRequestQueue(context);
        rq.add(sr);
    }
}