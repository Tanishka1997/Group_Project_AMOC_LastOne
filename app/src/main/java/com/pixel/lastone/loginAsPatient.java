package com.pixel.lastone;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class loginAsPatient extends AppCompatActivity {

    TextInputEditText admNo,password;
    TextView loginPageMessage;
    Button loginButton,registerButton;
    String admNoString,passwordString;
    boolean isDataValid = false;
    SaveSharedPreference saveSharedPreference;
    Context context;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_as_patient);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        saveSharedPreference = new SaveSharedPreference();

        admNo = (TextInputEditText) findViewById(R.id.adm_no_login);
        password = (TextInputEditText) findViewById(R.id.password_login);
        loginPageMessage= (TextView) findViewById(R.id.loginPageMessage);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(getIntent());
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            loginPageMessage.setVisibility(View.INVISIBLE);
        }
        else{
            loginButton.setEnabled(false);
            registerButton.setEnabled(false);
            loginPageMessage.setText("No Internet Connection");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.login_page_refresh) {
            swipeRefreshLayout.setRefreshing(true);
            finish();
            startActivity(getIntent());
            swipeRefreshLayout.setRefreshing(false);
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private boolean validateDetails() {

        boolean validateData = true;
        // Reset errors.
        admNo.setError(null);
        password.setError(null);



        // Store values at the time of the login attempt.
        String admNoString = admNo.getText().toString();
        String passwordString = password.getText().toString();

        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passwordString)) {
            password.setError("This field is required");
            validateData = false;
            focusView = password;
        }
        // Check for a valid Admission no, if the user entered one.
        if (TextUtils.isEmpty(admNoString)) {
            admNo.setError("This field is required");
            validateData = false;
            focusView = admNo;
        }else if (!isAdmissionNoValid(admNoString)) {
            admNo.setError("This admission no is invalid");
            validateData = false;
            focusView = admNo;
        }
        if (validateData==false) {
            focusView.requestFocus();
        } else {
        }

        return validateData;
    }

    private boolean isAdmissionNoValid(String adm_no) {
        return (adm_no.length() == 8);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    public void loginCheckAsPatient(View view){
        isDataValid = validateDetails();
        if (isDataValid == true) {
            admNoString = admNo.getText().toString();
            passwordString = password.getText().toString();
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute(admNoString, passwordString);
        }
    }
    public void registerAsPatient(View view){
        Intent intent = new Intent(this,registerAsPatient.class);
        startActivity(intent);
    }


    public class BackgroundTask extends AsyncTask<String,Void,String> {
        String loginAsPatientURL;
        Context context;

        BackgroundTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            loginAsPatientURL = "http://mediworld.orgfree.com/loginAsPatient.php";
        }

        @Override
        protected String doInBackground(String... params) {
            String PATIENT_ADM_NO=params[0];
            String PATIENT_PASSWORD=params[1];

            try {
                URL url = new URL(loginAsPatientURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("PATIENT_ADM_NO", "UTF-8") +"="+URLEncoder.encode(PATIENT_ADM_NO,"UTF-8")+"&"+
                        URLEncoder.encode("PATIENT_PASSWORD", "UTF-8") +"="+URLEncoder.encode(PATIENT_PASSWORD,"UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String response = "";
                String line = "";
                while ((line=bufferedReader.readLine())!=null){
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response.trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onProgressUpdate(Void... avoid){
            super.onProgressUpdate(avoid);
        }

        @Override
        protected void onPostExecute(String result){

            Boolean loginCheck = result.equals("Login Sucessful");
            if(loginCheck)
            {
                saveSharedPreference.setUserName(context,admNoString);

                Intent intent = new Intent(context,HomeScreenPatient.class);
               // intent.putExtra("admissionNo", admNoString);
                admNo.setText("");
                password.setText("");
                loginPageMessage.setVisibility(View.INVISIBLE);
                startActivity(intent);
            }
            else
            {
                if(result.equals("Invalid Admission No or Password")) {
                    loginPageMessage.setVisibility(View.VISIBLE);
                    loginPageMessage.setText("Invalid Admission No or Password");
                }else {
                    loginPageMessage.setVisibility(View.VISIBLE);
                    loginPageMessage.setText("No Internet Access");
                }
                admNo.setText("");
                password.setText("");
            }
        }
    }

}
