package alireza.sn.mycontacts.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import alireza.sn.mycontacts.MainActivity;

public class MyPreferenceManager {
    private static MyPreferenceManager instance;
    private  SharedPreferences preferences;
    private  SharedPreferences.Editor editor;

    private MyPreferenceManager (Context context){
        preferences = context.getSharedPreferences("share_preference",Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static MyPreferenceManager getInstance(Context context) {
        if (instance == null)
        instance = new MyPreferenceManager(context);

        return instance;
    }

    public void putContactsList(List<MyInfo> list) {
        Gson gson = new Gson();
        String contacts = gson.toJson(list,List.class);
        editor.putString("contacts",contacts);
        editor.apply();
    }

    public List<MyInfo> getContactsList (){
        Gson gson = new Gson();
        String str = preferences.getString("contacts",null);

        if ( str == null)
            return new ArrayList<>();
        else

        return gson.fromJson(str,new TypeToken<List<MyInfo>>(){}.getType());
    }

    public void putFavoritesList(List<MyInfo> list) {
        Gson gson = new Gson();
        String contacts = gson.toJson(list,List.class);
        editor.putString("favorites",contacts);
        editor.apply();
    }

    public List<MyInfo> getFavoritesList (){
        Gson gson = new Gson();
        String str = preferences.getString("favorites",null);

        if ( str == null)
            return new ArrayList<>();
        else

        return gson.fromJson(str,new TypeToken<List<MyInfo>>(){}.getType());
    }



}
