package by.may.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import by.may.R;
import by.may.engine.MainCharManager;
import by.may.settings.UIsetting;

public class WardrobeActivity extends AppCompatActivity {

    private ImageView lookImage;
    private ImageButton arrowLeft, arrowRight;
    private Button confirmButton;
    private MainCharManager charManager;
    private String[] looks;
    private int currentIndex = 0;
    private MediaPlayer backMelody;
    private UIsetting uiSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wardrobe);

        lookImage = findViewById(R.id.look_wardrobe);
        arrowLeft = findViewById(R.id.arrow_left);
        arrowRight = findViewById(R.id.arrow_right);
        confirmButton = findViewById(R.id.confirm_button);

        charManager = new MainCharManager(this);
        looks = charManager.findLook(this);
        if (looks.length == 0) return;

        String savedLook = charManager.getLook();
        for (int i = 0; i < looks.length; i++) {
            if (looks[i].equals(savedLook)) {
                currentIndex = i;
                break;
            }
        }

        showCurrentLook();

        arrowRight.setOnClickListener(v -> nextLook());
        arrowLeft.setOnClickListener(v -> prevLook());

        confirmButton.setOnClickListener(v -> {
            charManager.setLook(looks[currentIndex]);
            finish();
        });

        uiSetting = new UIsetting(this);
        uiSetting.hideSystemUI();
    }

    private void nextLook() {
        currentIndex++;
        if (currentIndex >= looks.length) {
            currentIndex = 0;
        }
        showCurrentLook();
    }

    private void prevLook() {
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = looks.length - 1;
        }
        showCurrentLook();
    }

    private void showCurrentLook() {
        try {
            InputStream is = getAssets().open("looks/" + looks[currentIndex]);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            lookImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
