package com.example.testmqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.hivemq.client.internal.mqtt.MqttClientConfig;
import com.hivemq.client.internal.mqtt.MqttClientConnectionConfig;
import com.hivemq.client.internal.mqtt.MqttRxClient;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientBuilder;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.Mqtt3ClientConfig;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.Mqtt5ClientConfig;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int SENSOR_SENSITIVITY = 4;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private static Mqtt5BlockingClient client;
    private static boolean envia;
    private static Switch simpleSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleSwitch = (Switch) findViewById(R.id.switch1);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost("10.180.14.80")
                .serverPort(1883)
                .buildBlocking();
        client.connect();
        simpleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(simpleSwitch.isChecked()){
                    simpleSwitch.setText("Desativar");
                }else{
                    simpleSwitch.setText("Ativar");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (simpleSwitch.isChecked()){
            if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                Toast.makeText(getApplicationContext(), "Perto", Toast.LENGTH_SHORT).show();
                client.publishWith().topic("celular/sensor").qos(MqttQos.AT_LEAST_ONCE).payload("Perto".getBytes()).send();
            } else {
                Toast.makeText(getApplicationContext(), "Longe", Toast.LENGTH_SHORT).show();
                client.publishWith().topic("celular/sensor").qos(MqttQos.AT_LEAST_ONCE).payload("Longe".getBytes()).send();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Desativado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
