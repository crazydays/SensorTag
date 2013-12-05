package org.crazydays.sensortag;

import java.util.UUID;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import org.crazydays.sensortag.log.BluetoothGattLogger;

public class ServicesFragment
    extends Fragment
{
    public interface SelectBluetoothGattServiceListener
    {
        public void selectService(String deviceAddress, UUID serviceUuid);
    }

    public final static String DEVICE_ADDRESS = "deviceAddress";

    private final static String TAG = ServicesFragment.class.getSimpleName();

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;

    private int connectionState = BluetoothProfile.STATE_DISCONNECTED;

    private ServiceAdapter serviceListViewAdapter;

    private SelectBluetoothGattServiceListener listener;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        listener = (SelectBluetoothGattServiceListener) activity;

        setupBluetoothManager(activity);
        setupBluetoothAdapter();
        setupBluetoothDevice();
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

    private void setupBluetoothDevice()
    {
        bluetoothDevice =
            bluetoothAdapter.getRemoteDevice(getArguments().getString(
                DEVICE_ADDRESS));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root,
        Bundle savedInstanceState)
    {
        setupServiceListViewAdapter(inflater);

        return inflater.inflate(R.layout.services_fragment, root, false);
    }

    private void setupServiceListViewAdapter(LayoutInflater inflater)
    {
        serviceListViewAdapter = new ServiceAdapter(inflater);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        setupConnectionButton();
        setupServicesButton();
        setupDiscoveredServices();
    }

    private void setupConnectionButton()
    {
        Button button = (Button) getView().findViewById(R.id.connection_button);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
                toggleConnection();
            }
        });

        updateConnectionButton();
    }

    private void toggleConnection()
    {
        // disable button
        getView().findViewById(R.id.connection_button).setEnabled(false);

        switch (connectionState) {
        case BluetoothProfile.STATE_CONNECTED:
            Log.i(TAG, "state: STATE_CONNECTED");
            disconnect();
            break;
        case BluetoothProfile.STATE_DISCONNECTED:
            Log.i(TAG, "state: STATE_DISCONNECTED");
            connect();
            break;
        }
    }

    private void connect()
    {
        if (bluetoothGatt == null) {
            bluetoothGatt =
                bluetoothDevice.connectGatt(getActivity(), true, callback);
        } else {
            bluetoothGatt.connect();
        }
    }

    private void disconnect()
    {
        bluetoothGatt.disconnect();
    }

    private BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
            int state)
        {
            Log.i(TAG, "onConnectionStateChange gatt: " + gatt + " status: " +
                status + " newState: " + state);

            super.onConnectionStateChange(gatt, status, state);

            connectionState = state;
            updateConnectionButton();
            updateServicesButton();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            Log.i(TAG, "onServicesDiscovered gatt: " + gatt + " status: " +
                status);

            Log.i(TAG, "service.size: " + bluetoothGatt.getServices().size());
            for (BluetoothGattService service : bluetoothGatt.getServices()) {
                Log.i(TAG, "service: " + service.getUuid());
                addService(service);
            }

            BluetoothGattLogger.log(getActivity(), gatt);
        }
    };

    private void updateConnectionButton()
    {
        switch (connectionState) {
        case BluetoothProfile.STATE_CONNECTING:
            Log.i(TAG, "state: STATE_CONNECTING");
            updateConnectionButton(false, R.string.disconnect);
            break;
        case BluetoothProfile.STATE_CONNECTED:
            Log.i(TAG, "state: STATE_CONNECTED");
            updateConnectionButton(true, R.string.disconnect);
            break;
        case BluetoothProfile.STATE_DISCONNECTING:
            Log.i(TAG, "state: STATE_DISCONNECTING");
            updateConnectionButton(false, R.string.connect);
            break;
        case BluetoothProfile.STATE_DISCONNECTED:
            Log.i(TAG, "state: STATE_DISCONNECTED");
            updateConnectionButton(true, R.string.connect);
            break;
        default:
            Log.i(TAG, "state: Unknown");
            updateConnectionButton(true, R.string.connect);
        }
    }

    private void updateConnectionButton(final boolean enabled,
        final int resourceId)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                Button button =
                    (Button) getView().findViewById(R.id.connection_button);

                button.setEnabled(enabled);
                button.setText(getActivity().getString(resourceId));
            }
        });
    }

    private void setupServicesButton()
    {
        Button button = (Button) getView().findViewById(R.id.services_button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
                discoverServices();
            }
        });
    }

    private void discoverServices()
    {
        boolean success = bluetoothGatt.discoverServices();
        Log.i(TAG, "discoverServices: " + success);
    }

    private void updateServicesButton()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                getView().findViewById(R.id.services_button).setEnabled(
                    connectionState == BluetoothProfile.STATE_CONNECTED);
            }
        });
    }

    private void setupDiscoveredServices()
    {
        ListView list =
            (ListView) getView().findViewById(R.id.discovered_services);
        list.setAdapter(serviceListViewAdapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                int index, long id)
            {
                Log.d(TAG, "onItemClick: " + index);
                selectService((BluetoothGattService) serviceListViewAdapter
                    .getItem(index));
            }
        });
    }

    private void selectService(BluetoothGattService service)
    {
        listener.selectService(bluetoothDevice.getAddress(), service.getUuid());
    }

    private void addService(final BluetoothGattService service)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                serviceListViewAdapter.addService(service);
                serviceListViewAdapter.notifyDataSetChanged();
            }
        });
    }
}
