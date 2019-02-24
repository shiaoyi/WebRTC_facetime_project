package org.appspot.apprtc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;
import java.net.URL;

public class CreditCard extends AppCompatActivity {

    private Button button_editp;
    private Button button_oneh;
    private Button button_three_h;
    private Button button_fiveh;
    private Button button_onet;
    private TextView card_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credit_card);

        button_editp = (Button)findViewById(R.id.button_editp);
        button_editp.setOnClickListener(listener_editp);
        card_id = (TextView)findViewById(R.id.card_id);
        card_id.setText(getCard_id());
    }
    private String getCard_id() {
        String cid="";
        try{

        }catch(Exception e){
            Log.e("log_tag", e.toString());
        }
        return "···· ···· ···· 1234";
    };

    private Button.OnClickListener listener_editp= new Button.OnClickListener(){
        @Override
        public void onClick(View arg0){

        }
    };
}
