package com.example.test20;

import android.hardware.usb.UsbDevice;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

public class MainActivity extends AppCompatActivity implements ArduinoListener {
    private Arduino arduino;
    private TextView displayTextView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayTextView = findViewById(R.id.diplayTextView);
        editText = findViewById(R.id.editText);
        Button sendBtn = findViewById(R.id.sendBtn);
        displayTextView.setMovementMethod(new ScrollingMovementMethod());


        sendBtn.setOnClickListener(
                v -> {
                    String editTextString =   editText.getText().toString();
                    arduino.send(editTextString.getBytes());
                    editText.getText().clear();
                }
        );
        arduino = new Arduino(this);
        arduino.addVendorId(2341);
    }

    @Override
    protected void onStart() {
        super.onStart();
        arduino.setArduinoListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        arduino.unsetArduinoListener();
        arduino.close();
    }
    @Override
    public void onArduinoAttached(UsbDevice device) {
        display("arduino attached...");
        arduino.open(device);
    }
    @Override
    public void onArduinoDetached() {
        display("arduino detached.");
    }
    @Override
    public void onArduinoMessage(byte[] bytes) {
        display(new String(bytes));
    }
    @Override
    public void onArduinoOpened() {
        String str = "arduino opened...";
        arduino.send(str.getBytes());
    }
    @Override
    public void onUsbPermissionDenied() {
        display("Permission denied. Attempting again in 3 sec...");
        new Handler().postDelayed(() -> arduino.reopen(), 3000);
    }

    private void display(final String message){
        runOnUiThread(() -> displayTextView.append(message));
    }
}