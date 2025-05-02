package com.example.barbershop.sharedData;

import com.example.barbershop.LoginActivity;
import com.example.barbershop.MainActivity;
import com.example.barbershop.fragments.ChooseQueueFragment;

import java.util.LinkedList;

public class DataHolderClass
{
    public enum Action {ADD_QUEUE,UPDATE_QUEUE}
    public static MainActivity mainActivity;
    public static LoginActivity loginActivity;
    public static ChooseQueueFragment chooseQueueFragment;
    public static String userMail,userName,userPhone,msg,userReservedQueue,userSecretKey; // connected login
    public static LinkedList<LinkedList<String>> emptyQueuesHours; //for show empty queues
    public static LinkedList<String> emptyQueuesDates; //for show empty queues
}




