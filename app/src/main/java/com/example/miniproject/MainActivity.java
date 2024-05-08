package com.example.miniproject;


import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    TextView txtTemp, txtHumi;
    LabeledSwitch btnLED, btnPUMP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        txtTemp = findViewById(R.id.txtTemperature);
        txtHumi = findViewById(R.id.txtHumidity);
        btnLED = findViewById(R.id.btnLED);
        btnPUMP = findViewById(R.id.btnPUMP);

        btnLED.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sendDataMQTT("m2a0n0h3/feeds/led", "1");
                }else {
                    sendDataMQTT("m2a0n0h3/feeds/led", "0");
                }
            }
        });

        btnPUMP.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sendDataMQTT("m2a0n0h3/feeds/pump", "1");
                }else {
                    sendDataMQTT("m2a0n0h3/feeds/pump", "0");
                }
            }
        });
        startMQTT();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }
    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + "***" + message.toString());
                if(topic.contains("temperature")) {
                    String str = message.toString();
                    int tmp =  Integer.parseInt(str);
                    if(tmp < 25) sendNotification(getApplicationContext(), "temperature",message.toString(), "duoi");
                    if(tmp > 35) sendNotification(getApplicationContext(), "temperature",message.toString(), "tren");
                    txtTemp.setText(message.toString() + "°C");
                } else if (topic.contains("humidity")) {
                    String str = message.toString();
                    int tmp =  Integer.parseInt(str);
                    if(tmp < 40) sendNotification(getApplicationContext(),"humidity",message.toString(), "duoi");
                    if(tmp > 60) sendNotification(getApplicationContext(), "humidity",message.toString(), "tren");
                    txtHumi.setText(message.toString() + "%");
                } else if (topic.contains("led")) {
                    if(message.toString().equals("1")) {
                        btnLED.setOn(true);
                    }else {
                        btnLED.setOn(false);
                    }
                } else if (topic.contains("pump")) {
                    if(message.toString().equals("1")) {
                        btnPUMP.setOn(true);
                    }else {
                        btnPUMP.setOn(false);
                    }
                }
            }

            private void sendNotification(Context context,String topic, String message, String txt) {
                    if(topic == "temperature"){
                        if(txt == "tren"){
                            Notification notification = new NotificationCompat.Builder(context, MyNotification.CHANNEL_ID)
                                    .setSmallIcon(R.drawable.warning)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Warning!!!")
                                    .setContentText("The temperature is too high: " + message + "*C")
                                    .build();
                            NotificationManagerCompat.from(context).notify(new Random().nextInt(), notification);
                        }
                        else{
                            Notification notification = new NotificationCompat.Builder(context, MyNotification.CHANNEL_ID)
                                    .setSmallIcon(R.drawable.warning)
                                    .setContentTitle("Warning!!!")
                                    .setContentText("The temperature is too low: " + message + "*C")
                                    .build();

                            NotificationManagerCompat.from(context).notify(new Random().nextInt(), notification);
                        }
                    }
                    if(topic == "humidity"){
                        if(txt == "tren"){
                            Notification notification = new NotificationCompat.Builder(context, MyNotification.CHANNEL_ID)
                                    .setSmallIcon(R.drawable.warning)
                                    .setContentTitle("Warning!!!")
                                    .setContentText("The air humidity is too high: " + message + "%")
                                    .build();
                            NotificationManagerCompat.from(context).notify(new Random().nextInt(), notification);
                        }
                        else{
                            Notification notification = new NotificationCompat.Builder(context, MyNotification.CHANNEL_ID)
                                    .setSmallIcon(R.drawable.warning)
                                    .setContentTitle("Warning!!!")
                                    .setContentText("The air humidity is too low: " + message + "%")
                                    .build();

                            NotificationManagerCompat.from(context).notify(new Random().nextInt(), notification);
                        }
                    }
            }


            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

}