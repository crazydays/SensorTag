package org.crazydays.sensortag;

import java.util.LinkedList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceAdapter
    extends BaseAdapter
{
    private LayoutInflater inflater;
    private List<BluetoothDevice> devices = new LinkedList<BluetoothDevice>();

    public DeviceAdapter(LayoutInflater inflater)
    {
        this.inflater = inflater;
    }

    public void addDevice(BluetoothDevice device)
    {
        if (!devices.contains(device)) {
            devices.add(device);
        }
    }

    @Override
    public int getCount()
    {
        return devices.size();
    }

    @Override
    public Object getItem(int position)
    {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        BluetoothDevice device = devices.get(position);

        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_list_item_2, null);
        }

        ((TextView) view.findViewById(android.R.id.text1)).setText(device
            .getName());
        ((TextView) view.findViewById(android.R.id.text2)).setText(device
            .getAddress());

        return view;
    }
}
