package mx.org.fide.smsmessaging;



import android.util.Log;

import java.io.InputStream;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;



public class ValidateData  {
    private String webserviceUrl="";
    private String resultado= "";
    public ValidateData(String webserviceUrl) {
        this.webserviceUrl = webserviceUrl;
    }

    public Respuesta getWebService() throws XmlPullParserException, IOException {
        Respuesta respuesta = null;
        try {
              respuesta=loadXmlFromNetwork(this.webserviceUrl);
        } catch (IOException exio) {
            respuesta = new Respuesta("rechazado","El servicio no se encuentra disponible, espere unos minutos e intente nuevamente, por favor");
        } catch (XmlPullParserException exxml) {
            respuesta = new Respuesta("rechazado","Error en el formato de la respuesta");
        }

        return respuesta;
    }

    private Respuesta loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        SMSWebServiceParser SMSWebServiceParser = new SMSWebServiceParser();
        Respuesta respuesta = null;
        try {
            stream = downloadUrl(urlString);
            respuesta = SMSWebServiceParser.parse(stream);
            //resultado = convertStreamToString(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (IOException exio) {
            respuesta = new Respuesta("rechazado","El servidor SICOM no responde, espere unos minutos e intente nuevamente, por favor");
        } catch (XmlPullParserException exxml) {
            respuesta = new Respuesta("rechazado","Error en el formato de la respuesta");
        }
        finally {
            if (stream != null) {
                stream.close();
            }
        }

        return respuesta;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        /*conn.setReadTimeout(10000);  //milisegundo
        conn.setConnectTimeout(15000); //milisegundos */
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
