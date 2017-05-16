package lab4_203_07.uwaterloo.ca.myapplication;

import android.graphics.Color;
import android.graphics.PointF;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by Ashwin and Cedric on 2015-07-12.
 */
public class GPS implements PositionListener {

    MapView map;
    TextView stepCount;

    public GPS(TextView stepCount, MapView map) {
        this.map = map;
        this.stepCount = stepCount;
        this.stepCount = stepCount;
        this.stepCount.setTextColor(Color.BLACK);
        this.stepCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
    }

    @Override
    public void originChanged(MapView source, PointF loc) {

        source.setOriginPoint(loc);
        source.setUserPoint(loc);
        map.setUserPoint(loc);
        map.setOriginPoint(loc);
        stepCount.setText("HI");


    }

    @Override
    public void destinationChanged(MapView source, PointF dest) {

        source.setUserPoint(dest);
        source.setOriginPoint(dest);

        stepCount.setText("HI");

    }
}
