package uncc.abilash.edu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by devendra on 11/29/15.
 */
public class CheckSchedule extends Activity implements MyMacros{

    public String[][] apl_list = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.check_schedule);


        final Button back = (Button) findViewById(R.id.button1);
        final Button decline = (Button) findViewById(R.id.button4);
        final Button view = (Button) findViewById(R.id.button2);
        final Button accept = (Button) findViewById(R.id.button3);


        Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
        String appliance = mySpinner.getSelectedItem().toString();

        String house_array = getIntent().getExtras().getString("house");
        String[] single_house = house_array.split("\n");
        String house = HTDOCS_PATH + HOME_SCHEDULE_PATH + single_house[0] + ".txt";

        String list = getConnection(house, appliance);  // Complete file

        String[] list_apl = list.split("\n");           // Array for each appliance as a line

        apl_list = new String[list_apl.length][];
        for(int i=0; i<list_apl.length; i++){
            apl_list[i] = list_apl[i].split(",");   // Array with individual element for appliance i
        }

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
                String appliance = mySpinner.getSelectedItem().toString();

                TextView wattage = (TextView) findViewById(R.id.editText1);
                wattage.setText("");
                TextView starttime = (TextView) findViewById(R.id.editText2);
                starttime.setText("");
                TextView deadline = (TextView) findViewById(R.id.editText3);
                deadline.setText("");
                TextView runtime = (TextView) findViewById(R.id.editText4);
                runtime.setText("");
                TextView type = (TextView) findViewById(R.id.editText5);
                type.setText("");
                TextView prevCost = (TextView) findViewById(R.id.editText6);
                prevCost.setText("");
                TextView schCost = (TextView) findViewById(R.id.editText7);
                schCost.setText("");

                int index;
                try {
                    for (int i = 0; i < apl_list.length; i++) {
                        if (appliance.equals(apl_list[i][1])) {
                            index = i;

                            //TextView wattage = (TextView) findViewById(R.id.editText1);
                            wattage.setText(apl_list[index][2]);
                            //TextView starttime = (TextView) findViewById(R.id.editText2);
                            starttime.setText(apl_list[index][3]);
                            //TextView deadline = (TextView) findViewById(R.id.editText3);
                            deadline.setText(apl_list[index][4]);
                            //TextView runtime = (TextView) findViewById(R.id.editText4);
                            runtime.setText(apl_list[index][5]);
                            //TextView type = (TextView) findViewById(R.id.editText5);
                            type.setText(apl_list[index][6]);
                            //TextView prevCost = (TextView) findViewById(R.id.editText6);
                            prevCost.setText(apl_list[index][7]);
                            //TextView schCost = (TextView) findViewById(R.id.editText7);
                            schCost.setText(apl_list[index][8]);
                        }
                    }
                }
                catch (Exception e){
                    Toast.makeText(getBaseContext(), "Schedule Not Ready!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });


        accept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String house_arr = getIntent().getExtras().getString("house");
                String[] single_house = house_arr.split("\n");
                String house = single_house[0];

                String decision = "accept";

                String status = null;
                try {
                    for (int i = 0; i < apl_list.length; i++) {
                        status = getConnection(UTILITY_IP_ADDRESS, decision, house, apl_list[i][1], apl_list[i][2], apl_list[i][3], apl_list[i][4], apl_list[i][5], apl_list[i][6]);
                    }
                    Log.d("deva", "status: " + status);
                    if (status.equals("true\n")) {
                        Toast.makeText(getBaseContext(), "Schedule Accepted!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Toast.makeText(getBaseContext(), "Error!", Toast.LENGTH_SHORT).show();
                    }

                    status = getConnection(house);
                    if(status.equals("true\n")){
                        Toast.makeText(getBaseContext(),"Appliances Sent for Scheduling!",Toast.LENGTH_SHORT).show();
                        //finish();
                    }
                    else if(status.equals("false\n") || status.equals("empty\n")){
                        Toast.makeText(getBaseContext(),"No Appliances Added!\nAdd Appliances for Scheduling!",Toast.LENGTH_SHORT).show();
                        //finish();
                    }
                }
                catch (Exception e){
                    Toast.makeText(getBaseContext(), "Schedule Not Ready!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String house_arr = getIntent().getExtras().getString("house");
                String[] single_house = house_arr.split("\n");
                String house = single_house[0];

                String decision = "decline";    // decline homeserver's schedule

                String status = "";
                //for (int i = 0; i < 5; i++) {
                    status = getConnection(HOME_IP_ADDRESS, decision, house_arr, "", "", "", "", "", "");
                //}
                Log.d("deva", "status: " + status);
                if (status.equals("does_not_exist\n") || status.equals("does_not_exist")) {
                    Toast.makeText(getBaseContext(), "No Appliances Found!! Add Appliances", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(getBaseContext(), "Homeserver Schedule Declined!!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getBaseContext(), " Sending User Schedule to Utility Server!!", Toast.LENGTH_SHORT).show();

                    try {
                        JSONArray jArray = new JSONArray(status);
                        String str = "";
                        for(int i=(jArray.length()-1); i>=0; i--){
                            JSONObject json_data = jArray.getJSONObject(i);
                            decision = "accept";    // accept user's schedule

                            str = getConnection(UTILITY_IP_ADDRESS, decision, house, json_data.getString("appliance"), json_data.getString("wattage"), json_data.getString("starttime"), json_data.getString("deadline"), json_data.getString("runtime"), json_data.getString("type"));
                            Log.d("deva", "str: " + str);
                        }

                        if (str.equals("true\n")) {
                            Toast.makeText(getBaseContext(), "User Schedule Sent to Utility Server!!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    catch(JSONException e){
                        Toast.makeText(getBaseContext(), "Error in appliance list!!", Toast.LENGTH_SHORT).show();
                        Log.e("log_tag", "Error parsing data "+e.toString());
                    }

                    finish();
                }

            }
        });

    }


    public String getConnection(String hse, String apl){

        InputStream inputStream = null;
        String result = "";
        ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
        nameValuePairs1.add(new BasicNameValuePair("house", hse));
        nameValuePairs1.add(new BasicNameValuePair("appliance", apl));

        //http postappSpinners
        try{
            HttpClient httpclient = new DefaultHttpClient();

            // have to change the ip here to correct ip
            HttpPost httppost = new HttpPost("http://"+HOME_IP_ADDRESS+"/check_schedule.php");
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
        }
        return result;

    }


    public String getConnection(String IP_ADDRESS, String dec, String hse, String apl, String watt, String strt, String dead, String run, String typ){

        InputStream inputStream = null;
        String result = "";
        ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();

        if(dec.equals("accept")) {

            nameValuePairs1.add(new BasicNameValuePair("house", hse));
            nameValuePairs1.add(new BasicNameValuePair("appliance", apl));
            nameValuePairs1.add(new BasicNameValuePair("wattage", watt));
            nameValuePairs1.add(new BasicNameValuePair("starttime", strt));
            nameValuePairs1.add(new BasicNameValuePair("deadline", dead));
            nameValuePairs1.add(new BasicNameValuePair("runtime", run));
            nameValuePairs1.add(new BasicNameValuePair("type", typ));

            //http postappSpinners
            try {
                HttpClient httpclient = new DefaultHttpClient();

                // have to change the ip here to correct ip
                HttpPost httppost = new HttpPost("http://" + IP_ADDRESS + "/accept.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs1));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
                Toast.makeText(getBaseContext(), "Server Not Responding", Toast.LENGTH_SHORT).show();
                return "";
            }
        }

        else if(dec.equals("decline")) {
            nameValuePairs1.add(new BasicNameValuePair("house", hse));

            //http postappSpinners
            try {
                HttpClient httpclient = new DefaultHttpClient();

                // have to change the ip here to correct ip
                HttpPost httppost = new HttpPost("http://" + HOME_IP_ADDRESS + "/decline.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs1));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
                Toast.makeText(getBaseContext(), "Server Not Responding", Toast.LENGTH_SHORT).show();
                return "";
            }
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

    public String getConnection(String hseSch){

        InputStream inputStream = null;
        String result = "";

        ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();


        nameValuePairs1.add(new BasicNameValuePair("house", hseSch));
        //http postappSpinners
        try {
            HttpClient httpclient = new DefaultHttpClient();

            // have to change the ip here to correct ip
            HttpPost httppost = new HttpPost("http://" + HOME_IP_ADDRESS + "/scheduleUtility.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs1));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
        } catch (Exception e) {
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
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result "+e.toString());
        }
        return result;

    }

}
