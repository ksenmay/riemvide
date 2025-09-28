package by.may.engine;

import android.app.Activity;
import android.media.MediaPlayer;

public class MusicManager {

    private MediaPlayer backMelody;
    private Activity activity;
    private VisualManager visual;
    private String currentMusicName = null;

    public MusicManager(Activity activity, VisualManager visual) {
        this.activity = activity;
        this.visual = visual;
    }

    public void updateMusic() {
        String music = visual.getCurrentMusic();

        if (music == null) {
            if (currentMusicName == null) {
                loadSavedMusic();
            }
            return;
        }

        if (music.equals(currentMusicName)) {
            return;
        }

        setMusic(music);
    }


    public void setMusic(String music) {

        int resId = activity.getResources().getIdentifier(music, "raw", activity.getPackageName());

        if (resId == 0) {
            stopMusic();
            currentMusicName = null;
            return;
        }

        if (backMelody != null) {
            backMelody.stop();
            backMelody.release();
            backMelody = null;
        }

        backMelody = MediaPlayer.create(activity, resId);
        if (backMelody == null) {
            currentMusicName = null;
            return;
        }

        currentMusicName = music;
        backMelody.setLooping(true);
        backMelody.start();
    }

    public void saveCurrentMusic() {
        activity.getSharedPreferences("game process", Activity.MODE_PRIVATE).edit().putString("current music", currentMusicName).apply();
    }

    public void loadSavedMusic() {
        String music = activity.getSharedPreferences("game process", Activity.MODE_PRIVATE).getString("current music", null);

        if (music != null) {
            setMusic(music);
        }
    }

    public void pauseMusic() {
        if (backMelody != null && backMelody.isPlaying()) {
            backMelody.pause();
        }
    }

    public void resumeMusic() {
        if (backMelody != null) {
            backMelody.start();
        }
    }

    public void stopMusic() {
        if (backMelody != null) {
            backMelody.stop();
            backMelody.release();
            backMelody = null;
        }
    }

    public void disableMusic(boolean flag) {

        if (!flag) {
            backMelody.pause();
        }
        else {
            backMelody.start();

        }
    }
}
