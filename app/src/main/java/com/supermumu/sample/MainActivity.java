package com.supermumu.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.supermumu.ui.widget.SingleSelectBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final ArrayList<CharSequence> list = new ArrayList<>(5);
        list.add("ONE");
        list.add("TWO");
        list.add("THREE");
        list.add("FOUR");
        list.add("FIVE");
        
        View sampleView = findViewById(R.id.sample_1);
        SingleSelectBar selectBar = sampleView.findViewById(R.id.single_select_bar);
        TextView title = sampleView.findViewById(R.id.title);
        final TextView result1 = sampleView.findViewById(R.id.result);
        title.setText("Two items(default style)");
        selectBar.setOnTabSelectListener(new SingleSelectBar.OnTabSelectListener() {
            @Override
            public void onSelect(int position, View view) {
                Toast.makeText(MainActivity.this, "view id: "+view.getId(), Toast.LENGTH_SHORT).show();
                result1.setText(list.get(position));
            }
        });
        selectBar.setTabs(list.subList(0, 2));
    
        sampleView = findViewById(R.id.sample_2);
        selectBar = sampleView.findViewById(R.id.single_select_bar);
        title = sampleView.findViewById(R.id.title);
        final TextView result2 = sampleView.findViewById(R.id.result);
        title.setText("Three items:\n - customize text appearance");
        selectBar.setOnTabSelectListener(new SingleSelectBar.OnTabSelectListener() {
            @Override
            public void onSelect(int position, View view) {
                Toast.makeText(MainActivity.this, "view id: "+view.getId(), Toast.LENGTH_SHORT).show();
                result2.setText(list.get(position));
            }
        });
        selectBar.setTabs(list.subList(0, 3));
    
        sampleView = findViewById(R.id.sample_3);
        selectBar = sampleView.findViewById(R.id.single_select_bar);
        selectBar.setSelectedColor(Color.RED);
        selectBar.setUnselectedColor(Color.CYAN);
        title = sampleView.findViewById(R.id.title);
        final TextView result3 = sampleView.findViewById(R.id.result);
        title.setText("Four items: " +
                "\n - customize selected/unselected colors" +
                "\n - customize pressed effect colors=WHITE");
        selectBar.setPressedEffectStyle(SingleSelectBar.LIGHT);
        selectBar.setOnTabSelectListener(new SingleSelectBar.OnTabSelectListener() {
            @Override
            public void onSelect(int position, View view) {
                Toast.makeText(MainActivity.this, "view id: "+view.getId(), Toast.LENGTH_SHORT).show();
                result3.setText(list.get(position));
            }
        });
        selectBar.setTabs(list.subList(0, 4));
    
        sampleView = findViewById(R.id.sample_4);
        selectBar = sampleView.findViewById(R.id.single_select_bar);
        title = sampleView.findViewById(R.id.title);
        final TextView result4 = sampleView.findViewById(R.id.result);
        title.setText("Five items:" +
                "\n - customize height=100dp" +
                "\n - customize stroke width=3dp" +
                "\n - customize round corner radius=180px" +
                "\n - disable pressed effect=none ");
        selectBar.setOnTabSelectListener(new SingleSelectBar.OnTabSelectListener() {
            @Override
            public void onSelect(int position, View view) {
                Toast.makeText(MainActivity.this, "view id: "+view.getId(), Toast.LENGTH_SHORT).show();
                result4.setText(list.get(position));
            }
        });
        selectBar.setTabs(list.subList(0, 5), 2);
        
        for (int i=0; i<5; i++) {
            selectBar.setTabId(i, i+1);
        }
    
//        BubbleView bubbleView = findViewById(R.id.bubble_view1);
//        bubbleView.setBubbleCount(1);
//
//        bubbleView = findViewById(R.id.bubble_view2);
//        bubbleView.setBubbleCount(100);
//
//        bubbleView = findViewById(R.id.bubble_view3);
//        bubbleView.setBubbleCount(22, 10);
//        bubbleView.clearBubbleCount();
//
//        bubbleView = findViewById(R.id.bubble_view4);
//        bubbleView.setBubbleCount(100, 99);
    }
}
