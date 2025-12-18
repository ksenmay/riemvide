package by.may.activities;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import by.may.R;
import by.may.engine.MainCharManager;
import by.may.settings.UIsetting;

public class WardrobeActivity extends AppCompatActivity {

    private ImageView character;
    private MainCharManager charManager;
    private ImageView lookWardrobe;
    private ArrayList<Drawable> mainCharLooks;
    private String[] looks;
    private int currentIndex = 0;
    private MediaPlayer backMelody;
    private UIsetting uiSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wardrobe);

        lookWardrobe = findViewById(R.id.look_wardrobe);


        uiSetting = new UIsetting(this);
        uiSetting.hideSystemUI();
    }

    private void showCurrentLook() {
        lookWardrobe.setImageDrawable(mainCharLooks.get(currentIndex));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            uiSetting.hideSystemUI();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(backMelody != null && backMelody.isPlaying()) {
            backMelody.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backMelody != null) {
            backMelody.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backMelody != null) {
            backMelody.stop();
            backMelody.release();
            backMelody = null;
        }
    }

}
