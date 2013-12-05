package org.crazydays.sensortag;

import java.util.UUID;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ServiceDetailFragment
    extends Fragment
{
    public final static String DEVICE_ADDRESS = "deviceAddress";
    public final static String SERVICE_UUID = "serviceUuid";

    private final static String TAG = ServiceDetailFragment.class
        .getSimpleName();

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService bluetoothService;

    private CharacteristicsFragment characteristicsFragment;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        setupBluetoothManager(activity);
        setupBluetoothAdapter();
        setupBluetoothDevice();
        setupBluetoothGatt();

        setupServiceCharacteristicsFragment();
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

    private void setupBluetoothGatt()
    {
        bluetoothGatt =
            bluetoothDevice.connectGatt(getActivity(), true,
                bluetoothGattCallback);
    }

    private BluetoothGattCallback bluetoothGattCallback =
        new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status,
                int state)
            {
                Log.i(TAG, "onConnectionStateChange gatt: " + gatt +
                    " status: " + status + " newState: " + state);

                super.onConnectionStateChange(gatt, status, state);
                updateConnectionState(state);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status)
            {
                Log.i(TAG, "onServicesDiscovered gatt: " + gatt + " status: " +
                    status);
                processDiscoveredServices();
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic, int status)
            {
                Log.i(TAG, "onCharacteristicRead gatt: " + gatt +
                    " characteristic: " + characteristic.getUuid().toString() +
                    " status: " + status);
                processReadCharacteristic(characteristic);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic, int status)
            {
                Log.i(TAG, "onCharacteristicWrite gatt: " + gatt +
                    " characteristic: " + characteristic.getUuid().toString() +
                    " status: " + status);
                processWriteCharacteristic(characteristic);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic)
            {
                Log.i(TAG, "onCharacteristicChanged gatt: " + gatt +
                    " characteristic: " + characteristic.getUuid().toString());
                processChangedCharacteristic(characteristic);
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt,
                BluetoothGattDescriptor descriptor, int status)
            {
                Log.i(TAG, "onDescriptorWrite gatt: " + gatt + " descriptor: " +
                    descriptor.getUuid().toString());
                processWriteDescriptor(descriptor);
            }
        };

    private void updateConnectionState(int state)
    {
        characteristicsFragment.updateConnectionState(state);

        switch (state) {
        case BluetoothProfile.STATE_CONNECTED:
            if (bluetoothGatt.getServices().isEmpty()) {
                updateServiceType();
                discoverServices();
            }
        }
    }

    private void updateServiceType()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                setupServiceType();
            }
        });
    }

    private void discoverServices()
    {
        if (bluetoothGatt.getServices().isEmpty()) {
            boolean success = bluetoothGatt.discoverServices();
            Log.d(TAG, "discoverServices: " + success);
        } else {
            Log.w(TAG, "duplicate discoverServices; skipping");
        }
    }

    private void processDiscoveredServices()
    {
        UUID uuid = (UUID) getArguments().get(SERVICE_UUID);
        bluetoothService = bluetoothGatt.getService(uuid);

        for (BluetoothGattCharacteristic characteristic : bluetoothService
            .getCharacteristics()) {
            Log.i(TAG, "addCharacteristic: " +
                characteristic.getUuid().toString());
            characteristicsFragment.addCharacteristic(characteristic);
        }
    }

    private void processReadCharacteristic(
        BluetoothGattCharacteristic characteristic)
    {
        characteristicsFragment.readCharacteristic(characteristic);
    }

    private void processWriteCharacteristic(
        BluetoothGattCharacteristic characteristic)
    {
        characteristicsFragment.writeCharacteristic(characteristic);
    }

    private void processChangedCharacteristic(
        BluetoothGattCharacteristic characteristic)
    {
        characteristicsFragment.changedCharacteristic(characteristic);
    }

    private void processWriteDescriptor(BluetoothGattDescriptor descriptor)
    {
        characteristicsFragment.writeDescriptor(descriptor);
    }

    private void setupServiceCharacteristicsFragment()
    {
        UUID uuid = (UUID) getArguments().getSerializable(SERVICE_UUID);

        characteristicsFragment =
            CharacteristicsFragment.build(bluetoothGatt, uuid);

        getFragmentManager().beginTransaction()
            .replace(R.id.characteristic_details, characteristicsFragment)
            .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root,
        Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.service_detail_fragment, root, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        setupServiceName();
        setupServiceUuid();
        setupServiceType();
    }

    private void setupServiceName()
    {
        Context context = getActivity();
        UUID uuid = (UUID) getArguments().getSerializable(SERVICE_UUID);

        ((TextView) getView().findViewById(R.id.service_name))
            .setText(UUIDHelper.toString(context, uuid));
    }

    private void setupServiceUuid()
    {
        UUID uuid = (UUID) getArguments().getSerializable(SERVICE_UUID);

        ((TextView) getView().findViewById(R.id.service_uuid)).setText(uuid
            .toString());
    }

    private void setupServiceType()
    {
        int type = R.string.service_type_unknown;
        if (bluetoothService != null) {
            switch (bluetoothService.getType()) {
            case BluetoothGattService.SERVICE_TYPE_PRIMARY:
                type = R.string.service_type_primary;
                break;
            case BluetoothGattService.SERVICE_TYPE_SECONDARY:
                type = R.string.service_type_secondary;
                break;
            }
        }

        ((TextView) getView().findViewById(R.id.service_type))
            .setText(getString(type));
    }
}
