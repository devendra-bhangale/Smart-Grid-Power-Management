package uncc.abilash.edu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;



public class UserHomeActivity extends Activity{

    //private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_home);

        final Button logoutButton = (Button) findViewById(R.id.button3);
        final Button manageAppliance = (Button) findViewById(R.id.button2);
        final Button manageSchedule = (Button) findViewById(R.id.button5);
        final Button checkSchedule = (Button) findViewById(R.id.button6);
        final Button viewAppliance = (Button) findViewById(R.id.button1);
        final Button checkScheduleUtility = (Button) findViewById(R.id.button7);
        final Button viewSchedule = (Button) findViewById(R.id.button4);


        logoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        manageAppliance.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Time now = new Time();
                now.setToNow();

                String hr = Integer.toString(now.hour);
                String min = Integer.toString(now.minute);
                String sec = Integer.toString(now.second);

                String time = hr + ":" + min + ":" + sec;

                String house = getIntent().getExtras().getString("house");
                Bundle bundle = new Bundle();
                bundle.putString("time", time);
                bundle.putString("house", house);
                Intent ManageAppliance = new Intent(getBaseContext(),ManageAppliance.class);
                ManageAppliance.putExtras(bundle);
                startActivity(ManageAppliance);
            }
        });

        viewAppliance.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String house = getIntent().getExtras().getString("house");
                Bundle bundle = new Bundle();
                bundle.putString("house", house);
                Intent ViewAppliance = new Intent(getBaseContext(),ViewAppliance.class);
                ViewAppliance.putExtras(bundle);
                startActivity(ViewAppliance);
            }
        });

        manageSchedule.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String house = getIntent().getExtras().getString("house");
                Bundle bundle = new Bundle();
                bundle.putString("house", house);
                Intent ManageSchedule = new Intent(getBaseContext(),ManageSchedule.class);
                ManageSchedule.putExtras(bundle);
                startActivity(ManageSchedule);
            }
        });

        checkSchedule.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String house = getIntent().getExtras().getString("house");
                Bundle bundle = new Bundle();
                bundle.putString("house", house);

                Intent CheckSchedule = new Intent(getBaseContext(),CheckSchedule.class);
                CheckSchedule.putExtras(bundle);
                startActivity(CheckSchedule);
            }
        });

        checkScheduleUtility.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String house = getIntent().getExtras().getString("house");
                Bundle bundle = new Bundle();
                bundle.putString("house", house);

                //Intent CheckSchedule = new Intent(getBaseContext(),CheckSchedule.class);
                Intent CheckSchedule_Utility = new Intent(getBaseContext(),CheckSchedule_Utility.class);
                CheckSchedule_Utility.putExtras(bundle);
                startActivity(CheckSchedule_Utility);
            }
        });

        viewSchedule.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String house = getIntent().getExtras().getString("house");
                Bundle bundle = new Bundle();
                bundle.putString("house", house);
                Intent ViewSchedule = new Intent(getBaseContext(),ViewSchedule.class);
                ViewSchedule.putExtras(bundle);
                startActivity(ViewSchedule);
            }
        });

    }
}
