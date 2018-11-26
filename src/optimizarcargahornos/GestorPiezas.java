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
    static int cantidadPiezas;
    
    public GestorPiezas(){
        maxFaltantes=0;
    }
    public void add(Pieza piezaAgregar, int ind){
        this.lPiezas[ind]=piezaAgregar;
    }
    public int size(){
        return lPiezas.length;
    }
    public void completarIni(int cant){
        this.maxFaltantes=0;
        this.lPiezas=new Pieza[cant];
        this.rPiezas=new int[cant][4];//PEDIDOS, EN ALMACEN, FALTANTES, PENDIENTES POR HORNEAR
        this.prioridadProm=new double[cant];
        cantidadPiezas=cant;
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
    public boolean cabeEnCompartimento(int ind,double maximoDC,double minimoDC,double medioDC){
        return this.lPiezas[ind].cabeEnCompartimento(maximoDC, minimoDC, medioDC);
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
        return this.lPiezas[ind].volumen();
    }
    public double getPeso(int ind){
        return this.lPiezas[ind].peso();
    }
    public Pieza getPieza(int ind){
        return this.lPiezas[ind];
    }
    public int pendientes(int ind){
        return rPiezas[ind][3];
    }
    public int faltantes(int ind){
        return rPiezas[ind][2];
    }
    public int stock(int ind){
        return rPiezas[ind][1];
    }
}
