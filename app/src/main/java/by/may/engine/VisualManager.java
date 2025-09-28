package by.may.engine;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import by.may.R;

public class VisualManager {

    private final Activity activity;
    private final GameProgress gameProgress;
    private TextView textWindow;
    private ImageView characterImageView;
    private ImageView backgroundImageView;
    private TextView nameTextView;
    private View touchArea;
    private String previousCharacter = null;

    public VisualManager(Activity activity, GameProgress gameProgress) {
        this.activity = activity;
        this.gameProgress = gameProgress;
    }

    public String getCurrentBackground() {

        Map<String, Object> scene = gameProgress.getCurrentScene();

        if (scene != null && scene.containsKey("background")) {
            return (String) scene.get("background");
        }
        return null;
    }

    public String getCurrentCharacter() {
        Map<String, Object> scene = gameProgress.getCurrentScene();

        if (scene != null && scene.containsKey("char")) {
            return (String) scene.get("char");
        }
        return null;
    }

    public String getCurrentName() {

        SharedPreferences sp = activity.getSharedPreferences("game process", Context.MODE_PRIVATE);
        String savedName = sp.getString("player name", null);

        if (savedName != null && !savedName.trim().isEmpty()) {
            return savedName;
        }

        Map<String, Object> scene = gameProgress.getCurrentScene();

        if (scene != null && scene.containsKey("name")) {
            String name = (String) scene.get("name");

            if (name != null && !name.trim().isEmpty()) {
                return name;
            }

            else return null;
        }
        return null;
    }

    public String getCurrentMusic() {

        Map<String, Object> scene = gameProgress.getCurrentScene();

        if (scene != null && scene.containsKey("music")) {
            String music = (String) scene.get("music");
            System.out.println("music" + scene.get("music"));
            if (music != null && !music.trim().isEmpty()) {
                return music;
            }

            else return null;
        }

        return null;

    }

    public void updatesView() {
        updateBackground();
        try {
            updateCharacter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        updateName();
        updateWindow();
    }

    public void updateName() {

        String name = getCurrentName();

        if (name != null) {
            nameTextView.setText(name);
            nameTextView.setVisibility(View.VISIBLE);
        }
        else {
            nameTextView.setVisibility(View.INVISIBLE);
        }

    }

    private void updateCharacter() throws IOException {

        Animation animIn = AnimationUtils.loadAnimation(activity, R.anim.fade_in_char);
        Animation animOut = AnimationUtils.loadAnimation(activity, R.anim.fade_out_char);

        String character = getCurrentCharacter();
        String oldCharacter = previousCharacter;

        if (character != null) {

            if (!character.equals(oldCharacter)) {

                if (character.equals("main")){

                    MainCharManager charManager = new MainCharManager(activity);
                    String look = charManager.getLook();

                    if (look != null && !look.isEmpty()) {
                        try {
                            String lookFileName = look.endsWith(".png") ? look : look + ".png";
                            InputStream is = activity.getAssets().open("looks/" + lookFileName);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            is.close();
                            characterImageView.setImageBitmap(bitmap);
                        }
                    catch (IOException e) {
                        e.printStackTrace();
                        characterImageView.setImageResource(getDrawableByName("main_character"));
                    }

                    } else {
                        characterImageView.setImageResource(getDrawableByName("main_character"));
                    }

                }

                else {
                    characterImageView.setImageResource(getDrawableByName(character));
                }
                
                characterImageView.setVisibility(View.VISIBLE);
                characterImageView.startAnimation(animIn);
            }
        }

        else if (character == null && oldCharacter != null) {

            characterImageView.startAnimation(animOut);
            animOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    characterImageView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }

        previousCharacter = character;

        movingCharacter();

    }

    private void movingCharacter() {

        SharedPreferences sp = activity.getSharedPreferences("game process", Context.MODE_PRIVATE);
        String name = sp.getString("player name", "■");

        int wight = characterImageView.getWidth()/4;

        String currentName = getCurrentName();

        if (currentName == null) {
            currentName = "■";
        }

        if (!currentName.equals(name)) {
            characterImageView.setTranslationX(-wight);
            characterImageView.setScaleX(-1f);

        }

        else {
            characterImageView.setTranslationX(0);
            characterImageView.setScaleX(1f);

        }
    }

    private void updateBackground() {

        String bg = getCurrentBackground();

        if (bg != null) {
            backgroundImageView.setImageResource(getDrawableByName(bg));
        }
        else {
            backgroundImageView.setVisibility(View.INVISIBLE);
        }

    }

    public void hideUI() {

        if (textWindow != null) {
            textWindow.setVisibility(View.GONE);
        }
        nameTextView.setVisibility(View.INVISIBLE);
    }

    public void makeTextView() {

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) textWindow.getLayoutParams();

        if (getCurrentName() == null) {
            params.verticalBias = 0.5f;
        }

        else {
            params.verticalBias = 0.85f;
        }

        textWindow.setLayoutParams(params);
    }

    public void updateWindow() {

        String charName = getMainCharacterName();
        String currentName = getCurrentName();

        boolean isVisible = false;

        if (nameTextView.getVisibility() == View.VISIBLE) {
            isVisible = true;
        }

        if (!isVisible) {
            int resId = getDrawableByName("w");
            if (resId != 0) {
                textWindow.setBackgroundResource(resId);
            }
        }

        else if (isVisible && charName.equals(currentName)) {
            int resId = getDrawableByName("w1");
            if (resId != 0) {
                textWindow.setBackgroundResource(resId);
            }
        }

        else if (isVisible  && !charName.equals(currentName)) {
            int resId = getDrawableByName("w2");
            if (resId != 0) {
                textWindow.setBackgroundResource(resId);
            }
        }

    }

    public String getMainCharacterName() {

        SharedPreferences sp = activity.getSharedPreferences("game process", Context.MODE_PRIVATE);
        String name = sp.getString("player name", "■");

        return name;
    }

    public void typewriterAnimation(TextView tv, int delay) {

        tv.postDelayed(new Runnable() {
            @Override
            public void run() {

                String text = tv.getText().toString(); //тут то, что щас отрисовано
                String textTag = tv.getTag().toString(); //здесь вообще весь текст из вьюшки

                if (text.length() < textTag.length()) {
                    tv.setText(text + textTag.substring(text.length(), text.length() + 1));
                    tv.postDelayed(this, delay);
                }
            }
        }, delay);

    }

    private int getDrawableByName(String name) {
        if (name == null) return 0;
        return activity.getResources().getIdentifier(name, "drawable", activity.getPackageName());
    }

    public void componentSearch(Activity activity) {

        characterImageView = activity.findViewById(R.id.characterImage);
        backgroundImageView = activity.findViewById(R.id.background);
        nameTextView = activity.findViewById(R.id.name);
        touchArea = activity.findViewById(R.id.touchArea);
        textWindow = activity.findViewById(R.id.textWindow);
    }

    public void moveTextView(float value) {

        if (textWindow == null) {
            return;
        }

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) textWindow.getLayoutParams();
        params.verticalBias = value;
        textWindow.setLayoutParams(params);

    }

    public void setTouchAreaEnabled(boolean enabled) {
        touchArea.setEnabled(enabled);
        touchArea.setClickable(enabled);
    }


    public TextView getTextWindow() {
        return textWindow;
    }

    public View getTouchArea() {
        return touchArea;
    }

}
