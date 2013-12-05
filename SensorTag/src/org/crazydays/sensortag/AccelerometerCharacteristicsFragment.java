package org.crazydays.sensortag;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import static org.crazydays.sensortag.UUIDHelper.Accelerometer.*;

public class AccelerometerCharacteristicsFragment
    extends CharacteristicsFragment
{
    private final static String TAG =
        AccelerometerCharacteristicsFragment.class.getSimpleName();

    private BluetoothGattCharacteristic configurationCharacteristic;
    private BluetoothGattCharacteristic periodCharacteristic;
    private BluetoothGattCharacteristic dataCharacteristic;

    private final static int CONFIGURATION_DISABLED = 0x00;
    private final static int CONFIGURATION_ENABLED = 0x01;

    private int configuration;
    private int period;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root,
        Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.accelerometer_fragment, root, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        setupEnableButton();
        setupReadButton();
        setupSubscribeButton();
    }

    private void setupEnableButton()
    {
        final ToggleButton button =
            (ToggleButton) getView().findViewById(R.id.enable);

        button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean checked)
            {
                button.setEnabled(false);
                toggleEnabled(checked);
            }
        });
    }

    private void toggleEnabled(boolean checked)
    {
        int value;

        if (configuration == CONFIGURATION_DISABLED) {
            value = CONFIGURATION_ENABLED;
        } else {
            value = CONFIGURATION_DISABLED;
        }

        configurationCharacteristic.setValue(value,
            BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        requestWrite(configurationCharacteristic);
    }

    private void setupSubscribeButton()
    {
        final ToggleButton button =
            (ToggleButton) getView().findViewById(R.id.subscribe);

        button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean checked)
            {
                toggleSubscribe(checked);
            }
        });
    }

    private void toggleSubscribe(boolean checked)
    {
        requestNotification(dataCharacteristic, checked);

        final ToggleButton button =
            (ToggleButton) getView().findViewById(R.id.subscribe);
        button.setEnabled(false);
    }

    private void setupReadButton()
    {
        final Button button = (Button) getView().findViewById(R.id.read);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
                button.setEnabled(false);
                requestData();
            }
        });
    }

    @Override
    public void updateConnectionState(int state)
    {
        // nothing: assume connected
    }

    @Override
    public void addCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        if (characteristic.getUuid().toString().equals(DATA)) {
            if (dataCharacteristic == null) {
                Log.i(TAG, "addCharacteristic: DATA");
                dataCharacteristic = characteristic;
            }
        } else if (characteristic.getUuid().toString().equals(CONFIGURATION)) {
            if (configurationCharacteristic == null) {
                Log.i(TAG, "addCharacteristic: CONFIGURATION");
                configurationCharacteristic = characteristic;
                requestConfiguration();
            }
        } else if (characteristic.getUuid().toString().equals(PERIOD)) {
            if (periodCharacteristic == null) {
                Log.i(TAG, "addCharacteristic: PERIOD");
                periodCharacteristic = characteristic;
                // requestPeriod();
            }
        }
    }

    private void requestData()
    {
        requestRead(dataCharacteristic);
    }

    private void requestConfiguration()
    {
        requestRead(configurationCharacteristic);
    }

    private void requestPeriod()
    {
        requestRead(periodCharacteristic);
    }

    @Override
    public void readCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        if (characteristic.getUuid().toString().equals(DATA)) {
            readData();
        } else if (characteristic.getUuid().toString().equals(CONFIGURATION)) {
            readConfiguration();
        } else if (characteristic.getUuid().toString().equals(PERIOD)) {
            readPeriod();
        }
    }

    @Override
    public void
        changedCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        if (characteristic.getUuid().toString().equals(DATA)) {
            readData();
        }
    }

    private void readData()
    {
        Log.i(TAG, "readData");

        final double xAxis = extractXAxis(dataCharacteristic);
        final double yAxis = extractYAxis(dataCharacteristic);
        final double zAxis = extractZAxis(dataCharacteristic);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                updateReadButton();
                updateXAxis(xAxis);
                updateYAxis(yAxis);
                updateZAxis(zAxis);
            }
        });
    }

    private void readConfiguration()
    {
        Log.i(TAG, "readConfiguration");

        Integer value =
            configurationCharacteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 0);

        if (value == null) {
            Log.w(TAG, "premature readConfiguration");
        } else {
            Log.i(TAG, "value: " + value);
            configuration = value;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                updateEnabledButton();
                updateSubscribeButton();
                updateReadButton();
            }
        });

        requestPeriod();
    }

    private void readPeriod()
    {
        Log.i(TAG, "readPeriod");

        Integer value =
            configurationCharacteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 0);

        if (value == null) {
            Log.w(TAG, "premature readPeriod");
        } else {
            Log.i(TAG, "value: " + value);
            period = value * 10;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                updatePeriod();
            }
        });
    }

    @Override
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        if (characteristic.getUuid().toString().equals(CONFIGURATION)) {
            readConfiguration();
        }
    }

    private void updateEnabledButton()
    {
        ToggleButton button =
            (ToggleButton) getView().findViewById(R.id.enable);
        button.setEnabled(true);
        button.setChecked(configuration == CONFIGURATION_ENABLED);
    }

    private void updateSubscribeButton()
    {
        ToggleButton button =
            (ToggleButton) getView().findViewById(R.id.subscribe);
        button.setEnabled(configuration == CONFIGURATION_ENABLED);
    }

    private void updateReadButton()
    {
        Button button = (Button) getView().findViewById(R.id.read);
        button.setEnabled(configuration == CONFIGURATION_ENABLED);
    }

    private void updatePeriod()
    {
        updateTextView(R.id.period, String.format("%d", period));
    }

    private void updateXAxis(double value)
    {
        updateTextView(R.id.x_axis, String.format("%2.2f", ((float) value)));
    }

    private void updateYAxis(double value)
    {
        updateTextView(R.id.y_axis, String.format("%2.2f", ((float) value)));
    }

    private void updateZAxis(double value)
    {
        updateTextView(R.id.z_axis, String.format("%2.2f", ((float) value)));
    }

    private void updateTextView(int id, String value)
    {
        TextView view = (TextView) getView().findViewById(id);
        view.setText(value);
    }

    private double extractXAxis(BluetoothGattCharacteristic characteristic)
    {
        Integer value =
            characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_SINT8, 0);

        return value / 64.0;
    }

    private double extractYAxis(BluetoothGattCharacteristic characteristic)
    {
        Integer value =
            characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_SINT8, 1);

        return value / 64.0;
    }

    private double extractZAxis(BluetoothGattCharacteristic characteristic)
    {
        Integer value =
            characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_SINT8, 2);

        return value / 64.0 * -1.0;
    }

    @Override
    public void writeDescriptor(BluetoothGattDescriptor descriptor)
    {
        enableSubscribeButton();
    }

    private void enableSubscribeButton()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                final ToggleButton button =
                    (ToggleButton) getView().findViewById(R.id.subscribe);
                button.setEnabled(true);
            }
        });
    }
}
