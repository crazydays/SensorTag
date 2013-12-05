package org.crazydays.sensortag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.crazydays.sensortag.R;

import android.content.Context;

public class UUIDHelper
{
    public interface Gap
    {
        public final static String SERVICE =
            "00001800-0000-1000-8000-00805f9b34fb";
        public final static String DEVICE_NAME =
            "00002a00-0000-1000-8000-00805f9b34fb";
        public final static String APPEARANCE =
            "00002a01-0000-1000-8000-00805f9b34fb";
        public final static String PERIPERHAL_PRIVACY_FLAG =
            "00002a02-0000-1000-8000-00805f9b34fb";
        public final static String RECONNECT_ADDRESS =
            "00002a03-0000-1000-8000-00805f9b34fb";
        public final static String PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS =
            "00001800-0000-1000-8000-00805f9b34fb";
    }

    public interface Gatt
    {
        public final static String SERVICE =
            "00001801-0000-1000-8000-00805f9b34fb";
        public final static String SERVICE_CHANGED =
            "00002a05-0000-1000-8000-00805f9b34fb";
    }

    public interface DeviceInfo
    {
        public final static String SERVICE =
            "0000180a-0000-1000-8000-00805f9b34fb";
        public final static String SYSTEM_ID =
            "00002a23-0000-1000-8000-00805f9b34fb";
        public final static String MODEL_NUMBER =
            "00002a24-0000-1000-8000-00805f9b34fb";
        public final static String SERIAL_NUMBER =
            "00002a25-0000-1000-8000-00805f9b34fb";
        public final static String FIRMWARE_REVISION =
            "00002a26-0000-1000-8000-00805f9b34fb";
        public final static String HARDWARE_REVISION =
            "00002a27-0000-1000-8000-00805f9b34fb";
        public final static String SOFTWARE_REVISION =
            "00002a28-0000-1000-8000-00805f9b34fb";
        public final static String MANUFACTURE_NAME =
            "00002a29-0000-1000-8000-00805f9b34fb";
        public final static String _11073_CERT_DATA =
            "00002a2a-0000-1000-8000-00805f9b34fb";
        public final static String PNP_ID =
            "00002a50-0000-1000-8000-00805f9b34fb";
    }

    public interface IrTemperature
    {
        public final static String SERVICE =
            "f000aa00-0451-4000-b000-000000000000";
        public final static String DATA =
            "f000aa01-0451-4000-b000-000000000000";
        public final static String CONFIGURATION =
            "f000aa02-0451-4000-b000-000000000000";
    }

    public interface Accelerometer
    {
        public final static String SERVICE =
            "f000aa10-0451-4000-b000-000000000000";
        public final static String DATA =
            "f000aa11-0451-4000-b000-000000000000";
        public final static String CONFIGURATION =
            "f000aa12-0451-4000-b000-000000000000";
        public final static String PERIOD =
            "f000aa13-0451-4000-b000-000000000000";
    }

    public interface Humidity
    {
        public final static String SERVICE =
            "f000aa20-0451-4000-b000-000000000000";
        public final static String DATA =
            "f000aa21-0451-4000-b000-000000000000";
        public final static String CONFIGURATION =
            "f000aa22-0451-4000-b000-000000000000";
    }

    public interface Magnetometer
    {
        public final static String SERVICE =
            "f000aa30-0451-4000-b000-000000000000";
        public final static String DATA =
            "f000aa31-0451-4000-b000-000000000000";
        public final static String CONFIGURATION =
            "f000aa32-0451-4000-b000-000000000000";
        public final static String PERIOD =
            "f000aa33-0451-4000-b000-000000000000";
    }

    public interface Barometer
    {
        public final static String SERVICE =
            "f000aa40-0451-4000-b000-000000000000";
        public final static String DATA =
            "f000aa41-0451-4000-b000-000000000000";
        public final static String CONFIGURATION =
            "f000aa42-0451-4000-b000-000000000000";
        public final static String PERIOD =
            "f000aa43-0451-4000-b000-000000000000";
    }

    public interface Gyroscope
    {
        public final static String SERVICE =
            "f000aa50-0451-4000-b000-000000000000";
        public final static String DATA =
            "f000aa51-0451-4000-b000-000000000000";
        public final static String CONFIGURATION =
            "f000aa52-0451-4000-b000-000000000000";
    }

    public interface Test
    {
        public final static String SERVICE =
            "f000aa60-0451-4000-b000-000000000000";
        public final static String DATA =
            "f000aa61-0451-4000-b000-000000000000";
        public final static String CONFIGURATION =
            "f000aa62-0451-4000-b000-000000000000";
    }

    private final static Map<String, Integer> UUID_TO_RESOURCE_ID =
        new HashMap<String, Integer>();

    static {
        // gap service
        set(Gap.SERVICE, R.string.gap_service);
        set(Gap.DEVICE_NAME, R.string.gap_device_name);
        set(Gap.APPEARANCE, R.string.gap_appearance);
        set(Gap.PERIPERHAL_PRIVACY_FLAG, R.string.gap_peripheral_privacy_flag);
        set(Gap.RECONNECT_ADDRESS, R.string.gap_reconnect_address);
        set(Gap.PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS,
            R.string.gap_peripheral_preferred_connection_parameters);

        // gatt service
        set(Gatt.SERVICE, R.string.gatt_service);
        set(Gatt.SERVICE_CHANGED, R.string.gatt_service_changed);

        // device info
        set(DeviceInfo.SERVICE, R.string.device_info_service);
        set(DeviceInfo.SYSTEM_ID, R.string.device_info_system_id);
        set(DeviceInfo.MODEL_NUMBER, R.string.device_info_model_number);
        set(DeviceInfo.SERIAL_NUMBER, R.string.device_info_serial_number);
        set(DeviceInfo.FIRMWARE_REVISION,
            R.string.device_info_firmware_revision);
        set(DeviceInfo.HARDWARE_REVISION,
            R.string.device_info_hardware_revision);
        set(DeviceInfo.SOFTWARE_REVISION,
            R.string.device_info_software_revision);
        set(DeviceInfo.MANUFACTURE_NAME, R.string.device_info_manufacture_name);
        set(DeviceInfo._11073_CERT_DATA, R.string.device_info_11073_cert_data);
        set(DeviceInfo.PNP_ID, R.string.pnp_id);

        // ir temperature
        set(IrTemperature.SERVICE, R.string.ir_temperature_service);
        set(IrTemperature.DATA, R.string.ir_temperature_data);
        set(IrTemperature.CONFIGURATION, R.string.ir_temperature_conf);

        // accelerometer
        set(Accelerometer.SERVICE, R.string.accelerometer_service);
        set(Accelerometer.DATA, R.string.accelerometer_data);
        set(Accelerometer.CONFIGURATION, R.string.accelerometer_conf);
        set(Accelerometer.PERIOD, R.string.accelerometer_period);

        // humidity
        set(Humidity.SERVICE, R.string.humidity_service);
        set(Humidity.DATA, R.string.humidity_data);
        set(Humidity.CONFIGURATION, R.string.humidity_conf);

        // magnetometer
        set(Magnetometer.SERVICE, R.string.magnetometer_service);
        set(Magnetometer.DATA, R.string.magnetometer_data);
        set(Magnetometer.CONFIGURATION, R.string.magnetometer_conf);
        set(Magnetometer.PERIOD, R.string.magnetometer_period);

        // barometer
        set(Barometer.SERVICE, R.string.barometer_service);
        set(Barometer.DATA, R.string.barometer_data);
        set(Barometer.CONFIGURATION, R.string.barometer_conf);
        set(Barometer.PERIOD, R.string.barometer_period);

        // gyroscope
        set(Gyroscope.SERVICE, R.string.gyroscope_service);
        set(Gyroscope.DATA, R.string.gyroscope_data);
        set(Gyroscope.CONFIGURATION, R.string.gyroscope_conf);

        // test
        set(Test.SERVICE, R.string.test_service);
        set(Test.DATA, R.string.test_data);
        set(Test.CONFIGURATION, R.string.test_conf);
    }

    private final static void set(String uuid, int resourceId)
    {
        UUID_TO_RESOURCE_ID.put(uuid, resourceId);
    }

    public final static int toResourceId(String uuid)
    {
        if (UUID_TO_RESOURCE_ID.containsKey(uuid)) {
            return UUID_TO_RESOURCE_ID.get(uuid);
        } else {
            return R.string.uuid_unknown;
        }
    }

    public final static String toString(Context context, UUID uuid)
    {
        return context.getString(toResourceId(uuid.toString()));
    }
}
