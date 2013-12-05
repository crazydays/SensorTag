package org.crazydays.sensortag;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.ToggleButton;

public class DiscoveryFragment
    extends Fragment
{
    public interface SelectDeviceListener
    {
        public void selectDevice(String deviceAddress);
    }

    private final static long SCAN_DURATION_MILLIS = 10000;

    private Handler handler = new Handler();

    private DeviceAdapter devicesAdapter;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private SelectDeviceListener listener;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        listener = (SelectDeviceListener) activity;

        setupBluetoothManager(activity);
        setupBluetoothAdapter();
    }

    private void setupBluetoothManager(Context context)
    {
        bluetoothManager =
            (BluetoothManager) context
                .getSystemService(Context.BLUETOOTH_SERVICE);
    }

    private void setupBluetoothAdapter()
    {
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Override
    public void onDetach()
    {
        listener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root,
        Bundle savedInstanceState)
    {
        setupAdapter(inflater);

        return inflater.inflate(R.layout.discovery_fragment, root, false);
    }

    private void setupAdapter(LayoutInflater inflater)
    {
        devicesAdapter = new DeviceAdapter(inflater);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        setupDeviceList();
        setupScanButton();
    }

    private void setupDeviceList()
    {
        ListView list =
            (ListView) getView().findViewById(R.id.discovered_devices);
        list.setAdapter(devicesAdapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list, View view,
                int position, long id)
            {
                selectDevice(position);
            }
        });
    }

    private void selectDevice(int position)
    {
        clearStopScan();

        BluetoothDevice device =
            (BluetoothDevice) devicesAdapter.getItem(position);
        listener.selectDevice(device.getAddress());
    }

    private void setupScanButton()
    {
        ToggleButton button =
            (ToggleButton) getView().findViewById(R.id.scan_button);
        button.setText(getActivity().getString(R.string.start_scan));
        button.setTextOff(getActivity().getString(R.string.start_scan));
        button.setTextOn(getActivity().getString(R.string.stop_scan));

        button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked)
            {
                toggleScan(isChecked);
            }
        });
    }

    private void toggleScan(boolean scan)
    {
        if (scan) {
            startScan();
        } else {
            stopScan();
        }
    }

    private void stopScan()
    {
        bluetoothAdapter.stopLeScan(btleScanCallback);
    }

    private void startScan()
    {
        bluetoothAdapter.startLeScan(btleScanCallback);
        stopScanDelayed();
    }

    private void stopScanDelayed()
    {
        handler.postDelayed(stopScanRunnable, SCAN_DURATION_MILLIS);
    }

    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run()
        {
            ToggleButton button =
                (ToggleButton) getView().findViewById(R.id.scan_button);
            if (button.isChecked()) {
                button.toggle();
            }
        }
    };

    private void clearStopScan()
    {
        handler.removeCallbacks(stopScanRunnable);
        stopScanRunnable.run();
    }

    private LeScanCallback btleScanCallback = new LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
            byte[] scanRecord)
        {
            addDevice(device);
        }
    };

    private void addDevice(final BluetoothDevice device)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                devicesAdapter.addDevice(device);
                devicesAdapter.notifyDataSetChanged();
            }
        });
    }
}
