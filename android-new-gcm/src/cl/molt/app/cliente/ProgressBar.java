package cl.molt.app.cliente;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ProgressBar extends Activity {
	
	//variables necesarias
    private ProgressDialog pd = null;
    private Object data = null;
    
    private TextView texto;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressbar);

        //asignamos el TextView para mostrar luego los datos procesados
        this.texto = (TextView) findViewById(R.id.texto);
        
        // Mostrar el ProgressDialog en este Thread
        this.pd = ProgressDialog.show(this, "Procesando", "Espere unos segundos...", true, false);
    
    }
    
    /**
     * Muestra el texto resultado
     * @param String textoAMostrar
     */
	public void mostrarResultado(String textoAMostrar){
    	this.texto.setText(textoAMostrar);
    }

}
