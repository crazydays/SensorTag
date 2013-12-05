package org.crazydays.sensortag.log;

import org.crazydays.sensortag.R;
import org.crazydays.sensortag.UUIDHelper;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

public class BluetoothGattLogger
{
    public final static void log(Context context, BluetoothGatt gatt)
    {
        log(context, gatt, 0);
    }

    private final static void
        log(Context context, BluetoothGatt gatt, int level)
    {
        StringBuilder buffer = indentedBuffer(level);
        buffer.append("gatt -");

        log(gatt.getClass(), buffer);

        for (BluetoothGattService service : gatt.getServices()) {
            log(context, service, level + 1);
        }
    }

    public final static void log(Context context, BluetoothGattService service)
    {
        log(context, service, 0);
    }

    private final static void log(Context context,
        BluetoothGattService service, int level)
    {
        String uuid = service.getUuid().toString();
        String name = UUIDHelper.toString(context, service.getUuid());
        String type = getServiceType(context, service.getType());

        StringBuilder buffer = indentedBuffer(level);
        buffer.append("service - ");
        buffer.append(uuid);
        buffer.append(" (").append(name).append(") ");
        buffer.append("type: ").append(type);

        log(service.getClass(), buffer);

        for (BluetoothGattCharacteristic characteristic : service
            .getCharacteristics()) {
            log(context, characteristic, level + 1);
        }

        for (BluetoothGattService secondary : service.getIncludedServices()) {
            log(context, secondary, level + 1);
        }
    }

    private final static void log(Context context,
        BluetoothGattCharacteristic characteristic, int level)
    {
        String uuid = characteristic.getUuid().toString();
        String name = UUIDHelper.toString(context, characteristic.getUuid());
        String properties =
            getCharacteristicProperties(context, characteristic.getProperties());

        StringBuilder buffer = indentedBuffer(level);
        buffer.append("characteristic - ");
        buffer.append(uuid);
        buffer.append(" (").append(name).append(") ");
        buffer.append("properties: ").append(properties);

        log(characteristic.getClass(), buffer);
    }

    private final static StringBuilder indentedBuffer(int level)
    {
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < level; i++) {
            buffer.append("  ");
        }

        return buffer;
    }

    private final static void log(Class<?> clazz, StringBuilder message)
    {
        Log.d(clazz.getSimpleName(), message.toString());
    }

    private final static String
        getServiceType(Context context, int serviceType)
    {
        switch (serviceType) {
        case BluetoothGattService.SERVICE_TYPE_PRIMARY:
            return context.getString(R.string.service_type_primary);
        case BluetoothGattService.SERVICE_TYPE_SECONDARY:
            return context.getString(R.string.service_type_secondary);
        default:
            return context.getString(R.string.service_type_unknown);
        }
    }

    private final static String getCharacteristicProperties(Context context,
        int properties)
    {
        StringBuilder buffer = new StringBuilder();

        if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            buffer.append(context
                .getString(R.string.characteristic_property_read));
        }
        if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
            buffer.append(context
                .getString(R.string.characteristic_property_write));
        }
        if ((properties & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) > 0) {
            buffer.append(context
                .getString(R.string.characteristic_property_signed_write));
        }
        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            buffer.append(context
                .getString(R.string.characteristic_property_notify));
        }
        if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            buffer.append(context
                .getString(R.string.characteristic_property_indicate));
        }
        if ((properties & BluetoothGattCharacteristic.PROPERTY_BROADCAST) > 0) {
            buffer.append(context
                .getString(R.string.characteristic_property_broadcast));
        }
        if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
            buffer.append(context
                .getString(R.string.characteristic_property_write_no_response));
        }
        if ((properties & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) > 0) {
            buffer.append(context
                .getString(R.string.characteristic_property_extended_props));
        }

        return buffer.toString();
    }
}
