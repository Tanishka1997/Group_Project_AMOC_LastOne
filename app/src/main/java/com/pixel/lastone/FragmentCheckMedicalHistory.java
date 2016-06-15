package com.pixel.lastone;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
 * Created by Shailesh on 07-Apr-16.
 */
public class FragmentCheckMedicalHistory extends Libs {

    public static String admNoImportedString = null;
    String JSON_STRING;
    JSONObject jsonObject;
    JSONArray jsonArray;
    MedicalHistoryAdapter medicalHistoryAdapter;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.check_medical_history_fragment, container, false);
    }

    public void setAdmissionNo(String AdmNo) {
        admNoImportedString = AdmNo;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        medicalHistoryAdapter = new MedicalHistoryAdapter(getContext(), R.layout.layout_medical_history);
        listView = (ListView) getActivity().findViewById(R.id.listViewMedicalHistoryPatientHomeScreen);
        listView.setAdapter(medicalHistoryAdapter);

        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        backgroundTask.execute(admNoImportedString);

    }


    public class BackgroundTask extends AsyncTask<String, Void, String> {
        String jsonGetMedicalHistoryURL;
        Context context;

        BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            jsonGetMedicalHistoryURL = "http://mediworld.orgfree.com/jsonGetMedicalHistory.php";
        }

        @Override
        protected String doInBackground(String... params) {
            String PATIENT_MEDICAL_HISTORY_ADM_NO = params[0];

            try {
                URL url = new URL(jsonGetMedicalHistoryURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("PATIENT_MEDICAL_HISTORY_ADM_NO", "UTF-8") + "=" + URLEncoder.encode(PATIENT_MEDICAL_HISTORY_ADM_NO, "UTF-8");

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
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onProgressUpdate(Void... avoid) {
            super.onProgressUpdate(avoid);
        }

        @Override
        protected void onPostExecute(String result) {
            JSON_STRING = result;

            try {
                jsonObject = new JSONObject(JSON_STRING);
                jsonArray = jsonObject.getJSONArray("Medical_History_From_Server");
                int count = 0;
                String symp, dateTime, mediTest, medicines;

                while (count < jsonArray.length()) {
                    JSONObject JO = jsonArray.getJSONObject(count);
                    symp = JO.getString("Symptoms");
                    dateTime = JO.getString("Date_Time_Show");
                    mediTest = JO.getString("Medical_Test");
                    medicines = JO.getString("Medicines_Prescribed");

                    MedicalHistory medicalHistory = new MedicalHistory(symp, dateTime, mediTest, medicines);
                    medicalHistoryAdapter.add(medicalHistory);
                    count++;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
