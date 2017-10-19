package com.supermumu.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.supermumu.ui.widget.BubbleView;
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
                result2.setText(list.get(position));
            }
        });
        selectBar.setTabs(list.subList(0, 3));
    
        sampleView = findViewById(R.id.sample_3);
        selectBar = sampleView.findViewById(R.id.single_select_bar);
        selectBar.setSelectedColor(Color.RED);
        selectBar.setUnselectedColor(Color.BLACK);
        title = sampleView.findViewById(R.id.title);
        final TextView result3 = sampleView.findViewById(R.id.result);
        title.setText("Four items: \n - customize selected/unselected colors ");
        selectBar.setOnTabSelectListener(new SingleSelectBar.OnTabSelectListener() {
            @Override
            public void onSelect(int position, View view) {
                result3.setText(list.get(position));
            }
        });
        selectBar.setTabs(list.subList(0, 4));
    
        sampleView = findViewById(R.id.sample_4);
        selectBar = sampleView.findViewById(R.id.single_select_bar);
        title = sampleView.findViewById(R.id.title);
        final TextView result4 = sampleView.findViewById(R.id.result);
        title.setText("Five items:\n - customize height\n - customize stroke width");
        selectBar.setOnTabSelectListener(new SingleSelectBar.OnTabSelectListener() {
            @Override
            public void onSelect(int position, View view) {
                result4.setText(list.get(position));
            }
        });
        selectBar.setTabs(list.subList(0, 5), 2);
    
        BubbleView bubbleView = findViewById(R.id.bubble_view1);
        bubbleView.setBubbleCount(1);
    
        bubbleView = findViewById(R.id.bubble_view2);
        bubbleView.setBubbleCount(100);
    
        bubbleView = findViewById(R.id.bubble_view3);
        bubbleView.setBubbleCount(22, 10);
        bubbleView.clearBubbleCount();
    
        bubbleView = findViewById(R.id.bubble_view4);
        bubbleView.setBubbleCount(100, 99);
    }
}
