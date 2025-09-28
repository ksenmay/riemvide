package by.may.engine;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import by.may.entity.Chapter;

public class GameProgress {

    private Engine engine;
    private final Activity activity;
    private ActivityControl control;
    private final EffectManager effect;
    private final Map<Integer, Chapter> chapters;
    private String currentChapterName;
    private int currentSceneIndex;
    private int currentLineIndex = 0;
    private int currentChapterIndex = 0;
    private Chapter currentChapter;
    private boolean chapterCompleted = false;

    public GameProgress(Activity activity) {
        this.chapters = new HashMap<>();
        this.activity = activity;
        this.effect = new EffectManager(activity);
    }

    public void startGame() {

        makeChaptersMap();
        loadProgress();

        if (currentChapter == null) {
            loadChapter(0);
            currentSceneIndex = 0;
        }
    }

    public boolean nextScene() {

        if (currentChapter == null) return false;

        if (currentSceneIndex + 1 < currentChapter.getScenes().size()) {
            currentSceneIndex++;

            if (isChapterCompleted()) {
                effect.showOutro();
            }

            return true;
        }
        return false;
    }

    public void saveProgress() {

        SharedPreferences sp = activity.getSharedPreferences("game process", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("chapter name", currentChapterName);
        editor.putInt("current scene", currentSceneIndex);
        editor.putInt("line index", currentLineIndex);

        editor.apply();

    }

    public void loadProgress() {

        SharedPreferences sp = activity.getSharedPreferences("game process", Context.MODE_PRIVATE);

        String title = sp.getString("chapter name",null);
        int currentScene = sp.getInt("current scene", 0);
        int lineIndex = sp.getInt("line index", 0);

        if (title == null) {

            loadChapter(0);
            currentSceneIndex = 0;
            currentLineIndex = 0;
            return;

        }

        for (Map.Entry<Integer, Chapter> entry: chapters.entrySet()) {

            if (entry.getValue().getTitle().equals(title)) {

                loadChapter(entry.getKey());
                this.currentSceneIndex = currentScene;
                this.currentLineIndex = Math.max(0, lineIndex - 1);
                break;

            }
        }

    }

    public void saveProgressOnExit() {
        if (currentChapter == null) return;

        SharedPreferences prefs = activity.getSharedPreferences("game process", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("chapter name", currentChapter.getTitle());
        editor.putInt("current scene", currentSceneIndex);
        editor.putInt("line index", currentLineIndex);

        editor.apply();
    }

    public void setControl(ActivityControl control) {
        this.control = control;
    }

    public void flagProcessing(String flag) {

        switch (flag) {

            case "end":
                markChapterCompleted(getCurrentChapterIndex());

                if (currentChapterIndex + 1 < chapters.size()) {
                    loadChapter(currentChapterIndex + 1);
                    currentSceneIndex = 0;
                    currentLineIndex = 0;}

                saveProgress();
                setChapterCompleted(true);
                break;

            case "setName":

                engine.isInputLocked = true;

                new Handler(Looper.getMainLooper()).postDelayed(() -> control.showInputAlert(() -> activity.runOnUiThread(() -> {
                    engine.isInputLocked = false;
                    engine.showCurrentLine();
                })), 2000);

                break;

            default:
        }
    }

    private void markChapterCompleted(int index) {

        SharedPreferences sp = activity.getSharedPreferences("game process", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("chapter " + index +" completed", true);
        editor.apply();

    }

    public void reset() {
        currentChapter = null;
        currentChapterName = null;
        currentSceneIndex = 0;
        currentLineIndex = 0;

        SharedPreferences sp1 = activity.getSharedPreferences("game process", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sp1.edit();
        editor1.clear().apply();

        SharedPreferences sp2 = activity.getSharedPreferences("main character", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sp2.edit();
        editor2.clear().apply();
    }

    public void loadChapter(int index) {

        Chapter chapter = chapters.get(index);

        if (chapter != null) {
            this.currentChapter = chapter;
            this.currentSceneIndex = 0;
            this.currentChapterName = chapter.getTitle();
            this.currentChapterIndex = index;
        }

    }

    public void makeChaptersMap() {

        String[] fileNames = listFiles();

        for (int i = 0; i < fileNames.length; i++) {

            Chapter chapter = new Chapter();
            chapter.parseFile(activity, fileNames[i]);

            chapters.put(i, chapter);

        }

    }

    public Chapter getCurrentChapter() {
        return currentChapter;
    }

    public Map<Integer, Chapter> getChapters() {
        return chapters;
    }

    public ArrayList<String> getAllTitles() {

        Map<Integer, Chapter> chapters = getChapters();
        ArrayList<String> titles = new ArrayList<>();

        List<Integer> keys = new ArrayList<>(chapters.keySet());
        Collections.sort(keys);

        for (int key : keys) {
            titles.add(Objects.requireNonNull(chapters.get(key)).getTitle());
        }

        return titles;
    }

    public int getCurrentChapterIndex() {
        return currentChapterIndex;
    }

    public void setChapterCompleted(Boolean completed) {
        chapterCompleted = completed;
    }

    public Map<String, Object> getCurrentScene() {
        if (currentChapter == null) return null;
        return currentChapter.getScenes().get(currentSceneIndex);
    }

    public int getCurrentSceneIndex() {
        return currentSceneIndex;
    }

    public String getCurrentChapterName() {
        return currentChapterName;
    }

    public Boolean isChapterCompleted() {
        return chapterCompleted;
    }

    public int getCurrentLineIndex() {
        return currentLineIndex;
    }

    public void incrementLineIndex() {
        currentLineIndex++;
    }

    public void resetLineIndex() {
        currentLineIndex = 0;
    }

    public String[] listFiles() {

        AssetManager assetManager = activity.getAssets();
        ArrayList<String> chapterFiles = new ArrayList<>();

        try {
            String[] files = assetManager.list("");
            assert files != null;
            for (String file : files) {
                if (file.endsWith(".txt")) {
                    chapterFiles.add(file);
                }
            }
        }

        catch (IOException e) {
            return new String[0];
        }

        return chapterFiles.toArray(new String[0]);

    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

}
