package com.example.hiroto.gpsarapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SearchRegisterActivity extends Activity implements View.OnClickListener{
    Button button_decide;
    Button button_clear;
    Button button_back;
    EditText text_searchWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search_register);
        super.onCreate(savedInstanceState);
        button_decide = (Button)findViewById(R.id.button_decide);
        button_clear = (Button)findViewById(R.id.button_clear);
        button_back = (Button)findViewById(R.id.button_back);
        text_searchWord = (EditText)findViewById(R.id.searchWord);

        button_decide.setOnClickListener(this);
        button_clear.setOnClickListener(this);
        button_back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == button_decide){
            Intent intent = new Intent(SearchRegisterActivity.this, RegisterActivity.class);
            intent.putExtra("word",text_searchWord.getText());
            startActivity(intent);
        }else if (v == button_clear){
            text_searchWord.setText("");
//            Intent intent = new Intent(SearchRegisterActivity.this, RegisterActivity.class);
//            startActivity(intent);
        }else if (v == button_back){
            Intent intent = new Intent(SearchRegisterActivity.this, RegisterActivity.class);
            startActivity(intent);
        }else {
            Toast.makeText(this, "No detected Button", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
