package by.may.entity;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import by.may.R;
import by.may.engine.ActivityControl;
import by.may.engine.EffectManager;
import by.may.engine.Engine;
import by.may.engine.GameProgress;
import by.may.engine.MainCharManager;
import by.may.engine.VisualManager;

public class Choice {

    private Activity activity;
    private VisualManager visual;
    private Engine engine;
    private String line;
    private String[] choices;
    private Button[] buttons;
    private MainCharManager charManager;
    private String[] stats;
    private int[] values;


    public Choice (Activity activity, String line, Engine engine, VisualManager visual) {
        this.line = line;
        this.activity = activity;
        this.choices = new String [4];
        this.charManager = new MainCharManager(activity);
        this.engine = engine;
        this.visual = visual;
    }

    public void setChoices() {

        visual.componentSearch(activity);
        visual.setTouchAreaEnabled(false);

        line = line.trim();
        if (line.startsWith("\"") && line.endsWith("\"")) {
            line = line.substring(1, line.length() - 1).trim();
        }

        choices = line.split("\\s*;\\s*");

        statProcessing();
        findButtons();

        for (int i = 0; i<buttons.length; i++) {

            ActivityControl.applyAnimatedTouchWithoutColor(buttons[i], R.drawable.button, activity);

            if (i < choices.length) {
                buttons[i].setText(choices[i]);
                buttons[i].setVisibility(View.VISIBLE);

                final int index = i;
                buttons[i].setOnClickListener(v -> {
                    charManager.changeStat(stats[index], values[index]);

                    for (Button b : buttons) {
                        b.setEnabled(false);
                        b.setVisibility(View.GONE);
                    }

                    visual.setTouchAreaEnabled(true);
                    Engine.isChoice = false;
                    Engine.isInputLocked = false;

                    engine.showCurrentLine();

                });
                buttons[i].setEnabled(true);
            }

            else {
                buttons[i].setVisibility(View.GONE);
                buttons[i].setEnabled(true);
            }
        }

        if (choices.length == 2) {
            visual.moveTextView(0.8f);
        }
        else if (choices.length == 4) {
            visual.moveTextView(0.6f);
        }
        else visual.moveTextView(1.0f);

    }

    private void statProcessing() {

        stats = new String[choices.length];
        values = new int[choices.length];

        for (int i = 0; i< choices.length; i++) {
            stats[i] = null;
            values[i] = 0;

            if (choices[i].contains("<stat=")) {
                String line = Chapter.extract(choices[i], "stat", "<", ">");
                String[] parts = line.split(" ");
                stats[i] = parts[0];
                values[i] = Integer.parseInt(parts[1]);
                choices[i] = choices[i].replaceAll("\\<stat=.*?\\>", "").trim();
            }

        }
    }

    private void findButtons() {

        buttons = new Button[]{
             activity.findViewById(R.id.choice_button_1),
             activity.findViewById(R.id.choice_button_2),
             activity.findViewById(R.id.choice_button_3),
             activity.findViewById(R.id.choice_button_4)
        };
    }
}
