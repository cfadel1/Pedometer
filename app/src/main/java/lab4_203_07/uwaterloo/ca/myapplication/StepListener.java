package lab4_203_07.uwaterloo.ca.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashwin on 2015-05-11.]
 * This class implements the SensorEventListener interface and is designed to collect data from
 * the accelerometer. This is done by storing the results from the accelerometer in a float array and
 * processing it in different methods to output the steps taken
 */
public class StepListener implements SensorEventListener {

    // Declaring fields used
    //LineGraphView graph;
    TextView stepCount;
    List<Float> xdataHolder = new ArrayList<Float>();
    List<Float> ydataHolder = new ArrayList<Float>();
    List<Float> zdataHolder = new ArrayList<Float>();
    List<Float> orientationValues = new ArrayList<Float>();
    NavigationalMap navMap;

    DecimalFormat decimalPlaces = new DecimalFormat();
    PointF userPoint = new PointF();
    Boolean hitNWall = false;
    Boolean hitSWall = false;
    Boolean hitEWall = false;
    Boolean hitWWall = false;
    Boolean toggle = false;


    int xNorth = 0;
    int xSouth = 0;
    int xEast = 0;
    int xWest = 0;
    int xDisplacement = 0;
    int yDisplacement = 0;
    public List<InterceptPoint> interceptPoints = new ArrayList<>();
    List<InterceptPoint> intersectionsNorth;
    List<InterceptPoint> intersectionsSouth;
    List<InterceptPoint> intersectionsEast;
    List<InterceptPoint> intersectionsWest;

    PointF farNorth = new PointF();
    PointF farSouth = new PointF();
    PointF farEast = new PointF();
    PointF farWest = new PointF();

    PointF farNorthD = new PointF();
    PointF farSouthD = new PointF();
    PointF farEastD = new PointF();
    PointF farWestD = new PointF();

    MediaPlayer pNorth;
    MediaPlayer pSouth;
    MediaPlayer pEast;
    MediaPlayer pWest;
    MediaPlayer pDest;
    MediaPlayer pCalc;
    MediaPlayer pApproach;
    double netDisplacement = 0;
    double distance = 0;

    String direction;
    String GPS;
    String dummy = "Null";
    double dummy1 = 0d;
    float border = 1.2f;
    float stepLength = 1.2f;
    float[] result;
    float orientation;
    static final float ALPHA = 0.5f; // if ALPHA = 1 OR 0, no filter applies.
    int dataCount = 0;
    int counter = 0;
    float sum = 0;
    float average = 0;
    MapView map;
    Context context;

    // Constructor takes in LineGraphView and TextView Objects to show accelerometer data
    public StepListener(TextView stepCount, MapView map, NavigationalMap navMap, Context context) {

        //this.graph = graph;
        this.navMap = navMap;
        this.stepCount = stepCount;
        this.stepCount.setTextColor(Color.YELLOW);
        this.stepCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        this.userPoint = map.getUserPoint();
        this.map = map;
        pNorth = MediaPlayer.create(context, R.raw.north);
        pSouth = MediaPlayer.create(context, R.raw.south);
        pEast = MediaPlayer.create(context, R.raw.east);
        pWest = MediaPlayer.create(context, R.raw.west);
        pDest = MediaPlayer.create(context, R.raw.dest);
        pCalc = MediaPlayer.create(context, R.raw.calc);
        pApproach = MediaPlayer.create(context, R.raw.approach);
        this.context = context;
//
//        farNorth.y = 0.81f;
//        farSouth.y = 12.2f;
//        farEast.x = 18.6f;
//        farWest.x = 0.92f;

        farNorth.y = 0.71f;
        farSouth.y = 22.2f;
        farEast.x = 25.6f;
        farWest.x = 0.21f;

        farNorthD.y = 0.71f;
        farSouthD.y = 22.2f;
        farEastD.x = 25.6f;
        farWestD.x = 0.21f;
        decimalPlaces.setMinimumFractionDigits(2);
        decimalPlaces.setMinimumIntegerDigits(2);
        decimalPlaces.setMaximumFractionDigits(2);


    }

    public double getDistance(PointF start, PointF end) {
        double x1 = start.x;
        double x2 = end.x;
        double y1 = start.y;
        double y2 = end.y;

        double dist = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

        return dist;
    }

    public double getDisplacement() {

        yDisplacement = xNorth - xSouth;
        xDisplacement = xEast - xWest;
        netDisplacement = Math.sqrt(Math.pow(xDisplacement, 2) + Math.pow(yDisplacement, 2));

        return netDisplacement;


    }

    public String getDirection(List<Float> orientationValues) {
        for (Float value : orientationValues) {
            sum = sum + value;
        }

        average = sum / 15;
        sum = 0;
        // Takes care of north
        if (average > 315 || average <= 45) {
            return "north";

        }

        if (average > 135 && average <= 225) {
            return "south";

        }

        if (average > 225 && average <= 315) {
            return "west";

        }

        // Takes care of east Direction
        if (average > 45 && average <= 135) {
            return "east";

        }

        return null;
    }

    public void wallDetector() {

        intersectionsNorth = navMap.calculateIntersections(userPoint, farNorth);
        intersectionsSouth = navMap.calculateIntersections(userPoint, farSouth);
        intersectionsEast = navMap.calculateIntersections(userPoint, farEast);
        intersectionsWest = navMap.calculateIntersections(userPoint, farWest);


        if (intersectionsNorth.isEmpty() == false) {

            if (Math.abs(Math.abs(userPoint.y) - Math.abs(intersectionsNorth.get(0).getPoint().y)) < border) {
                hitNWall = true;
            } else {
                hitNWall = false;
            }
        }

        if (intersectionsSouth.isEmpty() == false) {

            if (Math.abs(Math.abs(userPoint.y) - Math.abs(intersectionsSouth.get(0).getPoint().y)) < border) {
                hitSWall = true;
            } else {
                hitSWall = false;
            }
        }

        if (intersectionsEast.isEmpty() == false) {

            if (Math.abs(Math.abs(userPoint.x) - Math.abs(intersectionsEast.get(0).getPoint().x)) < border) {
                hitEWall = true;
            } else {
                hitEWall = false;
            }
        }

        if (intersectionsWest.isEmpty() == false) {

            if (Math.abs(Math.abs(userPoint.x) - Math.abs(intersectionsWest.get(0).getPoint().x)) < border) {
                hitWWall = true;
            } else {
                hitWWall = false;
            }
        }


    }

    public void playApproachMusic(){
        pApproach.start();
    }
    public void playCalcMusic(){
        pCalc.start();
    }

    public void playDestMusic(){
        pDest.start();
    }

    public void playMusic(String dummy) {


        if (dummy != GPS && GPS == "Head north!")
        {
            pNorth.start();
        }

        if (dummy != GPS && GPS == "Head south!")
        {
            pSouth.start();
        }

        if (dummy != GPS && GPS == "Head east!")
        {
            pEast.start();
        }

        if (dummy != GPS && GPS == "Head west!")
        {
            pWest.start();
        }

    }

    public void pathMaker(List<InterceptPoint> interceptPoints) {
        List<PointF> linePoints = new ArrayList<PointF>();
        if (interceptPoints.isEmpty()) {
            linePoints.clear();
            linePoints.add(map.getUserPoint());
            linePoints.add(map.getDestinationPoint());
            map.setUserPath(linePoints);
            Log.d("string","Test1");
            distance = getDistance(map.getUserPoint(), map.getDestinationPoint());

            if (Math.abs(Math.abs(map.getUserPoint().y) - Math.abs(map.getDestinationPoint().y)) > Math.abs(Math.abs(map.getUserPoint().x) - Math.abs(map.getDestinationPoint().x))) {
                if (map.getDestinationPoint().y < map.getUserPoint().y) {
                    GPS = "Head north!";

                }

                if (map.getDestinationPoint().y > map.getUserPoint().y) {
                    GPS = "Head south!";

                }
            }

            if (Math.abs(Math.abs(map.getUserPoint().y) - Math.abs(map.getDestinationPoint().y)) < Math.abs(Math.abs(map.getUserPoint().x) - Math.abs(map.getDestinationPoint().x))) {
                if (map.getDestinationPoint().x < map.getUserPoint().x) {
                    GPS = "Head west!";

                }

                if (map.getDestinationPoint().x > map.getUserPoint().x) {
                    GPS = "Head east!";

                }
            }
        }

        // Implements what to do if wall is encountered
        if (interceptPoints.isEmpty() == false && navMap.calculateIntersections(userPoint, farSouth).size() != 0 && navMap.calculateIntersections(map.getDestinationPoint(), farSouthD).size() != 0) {

            linePoints.clear();
            PointF point1 = navMap.calculateIntersections(map.getDestinationPoint(), farSouthD).get(0).getPoint();
            PointF point2 = navMap.calculateIntersections(map.getDestinationPoint(), farNorthD).get(0).getPoint();
            PointF point3 = navMap.calculateIntersections(userPoint, farSouth).get(0).getPoint();
            PointF point4 = navMap.calculateIntersections(map.getDestinationPoint(), farWestD).get(0).getPoint();
            PointF point5 = navMap.calculateIntersections(map.getDestinationPoint(), farWestD).get(0).getPoint();
            point1.y = userPoint.y;
            point2.y = userPoint.y;
            point3.x = userPoint.x;
            point4.x = userPoint.x;


            if (navMap.calculateIntersections(userPoint, point1).size() == 0) {
                linePoints.add(userPoint);
                linePoints.add(point1);
                linePoints.add(map.getDestinationPoint());
                map.setUserPath(linePoints);
                Log.d("string","Test2");


                if (Math.abs(Math.abs(map.getUserPoint().y) - Math.abs(point1.y)) > Math.abs(Math.abs(map.getUserPoint().x) - Math.abs(point1.x))) {
                    if (point1.y < map.getUserPoint().y) {
                        GPS = "Head north!";

                    }

                    if (point1.y > map.getUserPoint().y) {
                        GPS = "Head south!";

                    }
                }

                if (Math.abs(Math.abs(map.getUserPoint().y) - Math.abs(point1.y)) < Math.abs(Math.abs(map.getUserPoint().x) - Math.abs(point1.x))) {
                    if (point1.x < map.getUserPoint().x) {
                        GPS = "Head west!";

                    }

                    if (point1.x > map.getUserPoint().x) {
                        GPS = "Head east!";

                    }
                }

                distance = getDistance(userPoint, point1) + getDistance(point1, map.getDestinationPoint());


            } else if (navMap.calculateIntersections(map.getDestinationPoint(), point4).size() == 0) {
                linePoints.clear();
                linePoints.add(userPoint);
                linePoints.add(point4);
                linePoints.add(map.getDestinationPoint());
                map.setUserPath(linePoints);
                Log.d("string","Test3");

                if (Math.abs(Math.abs(map.getUserPoint().y) - Math.abs(point4.y)) > Math.abs(Math.abs(map.getUserPoint().x) - Math.abs(point4.x))) {
                    if (point4.y < map.getUserPoint().y) {
                        GPS = "Head north!";

                    }

                    if (point4.y > map.getUserPoint().y) {
                        GPS = "Head south!";

                    }
                }

                if (Math.abs(Math.abs(map.getUserPoint().y) - Math.abs(point4.y)) < Math.abs(Math.abs(map.getUserPoint().x) - Math.abs(point4.x))) {
                    if (point4.x < map.getUserPoint().x) {
                        GPS = "Head west!";

                    }

                    if (point4.x > map.getUserPoint().x) {
                        GPS = "Head east!";

                    }
                }

                distance = getDistance(userPoint, point4) + getDistance(point1, map.getDestinationPoint());
            } else {
                linePoints.clear();
                //Log.d("string","Test4");

                    Log.d("string","Test4");
                    point3.y = point3.y - 1.0f;
                    point1.y = point3.y;
                    linePoints.add(userPoint);
                    linePoints.add(point3);
                    linePoints.add(point1);
                    linePoints.add(map.getDestinationPoint());
                    map.setUserPath(linePoints);


                if(navMap.calculateIntersections(point3, point1).size() != 0){
                    linePoints.clear();
                    Log.d("string","Test5");
                    point3.y = point3.y - 1.6f;
                    point1.y = point3.y;
                    linePoints.add(userPoint);
                    linePoints.add(point3);
                    linePoints.add(point1);
                    linePoints.add(map.getDestinationPoint());
                    map.setUserPath(linePoints);

                }

                if(navMap.calculateIntersections(point3,point1).size() != 0){
                    //Log.d("Ali","Ali");
                    linePoints.clear();
                    //Log.d("string","Test5");
                    point3.y = userPoint.y;
                    point3.x = userPoint.x - 4.2f;
                    point1.y = map.getDestinationPoint().y - 1.6f;
                    point1.x = point3.x;
                    point5.y = point1.y;
                    point5.x = map.getDestinationPoint().x;

                    linePoints.add(userPoint);
                    linePoints.add(point3);
                    linePoints.add(point1);
                    linePoints.add(point5);
                    linePoints.add(map.getDestinationPoint());
                    map.setUserPath(linePoints);
                }



                if (Math.abs(Math.abs(map.getUserPoint().y) - Math.abs(point3.y)) > Math.abs(Math.abs(map.getUserPoint().x) - Math.abs(point3.x))) {
                    if (point3.y < map.getUserPoint().y) {
                        GPS = "Head north!";

                    }

                    if (point3.y > map.getUserPoint().y) {
                        GPS = "Head south!";

                    }
                }

                distance = getDistance(userPoint, point3) + getDistance(point3, point1) + getDistance(point1, map.getDestinationPoint());


            }

        }

        else{

            Log.d("string", map.getDestinationPoint().toString());
            //Log.d("Value",Integer.toString(navMap.calculateIntersections(map.getDestinationPoint(), farSouthD).size()));
            //Log.d("hello","Gotacha");
           // Log.d("Value",Integer.toString(navMap.calculateIntersections(userPoint, map.getDestinationPoint()).size()));
        }


    }

    // This method is responsible for analyzing the filtered acceleration values from the low pass filter
    public int dataProcessor(List<Float> xdataHolder, List<Float> ydataHolder, List<Float> zdataHolder) {
        float xThreshold = 0.2f;
        float yThreshold = 0.2f;
        float zThreshold = 0.65f;

        for (Float value : xdataHolder) {
            if (Math.abs(value) >= xThreshold) {
                dataCount++;
            }
        }

        for (Float value : ydataHolder) {
            if (Math.abs(value) >= yThreshold) {
                dataCount++;
            }
        }

        for (Float value : zdataHolder) {
            if (Math.abs(value) >= zThreshold) {
                dataCount++;
            }
        }


        if (dataCount >= 10 && dataCount < 27) {

            if (direction == "north") {
                xNorth++;

                if (hitNWall == false) {
                    userPoint.y = userPoint.y - stepLength;
                }




            }
            if (direction == "south") {
                xSouth++;

                if (hitSWall == false) {
                    userPoint.y = userPoint.y + stepLength;
                }



            }
            if (direction == "east") {
                xEast++;

                if (hitEWall == false) {
                    userPoint.x = userPoint.x + stepLength;
                }



            }
            if (direction == "west") {
                xWest++;

                if (hitWWall == false) {
                    userPoint.x = userPoint.x - stepLength;
                }



            }

            //stepCounter++;
        }

        dataCount = 0;
        return dataCount;
    }

    // This method acts as a low pass filter and attenuates the noise
    public float[] lowPassFilter(float[] input, float[] output) {
        if (output == null) return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    // Event is triggered every time the accelerometer reading changes
    public void onSensorChanged(SensorEvent se) {

        if(toggle == true) {
            playMusic(dummy);
            dummy = GPS;
            dummy1 = distance;

            if (se.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                orientation = se.values[0];
                orientationValues.add(orientation);

                if (orientationValues.size() == 15) {
                    direction = getDirection(orientationValues);

                    orientationValues.clear();
                }

            }

            if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

                float[] input = se.values; // Array that stores the results from the accelerometer
                result = lowPassFilter(input, result); // Passed into a low pass filter
                //graph.addPoint(result);     // Adds the point to the graph


                interceptPoints = navMap.calculateIntersections(map.getUserPoint(), map.getDestinationPoint());
                wallDetector();

                pathMaker(interceptPoints);


//            for(int i = 0; i < test.size();i++){
//                stepCount.append(Integer.toString(test.indexOf(i)));
//            }


                xdataHolder.add(result[0]);
                ydataHolder.add(result[1]);
                zdataHolder.add(result[2]);

                if (zdataHolder.size() == 10) {
                    dataProcessor(xdataHolder, ydataHolder, zdataHolder);

                    map.setUserPoint(userPoint);


                    farNorth.x = userPoint.x;
                    farSouth.x = userPoint.x;
                    farWest.y = userPoint.y;
                    farEast.y = userPoint.y;

                    farNorthD.x = map.getDestinationPoint().x;
                    farSouthD.x = map.getDestinationPoint().x;
                    farWestD.y = map.getDestinationPoint().y;
                    farEastD.y = map.getDestinationPoint().y;

                    xdataHolder.clear();
                    ydataHolder.clear();
                    zdataHolder.clear();
                }


                //stepCount.setText("\n" + "Steps" + "\n" + "-------------" + "\n" + "north: " + xNorth + "\n" + "south: " + xSouth + "\n" + "east: " + xEast + "\n" + "west: " + xWest + "\n" + "Direction: " + direction + "\n" + "Orientation: " + decimalPlaces.format(orientation) + "\n" +
                // "Displacement: " + decimalPlaces.format(getDisplacement()));
                this.stepCount.setTextColor(Color.YELLOW);


                stepCount.setText("GPS: " + GPS + "\n");
                stepCount.append("Distance Left: " + decimalPlaces.format(distance) + " mts" + "\n" + "\n");
                stepCount.append("Direction: " + direction + "\n");
                stepCount.append("Orientation: " + decimalPlaces.format(orientation) + "\n");

                if(distance < 10.0d && distance > 8.0d){
                    playApproachMusic();
                }


                if (distance < 0.7d && distance != 0) {
                    this.stepCount.setTextColor(Color.GREEN);


                    playDestMusic();
                    stepCount.append("DESTINATION REACHED!");

                    //this.stepCount.setTextColor(Color.YELLOW);
                }


            }
        }

    }


    public void onAccuracyChanged(Sensor s, int accuracy) {
        // Do nothing
    }


}