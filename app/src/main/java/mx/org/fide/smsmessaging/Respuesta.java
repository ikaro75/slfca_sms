package mx.org.fide.smsmessaging;

/**
 * Created by daniel.martinez on 08/05/2015.
 */
public class Respuesta {
    private String resultado;
    private String estatus;

    public Respuesta() {
    }

    public Respuesta(String resultado, String estatus) {
        this.resultado = resultado;
        this.estatus = estatus;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}
