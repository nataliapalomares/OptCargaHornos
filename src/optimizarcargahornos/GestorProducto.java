package optimizarcargahornos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Natalia Palomares Melgarejo
 */
public class GestorProducto {
    //List<Producto> lProd;
    Producto[] lProd;
    int[][] rProd;
    double[] prioridadProm;
    public GestorProducto(){
        //lProd=new ArrayList();
    }
    public void add(Producto prodAgregar,int ind){
        this.lProd[ind]=prodAgregar;
        //this.lProd.add(prodAgregar);
    }
    public int size(){
        return lProd.length;
//        return lProd.size();
    }
    public void completarIni(int cant){
        this.lProd=new Producto[cant];
        this.rProd=new int[cant][3]; //PEDIDO, ALMACEN, FALTANTE
        this.prioridadProm=new double[cant];
    }
    public void addRProd(int id,char tipo,int cant){
        switch(tipo){
            case'P':rProd[id][0]=cant;// PEDIDO
                    break;
            case'A':rProd[id][1]=cant;// ALMACEN
                    break;
        }
    }
    public int calcularFaltante(int id){
        rProd[id][2]=Math.max(rProd[id][0]-rProd[id][1],0);
        return rProd[id][2];
    }
    public double pPromedio(int id,double prioridad){
        //Si no faltan productos para cumplir lo necesitado, entonces prioridad=0
        if(rProd[id][2]>0){
            prioridadProm[id]=prioridad;
        }else prioridadProm[id]=0;
        return prioridadProm[id];
    }
    public int[] piezas(int id){
        return this.lProd[id].listaPiezas();
//        return this.lProd.get(id).listaPiezas();
    }
}
