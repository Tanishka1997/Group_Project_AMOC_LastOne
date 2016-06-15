package com.pixel.lastone;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class FragmentProfile extends Libs {

    public static String admNoImportedString = null;
    TextView admNoShow, nameShow, contactNoShow, emailShow, genderShow, dobShow, bloodGroupShow, heightShow, weightShow, metabolicDisorderShow;
    JSONObject jsonObject;
    JSONArray jsonArray;
    String JSON_STRING = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    public void setAdmissionNo(String AdmNo) {
        admNoImportedString = AdmNo;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        admNoShow = (TextView) getActivity().findViewById(R.id.admNoShow);
        nameShow = (TextView) getActivity().findViewById(R.id.nameShow);
        contactNoShow = (TextView) getActivity().findViewById(R.id.contactNoShow);
        emailShow = (TextView) getActivity().findViewById(R.id.emailShow);
        genderShow = (TextView) getActivity().findViewById(R.id.genderShow);
        dobShow = (TextView) getActivity().findViewById(R.id.dobShow);
        bloodGroupShow = (TextView) getActivity().findViewById(R.id.bloodGroupShow);
        heightShow = (TextView) getActivity().findViewById(R.id.heightShow);
        weightShow = (TextView) getActivity().findViewById(R.id.weightShow);
        metabolicDisorderShow = (TextView) getActivity().findViewById(R.id.metabolicDisorderShow);
        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        backgroundTask.execute(admNoImportedString);

    }

    public class BackgroundTask extends AsyncTask<String, Void, String[]> {
        String jsonGetPatientDetailsURL;
        Context context;

        BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            jsonGetPatientDetailsURL = "http://mediworld.orgfree.com/jsonGetPatientDetails.php";
        }

        @Override
        protected String[] doInBackground(String... params) {
            String PATIENT_ADM_NO = params[0];
            try {
                URL url = new URL(jsonGetPatientDetailsURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("PATIENT_ADM_NO", "UTF-8") + "=" + URLEncoder.encode(PATIENT_ADM_NO, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {

                    stringBuilder.append(JSON_STRING + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                JSON_STRING = stringBuilder.toString().trim();

                if (JSON_STRING == "") {
                    Message.message(context, "JSON_STRING is null");
                } else {
                    jsonObject = new JSONObject(JSON_STRING);
                    jsonArray = jsonObject.getJSONArray("Patient_Details_From_Server");
                    String[] details = {"", "", "", "", "", "", "", "", "", "", ""};
                    JSONObject JO = jsonArray.getJSONObject(0);
                    details[0] = JO.getString("_id");
                    details[1] = JO.getString("Name");
                    details[2] = JO.getString("Password");
                    details[3] = JO.getString("Contact_No");
                    details[4] = JO.getString("Email");
                    details[5] = JO.getString("Gender");
                    details[6] = JO.getString("Dob");
                    details[7] = JO.getString("BloodGroup");
                    details[8] = JO.getString("Height");
                    details[9] = JO.getString("Weight");
                    details[10] = JO.getString("Metabolic_Disorders");

                    return details;

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Void... avoid) {
            super.onProgressUpdate(avoid);
        }

        @Override
        protected void onPostExecute(String[] result) {
            String[] details = {"", "", "", "", "", "", "", "", "", "", ""};
            details = result;
            if(details != null) {
                admNoShow.setText(": " + details[0]);
                nameShow.setText(": " + details[1]);
                contactNoShow.setText(": " + details[3]);
                emailShow.setText(": " + details[4]);
                genderShow.setText(": " + details[5]);
                dobShow.setText(": " + details[6]);
                bloodGroupShow.setText(": " + details[7]);
                heightShow.setText(": " + details[8]);
                weightShow.setText(": " + details[9]);
                metabolicDisorderShow.setText(": " + details[10]);
            }
        }
    }

}
