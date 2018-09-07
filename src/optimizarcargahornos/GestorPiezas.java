package optimizarcargahornos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Natalia Palomares Melgarejo
 */
public class GestorPiezas {
    //List<Pieza> lPiezas;
    Pieza[] lPiezas;
    int[][] rPiezas;
    double[] prioridadProm;
    int maxFaltantes;
    
    public GestorPiezas(){
        //lPiezas=new ArrayList();
        maxFaltantes=0;
    }
    public void add(Pieza piezaAgregar, int ind){
        this.lPiezas[ind]=piezaAgregar;
        //this.lPiezas.add(piezaAgregar);
    }
    public int size(){
        return lPiezas.length;
        //return lPiezas.size();
    }
    public void completarIni(int cant){
        this.lPiezas=new Pieza[cant];
        this.rPiezas=new int[cant][4];//PEDIDOS, EN ALMACEN, FALTANTES, PENDIENTES POR HORNEAR
        this.prioridadProm=new double[cant];
    }
    public void addRPiezas(int id,char tipo,int cant){
        switch(tipo){
            case'P':rPiezas[id][0]=cant;// PEDIDO
                    break;
            case'A':rPiezas[id][1]=cant;// ALMACEN
                    break;
            case'Q':rPiezas[id][3]=cant;// PENDIENTES POR HORNEAR
        }
    }
    public boolean cabeEnCompartimento(int id,int maximoDC,int minimoDC,int medioDC){
        return this.lPiezas[id].cabeEnCompartimento(maximoDC, medioDC, medioDC);
        //return this.lPiezas.get(id).cabeEnCompartimento(maximoDC,minimoDC,medioDC);
    }
    public int calcularFaltante(int id){
        rPiezas[id][2]=Math.max(rPiezas[id][0]-rPiezas[id][1],0);
        return rPiezas[id][2];
    }
    
    public double getpPromedio(int id){
        return prioridadProm[id];
    }
    public double setpPromedio(int id,double prioridad){
        //Funcion para asignar valor a pPromedio
        //Si no faltan productos para cumplir lo necesitado, entonces prioridad=0
        if(rPiezas[id][2]>0){
            prioridadProm[id]=prioridad;
        }else prioridadProm[id]=0;
        return prioridadProm[id];
    }
    public double getVolumen(int id){
        return this.lPiezas[id].getVolumen();
        //return this.lPiezas.get(id).getVolumen();
    }
    public double getPeso(int id){
        return this.lPiezas[id].getPeso();
        //return this.lPiezas.get(id).getPeso();
    }
    public Pieza getPieza(int id){
        return this.lPiezas[id];
        //return this.lPiezas.get(id);
    }
    public int pendientes(int id){
        return rPiezas[id][3];
    }
    public int faltantes(int id){
        if(this.maxFaltantes<rPiezas[id][2])
            this.maxFaltantes=rPiezas[id][2];
        return rPiezas[id][2];
    }
}
