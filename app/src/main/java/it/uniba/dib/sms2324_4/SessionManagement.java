package it.uniba.dib.sms2324_4;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CheckBox;

public class SessionManagement {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String SESSION_KEY = "session_user";
    String SESSION_PROFILE = "session profile";
    String NAME = "Nome Utente";


    public SessionManagement(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public void saveSession(Genitori user, String profile,String nome){
        //save session of user whenever user is logged in
        String id = user.getCf();

        editor.putString(SESSION_KEY,id).commit();
        editor.putString(SESSION_PROFILE,profile).commit();
        editor.putString(NAME,nome).commit();
    }

    public void saveSession(Logopedisti user, String profile,String nome){
        //save session of user whenever user is logged in
        String id = user.getCf();

        editor.putString(SESSION_KEY,id).commit();
        editor.putString(SESSION_PROFILE,profile).commit();
        editor.putString(NAME,nome).commit();
    }

    public String getSession(){
        //return user id whose session is saved
        return sharedPreferences.getString(SESSION_KEY , "NULL");
    }

    public String getProfile(){
        return sharedPreferences.getString(SESSION_PROFILE, "NULL");
    }

    public String getNome(){
        return  sharedPreferences.getString(NAME,"NULL");
    }


    public void removeSession(){
        editor.putString(SESSION_KEY,"NULL").commit();
    }
}