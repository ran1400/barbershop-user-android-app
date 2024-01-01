package com.example.barbershop.alertDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.barbershop.R;
import com.example.barbershop.sharedData.DataHolderClass;

public class EditTextAlertDialog
{
    public enum Action {CHANGE_PHONE,CHANGE_NAME}
    private static Action action;
    private static EditTextAlertDialogInterface myInterface;
    private static String editTextContent;

    public static void showAlertDialog(Action act, EditTextAlertDialogInterface editTextAlertDialogInterface,String putInEditText)
    {
        editTextContent = putInEditText;
        action = act;
        myInterface = editTextAlertDialogInterface;
        EditTextAlertDialogHelper editTextAlertDialogHelper = new EditTextAlertDialogHelper();
        editTextAlertDialogHelper.show(DataHolderClass.mainActivity.getSupportFragmentManager(), "");
    }


    public static class EditTextAlertDialogHelper extends AppCompatDialogFragment
    {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.edit_text_alert_dialog, null);
            EditText editText = view.findViewById(R.id.editText);
            Button doButton = view.findViewById(R.id.okBtn);
            editText.setText(editTextContent);
            if (action == Action.CHANGE_NAME)
            {
                editText.setHint("הכנס שם");
                InputFilter[] filters = new InputFilter[1];
                filters[0] = new InputFilter.LengthFilter(25);
                editText.setFilters(filters);
            }
            else // (action == Action.CHANGE_PHONE)
            {
                editText.setHint("הכנס מספר פלאפון");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                InputFilter[] filters = new InputFilter[1];
                filters[0] = new InputFilter.LengthFilter(15);
                editText.setFilters(filters);
            }
            doButton.setOnClickListener((View v) ->
            {
                editTextContent = editText.getText().toString();
                if (myInterface.inputCheck(editTextContent))
                {
                    dismiss();
                    myInterface.doIfUserPressOk(editTextContent);
                }
            });
            builder.setView(view)
                    .setNegativeButton("ביטול", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });

            return builder.create();
        }
    }
}

