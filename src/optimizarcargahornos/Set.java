package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Set {
   private int id;
   private String modelo;//int idModelo;
   private String color;//int idColor;
   private int nProductos; //numero de productos que componen el set
   final private int[] lProductos; //lista de los id de los productos

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
   public int[] listaProd(){
       return this.lProductos;
   }
   public int id(){
       return this.id;
   }
   public String modelo(){
       return this.modelo;
   }
   public String color(){
       return this.color;
   }
   public int nProductos(){
       return this.nProductos;
   }
   public String cadenaListaProd(){
       String cadena="";
       for(int i=0;i<nProductos;i++){
           if(i>0) cadena=cadena+", ";
           cadena=cadena+lProductos[i];
       }
       return cadena;
   }
}
