package com.pixel.lastone;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

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

/**
 * Created by Shailesh on 04-Apr-16.
 */
public class FragmentEditProfile extends Libs  {

    public static String admNoImportedString = null;
    TextInputEditText password, contact, email, height, weight, metabolicDisorders;
    Button reset,submit;
    boolean isDataValid = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_profile_fragment, container, false);
    }

    public void setAdmissionNo(String AdmNo) {
        admNoImportedString = AdmNo;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        password = (TextInputEditText)getActivity().findViewById(R.id.password_update);
        contact = (TextInputEditText)getActivity(). findViewById(R.id.contact_update);
        email = (TextInputEditText)getActivity().findViewById(R.id.email_update);
        height = (TextInputEditText)getActivity().findViewById(R.id.height_update);
        weight = (TextInputEditText)getActivity().findViewById(R.id.weight_update);
        metabolicDisorders = (TextInputEditText)getActivity().findViewById(R.id.metabolicDisorders_update);

        reset = (Button)getActivity().findViewById(R.id.reset);
        submit = (Button)getActivity().findViewById(R.id.submitAsPatient);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAsPatient();
            }
        });
    }

    public void submitAsPatient() {

        isDataValid = validateDetails();
        if (isDataValid == true) {

            String passwordString = password.getText().toString();
            String contactString = contact.getText().toString();
            String emailString = email.getText().toString();
            String heightString = height.getText().toString();
            String weightString = weight.getText().toString();
            String metabolicDisordersString = metabolicDisorders.getText().toString();

            BackgroundTask backgroundTask = new BackgroundTask(getContext());
            backgroundTask.execute(admNoImportedString, passwordString, contactString, emailString, heightString, weightString, metabolicDisordersString);
            reset();
        }
    }

    public void reset() {

        password.setText("");
        contact.setText("");
        email.setText("");
        height.setText("");
        weight.setText("");
        metabolicDisorders.setText("");

        password.setError(null);
        contact.setError(null);
        email.setError(null);
        height.setError(null);
        weight.setError(null);
        metabolicDisorders.setError(null);
    }

    public class BackgroundTask extends AsyncTask<String, Void, String> {
        String updatePatientDetailsURL;
        Context context;

        BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            updatePatientDetailsURL= "http://mediworld.orgfree.com/updatePatientDetails.php";
        }

        @Override
        protected String doInBackground(String... params) {
            String PATIENT_ADM_NO = params[0];
            String PATIENT_PASSWORD = params[1];
            String PATIENT_CONTACT_NO = params[2];
            String PATIENT_EMAIL = params[3];
            String PATIENT_HEIGHT = params[4];
            String PATIENT_WEIGHT = params[5];
            String PATIENT_METABOLIC_DISORDERS = params[6];
            try {
                URL url = new URL(updatePatientDetailsURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("PATIENT_ADM_NO", "UTF-8") + "=" + URLEncoder.encode(PATIENT_ADM_NO, "UTF-8") + "&" +
                        URLEncoder.encode("PATIENT_PASSWORD", "UTF-8") + "=" + URLEncoder.encode(PATIENT_PASSWORD, "UTF-8") + "&" +
                        URLEncoder.encode("PATIENT_CONTACT_NO", "UTF-8") + "=" + URLEncoder.encode(PATIENT_CONTACT_NO, "UTF-8") + "&" +
                        URLEncoder.encode("PATIENT_EMAIL", "UTF-8") + "=" + URLEncoder.encode(PATIENT_EMAIL, "UTF-8") + "&" +
                        URLEncoder.encode("PATIENT_HEIGHT", "UTF-8") + "=" + URLEncoder.encode(PATIENT_HEIGHT, "UTF-8") + "&" +
                        URLEncoder.encode("PATIENT_WEIGHT", "UTF-8") + "=" + URLEncoder.encode(PATIENT_WEIGHT, "UTF-8") + "&" +
                        URLEncoder.encode("PATIENT_METABOLIC_DISORDERS", "UTF-8") + "=" + URLEncoder.encode(PATIENT_METABOLIC_DISORDERS, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                response = bufferedReader.readLine();
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Not Updateded!!!";
        }

        @Override
        protected void onProgressUpdate(Void... avoid) {
            super.onProgressUpdate(avoid);
        }

        @Override
        protected void onPostExecute(String result) {
            Message.message(context, result);
        }
    }

    private boolean validateDetails() {

        boolean validateData = true;

        // Reset errors.
        password.setError(null);
        contact.setError(null);
        email.setError(null);
        height.setError(null);
        weight.setError(null);
        metabolicDisorders.setError(null);

        // Store values at the time of the login attempt.
        String passwordString = password.getText().toString();
        String contactString = contact.getText().toString();
        String emailString = email.getText().toString();
        String heightString = height.getText().toString();
        String weightString = weight.getText().toString();
        String metabolicDisordersString = metabolicDisorders.getText().toString();

        View focusView = null;

        if (TextUtils.isEmpty(metabolicDisordersString)) {
            metabolicDisorders.setError("This field is required");
            validateData = false;
            focusView = metabolicDisorders;
        }

        if (TextUtils.isEmpty(weightString)) {
            weight.setError("This field is required");
            validateData = false;
            focusView = weight;
        }

        if (TextUtils.isEmpty(heightString)) {
            height.setError("This field is required");
            validateData = false;
            focusView = height;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailString)) {
            email.setError("This field is required");
            validateData = false;
            focusView = email;
        } else if (!isEmailValid(emailString)) {
            email.setError("This email address is invalid");
            validateData = false;
            focusView = email;
        }

        // Check for a valid Mobile no, if the user entered one.
        if (TextUtils.isEmpty(contactString)) {
            contact.setError("This field is required");
            validateData = false;
            focusView = contact;
        } else if (!isContactValid(contactString)) {
            contact.setError("This mobile no is invalid");
            validateData = false;
            focusView = contact;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passwordString)) {
            password.setError("This field is required");
            validateData = false;
            focusView = password;
        } else if (!isPasswordValid(passwordString)) {
            password.setError("This password  is too short");
            validateData = false;
            focusView = password;
        }

        if (validateData==false) {
            focusView.requestFocus();
        }
        // Check for a valid Admission no, if the user entered one.
        return validateData;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isContactValid(String mobile_no) {
        return (mobile_no.length() == 10);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

}
