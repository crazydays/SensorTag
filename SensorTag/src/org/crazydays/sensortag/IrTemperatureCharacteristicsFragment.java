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
import static org.crazydays.sensortag.UUIDHelper.IrTemperature.*;

public class IrTemperatureCharacteristicsFragment
    extends CharacteristicsFragment
{
    private final static String TAG =
        IrTemperatureCharacteristicsFragment.class.getSimpleName();

    private BluetoothGattCharacteristic dataCharacteristic;
    private BluetoothGattCharacteristic configurationCharacteristic;

    private final static int CONFIGURATION_DISABLED = 0x00;
    private final static int CONFIGURATION_ENABLED = 0x01;

    private int configuration;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root,
        Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.ir_temperature_fragment, root, false);
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

    @Override
    public void updateConnectionState(int state)
    {
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
        }
    }

    private void requestData()
    {
        requestRead(dataCharacteristic);
    }

    private void requestConfiguration()
    {
        Log.i(TAG, "requestConfiguration");
        requestRead(configurationCharacteristic);
    }

    @Override
    public void readCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        if (characteristic.getUuid().toString().equals(DATA)) {
            readData();
        } else if (characteristic.getUuid().toString().equals(CONFIGURATION)) {
            readConfiguration();
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

        final double ambient;
        final double target;

        Integer value =
            dataCharacteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT32, 0);
        if (value == null) {
            Log.w(TAG, "premature readData");
            ambient = 0;
            target = 0;
        } else {
            Log.i(TAG, "value: " + value);
            ambient = extractAmbientTemperature(value);
            target = extractTargetTemperature(value, ambient);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                updateReadButton();
                updateIrAmbientTemperature(fahrenheit(ambient));
                updateIrTargetTemperature(fahrenheit(target));
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

    private void updateIrAmbientTemperature(double temperature)
    {
        TextView view =
            (TextView) getView().findViewById(R.id.ir_temperature_ambient);
        view.setText(String.format("%1.2f", ((float) temperature)));
    }

    private void updateIrTargetTemperature(double temperature)
    {
        TextView view =
            (TextView) getView().findViewById(R.id.ir_temperature_target);
        view.setText(String.format("%1.2f", ((float) temperature)));
    }

    private double extractAmbientTemperature(int value)
    {
        return ((value & 0xffff0000) >> 16) / 128.0d;
    }

    private double extractTargetTemperature(int value, double ambient)
    {
        Short raw = (short) (value & 0x0000ffff);
        double doubleValue = raw.doubleValue();
        doubleValue *= 0.00000015625;

        double Tdie = ambient + 273.15;

        double S0 = 5.593E-14; // Calibration factor
        double a1 = 1.75E-3;
        double a2 = -1.678E-5;
        double b0 = -2.94E-5;
        double b1 = -5.7E-7;
        double b2 = 4.63E-9;
        double c2 = 13.4;
        double Tref = 298.15;
        double S =
            S0 * (1 + a1 * (Tdie - Tref) + a2 * Math.pow((Tdie - Tref), 2));
        double Vos = b0 + b1 * (Tdie - Tref) + b2 * Math.pow((Tdie - Tref), 2);
        double fObj =
            (doubleValue - Vos) + c2 * Math.pow((doubleValue - Vos), 2);
        double tObj = Math.pow(Math.pow(Tdie, 4) + (fObj / S), .25);

        return tObj - 273.15;
    }

    private double fahrenheit(double celcius)
    {
        return celcius * 1.8 + 32.00;
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
