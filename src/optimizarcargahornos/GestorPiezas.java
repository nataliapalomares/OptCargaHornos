package optimizarcargahornos;

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
    public void addRPiezas(int ind,char tipo,int cant){
        switch(tipo){
            case'P':rPiezas[ind][0]=cant;// PEDIDO
                    break;
            case'A':rPiezas[ind][1]=cant;// ALMACEN
                    break;
            case'Q':rPiezas[ind][3]=cant;// PENDIENTES POR HORNEAR
        }
    }
    public boolean cabeEnCompartimento(int id,int maximoDC,int minimoDC,int medioDC){
        return this.lPiezas[id].cabeEnCompartimento(maximoDC, medioDC, medioDC);
        //return this.lPiezas.get(id).cabeEnCompartimento(maximoDC,minimoDC,medioDC);
    }
    public int calcularFaltante(int ind){
        rPiezas[ind][2]=Math.max(rPiezas[ind][0]-rPiezas[ind][1],0);
        if(this.maxFaltantes<rPiezas[ind][2])
            this.maxFaltantes=rPiezas[ind][2];
        return rPiezas[ind][2];
    }
    public int maxFaltantes(){
        return maxFaltantes;
    }
    public double getpPromedio(int ind){
        return prioridadProm[ind];
    }
    public double setpPromedio(int ind,double prioridad){
        //Funcion para asignar valor a pPromedio
        //Si no faltan productos para cumplir lo necesitado, entonces prioridad=0
        if(rPiezas[ind][2]>0){
            prioridadProm[ind]=prioridad;
        }else prioridadProm[ind]=0;
        return prioridadProm[ind];
    }
    public double getVolumen(int ind){
        return this.lPiezas[ind].getVolumen();
        //return this.lPiezas.get(id).getVolumen();
    }
    public double getPeso(int ind){
        return this.lPiezas[ind].getPeso();
        //return this.lPiezas.get(id).getPeso();
    }
    public Pieza getPieza(int ind){
        return this.lPiezas[ind];
        //return this.lPiezas.get(id);
    }
    public int pendientes(int ind){
        return rPiezas[ind][3];
    }
    public int faltantes(int ind){
        return rPiezas[ind][2];
    }
}
