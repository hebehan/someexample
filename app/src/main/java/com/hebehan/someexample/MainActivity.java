package com.hebehan.someexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hebehan.someexample.game2048.Game2048Activity;
import com.hebehan.someexample.gesturelock.Gesturelock;

public class MainActivity extends AppCompatActivity {

    private ListView titleListview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleListview = (ListView) findViewById(R.id.title_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,new String[]{"手势解锁","2048"});
        titleListview.setAdapter(adapter);
        titleListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(MainActivity.this, Gesturelock.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, Game2048Activity.class));
                        break;
                }
            }
        });
    }
}
