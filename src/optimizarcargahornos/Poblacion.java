package optimizarcargahornos;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Poblacion {
    int tamanio;
    public Poblacion(){
        this.tamanio=0;
    }
    public int size(){
        return this.tamanio;
    }
    public int ruleta(double[] rangosRuleta,int izq,int der,double r){
        //Ruleta búsqueda binaria
        int mitad=(izq+der)/2;
        while (izq <= der) {
            if (rangosRuleta[mitad] < r)
                izq = mitad + 1;    
            else if (rangosRuleta[mitad] > r) {
                if(mitad==izq || rangosRuleta[mitad-1]<r){
                    return mitad;
                }
                else der= mitad-1;
            }
            else return mitad;
            mitad = (izq + der)/2;
        }
        return -1;  
    }
}
