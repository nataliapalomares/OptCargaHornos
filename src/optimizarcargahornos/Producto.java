package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Producto {
    private int id;
    private String descripcion;
    private int nPiezas; //indica la cantidad de piezas que componen un producto;
    final private int[] lPiezas; //lista de los ids de las piezas que componen un producto;
    
    public Producto(int id,String descripcion,int nPiezas,String lista){
        this.id=id;
        this.descripcion=descripcion;
        this.nPiezas=nPiezas;
        this.lPiezas=new int[nPiezas];
        String[] piezas=lista.split("/");
        for(int i=0;i<nPiezas;i++)
            lPiezas[i]=Integer.parseInt(piezas[i]);
    }
    public int id(){
        return this.id;
    }
    public int nPiezas(){
        return this.nPiezas;
    }
    public int[] listaPiezas(){
        return this.lPiezas;
    }
    public String cadenaPiezas(){
        String cadena="";
        for(int i=0;i<nPiezas;i++){
            if(i>0)cadena=cadena+", ";
            cadena=cadena+lPiezas[i];
        }
        return cadena;
    }
    public String[] descripcion(){
        String[] datosDescrip=descripcion.split("/");
        return datosDescrip;
    }
}
