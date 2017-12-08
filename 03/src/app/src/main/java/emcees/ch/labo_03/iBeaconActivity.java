package emcees.ch.labo_03;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;

import java.util.ArrayList;
import java.util.List;

public class iBeaconActivity extends AppCompatActivity implements BeaconConsumer {
    protected static final String BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

    private BeaconManager beaconManager;

    private ListView beaconsListView;
    private List<String> beaconsList;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibeacon);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BEACON_LAYOUT));
        beaconManager.bind(this);

        beaconsListView = (ListView)findViewById(R.id.beacons_list_view);
        beaconsList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, beaconsList);

        beaconsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier((beacons, region) -> {
            System.out.println("Detected new beacons");
            beaconsList.clear();
            for (Beacon beacon : beacons) {
                String beaconInfo = String.format("Major: %s, Minor: %s, RSSI: %d",
                    beacon.getId2(), beacon.getId3(), beacon.getRssi());
                System.out.println(beaconInfo);
                beaconsList.add(beaconInfo);
            }
            adapter.notifyDataSetChanged();
        });
    }
}
