package optimizarcargahornos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Natalia Palomares Melgarejo
 */
public class GestorSets {
    Set[] lSets;
    int[][] rSets;
    double[] prioridadProm;
    
    public GestorSets(){
    }

    public void add(Set setAgregar,int ind){
        this.lSets[ind]=setAgregar;
    }
    public int size(){
          return this.lSets.length; 
    }
    public void completarIni(int cant){
        this.lSets=new Set[cant];
        this.rSets=new int[cant][3];
        this.prioridadProm=new double[cant];
    }
    public void addRSet(int id,char tipo,int cant){
        switch(tipo){
            case'P':rSets[id][0]+=cant;// PEDIDO
                break;
            case'A':rSets[id][1]=cant;// ALMACEN
                    break;
            case'R':prioridadProm[id]+=cant;// PRIORIDAD PROMEDIO INICIAL
        }
    }
    public double pPromedio(int id){
        if(rSets[id][0]>0){
            prioridadProm[id]=prioridadProm[id]/rSets[id][0];
        }else prioridadProm[id]=0;
        return prioridadProm[id];
    }
    public int calcularFaltante(int id){
        rSets[id][2]=Math.max(rSets[id][0]-rSets[id][1],0);
        return rSets[id][2];
    }
    public int[] productos(int id){
        return this.lSets[id].listaProd();
    }
}
