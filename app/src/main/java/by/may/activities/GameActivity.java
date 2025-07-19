package by.may.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import by.may.R;
import by.may.engine.ActivityControl;
import by.may.engine.Engine;
import by.may.settings.UIsetting;

public class GameActivity extends AppCompatActivity {

    private Engine engine;
    private UIsetting uiSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        engine = new Engine(this);
        engine.init();
        engine.getMusicManager().loadSavedMusic();

        ActivityControl control = engine.getControl();
        uiSetting = new UIsetting(this);
        uiSetting.hideSystemUI();
        control.setupTop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            uiSetting.hideSystemUI();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        engine.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (engine != null) {
            engine.getMusicManager().pauseMusic();
            engine.getMusicManager().saveCurrentMusic();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (engine != null) {
            engine.getMusicManager().resumeMusic();
        }
    }

    public ActivityControl getControl() {
        return engine.getControl();
    }
}
