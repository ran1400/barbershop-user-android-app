package com.example.barbershop.alertDialog;

import android.content.Context;

public class AlertDialog
{
    public static void showAlertDialog(String title, String msg, SimpleMethod doIfPositive,Context context)
    {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);
        alertDialog.setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton("כן", (dialog, id) -> doIfPositive.execute())
                    .setNegativeButton("לא", null);
        alertDialog.show();
    }

    /* if i need - alert dialog only for show msg without action
    public static void showAlertDialog(String title, Context context)
    {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.show();
    }*/

}
