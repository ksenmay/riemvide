package by.may.entity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Chapter {

    private String title;
    private final Map<Integer, Map<String, Object>> scenes = new HashMap<>();
    private final Map<Integer, Fork> forks = new HashMap<>();

    public void parseFile(Activity activity, String fileName) {

        String playerName = getPlayerName(activity);

        try {

            InputStream is = activity.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            List<String> lines = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {

                if (line.contains("title=")) {
                    title = extract(line, "title", "[", "]");
                    line = line.replaceAll("\\[title=.*?]", "").trim();
                }

                lines.add(line);

            }

            this.scenes.putAll(parseLines(lines, playerName, this.forks));
            reader.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Map<Integer, Map<String, Object>> parseLines(List<String> lines, String playerName, Map<Integer, Fork> forks) {

        Map<Integer, Map<String, Object>> parsedScenes = new HashMap<>();
        int currentSceneKey = -1;
        int nextSceneKey = 0;

        List<String> currentLines = new ArrayList<>();
        Map<String, Object> currentScene = null;

        for (int i  = 0; i < lines.size(); i++) {

            String line = lines.get(i).replace("{name}", playerName);

            if (line.contains("[scene")) {

                if (currentSceneKey != -1) {
                    currentScene.put("lines", currentLines);
                    parsedScenes.put(currentSceneKey, currentScene);
                }

                String sceneNumberStr = extract(line, "scene", "[", "]");
                if (!sceneNumberStr.isEmpty()) {
                    try {
                        currentSceneKey = Integer.parseInt(sceneNumberStr);
                        nextSceneKey = currentSceneKey + 1;
                    } catch (NumberFormatException e) {
                        currentSceneKey = nextSceneKey++;
                    }
                } else {
                    currentSceneKey = nextSceneKey++;
                }

                currentScene = new HashMap<>();
                currentLines = new ArrayList<>();
            }

        else if (line.contains("<fork")) {

                String forkLine = extract(line, "fork", "<", ">");
                String forkStats = extract(forkLine, "stats", "[", "]");
                Map<String, Integer> requiredStats = Fork.getStatsMap(forkStats);

                List<String> forkLines = new ArrayList<>();
                while (++i < lines.size() && !lines.get(i).equals("</fork>")) {
                    forkLines.add(lines.get(i));
                }

                Fork fork = new Fork(forkLines, requiredStats);
                forks.put(forks.size(), fork);

                if (currentScene != null) {
                    currentScene.put("fork", fork);
                }
            }

            else {
                line = processLine(line, currentScene);
                if (!line.isEmpty()) {
                    currentLines.add(line);
                }
            }
        }

        if (currentSceneKey != -1) {
            currentScene.put("lines", currentLines);
            parsedScenes.put(currentSceneKey, currentScene);
        }

        return parsedScenes;
    }

    public void insertForkScenes(int afterSceneIndex, Map<Integer, Map<String, Object>> forkScenes) {

        Map<Integer, Map<String, Object>> newScenes = new LinkedHashMap<>();
        int shift = forkScenes.size();

        for (Map.Entry<Integer, Map<String, Object>> entry : scenes.entrySet()) {
            int key = entry.getKey();

            if (key <= afterSceneIndex) {
                newScenes.put(key, entry.getValue());
            }

            else {
                newScenes.put(key + shift, entry.getValue());
            }
        }

        int insertIndex = afterSceneIndex + 1;
        for (Map.Entry<Integer, Map<String, Object>> entry : forkScenes.entrySet()) {
            newScenes.put(insertIndex++, entry.getValue());
        }

        scenes.clear();
        scenes.putAll(newScenes);
    }


    public static String getPlayerName(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences("game process", Context.MODE_PRIVATE);
        return sp.getString("player name", "■");
    }

    public static String processLine(String line, Map<String, Object> currentScene) {

        String[][] tags = {
                {"background", "\\[background=.*?\\]"},
                {"char", "\\[char=.*?\\]"},
                {"name", "\\[name=.*?\\]"},
                {"music", "\\[music=.*?\\]"},
                {"flag", "\\[flag=.*?\\]"},
                {"choice", "\\[choice=.*?\\]"}
        };

        for (String[] tag: tags) {
            String key = tag[0];
            String regex = tag[1];

            if (line.contains("[" + key + "=")) {
                currentScene.put(key, extract(line, key, "[", "]"));
                line = line.replaceAll(regex, "").trim();
            }

        }

        return line;

    }

    public static String extract(String line, String key, String open, String close) {
        String search = open + key + "=";
        int start = line.indexOf(search);
        if (start == -1) {
            return "";
        }
        start += search.length();
        int end = line.indexOf(close, start);
        if (end == -1) {
            return "";
        }
        return line.substring(start, end);
    }


    public Map<Integer, Map<String, Object>> getScenes() {
        return scenes;
    }

    public String getTitle() {
        return title;
    }
}

