package rs.enginee.testbluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bojan on 12/4/2015.
 */
public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";

    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> mFoundBluetoothDevicesList = new ArrayList<BluetoothDevice>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public BluetoothService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        mBluetoothAdapter.startDiscovery();

        return Service.START_STICKY;
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG,"Found device " + device.getName() + " :: " + device.getAddress() + (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PHONE ? " phone" : " not phone"));

                // Add the name and address to an array adapter to show in a ListView
                boolean found = false;
                for (int i = 0; i < mFoundBluetoothDevicesList.size(); i++) {
                    if (mFoundBluetoothDevicesList.get(i).getAddress().equals(device.getAddress())) {
                        found = true;
                        break;
                    }

                }

                if (!found && device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PHONE) {
//                    mDiscoveredDevicesAdapter.add(device.getName() + " :: " + device.getAddress() + " " + (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PHONE ? "phone" : "not phone"));
                    mFoundBluetoothDevicesList.add(device);
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                Log.d(TAG,"Scan finished. Starting new scan");
                mBluetoothAdapter.startDiscovery();
            }
            else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action))
            {
                String previousScanMode = intent.getStringExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE);
                int newScanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -99);
                Log.d(TAG,"previousScanMode: " + previousScanMode + " || newScanMode: " + newScanMode);
                listCurrentFoundDevices();
                mBluetoothAdapter.startDiscovery();
            }
        }
    };

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void listCurrentFoundDevices() {
        for (BluetoothDevice device : mFoundBluetoothDevicesList) {
            Log.d(TAG,"Device " + device.getName() + " :: " + device.getAddress() + " :: " + (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PHONE ? "phone" : "not phone"));
        }
    }
}
