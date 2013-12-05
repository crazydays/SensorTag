package org.crazydays.sensortag;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class CharacteristicsListFragment
    extends CharacteristicsFragment
{
    private CharacteristicAdapter characteristicsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root,
        Bundle savedInstanceState)
    {
        characteristicsAdapter = new CharacteristicAdapter(inflater);

        return inflater.inflate(R.layout.characteristics_list_fragment, root,
            false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        setupCharacteristicsList();
    }

    private void setupCharacteristicsList()
    {
        ListView view =
            (ListView) getView().findViewById(R.id.characteristics_list);
        view.setAdapter(characteristicsAdapter);
    }

    @Override
    public void updateConnectionState(int state)
    {
        // nothing
    }

    @Override
    public void addCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        characteristicsAdapter.addCharacteristic(characteristic);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                characteristicsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void readCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        // nothing
    }

    @Override
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        // nothing
    }

    @Override
    public void
        changedCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        // nothing
    }

    @Override
    public void writeDescriptor(BluetoothGattDescriptor descriptor)
    {
        // nothing
    }
}
