package by.may.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import by.may.R;
import by.may.engine.Engine;
import by.may.settings.ChaptersAdapter;
import by.may.settings.UIsetting;

public class ChaptersActivity extends AppCompatActivity {

    RecyclerView chaptersRecyclerView;
    ChaptersAdapter adapter;
    ArrayList<String> titles;
    private UIsetting uiSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        Engine engine = new Engine(this);

        uiSetting = new UIsetting(this);
        uiSetting.hideSystemUI();

        chaptersRecyclerView = findViewById(R.id.recyclerView);
        engine.makeChaptersMap();
        titles = engine.getAllTitles();
        adapter = new ChaptersAdapter(titles);
        chaptersRecyclerView.setAdapter(adapter);
        chaptersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            uiSetting.hideSystemUI();
        }
    }

}
