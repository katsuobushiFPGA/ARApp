package com.example.hiroto.gpsarapp;

        import android.app.Activity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;

/**
 * スポットの登録最終処理を行うクラス
 */
public class RegisterActivity extends Activity implements View.OnClickListener{
    Button button_edit;
    Button button_add;
    Button button_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        button_edit = (Button) findViewById(R.id.button_edit_register);
        button_add = (Button) findViewById(R.id.button_add_register);
        button_cancel = (Button) findViewById(R.id.button_cancel_register);
    }
    @Override
    public void onClick(View v) {
        if (v == button_edit){
        }else if (v == button_add){
        }else if (v == button_cancel){
        }else {
            Toast.makeText(this, "No detected Button", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
