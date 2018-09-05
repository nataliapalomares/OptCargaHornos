package optimizarcargahornos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Algoritmo {

    public Algoritmo() {

    }

    public void cargarDatos(List<Set> lSets,List<Producto> lProductos,List<Pieza> lPiezas) {
        System.out.print("Archivo de Sets: ");
        //System.out.println("C:\\Users\\Natalia\\SkyDrive\\Documentos\\2018-2\\setsProdPiezas.csv");
        //PROVISIONAL NOMBRE DEL ARCHIVO
        String csvFile = "C:\\Users\\Natalia\\SkyDrive\\Documentos\\2018-2\\setsProdPiezas.csv";
       
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int cant=0,tipo=0;
            while ((line = br.readLine()) != null) {
                String[] linea = line.split(",");
                if(cant==0){
                    cant=Integer.parseInt(linea[0]);
                    tipo++;
                     System.out.println("CAMBIO" + tipo);
                    continue;
                }
                else if(tipo==1){//SETS
                    Set setActual=new Set(Integer.parseInt(linea[0]),linea[1],linea[2],Integer.parseInt(linea[3]),linea[4]);
                    lSets.add(setActual);
                    System.out.println("Set [id=" + linea[0]);
                }
                else if(tipo==2){//PRODUCTO
                    Producto prodActual=new Producto(Integer.parseInt(linea[0]),linea[1],Integer.parseInt(linea[2]),linea[3]);
                    lProductos.add(prodActual);
                    System.out.println("Producto [id="+linea[0]);
                }
                else{//PIEZA
                    String descripcion=linea[1]+" "+linea[2]+" "+linea[3];
                    int alto=Integer.parseInt(linea[4]);
                    int ancho=Integer.parseInt(linea[5]);
                    int largo=Integer.parseInt(linea[6]);
                    Double peso=Double.parseDouble(linea[7]);
                    //Double volumen=Double.parseDouble(linea[8]);
                    Pieza pActual=new Pieza(Integer.parseInt(linea[0]),descripcion,alto,ancho,largo,peso,0);
                    lPiezas.add(pActual);
                    System.out.println("Pieza [id="+linea[0]);
                }
                cant--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ejecutar() {
        List<Set> lSets=new ArrayList();
        List<Producto> lProductos=new ArrayList();
        List<Pieza> lPiezas=new ArrayList();
        cargarDatos(lSets,lProductos,lPiezas);
        System.out.println("Termino de cargar");
    }
}
