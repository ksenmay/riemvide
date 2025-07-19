package by.may.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import by.may.R;
import by.may.engine.ActivityControl;
import by.may.engine.Engine;
import by.may.engine.MusicManager;

public class SettingFragment extends Fragment {

    private ActivityControl control;
    private MusicManager musicManager;
    private boolean isSettingOpen = false;

    public SettingFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        control = ((GameActivity) getActivity()).getControl();
        control.getEngine().getVisual().getTouchArea().setEnabled(false);
        isSettingOpen = true;
        musicManager = ((GameActivity) requireActivity()).getControl().getMusicManager();

        View view = inflater.inflate(R.layout.setting, container, false);
        ImageButton closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> { getParentFragmentManager().popBackStack();});

        Switch musicSwitch = view.findViewById(R.id.music_switch);
        musicSwitch.setChecked(true);

        musicSwitch.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                musicManager.disableMusic(true);
            }
            else {
                musicManager.disableMusic(false);
            }
        });

        Button resetButton = view.findViewById(R.id.reset_button_2);
        resetButton.setOnClickListener(v -> {
            control.showAlertDialog("Перезапуск", "Перезапустить игру?", "reset");
        });

        Button wardrobeButton = view.findViewById(R.id.wardrobe_button_2);
        wardrobeButton.setOnClickListener(v -> {
            control.openWardrobe();
        });

        control.applyAnimatedTouch(resetButton);
        control.applyAnimatedTouch(wardrobeButton);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (control != null) {
            control.getEngine().getVisual().getTouchArea().setEnabled(true);
        }
        isSettingOpen = false;
    }

}
