package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Set {
   int id;
   String modelo;//int idModelo;
   String color;//int idColor;
   int nProductos; //numero de productos que componen el set
   int[] lProductos; //lista de los id de los productos
   //int stockActual; //cantidad de sets completos que se encuentran en el almacén

   public Set(int id, String modelo,String color,int nProductos,String lista){
       this.id=id;
       this.modelo=modelo;
       this.color=color;
       this.nProductos=nProductos;
       this.lProductos=new int[nProductos];
       String[] productos = lista.split("/");
       for(int i=0;i<nProductos;i++){
           lProductos[i]=Integer.parseInt(productos[i]);
       }
   }
}
