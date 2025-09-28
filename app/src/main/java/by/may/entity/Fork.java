package by.may.entity;

import android.app.Activity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fork {

    private List<String> lines;
    private Map<String, Integer> requiredStats;

    public Fork(List<String> lines, Map<String, Integer> requiredStats) {
        this.lines = lines;
        this.requiredStats = requiredStats;
    }

    public Map<Integer, Map<String, Object>> getForkScenes(Activity activity) {

        Map<Integer, Map<String, Object>> forkScenes;
        String playerName = Chapter.getPlayerName(activity);

        forkScenes = Chapter.parseLines(lines, playerName, null);
        return forkScenes;
    }

    public Map<String, Integer> getRequiredStats() {
        return requiredStats;
    }

    public static Map<String, Integer> getStatsMap(String forkStats) {

        Map<String, Integer> statsMap = new HashMap<>();

        String[] statsArray = forkStats.split(";");
        for (String str: statsArray) {

            str = str.trim();
            String[] temp = str.split(" ");

            String stat = temp[0];
            int value = Integer.parseInt(temp[1]);

            statsMap.put(stat, value);

        }
        return statsMap;
    }
}
