package org.crazydays.sensortag;

import java.util.UUID;

import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.content.pm.PackageManager;

public class SensorTagActivity
    extends Activity
    implements DiscoveryFragment.SelectDeviceListener,
    ServicesFragment.SelectBluetoothGattServiceListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_tag_activity);

        assertBtleAvailable();
        initializeContentView();
    }

    private void assertBtleAvailable()
    {
        if (!getPackageManager().hasSystemFeature(
            PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.btle_not_supported,
                Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeContentView()
    {
        getFragmentManager().beginTransaction()
            .replace(R.id.content_view, new DiscoveryFragment(), "discovery")
            .commit();
    }

    @Override
    public void selectDevice(String deviceAddress)
    {
        ServicesFragment fragment = new ServicesFragment();

        Bundle arguments = new Bundle();
        arguments.putString(ServicesFragment.DEVICE_ADDRESS, deviceAddress);
        fragment.setArguments(arguments);

        getFragmentManager().beginTransaction()
            .replace(R.id.content_view, fragment, "deviceServices")
            .addToBackStack("discovery").commit();
    }

    @Override
    public void selectService(String deviceAddress, UUID serviceUuid)
    {
        ServiceDetailFragment fragment = new ServiceDetailFragment();

        Bundle arguments = new Bundle();
        arguments
            .putString(ServiceDetailFragment.DEVICE_ADDRESS, deviceAddress);
        arguments.putSerializable(ServiceDetailFragment.SERVICE_UUID,
            serviceUuid);
        fragment.setArguments(arguments);

        getFragmentManager().beginTransaction()
            .replace(R.id.content_view, fragment, "deviceService")
            .addToBackStack("deviceServices").commit();
    }
}
