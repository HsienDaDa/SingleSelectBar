package com.supermumu.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.supermumu.ui.widget.SingleSelectBoard;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        ArrayList<CharSequence> list = new ArrayList<>(5);
        list.add("ONE");
        list.add("TWO");
        list.add("THREE");
        list.add("FOUR");
//        list.add("FIVE");
        
        SingleSelectBoard board = findViewById(R.id.single_select_board);
        board.setDisplayText(list);
        board.setClickListener(new SingleSelectBoard.IButtonClickListener() {
    
            @Override
            public void onClickListener(int position, View view) {
                Toast.makeText(getApplicationContext(), "click: "+position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
