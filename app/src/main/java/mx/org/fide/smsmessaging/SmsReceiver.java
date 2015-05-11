package mx.org.fide.smsmessaging;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;

public class SmsReceiver extends Activity {
    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    final SmsManager sms = SmsManager.getDefault();
    private Thread t;
    private String webserviceUrl = "http://ahorrateunaluz.org.mx:8080/slfca_20150507/control?$cmd=sms&$rpu=%1&$id_control=%2";
    private Dialog dialogo = null;
    private Integer mensajesProcesados =0 ;
    private Integer mensajesEnviados =0 ;
    private Integer nuevosBeneficiarios=0;
    private Integer registrosFallidos=0;

    private final Handler handler = new Handler();

    final Runnable updateRunnable = new Runnable() {
        public void run() {
            //call the activity method that updates the UI
            updateUI();
        }
    };

    private void updateUI() {
        dialogo.dismiss();
        TextView tvMensajesProcesados = (TextView) findViewById(R.id.tvMensajesProcesados);
        TextView tvMensajesEnviados = (TextView) findViewById(R.id.tvMensajesEnviados);
        TextView tvNuevosBeneficiarios = (TextView) findViewById(R.id.tvNuevosBeneficiariosIngresados);
        TextView tvRegistrosFallidos = (TextView) findViewById(R.id.tvRegisrosFallidos);
        tvMensajesProcesados.setText("Mensajes procesados: ".concat(mensajesProcesados.toString()));
        tvMensajesEnviados.setText("Mensajes enviados: ".concat(mensajesEnviados.toString()));
        tvNuevosBeneficiarios.setText("Solicitudes aceptadas: ".concat(nuevosBeneficiarios.toString()));
        tvRegistrosFallidos.setText("Solicitudes rechazadas: ".concat(registrosFallidos.toString()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsreceiver);


        IntentFilter filter = new IntentFilter(ACTION);
        this.registerReceiver(mReceivedSMSReceiver, filter);
    }

    private void validaDatos(final String numero, final String rpu, final String idControl) {

        dialogo = ProgressDialog.show(SmsReceiver.this, "Espere por favor", "Validación en progreso...");
        Thread hilo = new Thread(new Runnable() {
            public void run() {
                try {
                    Respuesta respuesta = null;
                    respuesta = new ValidateData(webserviceUrl.replace("%1", rpu).replace("%2", idControl)).getWebService();

                    if (respuesta.getEstatus().equals("rechazado")) {
                        registrosFallidos++;
                    } else {
                        nuevosBeneficiarios++;
                    }

                    sms.sendTextMessage(numero, null, respuesta.getResultado(), null, null);
                    mensajesEnviados++;
                } catch (XmlPullParserException e) {
                    registrosFallidos ++;
                    Log.e("Error al validar", "Error al validar " + e.getMessage());
                } catch (IOException e) {
                    registrosFallidos ++;
                    Log.e("Error al validar", "Error al validar " + e.getMessage());
                }

                handler.post(updateRunnable);

            }
        });
        hilo.start();
    }

    private final BroadcastReceiver mReceivedSMSReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //---get the SMS message passed in---
            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();
            String response = "";
            try {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {
                        mensajesProcesados++;

                        final SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        final String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        final String senderNum = phoneNumber;
                        final String message = currentMessage.getDisplayMessageBody();

                        //Valida los parametros
                        if (message.equals("")) {
                            response = "Favor de verificar el numero de servicio del usuario de 12 digitos";
                        } else {
                            final String[] aParameters = message.split(" ");
                            if (aParameters.length > 2) {
                                response = "Favor de verificar el numero de tienda DICONSA de 10 digitos";
                            }

                            if (aParameters[0].length() != 12) {
                                response = "Favor de verificar el número de servicio del usuario de 12 digitos";
                            }

                            if (response.equals("")) {
                                //dialogo = ProgressDialog.show(this.getActivity() ,"Espere","Validación en progreso...");
                                Toast toast = Toast.makeText(context,
                                        "Validación en progreso...", Toast.LENGTH_LONG);
                                final String rpu = aParameters[0];
                                final String idControl = aParameters[1];
                                validaDatos(senderNum, rpu, idControl);

                            } else {
                                //Se envía el error como respuesta
                                sms.sendTextMessage(senderNum, null, response, null, null);
                            }
                        }

                        // Show Alert
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,
                                "senderNum: " + senderNum + ", message: " + message, Toast.LENGTH_LONG);
                        toast.show();

                    } // end for loop
                } // bundle is null

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" + e);
                Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }

    };
}