package by.may.engine;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;

public class MainCharManager {

    private final SharedPreferences sp;

    public MainCharManager(Activity activity) {
        sp = activity.getSharedPreferences("main character", Context.MODE_PRIVATE);
    }

    public void setStat(String stat, int value) {
        sp.edit().putInt(stat, value).apply();
    }

    public int getStat(String stat) {
        return sp.getInt(stat, 0);
    }

    public void changeStat(String stat, int value) {
        int current = getStat(stat);
        setStat(stat, current + value);
    }

    public void init() {
        sp.edit().putInt("adaptation", 0).putInt("coldBloodedness", 0).putInt("impulsiveness", 0).putInt("hatred", 0).putInt("kindness", 0)
                .putInt("elis", 0)
                .putString("current look", "look1.png")
                .putBoolean("is first", true).apply();
    }

    public String[] findLook(Activity activity) {

        AssetManager assetManager = activity.getAssets();
        ArrayList<String> looks = new ArrayList<>();

        try {
            String[] images = assetManager.list("looks");
            assert images != null;
            for (String image : images) {
                if (image.endsWith(".png")) {
                    looks.add(image);
                }
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return looks.toArray(new String[0]);

    }

    public void logAllStats() {
        int adaptation = sp.getInt("adaptation", -1);
        int coldBloodedness = sp.getInt("coldBloodedness", -1);
        int impulsiveness = sp.getInt("impulsiveness", -1);
        int hatred = sp.getInt("hatred", -1);
        int kindness = sp.getInt("kindness", -1);
        int elis = sp.getInt("elis", -1);
        String currentLook = sp.getString("current look", "none");
        boolean isFirst = sp.getBoolean("is first", true);

        Log.d("статы", "adaptation = " + adaptation);
        Log.d("статы", "coldBloodedness = " + coldBloodedness);
        Log.d("статы", "impulsiveness = " + impulsiveness);
        Log.d("статы", "hatred = " + hatred);
        Log.d("статы", "kindness = " + kindness);
        Log.d("статы", "elis = " + elis);
        Log.d("статы", "current look = " + currentLook);
        Log.d("статы", "is first = " + isFirst);
    }

    public void setLook(String lookFileName) {
        sp.edit().putString("current look", lookFileName).apply();
    }

    public String getLook() {
        return sp.getString("current look", "look1.png");
    }



}
