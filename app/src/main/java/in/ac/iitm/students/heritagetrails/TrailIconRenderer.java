package in.ac.iitm.students.heritagetrails;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;

/**
 * Created by admin on 25-01-2017.
 */

public class TrailIconRenderer extends DefaultClusterRenderer<ClusterMarkerLocation> {
    private ArrayList<MarkerOptions> trailMarkerOptionsArray;
    private Context context;
    private MapsActivity mapsActivity;


    public TrailIconRenderer(Context context, GoogleMap map,
                             ClusterManager<ClusterMarkerLocation> clusterManager, ArrayList<MarkerOptions> trailMarkerOptionsArray, MapsActivity mapsActivity) {
        super(context, map, clusterManager);
        this.trailMarkerOptionsArray = trailMarkerOptionsArray;
        this.context = context;
        this.mapsActivity = mapsActivity;
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarkerLocation item, MarkerOptions markerOptions) {

        if (mapsActivity.getTrailCount() == 1) {
            for (MarkerOptions mOptions : trailMarkerOptionsArray) {
                if (mOptions.getPosition() == item.getPosition()) {
                    markerOptions.icon(vectorToBitmap(R.drawable.ic_trail_1_place_36dp))
                            .alpha(0.8f)
                            .title(mOptions.getTitle())
                            .snippet(mOptions.getSnippet());
                }
            }
        } else if (mapsActivity.getTrailCount() == 2) {
            for (MarkerOptions mOptions : trailMarkerOptionsArray) {
                if (mOptions.getPosition() == item.getPosition()) {
                    markerOptions.icon(vectorToBitmap(R.drawable.ic_trail_2_place_36dp))
                            .alpha(0.8f)
                            .title(mOptions.getTitle())
                            .snippet(mOptions.getSnippet());
                }
            }

            mapsActivity.setTrail_2_Shown();
        }


        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}
