package uncc.abilash.edu;

/**
 * Created by devendra on 11/29/15.
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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

public class ViewSchedule extends Activity implements MyMacros{

    private final int[] colors = {Color.GREEN, Color.CYAN, Color.RED, Color.YELLOW, Color.rgb(Color.RED, Color.GREEN, Color.BLUE), Color.BLUE, Color.MAGENTA, Color.WHITE, Color.GRAY, Color.DKGRAY};

    private String[] appl_list = null;
    private int[] startHour = null;
    private int[] startMin = null;
    private int[] endHour = null;
    private int[] endMin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_view_schedule);

        String[] hse = getIntent().getExtras().getString("house").split("\n");
        String house = hse[0];

        String status = getConnection(house);
        Log.d("deva", "status: " + status);

        if(status.equals("null") || status.equals("null\n")){
            Toast.makeText(getBaseContext(), "No Schedule Available", Toast.LENGTH_SHORT).show();
            finish();
        }

        try {
            JSONArray jArray = new JSONArray(status);
            Log.d("deva", "jArray.length(): " + jArray.length());

            appl_list = new String[jArray.length()+1];
            appl_list[0] = "";
            startHour = new int[jArray.length()+1];
            startHour[0] = 0;
            startMin = new int[jArray.length()+1];
            startMin[0] = 0;
            endHour = new int[jArray.length()+1];
            endHour[0] = 0;
            endMin = new int[jArray.length()+1];
            endMin[0] = 0;

            for(int i=1; i<=jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i-1);

                appl_list[i] = json_data.getString("appliance");

                String[] startTime = json_data.getString("starttime").split(":");
                String[] runTime = json_data.getString("runtime").split(":");

                startHour[i] = Integer.parseInt(startTime[0]);
                startMin[i] = Integer.parseInt(startTime[1]);
                endHour[i] = (startHour[i] + Integer.parseInt(runTime[0])) % 24;
                endMin[i] = startMin[i] + Integer.parseInt(runTime[1]);

                if (endMin[i] >= 60) {
                    endHour[i] = (endHour[i] + 1) % 24;
                    endMin[i] %= 60;
                }

                Log.d("deva", " appl_list["+i+"]: " + appl_list[i]);
                Log.d("deva", " startHour[i]: " + startHour[i] + " startMin[i]: " + startMin[i]);
                Log.d("deva", " endHour[i]: " + endHour[i] + " endMin[i]: " + endMin[i]);
            }

            // we get graph view instance
            GraphView graph = (GraphView) findViewById(R.id.graph);

            graph.getViewport().setScrollable(true);

            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
            //staticLabelsFormatter.setVerticalLabels(new String[]{"", "Waterheater", "Washer", "Vacuum", "TV", "Stereo", "Microwave", "Humidifier", "Hot Plate", "Dishwasher", "AC"});
            staticLabelsFormatter.setVerticalLabels(appl_list);
            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            graph.getGridLabelRenderer().setLabelVerticalWidth(250);

            graph.getViewport().setScrollable(true);

            Viewport viewport = graph.getViewport();
            viewport.setYAxisBoundsManual(true);
            viewport.setMinY(0);

            Log.d("deva", "appl_list.length: " + appl_list.length);

            viewport.setMaxY(jArray.length());
            viewport.setXAxisBoundsManual(true);
            viewport.setMinX(0);
            viewport.setMaxX(24);
            viewport.setScrollable(true);

            for(int j=1; j<=jArray.length(); j++) {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                        new DataPoint(startHour[j], j),
                        new DataPoint(endHour[j], j),
                });
                series.setColor(colors[j-1]);
                graph.addSeries(series);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getConnection(String hse){

        InputStream inputStream = null;
        String result = "";
        ArrayList<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
        nameValuePairs1.add(new BasicNameValuePair("house", hse));

        //http postappSpinners
        try{
            HttpClient httpclient = new DefaultHttpClient();

            // have to change the ip here to correct ip
            HttpPost httppost = new HttpPost("http://"+UTILITY_IP_ADDRESS+"/view_schedule.php");
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