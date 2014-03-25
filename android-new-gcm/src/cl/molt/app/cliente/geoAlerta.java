package cl.molt.app.cliente;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class geoAlerta implements KvmSerializable  {
	
	
	public int id;
	public String hora;
	public String tipo;
	
	public geoAlerta()
	{
		id = 0;
		hora="";
		tipo="";
	}
	
	public geoAlerta(int id, String hora, String tipo)
	{
		this.id = id;
		this.hora = hora;
		this.tipo = tipo;
	}
	
	@Override
	public Object getProperty(int arg0) {

		switch(arg0)
        {
        case 0:
            return id;
        case 1:
            return hora;
        case 2:
            return tipo;
        }
		
		return null;
	}
	
	@Override
	public int getPropertyCount() {
		return 3;
	}
	
	@Override
	public void getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
		switch(ind)
        {
	        case 0:
	            info.type = PropertyInfo.INTEGER_CLASS;
	            info.name = "Id";
	            break;
	        case 1:
	            info.type = PropertyInfo.STRING_CLASS;
	            info.name = "Hora";
	            break;
	        case 2:
	            info.type = PropertyInfo.STRING_CLASS;
	            info.name = "Tipo";
	            break;
	        default:break;
        }
	}
	
	@Override
	public void setProperty(int ind, Object val) {
		switch(ind)
        {
	        case 0:
	            id = Integer.parseInt(val.toString());
	            break;
	        case 1:
	            hora = val.toString();
	            break;
	        case 2:
	            tipo = val.toString();
	            break;
	        default:
	            break;
        }
	}
}