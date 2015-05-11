package mx.org.fide.smsmessaging;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SMS extends Activity
{
    Button btnSendSMS;
    EditText txtPhoneNo;
    EditText txtMessage;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
        txtMessage = (EditText) findViewById(R.id.txtMessage);

        btnSendSMS.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String phoneNo = txtPhoneNo.getText().toString();
                String message = txtMessage.getText().toString();
                if (phoneNo.length()>0 && message.length()>0)
                    try {
                        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNo, null, message, null, null);
                        Toast.makeText(getApplicationContext(), "SMS enviado",
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Falló el envío, por favor vuelva a intentarlo",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                else
                    Toast.makeText(getBaseContext(),
                            "Se requieren número y mensaje, verifique por favor.",
                            Toast.LENGTH_SHORT).show();
            }
        });
    }


}
