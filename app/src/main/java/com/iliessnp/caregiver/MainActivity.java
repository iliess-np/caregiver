package com.iliessnp.caregiver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_SENDERID = "sender_id";
    public static final String JSON_ARRAY = "result";
    private static final String TAG = "TAG_MainActivity";
    Button btnFetchUser, btnFetchGPS, btnFetchAlert, btnShowMap;
    TextView tv_fName, tv_lName, tv_phone, tv_accuracy, tv_gps_locationGPS, tv_reportTimeGPS, tv_alertType, tv_gps_locationAlert, tv_reportTimeAlert;
    ProgressDialog mProgressDialog;
    String f_name, l_name, phone, accuracy, gps_locationGPS, reportTimeGPS, alertType, gps_locationAlert, reportTimeAlert, senderId, alertTypePrev = "";
    boolean firstLaunch = true;
    Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            senderId = intent.getExtras().getString("sender_id");
        }
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        btnFetchUser = (Button) findViewById(R.id.btnFetchUser);
        btnFetchGPS = (Button) findViewById(R.id.btnFetchGPS);
        btnFetchAlert = (Button) findViewById(R.id.btnFetchAlert);
        btnShowMap = (Button) findViewById(R.id.btnShowMap);

        tv_fName = findViewById(R.id.tv_fname);
        tv_lName = findViewById(R.id.tv_lname);
        tv_phone = findViewById(R.id.tv_phone);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_gps_locationGPS = findViewById(R.id.tv_gps_locationGPS);
        tv_reportTimeGPS = findViewById(R.id.tv_reportTimeGPS);
        tv_alertType = findViewById(R.id.tv_alertType);
        tv_gps_locationAlert = findViewById(R.id.tv_gps_locationAlert);
        tv_reportTimeAlert = findViewById(R.id.tv_reportTimeAlert);

        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gps_locationGPS != null) {
                    showMap(gps_locationGPS);
                } else if (gps_locationAlert != null) {
                    showMap(gps_locationAlert);
                } else {
                    Toast.makeText(MainActivity.this, "There no gps_location provided", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnFetchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchUser();
            }
        });

        btnFetchGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchGPS(true);
            }
        });

        btnFetchAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAlert(true);
            }
        });


        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                boolean showMapOrNot = false;
                fetchAlert(false);
                fetchGPS(false);
            }
        }, 0, 5000);
        long timeNow = System.currentTimeMillis();
        long timePrv = timeNow + 10000;
        if (timeNow >= timePrv) {
            firstLaunch = false;
        }

    }

    private void fetchUser() {
        if (senderId != null) {
            String MATCHDATA_URL = "http://helptech29.000webhostapp.com/getDataCareGiver.php";
            String KEY_FNAME = "f_name";
            String KEY_LNAME = "l_name";
            String KEY_PHONE = "phone";
            int caseType = 1;

            GetMatchData(MATCHDATA_URL, KEY_FNAME, KEY_LNAME, KEY_PHONE, caseType, false);
        } else {
            Toast.makeText(MainActivity.this, "Please Enter Detail", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchGPS(boolean showMapOrNot) {
        if (senderId != null) {
            String MATCHDATA_URL = "http://helptech29.000webhostapp.com/getLocation.php";
            String KEY_FNAME = "accuracy";
            String KEY_LNAME = "gps_location";
            String KEY_PHONE = "reporttime";
            int caseType = 2;
            GetMatchData(MATCHDATA_URL, KEY_FNAME, KEY_LNAME, KEY_PHONE, caseType, showMapOrNot);
        } else {
            Toast.makeText(MainActivity.this, "Please Enter Detail", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAlert(boolean showMapOrNot) {
        if (senderId != null) {
            String MATCHDATA_URL = "http://helptech29.000webhostapp.com/getAlertType.php";
            String KEY_FNAME = "alert";
            String KEY_LNAME = "gps_location";
            String KEY_PHONE = "reporttime";

            int caseType = 3;
            GetMatchData(MATCHDATA_URL, KEY_FNAME, KEY_LNAME, KEY_PHONE, caseType, showMapOrNot);
        } else {
            Toast.makeText(MainActivity.this, "Please Enter Detail", Toast.LENGTH_SHORT).show();
        }
    }

    //Fetch data
    private void GetMatchData(String MATCHDATA_URL, String KEY_FNAME, String KEY_LNAME, String KEY_PHONE, int caseType, boolean showMapOrNot) {
        if (firstLaunch == false) {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMessage(getString(R.string.progress_detail));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setProgress(0);
            mProgressDialog.setProgressNumberFormat(null);
            mProgressDialog.setProgressPercentFormat(null);
            mProgressDialog.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MATCHDATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")) {
                            Toast.makeText(MainActivity.this, "There was no response from server", Toast.LENGTH_SHORT).show();
                        } else {
                            showJSON(response, KEY_FNAME, KEY_LNAME, KEY_PHONE, caseType, showMapOrNot);
                        }
                        if (firstLaunch == false) {
                            mProgressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "" + error, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(KEY_SENDERID, senderId);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response, String KEY_FNAME, String KEY_LNAME, String KEY_PHONE, int caseType, boolean showMapOrNot) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                switch (caseType) {
                    case 1:
                        f_name = jo.getString(KEY_FNAME);
                        l_name = jo.getString(KEY_LNAME);
                        phone = jo.getString(KEY_PHONE);
                        tv_fName.setText(f_name);
                        tv_lName.setText(l_name);
                        tv_phone.setText(phone);
                        break;
                    case 2:
                        accuracy = jo.getString(KEY_FNAME);
                        gps_locationGPS = jo.getString(KEY_LNAME);
                        reportTimeGPS = jo.getString(KEY_PHONE);

                        tv_accuracy.setText(accuracy);
                        tv_gps_locationGPS.setText(gps_locationGPS);
                        tv_reportTimeGPS.setText(reportTimeGPS);
                        if (showMapOrNot) {
                            showMap(gps_locationGPS);
                        }
                        break;
                    case 3:
                        alertType = jo.getString(KEY_FNAME);
                        gps_locationAlert = jo.getString(KEY_LNAME);
                        reportTimeAlert = jo.getString(KEY_PHONE);

                        tv_accuracy.setText(accuracy);
                        tv_gps_locationAlert.setText(gps_locationAlert);
                        tv_reportTimeAlert.setText(reportTimeAlert);

                        if (showMapOrNot) {
                            showMap(gps_locationAlert);
                        }
                        break;
                }
            }
            vibratePhone();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void vibratePhone() {
        Log.d(TAG, "alertType " + alertType);
        long[] pattern = {0,500, 100, 500, 100, 500, 100, 500, 100, 500};
        if (alertType != null && !alertType.equals(alertTypePrev)) {
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                for (int i = 0; i < 5; i++) {
                    v.vibrate(VibrationEffect.createWaveform(pattern, 1));
                    v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
                }
                Log.d(TAG, "LOOK AM HERE ");

            } else {
                //deprecated in API 26
                for (int i = 0; i < 5; i++) {
                    v.vibrate(pattern, 1);
                    v.vibrate(500);

                }
                Log.d(TAG, "or HERE ");
            }
            alertTypePrev = alertType;
            Log.d(TAG, "alertType " + alertType);

        } else {
            v.cancel();
        }
    }

    private void showMap(String gps_location) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("gps_location", gps_location);
        intent.putExtra("sender_id", senderId);
        if (alertType != null) {
            intent.putExtra("alert_type", alertType);
        }
        startActivity(intent);
    }


}