package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Compartimento {
    int id; //código del compartimento
    double ancho; //medida del ancho del compartimento en m
    double largo; //medida del largo del compartimento en m
    double alto; //medida del largo del compartimento en m
    double porcentVolumen;//para asignar proporcionalmente al volumen que ocupa en la vagoneta
    
    public Compartimento(int id,double w, double l, double h){
        this.id=id;
        this.ancho=w;
        this.largo=l;
        this.alto=h;
    }
    public double maximo(){
        return Math.max(ancho,Math.max(largo,alto));
    }
    public double minimo(){
        return Math.min(ancho,Math.min(largo,alto));
    }
    public double medio(){
        return this.ancho+this.largo+this.alto-maximo()-minimo();
    }
    public double ancho(){
        return this.ancho;
    }
    public double largo(){
        return this.largo;
    }
    public double alto(){
        return this.alto;
    }
    public double getPorcentVolumen(){
        return this.porcentVolumen;
    }
    public void setPorcentVolumen(){
        this.porcentVolumen=(ancho*alto*largo)/(Vagoneta.volumenTotalComp);
    }
    public double getPesoLimite(){
        return this.porcentVolumen*Vagoneta.pesoMaximo;
    }
    public double getVolumenLimite(){
        return this.porcentVolumen*(Horno.volMaximo/Horno.nVagonetas);
    }
    public int getId(){
        return this.id;
    }
}
