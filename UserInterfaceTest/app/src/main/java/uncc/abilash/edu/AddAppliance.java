package uncc.abilash.edu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by devendra on 11/22/15.
 */
public class AddAppliance extends Activity implements MyMacros{

    //private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add);

        final String apl = getIntent().getExtras().getString("appliance");
        final Button save = (Button) findViewById(R.id.button1);
        final Button back = (Button) findViewById(R.id.button2);

        final EditText watt = (EditText) findViewById(R.id.editText1);
        final EditText strt = (EditText) findViewById(R.id.editText2);
        final EditText dead = (EditText) findViewById(R.id.editText3);
        final EditText run = (EditText) findViewById(R.id.editText4);
        final CheckBox typeCBox = (CheckBox) findViewById(R.id.checkBox1);
        final CheckBox rpsCBox = (CheckBox) findViewById(R.id.checkBox2);


        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    //getting current string values from the view
                    String wattage = String.format("%.2f", Double.parseDouble(watt.getText().toString()));
                    String starttime = strt.getText().toString();
                    String deadline = dead.getText().toString();
                    String runtime = run.getText().toString();
                    String appliance = apl.toString();
                    String type = "soft";
                    String rps = "no";

                    String[] hse = getIntent().getExtras().getString("house").split("\n");
                    String house = hse[0];
                    //String time = getIntent().getExtras().getString("time");

                    //starttime = starttime.replace(".", ":");
                    //deadline = deadline.replace(".", ":");
                    //runtime = runtime.replace(".", ":");

                    String[] start = starttime.split(":");
                    String[] deadl = deadline.split(":");
                    String[] runt = runtime.split(":");

                    try {
                        int hr_st = Integer.parseInt(start[0]);
                        int min_st = Integer.parseInt(start[1]);
                        int hr_dl = Integer.parseInt(deadl[0]);
                        int min_dl = Integer.parseInt(deadl[1]);
                        int hr_run = Integer.parseInt(runt[0]);
                        int min_run = Integer.parseInt(runt[1]);

                        String start1 = String.format("%02d", hr_st);
                        String dead1 = String.format("%02d", hr_dl);
                        String run1 = String.format("%02d", hr_run);

                        starttime = start1 + ":" + String.format("%02d", min_st);
                        deadline = dead1 + ":" + String.format("%02d", min_dl);
                        runtime = run1 + ":" + String.format("%02d", min_run);

                        if (hr_st >= 24 || min_st >= 60) {
                            Toast.makeText(getBaseContext(), "Enter valid Start Time", Toast.LENGTH_SHORT).show();
                        } else if (hr_dl >= 24 || min_dl >= 60) {
                            Toast.makeText(getBaseContext(), "Enter valid Deadline", Toast.LENGTH_SHORT).show();
                        } else if (hr_dl != 0 && (hr_dl - hr_st) < 1) {
                            Toast.makeText(getBaseContext(), "Enter valid Time Window", Toast.LENGTH_SHORT).show();
                        } else if (hr_dl != 0 && (hr_dl - hr_st) < hr_run) {
                            Toast.makeText(getBaseContext(), "Enter valid Run Time", Toast.LENGTH_SHORT).show();
                        }
                        // all the validations are done in sequence
                        else if (wattage.length() == 0) {
                            watt.setError(getResources().getString(R.string.username_error_message));
                        }
                        //finally on successfull verification the appropriate message is displayed
                        else {

                            if (typeCBox.isChecked()) {
                                type = "hard";
                            }
                            if(rpsCBox.isChecked()){
                                rps = "yes";
                            }
                            Log.d("deva", "before status: " + wattage + " " + starttime + " " +  deadline + " " +  runtime + " " +  house + " " +  appliance + " " +  type + " " +  rps);// + time);
                            String status = getConnection(wattage, starttime, deadline, runtime, house, appliance, type, rps);   //, time);
                            Log.d("deva", "status: " + status);
                            if (status.equals("appliance_already_added\n")) {
                                Toast.makeText(getBaseContext(), "Appliance already exists!", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            if (status.equals("true\n")) {
                                Toast.makeText(getBaseContext(), "Appliance Added!", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }
                    }
                    catch (Exception e){
                        Toast.makeText(getBaseContext(), "Enter Time in valid format", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getBaseContext(), "Enter Time in valid format", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public String getConnection(String watt, String strt, String dead, String run, String hse, String apl, String typ, String rps){ //, String tim){

        InputStream inputStream = null;
        String result = "";
        ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
        nameValuePairs1.add(new BasicNameValuePair("wattage",watt));
        nameValuePairs1.add(new BasicNameValuePair("starttime",strt));
        nameValuePairs1.add(new BasicNameValuePair("deadline", dead));
        nameValuePairs1.add(new BasicNameValuePair("runtime", run));
        nameValuePairs1.add(new BasicNameValuePair("house", hse));
        nameValuePairs1.add(new BasicNameValuePair("appliance", apl));
        nameValuePairs1.add(new BasicNameValuePair("type", typ));
        nameValuePairs1.add(new BasicNameValuePair("rps", rps));

        //http postappSpinners
        try{
            HttpClient httpclient = new DefaultHttpClient();

            // have to change the ip here to correct ip
            HttpPost httppost = new HttpPost("http://"+HOME_IP_ADDRESS+"/add_appliance.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs1));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
        }
        catch(Exception e){
            Log.e("log_tag", "Error in http connection " + e.toString());
            Toast.makeText(getBaseContext(), "Server Not Responding", Toast.LENGTH_SHORT).show();
            return "";
        }
        //convert response to string
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            inputStream.close();
            result=sb.toString();
        }
        catch(Exception e){
            Log.e("log_tag", "Error converting result "+e.toString());
        }Log.d("deva", "result: " + result);
        return result;

    }


}