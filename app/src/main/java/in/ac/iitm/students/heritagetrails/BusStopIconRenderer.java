package in.ac.iitm.students.heritagetrails;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import static in.ac.iitm.students.heritagetrails.IITMBusStops.bt_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.crc_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.fourth_cross_street_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.gajendra_circle_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.hsb_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.jam_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.kv_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.main_gate;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.Gurunath_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.tgh_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.vanvani_bus_stop;
import static in.ac.iitm.students.heritagetrails.IITMBusStops.velachery_gate;

/**
 * Created by admin on 25-01-2017.
 */

class BusStopIconRenderer extends DefaultClusterRenderer<ClusterMarkerLocation> {
    private Context context;


    public BusStopIconRenderer(Context context, GoogleMap map,
                               ClusterManager<ClusterMarkerLocation> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }


    @Override
    protected void onBeforeClusterItemRendered(ClusterMarkerLocation item, MarkerOptions markerOptions) {


        if (item.getPosition() == main_gate) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                    .alpha(0.6f)
                    .title("Main Gate");
        }
        if (item.getPosition() == jam_bus_stop) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                    .alpha(0.6f)
                    .title("Jam Bus Stop");
        }
        if (item.getPosition() == gajendra_circle_bus_stop) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                    .alpha(0.5f)
                    .title("Gajendra Circle Bus Stop");
        }
        if (item.getPosition() == hsb_bus_stop) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                    .alpha(0.5f)
                    .title("HSB Bus Stop");
        }
        if (item.getPosition() == velachery_gate) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                    .alpha(0.6f)
                    .title("Velachery Gate");
        }
        if (item.getPosition() == crc_bus_stop) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                    .alpha(0.5f)
                    .title("CRC Bus Stop");
        }
        if (item.getPosition() == tgh_bus_stop) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                    .alpha(0.5f)
                    .title("TGH Bus Stop");
        }
        if (item.getPosition() == Gurunath_bus_stop) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                    .alpha(0.5f)
                    .title("Gurunath Bus Stop");
        }
        if (item.getPosition() == bt_bus_stop) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                    .alpha(0.5f)
                    .title("BT Bus Stop");
        }
        if (item.getPosition() == fourth_cross_street_bus_stop) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                    .alpha(0.5f)
                    .title("4th Cross Street Bus Stop");
        }
        if (item.getPosition() == kv_bus_stop) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                    .alpha(0.5f)
                    .title("KV Bus Stop");
        }
        if (item.getPosition() == vanvani_bus_stop) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                    .alpha(0.5f)
                    .title("Vanvani Bus Stop");
        }


        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
