package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Compartimento {
    int id; //código del compartimento
    int ancho; //medida del ancho del compartimento en mm
    int largo; //medida del largo del compartimento en mm
    int alto; //medida del largo del compartimento en mm
    double porcentVolumen;//para asignar proporcionalmente al volumen que ocupa en la vagoneta
    
    public Compartimento(int id,int w, int l, int h){
        this.id=id;
        this.ancho=w;
        this.largo=l;
        this.alto=h;
    }
    public int maximo(){
        return Math.max(ancho,Math.max(largo,alto));
    }
    public int minimo(){
        return Math.min(ancho,Math.min(largo,ancho));
    }
    public int medio(){
        return this.ancho+this.largo+this.alto-maximo()-minimo();
    }
    public double getPorcentVolumen(){
        return this.porcentVolumen;
    }
    public void setPorcentVolumen(){
        this.porcentVolumen=(ancho*alto*largo)/(Vagoneta.volumenTotalComp*Math.pow(10,3));
    }
    public double getPesoLimite(){
        return this.porcentVolumen*Vagoneta.pesoMaximo;
    }
    public double getVolumenLimite(){
        return this.porcentVolumen*(Horno.volMaximo/Vagoneta.nCompartimentos);
    }
}
