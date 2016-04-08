package uncc.abilash.edu;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
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
import java.util.List;

/**
 * Created by devendra on 11/30/15.
 */
public class CheckSchedule_Utility extends Activity implements MyMacros {

    public String[][] apl_list = null;
    public String[][] final_apl_list = null;
    private LinearLayout main, temp;
    private ScrollView scrollView;
    private int id = 0;
    private boolean done;

    private List<TextView> textViews = new ArrayList<TextView>();
    private List<EditText> editTexts = new ArrayList<EditText>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_schedule_utility);

        temp = (LinearLayout) findViewById(R.id.linearlayout);
        scrollView = new ScrollView(CheckSchedule_Utility.this);
        main = new LinearLayout(CheckSchedule_Utility.this);


        String house_array = getIntent().getExtras().getString("house");
        String[] single_house = house_array.split("\n");
        String house = HTDOCS_PATH + UTILITY_SCHEDULE_PATH + single_house[0] + ".txt";

        String list = getConnection(house);  // Complete file

        final String[] list_apl = list.split("\n");           // Array for each appliance as a line

        final_apl_list = new String[list_apl.length][];
        apl_list = new String[list_apl.length][];
        for(int i=0; i<list_apl.length; i++){
            apl_list[i] = list_apl[i].split(",");   // Array with individual element for appliance i
            final_apl_list[i] = list_apl[i].split(",");
        }

        main.setOrientation(LinearLayout.VERTICAL);
        main.canScrollVertically(1);

        try {
            done = true;
            for (int i = 0; i < apl_list.length; i++) {
                addLayout(apl_list[i][1], apl_list[i][2], apl_list[i][3], apl_list[i][4], apl_list[i][5], apl_list[i][6], apl_list[i][7], apl_list[i][8] );

            }
        }
        catch (Exception e){    e.printStackTrace();
            Toast.makeText(getBaseContext(), "Schedule Not Ready!", Toast.LENGTH_SHORT).show();
            finish();
        }
        Log.d("deva", " apl_list.length: " + apl_list.length + " apl_list[0].length: " + apl_list[0].length + " editTexts: "+editTexts.size());
        Button back = new Button (this);
        back.setText("Back");
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button accept = new Button(this);
        accept.setText("Accept");
        accept.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //for (EditText editText : editTexts) {
                    //editText.getText().toString();
                //}
                for(int i = 0; i<list_apl.length; i++){
                    final_apl_list[i][3] = editTexts.get(0+(3*i)).getText().toString();
                    final_apl_list[i][4] = editTexts.get(1+(3*i)).getText().toString();
                    final_apl_list[i][5] = editTexts.get(2+(3*i)).getText().toString();
                    //final_apl_list[i][6] = editTexts.get(3+(4*i)).getText().toString();
                }

                String house_array = getIntent().getExtras().getString("house");
                String[] single_house = house_array.split("\n");

                String server = "utility";

                String status = null;
                try {
                    for (int i = 0; i < final_apl_list.length; i++) {
                        server = "utility";
                        status = getConnection(UTILITY_IP_ADDRESS, server, single_house[0], final_apl_list[i][1], final_apl_list[i][2], final_apl_list[i][3], final_apl_list[i][4], final_apl_list[i][5], final_apl_list[i][6]);
                        server = "home";
                        status = getConnection(HOME_IP_ADDRESS, server, single_house[0], final_apl_list[i][1], final_apl_list[i][2], final_apl_list[i][3], final_apl_list[i][4], final_apl_list[i][5], final_apl_list[i][6]);
                    }
                    Log.d("deva", "status: "+status);
                    if (status.equals("true\n")) {
                        Toast.makeText(getBaseContext(), "Schedule Accepted!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    status = getConnection(single_house[0], "");
                    Log.d("deva", "status: "+status);
                    if (status.equals("true\n")) {
                        Toast.makeText(getBaseContext(), "Schedule Accepted!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                catch (Exception e){
                    Toast.makeText(getBaseContext(), "Schedule Not Ready!", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });

        //main.addView(addButton);
        main.addView(accept);
        main.addView(back);
        //setContentView(main);
        scrollView.addView(main);
        temp.addView(scrollView);
    }

    public void addLayout(String appl, String w, String st, String dl, String rt, String typ, String prevcost, String currcost) {
        int idLinearLayout = 0;
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setId(idLinearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        main.addView(linearLayout);

        if(done == true) {
            done = false;

            TextView prevCost = new TextView(this);
            prevCost.setText("Previous Cost:\t" + prevcost);
            linearLayout.addView(prevCost);
            textViews.add(prevCost);

            TextView currCost = new TextView(this);
            currCost.setText("Current Cost:\t" + currcost);
            linearLayout.addView(currCost);
            textViews.add(currCost);
        }

        TextView appliance = new TextView(this);
        appliance.setTextColor(0xffff0a0a);
        appliance.setText("\nAppliance: " + appl);
        linearLayout.addView(appliance);
        textViews.add(appliance);

        TextView watt = new TextView(this);
        watt.setText("Wattage: " + w);
        linearLayout.addView(watt);
        textViews.add(watt);

        TextView startTime = new TextView(this);
        startTime.setText("Start Time: ");
        linearLayout.addView(startTime);
        textViews.add(startTime);

        EditText starttime = new EditText(this);
        starttime.setId(id++);
        starttime.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
        starttime.setText(st);
        linearLayout.addView(starttime);
        editTexts.add(starttime);

        TextView deadLine = new TextView(this);
        deadLine.setText("Deadline: ");
        linearLayout.addView(deadLine);
        textViews.add(deadLine);

        EditText deadline = new EditText(this);
        deadline.setId(id++);
        deadline.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
        deadline.setText(dl);
        linearLayout.addView(deadline);
        editTexts.add(deadline);

        TextView runTime = new TextView(this);
        runTime.setText("Run Time: ");
        linearLayout.addView(runTime);
        textViews.add(runTime);

        EditText runtime = new EditText(this);
        runtime.setId(id++);
        deadline.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
        runtime.setText(rt);
        linearLayout.addView(runtime);
        editTexts.add(runtime);

        TextView aplType = new TextView(this);
        aplType.setText("Type: " + typ);
        linearLayout.addView(aplType);
        textViews.add(aplType);

        //EditText type = new EditText(this);
        //type.setId(id++);
        //type.setText(typ);
        //linearLayout.addView(type);
        //editTexts.add(type);

    }

    public String getConnection(String hse){

        InputStream inputStream = null;
        String result = "";
        ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
        nameValuePairs1.add(new BasicNameValuePair("house", hse));
        //nameValuePairs1.add(new BasicNameValuePair("appliance", apl));

        //http postappSpinners
        try{
            HttpClient httpclient = new DefaultHttpClient();

            // have to change the ip here to correct ip
            HttpPost httppost = new HttpPost("http://"+UTILITY_IP_ADDRESS+"/check_schedule_utility.php");
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

    public String getConnection(String IP_ADDRESS, String server, String hse, String apl, String watt, String strt, String dead, String run, String typ){

        InputStream inputStream = null;
        String result = "";
        ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();

        if(server.equals("utility")) {

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
                HttpPost httppost = new HttpPost("http://" + IP_ADDRESS + "/acceptFinal.php");
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

        else if(server.equals("home")) {
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
                HttpPost httppost = new HttpPost("http://" + IP_ADDRESS + "/broadcast.php");
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
        }
        return result;

    }

    public String getConnection(String hseSch, String aplSch){

        InputStream inputStream = null;
        String result = "";

        ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();

        //nameValuePairs1.add(new BasicNameValuePair("appliance", aplSch));
        nameValuePairs1.add(new BasicNameValuePair("house", hseSch));
        //http postappSpinners
        try {
            HttpClient httpclient = new DefaultHttpClient();

            // have to change the ip here to correct ip
            HttpPost httppost = new HttpPost("http://" + UTILITY_IP_ADDRESS + "/update_consumption.php");
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
            result =sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " +e.toString());
        }
        return result;

    }

}