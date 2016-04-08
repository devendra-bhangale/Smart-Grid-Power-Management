package uncc.abilash.edu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
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
public class ManageAppliance extends Activity implements MyMacros{

    public final static String appliance = "appliance";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.manage_appliance);


        final Button add = (Button) findViewById(R.id.button1);
        final Button schedule = (Button) findViewById(R.id.button4);
        final Button delete = (Button) findViewById(R.id.button2);
        final Button back = (Button) findViewById(R.id.button3);

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });


        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
                String appliance = mySpinner.getSelectedItem().toString();

                String[] hse = getIntent().getExtras().getString("house").split("\n");
                String house = hse[0];

                String status = getConnection(appliance, house, "", "");

                if(status.equals("appliance_not_added\n")){
                    Toast.makeText(getBaseContext(),"Appliance Not Found!",Toast.LENGTH_SHORT).show();
                    //finish();
                }
                else if(status.equals("true\n")){
                    Toast.makeText(getBaseContext(),"Appliance Deleted!",Toast.LENGTH_SHORT).show();
                    //finish();
                }
            }
        });


        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
                String text = mySpinner.getSelectedItem().toString();

                String[] hse = getIntent().getExtras().getString("house").split("\n");
                String house = hse[0];
                String time = getIntent().getExtras().getString("time");

                Bundle bundle = new Bundle();
                bundle.putString("time", time);
                bundle.putString("house", house);
                Intent AddAppliance = new Intent(getBaseContext(), AddAppliance.class);
                AddAppliance.putExtra(appliance, text);
                AddAppliance.putExtras(bundle);

                startActivity(AddAppliance);
            }
        });


        schedule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
                String appliance = mySpinner.getSelectedItem().toString();

                String[] hse = getIntent().getExtras().getString("house").split("\n");
                String house = hse[0];

                String status = getConnection("", "", house, appliance);

                if(status.equals("true\n")){
                    Toast.makeText(getBaseContext(),"Appliances Sent for Scheduling!",Toast.LENGTH_SHORT).show();
                    //finish();
                }
                else if(status.equals("false\n") || status.equals("empty\n")){
                    Toast.makeText(getBaseContext(),"No Appliances Added!\nAdd Appliances for Scheduling!",Toast.LENGTH_SHORT).show();
                    //finish();
                }

            }
        });



    }


    public String getConnection(String aplDel, String hseDel, String hseSch, String aplSch){

        InputStream inputStream = null;
        String result = "";

        ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();


        if(aplDel != "" && hseDel != "" && hseSch == "" && aplSch == "") {

            nameValuePairs1.add(new BasicNameValuePair("appliance", aplDel));
            nameValuePairs1.add(new BasicNameValuePair("house", hseDel));
            //http postappSpinners
            try {
                HttpClient httpclient = new DefaultHttpClient();

                // have to change the ip here to correct ip
                HttpPost httppost = new HttpPost("http://" + HOME_IP_ADDRESS + "/remove_appliance.php");
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
        else if(aplDel == "" && hseDel == "" && hseSch != "" && aplSch != ""){

            nameValuePairs1.add(new BasicNameValuePair("appliance", aplSch));
            nameValuePairs1.add(new BasicNameValuePair("house", hseSch));
            //http postappSpinners
            try {
                HttpClient httpclient = new DefaultHttpClient();

                // have to change the ip here to correct ip
                HttpPost httppost = new HttpPost("http://" + HOME_IP_ADDRESS + "/schedule.php");
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
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result "+e.toString());
        }
        return result;

    }

}
