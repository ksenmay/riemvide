package by.may.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import by.may.R;
import by.may.engine.Engine;
import by.may.settings.UIsetting;
import by.may.engine.ActivityControl;

public class MenuActivity extends AppCompatActivity {

    private UIsetting uiSetting;
    private ActivityControl control;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        Engine engine = new Engine(this);
        control = new ActivityControl(this, engine);
        control.startControl();

        uiSetting = new UIsetting(this);
        uiSetting.hideSystemUI();

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        control.showAlertDialog("Выход", "Вы уверены, что хотите покинуть Риэм-Виде?", "exit");
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            uiSetting.hideSystemUI();
        }
    }

}
