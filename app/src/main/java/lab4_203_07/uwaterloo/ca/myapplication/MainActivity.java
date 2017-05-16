package lab4_203_07.uwaterloo.ca.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    /**
     * This fragment processes the number of steps taken by the user and displays
     * it as a TextView.
     */
    public static class PlaceholderFragment extends Fragment {

        // Declaring fields used
        //LineGraphView graph;
        MapView map;

        NavigationalMap navMap;
        PointF currentPos = new PointF(0,0);

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);


            // Linking layout defined in fragment_main.xml to mainLayout object
            final LinearLayout mainLayout = (LinearLayout) rootView.findViewById(R.id.layout1);

            mainLayout.setGravity(Gravity.CENTER);

            Context c = rootView.getContext();

            Button setStartLocation = (Button)rootView.findViewById(R.id.setStartButton);
            Button setDestLocation = (Button)rootView.findViewById(R.id.setDestButton);
            Button start_GPS = (Button)rootView.findViewById(R.id.GPS_Button);

            // Declaring a Clear Button to clear record values, with its properties
            // Button clearGraphButton = new Button(rootView.getContext());
            // clearGraphButton.setText("Clear Counter!");
            // clearGraphButton.setGravity(Gravity.CENTER);
            // clearGraphButton.setTextColor(Color.GREEN);
            // clearGraphButton.setPadding(0, 10, 0, 10);
            // clearGraphButton.setBackgroundColor(Color.GRAY);

            // Declaring the TextView object for Step Counter, with its properties
            final TextView stepCount = new TextView(rootView.getContext());
            stepCount.setGravity(Gravity.CENTER);
            stepCount.setPadding(0, 20, 0, 20);

            // Modifying the graph field to show custom properties
            map = new MapView(rootView.getContext(), 1200, 900, 60 , 70 );
            navMap = MapLoader.loadMap(getActivity().getExternalFilesDir(null), "E2-3344-Lab-room-S15-tweaked.svg");
            //navMap = MapLoader.loadMap(getActivity().getExternalFilesDir(null), "E2-3344-Lab-room-S15-tweaked.svg");
            map.xScale = 45;
            map.yScale = 40;
            map.setMap(navMap);
            map.setBackgroundColor(Color.parseColor("#FBE9E7"));


            final List<PointF> path = new ArrayList<PointF>();



            setStartLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    map.setOriginPoint(currentPos);
                    map.setUserPoint(currentPos);

                    //map.setUserPath(path);

                }
            });

            setDestLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    map.setDestinationPoint(currentPos);
                    path.clear();
                    //((StepListener) stepListener).interceptPoints.clear();
                    //map.setUserPath(path);


                }
            });



            map.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    currentPos = new PointF(event.getX() / map.xScale, event.getY() / map.yScale);
                    //map.setDestinationPoint(currentPos);
                    return false;
                }
            });







            // Adding all views to the fragment layout

            mainLayout.addView(map);

            mainLayout.addView(stepCount);

            // Declaring a SensorManager object responsible for connecting us to the hardware of the phone
            final SensorManager sensorManager = (SensorManager) rootView.getContext().getSystemService(SENSOR_SERVICE);

            // Declaring individual sensors from the SensorManager
            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);


            // Declaring SensorEventListener for Accelerometer and registering it
            final SensorEventListener stepListener = new StepListener(stepCount, map,navMap,c);


            sensorManager.registerListener(stepListener , orientationSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(stepListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);

            start_GPS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( ((StepListener) stepListener).toggle == false) {
                        ((StepListener) stepListener).playCalcMusic();
                        SystemClock.sleep(3000);
                        ((StepListener) stepListener).toggle = true;

                    }

                    else{
                        ((StepListener) stepListener).toggle = false;
                    }
                }
            });



            // Resets Step Counter and clears Graph


            return rootView;
        }

    }

}
