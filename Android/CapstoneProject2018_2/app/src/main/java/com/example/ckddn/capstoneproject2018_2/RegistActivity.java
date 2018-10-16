package com.example.ckddn.capstoneproject2018_2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RegistActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton user_radio;
    private RadioButton parent_radio;
    private EditText name;
    private EditText id;
    private EditText password;
    private EditText mobile;
    //    private boolean regFlag = false;    //  flag that check the available ID
    private String tempId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        radioGroup = (RadioGroup) findViewById(R.id.type);
        user_radio = (RadioButton) findViewById(R.id.user_radio_btn);
        parent_radio = (RadioButton) findViewById(R.id.parent_radio_btn);
        name = (EditText) findViewById(R.id.name);
        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        mobile = (EditText) findViewById(R.id.mobile);

        Button sign_up_btn = (Button) findViewById(R.id.sign_up_button);
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFilled()) {
                    Log.d("Reg Main", "onClick: sign up btn");
                    new SignUpTask().execute("http://" + ServerInfo.ipAddress + "/register");
                }
            }
        });

        Button check_btn = (Button) findViewById(R.id.check_button);    //  check
        check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() == -1 || id.getText().toString() == "") {  //  id 랑 radiobut 클릭 확인
                    alertToast("please check the user or parent button and fill in the ID text");
//                    regFlag = false;
                } else if (tempId != id.getText().toString()) {    //  인증받은 id라면 만족하면 중복확인
                    new CheckIDTask().execute("http://" + ServerInfo.ipAddress + "/check/id");
                }
            }
        });
    }
    /*  Sign Up area is Filled? */
    private boolean isFilled() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            alertToast("please check the user or parent button");
            return false;
        }
        if (name.getText().toString().equals("")) {
            alertToast("please fill in the name text");
            return false;
        }
        if (!(id.getText().toString().equals(tempId)) || tempId == ""){
            alertToast("please fill in the ID text\ntempId : " + tempId);
            return false;
        }
        if (password.getText().toString().equals("")) {
            alertToast("please fill in the PW text");
            return false;
        }
        if (mobile.getText().toString().equals("")) {
            alertToast("please fill in the mobile text");
            return false;
        }

        return true;
    }

    public class CheckIDTask extends AsyncTask<String, String, String> {
        String TAG = "CheckIDTask>>>";
        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject regInfo = new JSONObject();
                regInfo.accumulate("id", id.getText().toString());
                if (user_radio.isChecked())   //  user 0
                    regInfo.accumulate("type", 0);
                else if (parent_radio.isChecked()) //  parent 1
                    regInfo.accumulate("type", 1);
                Log.d(TAG, "doInBackground: create json");
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(strings[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cache-Control", "no-cache");        // 컨트롤 캐쉬 설정(?)
                    conn.setRequestProperty("Content-Type", "application/json"); // json형식 전달
                    conn.setRequestProperty("Accept", "application/text");       // text형식 수신
                    conn.setRequestProperty("Accept", "application/json");       // json형식 수신
                    conn.setDoOutput(true); //  OutputStream으로 POST데이터 전송
                    conn.setDoInput(true);  //  InputStream으로 서버로부터 응답 전달받음
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    writer.write(regInfo.toString());
                    writer.flush();
                    writer.close(); //  submit regInfo

                    InputStream stream = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    /* Implement */
                    /* Should set form of json file when register step is finished... */
                    while((line = reader.readLine()) != null) {
                        //  readLine : string or null(if end of data...)
                        buffer.append(line);
                        Log.d(TAG, "doInBackground: readLine, " + line);
                    }
                    return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("")) { //  가능!    undefined == ""
                Log.d(TAG, "onPostExecute: available!");
                tempId = id.getText().toString();
                alertToast("tempID : " + tempId);
            } else {    //  불가능! : 이미존재
                alertToast("not available ID");
            }
        }
    }

    public class SignUpTask extends AsyncTask<String, String, String> {
        String TAG = "SignUpTask>>>";
        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject regInfo = new JSONObject();
                regInfo.accumulate("id", id.getText().toString());
                regInfo.accumulate("pw", password.getText().toString());
                regInfo.accumulate("name", name.getText().toString());
                regInfo.accumulate("mobile", mobile.getText().toString());
                if (user_radio.isChecked())   //  user 0
                    regInfo.accumulate("type", 0);
                else if (parent_radio.isChecked()) //  parent 1
                    regInfo.accumulate("type", 1);
                Log.d(TAG, "doInBackground: create json" + regInfo.toString());
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(strings[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cache-Control", "no-cache");        // 컨트롤 캐쉬 설정(?)
                    conn.setRequestProperty("Content-Type", "application/json"); // json형식 전달
                    conn.setRequestProperty("Accept", "application/text");       // text형식 수신
                    conn.setRequestProperty("Accept", "application/json");       // json형식 수신
                    conn.setDoOutput(true); //  OutputStream으로 POST데이터 전송
                    conn.setDoInput(true);  //  InputStream으로 서버로부터 응답 전달받음
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    writer.write(regInfo.toString());
                    writer.flush();
                    writer.close(); //  submit regInfo

                    InputStream stream = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    /* Implement */
                    /* Should set form of json file when register step is finished... */
                    while((line = reader.readLine()) != null) {
                        //  readLine : string or null(if end of data...)
                        buffer.append(line);
                        Log.d(TAG, "doInBackground: readLine, " + line);
                    }
                    return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute: result = " + result);
            if (result.equals(id.getText().toString())) //  성공
                finish();
        }
    }

    private void alertToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}