package org.crazydays.sensortag;

import java.util.LinkedList;
import java.util.List;

import android.bluetooth.BluetoothGattService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServiceAdapter
    extends BaseAdapter
{
    private LayoutInflater inflater;
    private List<BluetoothGattService> services =
        new LinkedList<BluetoothGattService>();

    public ServiceAdapter(LayoutInflater inflater)
    {
        this.inflater = inflater;
    }

    public void addService(BluetoothGattService service)
    {
        services.add(service);
    }

    @Override
    public int getCount()
    {
        return services.size();
    }

    @Override
    public Object getItem(int index)
    {
        return services.get(index);
    }

    @Override
    public long getItemId(int index)
    {
        return index;
    }

    @Override
    public View getView(int index, View view, ViewGroup root)
    {
        BluetoothGattService service = services.get(index);

        if (view == null) {
            // view = inflater.inflate(R.layout.bluetooth_service_item, null);
            view = inflater.inflate(android.R.layout.simple_list_item_2, null);
        }

        TextView first = (TextView) view.findViewById(android.R.id.text1);
        first
            .setText(UUIDHelper.toString(view.getContext(), service.getUuid()));

        TextView second = (TextView) view.findViewById(android.R.id.text2);
        second.setText(service.getUuid().toString());

        return view;
    }
}
