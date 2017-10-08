package com.supermumu.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.supermumu.ui.widget.SingleSelectBoard;

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
        SingleSelectBoard selectBoard = sampleView.findViewById(R.id.single_select_board);
        selectBoard.setItems(list.subList(0, 2));
        TextView title = sampleView.findViewById(R.id.title);
        final TextView result1 = sampleView.findViewById(R.id.result);
        title.setText("Two items(default style)");
        selectBoard.setOnItemSelectedListener(new SingleSelectBoard.OnItemSelectedListener() {
            @Override
            public void onClickListener(int position, View view) {
                result1.setText(list.get(position));
            }
        });
    
        sampleView = findViewById(R.id.sample_2);
        selectBoard = sampleView.findViewById(R.id.single_select_board);
        selectBoard.setItems(list.subList(0, 3));
        title = sampleView.findViewById(R.id.title);
        final TextView result2 = sampleView.findViewById(R.id.result);
        title.setText("Three items:\n1. bold text\n2. large text");
        selectBoard.setOnItemSelectedListener(new SingleSelectBoard.OnItemSelectedListener() {
            @Override
            public void onClickListener(int position, View view) {
                result2.setText(list.get(position));
            }
        });
    
        sampleView = findViewById(R.id.sample_3);
        selectBoard = sampleView.findViewById(R.id.single_select_board);
        selectBoard.setItems(list.subList(0, 4));
        selectBoard.setSelectedColor(Color.RED);
        selectBoard.setUnselectedColor(Color.BLACK);
        title = sampleView.findViewById(R.id.title);
        final TextView result3 = sampleView.findViewById(R.id.result);
        title.setText("Four items: \n1. custom selected/unselected colors");
        selectBoard.setOnItemSelectedListener(new SingleSelectBoard.OnItemSelectedListener() {
            @Override
            public void onClickListener(int position, View view) {
                result3.setText(list.get(position));
            }
        });
    
        sampleView = findViewById(R.id.sample_4);
        selectBoard = sampleView.findViewById(R.id.single_select_board);
        selectBoard.setItems(list.subList(0, 5));
        title = sampleView.findViewById(R.id.title);
        final TextView result4 = sampleView.findViewById(R.id.result);
        title.setText("Five items:\n1. custom height of board");
        selectBoard.setOnItemSelectedListener(new SingleSelectBoard.OnItemSelectedListener() {
            @Override
            public void onClickListener(int position, View view) {
                result4.setText(list.get(position));
            }
        });
    }
}
