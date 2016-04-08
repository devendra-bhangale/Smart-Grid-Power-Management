
package uncc.abilash.edu;

import android.app.Activity;
import android.content.Intent;
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
import java.util.concurrent.ExecutionException;

/**
 * Created by devendra on 11/22/15.
 */
public class ViewAppliance extends Activity implements MyMacros{

    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_appliance);

        final Button back = (Button) findViewById(R.id.button1);
        final Button view = (Button) findViewById(R.id.button2);

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
                String appliance = mySpinner.getSelectedItem().toString();

                String[] hse = getIntent().getExtras().getString("house").split("\n");
                String house = hse[0];

                String status = getConnection(house, appliance);


                try {


                    String watt = null;
                    String strt = null;
                    String dead = null;
                    String run = null;
                    String typ = null;
                    String rps = null;


                    if(status.equals("null\n")){
                        Toast.makeText(getBaseContext(),"Appliance Not Found!", Toast.LENGTH_SHORT).show();

                        TextView wattage = (TextView) findViewById(R.id.editText1);
                        wattage.setText(" ");
                        TextView starttime = (TextView) findViewById(R.id.editText2);
                        starttime.setText(" ");
                        TextView deadline = (TextView) findViewById(R.id.editText4);
                        deadline.setText(" ");
                        TextView end = (TextView) findViewById(R.id.editText3);
                        end.setText(" ");
                        TextView type = (TextView) findViewById(R.id.editText5);
                        type.setText(" ");
                        TextView rpsource = (TextView) findViewById(R.id.editText6);
                        rpsource.setText(" ");
                    }


                    JSONArray jArray = new JSONArray(status);
                    JSONObject json_data = jArray.getJSONObject(0);

                    watt = json_data.getString("wattage");
                    strt = json_data.getString("starttime");
                    dead = json_data.getString("deadline");
                    run = json_data.getString("runtime");
                    typ = json_data.getString("type");
                    rps = json_data.getString("rps");


                    String[] start = strt.split(":");
                    String[] plustime = run.split(":");

                    int hr = 0;
                    int min = 0;

                    hr = Integer.parseInt(start[0]) + Integer.parseInt(plustime[0]);
                    min = Integer.parseInt(start[1]) + Integer.parseInt(plustime[1]);
                    if (min == 60) {
                        min = 00;
                        hr++;
                    } else if (min == 0) {
                        min = 00;
                    }


                    String total1 = Integer.toString(hr);
                    String total2 = String.format("%02d", min);

                    String total = total1 + ":" + total2;

                    TextView wattage = (TextView) findViewById(R.id.editText1);
                    wattage.setText(watt);
                    TextView starttime = (TextView) findViewById(R.id.editText2);
                    starttime.setText(strt);
                    TextView deadline = (TextView) findViewById(R.id.editText4);
                    deadline.setText(dead);
                    TextView end = (TextView) findViewById(R.id.editText3);
                    end.setText(total);
                    TextView type = (TextView) findViewById(R.id.editText5);
                    type.setText(typ);
                    TextView rpsource = (TextView) findViewById(R.id.editText6);
                    rpsource.setText(rps);


                } catch (Exception e) {
                    e.printStackTrace();
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
            HttpPost httppost = new HttpPost("http://"+HOME_IP_ADDRESS+"/view_appliance.php");
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

}
