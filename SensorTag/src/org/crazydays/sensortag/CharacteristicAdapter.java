package org.crazydays.sensortag;

import java.util.LinkedList;
import java.util.List;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CharacteristicAdapter
    extends BaseAdapter
{
    private LayoutInflater inflater;

    private List<BluetoothGattCharacteristic> characteristics =
        new LinkedList<BluetoothGattCharacteristic>();

    public CharacteristicAdapter(LayoutInflater inflater)
    {
        this.inflater = inflater;
    }

    public void addCharacteristic(BluetoothGattCharacteristic characteristic)
    {
        // !contains doesn't work, so we have to do it the hard way
        for (BluetoothGattCharacteristic c : characteristics) {
            if (c.getUuid().equals(characteristic.getUuid())) {
                return;
            }
        }

        characteristics.add(characteristic);
    }

    @Override
    public int getCount()
    {
        return characteristics.size();
    }

    @Override
    public Object getItem(int index)
    {
        return characteristics.get(index);
    }

    @Override
    public long getItemId(int index)
    {
        return index;
    }

    @Override
    public View getView(int index, View view, ViewGroup root)
    {
        BluetoothGattCharacteristic characteristic = characteristics.get(index);

        if (view == null) {
            view =
                inflater.inflate(R.layout.characteristics_list_item_generic,
                    null);
        }

        TextView name = (TextView) view.findViewById(R.id.characteristic_name);
        name.setText(getName(view.getContext(), characteristic));

        TextView properties =
            (TextView) view.findViewById(R.id.characteristic_properties);
        properties.setText(getProperties(view.getContext(), characteristic));

        TextView uuid = (TextView) view.findViewById(R.id.characteristic_uuid);
        uuid.setText(characteristic.getUuid().toString());

        return view;
    }

    private String getName(Context context,
        BluetoothGattCharacteristic characteristic)
    {
        return UUIDHelper.toString(context, characteristic.getUuid());
    }

    private String getProperties(Context context,
        BluetoothGattCharacteristic characteristic)
    {
        StringBuilder buffer = new StringBuilder();

        if (hasProperty(characteristic,
            BluetoothGattCharacteristic.PROPERTY_READ)) {
            buffer.append(context
                .getString(R.string.characteristic_property_read));
        }
        if (hasProperty(characteristic,
            BluetoothGattCharacteristic.PROPERTY_WRITE)) {
            buffer.append(context
                .getString(R.string.characteristic_property_write));
        }
        if (hasProperty(characteristic,
            BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE)) {
            buffer.append(context
                .getString(R.string.characteristic_property_signed_write));
        }
        if (hasProperty(characteristic,
            BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {
            buffer.append(context
                .getString(R.string.characteristic_property_write_no_response));
        }
        if (hasProperty(characteristic,
            BluetoothGattCharacteristic.PROPERTY_BROADCAST)) {
            buffer.append(context
                .getString(R.string.characteristic_property_broadcast));
        }
        if (hasProperty(characteristic,
            BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            buffer.append(context
                .getString(R.string.characteristic_property_notify));
        }
        if (hasProperty(characteristic,
            BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            buffer.append(context
                .getString(R.string.characteristic_property_indicate));
        }
        if (hasProperty(characteristic,
            BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS)) {
            buffer.append(context
                .getString(R.string.characteristic_property_extended_props));
        }

        return buffer.toString();
    }

    private boolean hasProperty(BluetoothGattCharacteristic characteristic,
        int property)
    {
        return (characteristic.getProperties() & property) == property;
    }
}
