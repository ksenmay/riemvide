package by.may.engine;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import by.may.R;
import by.may.entity.Choice;
import by.may.entity.Fork;

public class Engine {

    private final EffectManager effect;
    private final Activity activity;
    private final GameProgress gameProgress;
    private final VisualManager visual;
    private final ActivityControl control;
    public MainCharManager charManager;
    private final MusicManager musicManager;
    public static boolean isInputLocked = false;
    public static boolean isChoice = false;

    public Engine(Activity activity) {
        this.activity = activity;
        this.control = new ActivityControl(activity, this);
        this.effect = new EffectManager(activity);
        this.gameProgress = new GameProgress(activity);
        this.visual = new VisualManager(activity, this.gameProgress);
        this.gameProgress.setControl(this.control);
        this.gameProgress.setEngine(this);
        this.musicManager = new MusicManager(activity, visual);
        this.charManager = new MainCharManager(activity);
    }

    public void init() {

        visual.componentSearch(activity);

        SharedPreferences sp = activity.getSharedPreferences("game process", activity.MODE_PRIVATE);
        int line = sp.getInt("line index", 0);

        gameProgress.startGame();
        String title = gameProgress.getCurrentChapterName();

        if (gameProgress.getCurrentSceneIndex() == 0 && line == 0) {
            effect.showIntro(title);
        }

        textClick();
        initChar();
        showCurrentLine();
    }

    public void showCurrentLine() {

        Log.d("выбор", String.valueOf(isChoice));
        Log.d("блокировка", String.valueOf(isInputLocked));

        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.window);
        visual.getTextWindow().startAnimation(anim);

        if (!isChoice) {
            visual.makeTextView();
        }

        Map<String, Object> scene = gameProgress.getCurrentScene();
        if (scene == null) return;

        List<String> lines = (List<String>) scene.get("lines");

        musicManager.updateMusic();

        if (gameProgress.getCurrentLineIndex() < lines.size()) {

            if (scene.containsKey("flag")) {
                String flag = (String) scene.get("flag");
                gameProgress.flagProcessing(flag);
                scene.remove("flag");
            }

            if (scene.containsKey("choice")) {

                isChoice = true;
                isInputLocked = true;
                String choiceLine = scene.get("choice").toString();
                Choice choice = new Choice(activity, choiceLine, this, visual);
                choice.setChoices();
                scene.remove("choice");
            }

            if (scene.containsKey("fork")) {

                Log.d("ФОРК", "форк есть радуйся");
                Fork fork = (Fork) scene.get("fork");

                if (checkStats(fork.getRequiredStats())) {
                    Log.d("ФОРК", "форк статы подходят");
                    Map<Integer, Map<String, Object>> forkScenes = fork.getForkScenes(activity);
                    scene.remove("fork");
                    gameProgress.getCurrentChapter().insertForkScenes(gameProgress.getCurrentSceneIndex(), forkScenes);
                }

            }

            String line = lines.get(gameProgress.getCurrentLineIndex());

            String playerName = visual.getCurrentName();

            if (playerName != null && !playerName.isEmpty()) {
                line = line.replace("■", playerName);
            }

            visual.getTextWindow().setTag(line);
            visual.getTextWindow().setText("");
            visual.typewriterAnimation(visual.getTextWindow(), 2);
            gameProgress.incrementLineIndex();
        }

        else {

            boolean hasNext = gameProgress.nextScene();

            if (hasNext) {
                gameProgress.resetLineIndex();
                musicManager.updateMusic();
                showCurrentLine();
            }

            else {

                if (scene.containsKey("flag")) {
                    String flag = (String) scene.get("flag");
                    gameProgress.flagProcessing(flag);
                }

                if (!gameProgress.isChapterCompleted()) {
                    visual.hideUI();
                }
            }
        }

        visual.updatesView();
        charManager.logAllStats();
    }

    public boolean checkStats(Map<String, Integer> map) {

        SharedPreferences sp = activity.getSharedPreferences("main character", Context.MODE_PRIVATE);

        for (Map.Entry<String, Integer> entry : map.entrySet()) {

            String statName = entry.getKey();
            int requiredValue = entry.getValue();
            int currentValue = sp.getInt(statName, 0);

            if (currentValue < requiredValue) {
                return false;
            }
        }
        return true;
    }

    public void textClick() {

        SoundPool soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        final int[] tap = new int[1];
        final boolean[] isLoaded = {false};

        tap[0] = soundPool.load(activity, R.raw.tap, 1);

        soundPool.setOnLoadCompleteListener((sp, id, status) -> {
            if (status == 0 && id == tap[0]) {
                isLoaded[0] = true;
            }
        });

        visual.getTouchArea().setOnClickListener(v -> {
            if (isInputLocked) return;
            if (isLoaded[0]) {
                soundPool.play(tap[0], 1f, 1f, 1, 0, 1f);
            }
            showCurrentLine();
        });
    }

    public void onPause() {
        gameProgress.saveProgressOnExit();
    }

    public void onBackPressed() {
        gameProgress.saveProgressOnExit();
        control.showAlertDialog("Выход", "Перейти в главное меню?", "menu");
    }

    public void reset() {
        gameProgress.reset();
    }

    public void makeChaptersMap() {
        gameProgress.makeChaptersMap();
    }

    public ArrayList<String> getAllTitles() {
        return gameProgress.getAllTitles();
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public VisualManager getVisual() {
        return visual;
    }

    private void initChar() {
        SharedPreferences sp = activity.getSharedPreferences("main character", Context.MODE_PRIVATE);
        boolean isFirst = sp.getBoolean("is first", false);
        if (!isFirst) {
            charManager.init();
            sp.edit().putBoolean("is first", true).apply();
        }
    }

    public ActivityControl getControl() {
        return control;
    }

}