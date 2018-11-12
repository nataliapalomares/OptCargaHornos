package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class GestorProducto {
    //List<Producto> lProd;
    Producto[] lProd;
    int[][] rProd;
    double[] prioridadProm;
    public GestorProducto(){
    }
    public void add(Producto prodAgregar,int ind){
        this.lProd[ind]=prodAgregar;
    }
    public int size(){
        return lProd.length;
    }
    public void completarIni(int cant){
        this.lProd=new Producto[cant];
        this.rProd=new int[cant][3]; //PEDIDO, ALMACEN, FALTANTE
        this.prioridadProm=new double[cant];
    }
    public void addRProd(int ind,char tipo,int cant){
        switch(tipo){
            case'P':rProd[ind][0]=cant;// PEDIDO
                    break;
            case'A':rProd[ind][1]=cant;// ALMACEN
                    break;
        }
    }
    public int calcularFaltante(int ind){
        rProd[ind][2]=Math.max(rProd[ind][0]-rProd[ind][1],0);
        return rProd[ind][2];
    }
    public double pPromedio(int ind,double prioridad){
        //Si no faltan productos para cumplir lo necesitado, entonces prioridad=0
        if(rProd[ind][2]>0){
            prioridadProm[ind]=prioridad;
        }else prioridadProm[ind]=0;
        return prioridadProm[ind];
    }
    public int[] piezas(int ind){
        return this.lProd[ind].listaPiezas();
    }
    public int stock(int ind){
        return rProd[ind][1];
    }
}
