package by.may.engine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Random;
import by.may.R;
import by.may.activities.ChaptersActivity;
import by.may.activities.GameActivity;
import by.may.activities.MenuActivity;
import by.may.activities.SettingFragment;
import by.may.activities.WardrobeActivity;

public class ActivityControl {

    private final Activity activity;
    private Engine engine;
    public Button startButton;
    public ImageButton chaptersButton;
    public ImageButton wardrobeButton;
    public ImageButton resetButton;
    public ConstraintLayout layout;
    private Button settingTopButton;
    private Button wardrobeTopButton;
    public SettingFragment setting = new SettingFragment();

    public ActivityControl(Activity activity, Engine engine) {
        this.activity = activity;
        this.engine = engine;
    }

    public Engine getEngine() {
        return engine;
    }

    public void startControl() {

        componentSearch();
        setListeners();
        setBackAnim();
        setStartButton();
    }

    private void componentSearch() {

        startButton = activity.findViewById(R.id.startButton);
        chaptersButton = activity.findViewById(R.id.chaptersButton);
        wardrobeButton = activity.findViewById(R.id.wardrobeButton);
        resetButton = activity.findViewById(R.id.resetButton);
        layout = activity.findViewById(R.id.menu);
    }

    private void setStartButton() {

        SharedPreferences sp = activity.getSharedPreferences("game process", activity.MODE_PRIVATE);
        boolean firstCompleted = sp.getBoolean("chapter 0 completed", false);
        int currentLine = sp.getInt("line index", 0);

        if (firstCompleted || currentLine > 0) {
            startButton.setText("Продолжить");
        }
        else {
            startButton.setText("Начать игру");
        }

    }

    private void setBackAnim() {

        layout.post(() -> startHandsLoop());
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();

        applyAnimatedTouch(startButton);
        applyAnimatedTouch(chaptersButton);
        applyAnimatedTouch(wardrobeButton);
        applyAnimatedTouch(resetButton);

    }

    public void applyAnimatedTouch(View view) {

        float cornerRadiusPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, activity.getResources().getDisplayMetrics());

        GradientDrawable pressedBackground = new GradientDrawable();
        pressedBackground.setColor(Color.parseColor("#CC3B0B0A"));
        pressedBackground.setCornerRadius(cornerRadiusPx);

        View.OnTouchListener animatedTouchListener = (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.scale_down));
                    v.setBackground(pressedBackground);
                    break;
                case MotionEvent.ACTION_UP:
                    v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.scale_up));
                    v.setBackgroundResource(R.drawable.rounded_button);
                    v.performClick();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.scale_up));
                    v.setBackgroundResource(R.drawable.rounded_button);
                    break;
            }
            return false;
        };

        view.setOnTouchListener(animatedTouchListener);

    }

    public static void applyAnimatedTouchWithoutColor(View view, int draw, Activity activity) {

        View.OnTouchListener animatedTouchListener = (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.scale_down));
                    break;
                case MotionEvent.ACTION_UP:
                    v.clearAnimation();
                    v.performClick();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.scale_up));
                    v.setBackgroundResource(draw);
                    break;
            }
            return false;
        };

        view.setOnTouchListener(animatedTouchListener);

    }

    private void setListeners() {

        wardrobeButton.setOnClickListener(v -> openWardrobe());
        startButton.setOnClickListener(v -> start());
        chaptersButton.setOnClickListener(v -> openChaptersList());
        resetButton.setOnClickListener(v -> showAlertDialog("Перезапуск", "Перезапустить игру?", "reset"));
    }

    public void showAlertDialog(String title, String note, String action) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialogView = activity.getLayoutInflater().inflate(R.layout.custom_dialog, null);

        TextView titleView = dialogView.findViewById(R.id.titleAlert);
        TextView textView = dialogView.findViewById(R.id.textAlert);

        titleView.setText(title);
        textView.setText(note);

        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();

        Button yesButton = dialogView.findViewById(R.id.yesButton);
        Button noButton = dialogView.findViewById(R.id.noButton);

        applyAnimatedTouch(yesButton);
        applyAnimatedTouch(noButton);

        yesButton.setOnClickListener(v -> {

            switch(action) {

                case "exit":
                    dialog.dismiss();
                    activity.finishAffinity();
                    break;

                case "reset": {
                    dialog.dismiss();
                    engine.reset();
                    setStartButton();
                    break;
                }

                case "menu":
                    dialog.dismiss();
                    Intent intent = new Intent(activity, MenuActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                    break;

                default:
                    return;
            }
        });

        noButton.setOnClickListener(v -> dialog.dismiss());

    }

    public void showInputAlert(Runnable onComplete) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View inputView = activity.getLayoutInflater().inflate(R.layout.custom_set_name_alert, null);

        TextInputLayout inputLayout = inputView.findViewById(R.id.textInputLayout);
        Button completeButton = inputView.findViewById(R.id.complete);
        TextInputEditText inputEditText = inputView.findViewById(R.id.inputField);

        builder.setView(inputView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        inputEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && inputEditText.getText().toString().isEmpty()) {
                String name = "Эльвия";
                inputEditText.setText(name);
                inputEditText.setSelection(name.length());
            }
        });

        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        completeButton.setOnClickListener(v -> {
            String text = inputEditText.getText() != null ? inputEditText.getText().toString().trim() : "";

            if (text.isEmpty()) {
                inputLayout.setError("Имя не может быть пустым");
            }

            else {

                savePlayerName(text);
                inputLayout.setError(null);
                dialog.dismiss();
                if (onComplete != null) onComplete.run();
            }
        });

        dialog.show();

    }

    private void savePlayerName(String name) {
        SharedPreferences sp = activity.getSharedPreferences("game process", activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("player name", name);
        editor.apply();
    }

    public void startHands() {

        final ViewGroup rootView = activity.findViewById(android.R.id.content);
        final Random random = new Random();

        int width = rootView.getWidth();
        int height = rootView.getHeight();

        ImageView image = new ImageView(activity);
        image.setImageResource(R.drawable.hand1);

        int size = 400 + random.nextInt(300);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size,size);

        int x = random.nextInt(Math.max(1, width - size));
        int y = random.nextInt(Math.max(1, height - size));

        params.leftMargin = x;
        params.topMargin = y;

        image.setLayoutParams(params);

        float rotation = -45 + random.nextInt(46);

        if (random.nextBoolean()) {
            image.setScaleX(-1f);
        } else {
            image.setScaleX(1f);
        }

        image.setRotation(rotation);

        rootView.addView(image);

        image.postDelayed(() -> {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(image, "alpha", 1f, 0f);
            fadeOut.setDuration(2000);

            fadeOut.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        rootView.removeView(image);
                    }
                });
        fadeOut.start();
        }, 1000);
    }

    public void startHandsLoop() {
        final Handler handler = new Handler(Looper.getMainLooper());

        Runnable generateHand = new Runnable() {
            @Override
            public void run() {
                startHands();
                handler.postDelayed(this, 3000);
            }
        };

        handler.post(generateHand);
    }

    public void openWardrobe() {
        Intent intent = new Intent(activity, WardrobeActivity.class);
        activity.startActivity(intent);
    }

    public void openSetting() {

        androidx.fragment.app.FragmentManager fragmentManager =
                ((androidx.appcompat.app.AppCompatActivity) activity).getSupportFragmentManager();

        if (!setting.isAdded()) {
            fragmentManager.beginTransaction()
                    .add(android.R.id.content, setting)
                    .addToBackStack(null)
                    .commit();
        }

    }

    public void openChaptersList() {
        Intent intent = new Intent(activity, ChaptersActivity.class);
        activity.startActivity(intent);
    }

    public void start() {
        Intent intent = new Intent(activity, GameActivity.class);
        activity.startActivity(intent);
    }

    public Toast createCustomToast(String message) {

        LayoutInflater inflater = LayoutInflater.from(activity);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView text = layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);

        return toast;

    }

    public void setupTop() {

        wardrobeTopButton = activity.findViewById(R.id.wardrobeButton2);
        settingTopButton = activity.findViewById(R.id.settingsButton);
        View topTouchArea = activity.findViewById(R.id.topTouchArea);

        wardrobeTopButton.setOnClickListener(v -> openWardrobe());
        settingTopButton.setOnClickListener(v -> {
            openSetting();
            engine.getVisual().getTouchArea().setEnabled(true);
        });

        settingTopButton.setVisibility(View.GONE);
        wardrobeTopButton.setVisibility(View.GONE);

        topTouchArea.setOnClickListener(v -> {

            if (settingTopButton.getVisibility() == View.GONE) {

                settingTopButton.setVisibility(View.VISIBLE);
                wardrobeTopButton.setVisibility(View.VISIBLE);

                settingTopButton.setAlpha(0.0f);
                wardrobeTopButton.setAlpha(0.0f);

                settingTopButton.animate().alpha(0.85f).setDuration(300).start();
                wardrobeTopButton.animate().alpha(0.85f).setDuration(300).start();

                new Handler().postDelayed(() -> {
                    settingTopButton.animate().alpha(0.0f).setDuration(300).withEndAction(() -> settingTopButton.setVisibility(View.GONE)).start();
                    wardrobeTopButton.animate().alpha(0.0f).setDuration(300).withEndAction(() -> wardrobeTopButton.setVisibility(View.GONE)).start();
                }, 3000);
            }
        });

    }

    public MusicManager getMusicManager() {
        return engine.getMusicManager();
    }
}
