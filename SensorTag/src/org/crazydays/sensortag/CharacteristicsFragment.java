package org.crazydays.sensortag;

import java.util.UUID;

import android.app.Fragment;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

public abstract class CharacteristicsFragment
    extends Fragment
{
    public final static String NOTIFY_UUID =
        "00002902-0000-1000-8000-00805f9b34fb";

    public final static byte[] DISABLE_NOTIFICATION_VALUE =
        BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
    public final static byte[] ENABLE_NOTIFICATION_VALUE =
        BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;

    public static CharacteristicsFragment build(BluetoothGatt bluetoothGatt,
        UUID uuid)
    {
        CharacteristicsFragment fragment = null;

        if (UUIDHelper.IrTemperature.SERVICE.equals(uuid.toString())) {
            fragment = new IrTemperatureCharacteristicsFragment();
        } else if (UUIDHelper.Accelerometer.SERVICE.equals(uuid.toString())) {
            fragment = new AccelerometerCharacteristicsFragment();
        } else {
            fragment = new CharacteristicsListFragment();
        }

        fragment.setBluetoothGatt(bluetoothGatt);

        return fragment;
    }

    private BluetoothGatt bluetoothGatt;

    private void setBluetoothGatt(BluetoothGatt bluetoothGatt)
    {
        this.bluetoothGatt = bluetoothGatt;
    }

    protected void requestRead(BluetoothGattCharacteristic characteristic)
    {
        bluetoothGatt.readCharacteristic(characteristic);
    }

    protected void requestWrite(BluetoothGattCharacteristic characteristic)
    {
        bluetoothGatt.writeCharacteristic(characteristic);
    }

    protected void requestNotification(
        BluetoothGattCharacteristic characteristic, boolean enable)
    {
        bluetoothGatt.setCharacteristicNotification(characteristic, enable);

        BluetoothGattDescriptor descriptor =
            characteristic.getDescriptor(UUID.fromString(NOTIFY_UUID));
        descriptor.setValue(enable ? ENABLE_NOTIFICATION_VALUE
            : DISABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
    }

    public abstract void updateConnectionState(int state);

    public abstract void addCharacteristic(
        BluetoothGattCharacteristic characteristic);

    public abstract void readCharacteristic(
        BluetoothGattCharacteristic characteristic);

    public abstract void writeCharacteristic(
        BluetoothGattCharacteristic characteristic);

    public abstract void changedCharacteristic(
        BluetoothGattCharacteristic characteristic);

    public abstract void writeDescriptor(BluetoothGattDescriptor descriptor);
}
