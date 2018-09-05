package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Producto {
    int id;
    String descripcion;
    int nPiezas; //indica la cantidad de piezas que componen un producto;
    int[] lPiezas; //lista de los ids de las piezas que componen un producto;
    
    public Producto(int id,String descripcion,int nPiezas,String lista){
        this.id=id;
        this.descripcion=descripcion;
        this.nPiezas=nPiezas;
        this.lPiezas=new int[nPiezas];
        String[] piezas=lista.split("/");
        for(int i=0;i<nPiezas;i++)
            lPiezas[i]=Integer.parseInt(piezas[i]);
    }
}
