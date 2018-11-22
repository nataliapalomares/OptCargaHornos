package optimizarcargahornos;


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
    public void reiniciarPedidos(){
        //restablece a 0 la cantidad pedida en el resumen de sets y la
        //prioridad promedio
        int cant=this.lSets.length;
        for(int i=0;i<cant;i++){
            rSets[i][0]=0;//Cantidad pedida
            prioridadProm[i]=0.0;//Prioridad promedio
        }
    }
    public void addRSet(int ind,char tipo,int cant){
        switch(tipo){
            case'P':rSets[ind][0]+=cant;// PEDIDO
                break;
            case'A':rSets[ind][1]=cant;// ALMACEN
                    break;
            case'R':prioridadProm[ind]+=cant;// PRIORIDAD PROMEDIO INICIAL
        }
    }
    public double pPromedio(int ind){
        if(rSets[ind][0]>0){
            prioridadProm[ind]=prioridadProm[ind]/rSets[ind][0];
        }else prioridadProm[ind]=0;
        return prioridadProm[ind];
    }
    public int calcularFaltante(int ind){
        rSets[ind][2]=Math.max(rSets[ind][0]-rSets[ind][1],0);
        return rSets[ind][2];
    }
    public int[] productos(int ind){
        return this.lSets[ind].listaProd();
    }
    public int stock(int ind){
        return rSets[ind][1];
    }
}
