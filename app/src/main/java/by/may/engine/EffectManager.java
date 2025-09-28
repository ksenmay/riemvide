package by.may.engine;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import by.may.R;
import by.may.activities.MenuActivity;

public class EffectManager {

    private final Activity activity;
    private View darkOverlay;
    private TextView titleView;

    public EffectManager(Activity activity) {
        this.activity = activity;
    }

    public void showIntro(String title) {

        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.transparation_out);
        componentSearch(activity);

        titleView.setText(title);
        darkOverlay.setVisibility(View.VISIBLE);
        titleView.setVisibility(View.VISIBLE);

        darkOverlay.setOnClickListener(v -> {
            darkOverlay.startAnimation(anim);
            titleView.startAnimation(anim);

            darkOverlay.setVisibility(View.INVISIBLE);
            titleView.setVisibility(View.INVISIBLE);

        });
    }

    public void showOutro() {

        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.transparation_out);

        componentSearch(activity);
        titleView.setText("Конец главы");
        darkOverlay.setVisibility(View.VISIBLE);
        titleView.setVisibility(View.VISIBLE);

        Intent intent = new Intent(activity, MenuActivity.class);

        darkOverlay.setOnClickListener(v -> {
            darkOverlay.startAnimation(anim);
            titleView.startAnimation(anim);

            darkOverlay.setVisibility(View.INVISIBLE);
            titleView.setVisibility(View.INVISIBLE);

            activity.startActivity(intent);
            activity.finish();

        });
    }

    private void componentSearch(Activity activity) {

        darkOverlay = activity.findViewById(R.id.darkOverlay);
        titleView = activity.findViewById(R.id.chapterName);

    }

}
