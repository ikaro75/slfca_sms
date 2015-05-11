package mx.org.fide.smsmessaging;

/**
 * Created by daniel.martinez on 07/05/2015.
 */
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;


public class SMSWebServiceParser  {


    public Respuesta parse(InputStream in) throws XmlPullParserException, IOException {
        Respuesta respuesta = null;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(in, null);
            int evento = parser.getEventType();


            while (evento != XmlPullParser.END_DOCUMENT)
            {
                String etiqueta = null;

                switch (evento)
                {
                    case XmlPullParser.START_DOCUMENT:
                        respuesta = null;
                        break;

                    case XmlPullParser.START_TAG:
                        etiqueta = parser.getName();

                        if (etiqueta.equals("ahorrateunaluz"))
                            respuesta = new Respuesta();
                        else if (respuesta != null)
                        {
                            if (etiqueta.equals("resultado"))
                            {
                                respuesta.setResultado(parser.nextText());
                            }
                            else if (etiqueta.equals("estatus"))
                            {
                                respuesta.setEstatus(parser.nextText());
                            }
                        }

                        break;

                    case XmlPullParser.END_TAG:

                        etiqueta = parser.getName();
                        break;
                }

                evento = parser.next();
            }

        } finally {
            in.close();
        }

        return respuesta;
    }
}


