package optimizarcargahornos;
import java.text.SimpleDateFormat;  
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Algoritmos {

    public Algoritmos() {
    }

    public void cargarDatos(List<Set> lSets, List<Producto> lProductos, List<Pieza> lPiezas) {
        System.out.print("Archivo de Sets: ");
        String csvFile = "C:\\Users\\Natalia\\SkyDrive\\Documentos\\2018-2\\setsProdPiezas.csv";

        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int cant = 0, tipo = 0;
            while ((line = br.readLine()) != null) {
                String[] linea = line.split(",");
                if (cant == 0) {
                    cant = Integer.parseInt(linea[0]);
                    tipo++;
                    continue;
                } else if (tipo == 1) {//SETS
                    Set setActual = new Set(Integer.parseInt(linea[0]), linea[1], linea[2], Integer.parseInt(linea[3]), linea[4]);
                    lSets.add(setActual);
                } else if (tipo == 2) {//PRODUCTO
                    Producto prodActual = new Producto(Integer.parseInt(linea[0]), linea[1], Integer.parseInt(linea[2]), linea[3]);
                    lProductos.add(prodActual);
                } else {//PIEZA
                    String descripcion = linea[1] + " " + linea[2] + " " + linea[3];
                    int alto = Integer.parseInt(linea[4]);
                    int ancho = Integer.parseInt(linea[5]);
                    int largo = Integer.parseInt(linea[6]);
                    Double peso = Double.parseDouble(linea[7]);
                    Pieza pActual = new Pieza(Integer.parseInt(linea[0]), descripcion, alto, ancho, largo, peso);
                    lPiezas.add(pActual);
                }
                cant--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Horno datosHorno() {
        System.out.print("Archivo de Horno: ");
        //PROVISIONAL NOMBRE DEL ARCHIVO
        String csvFile = "C:\\Users\\Natalia\\SkyDrive\\Documentos\\2018-2\\hornoCompartimentos.csv";
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            Horno oven;
            line = br.readLine();
            String[] linea = line.split(",");
            double volMaximo = Double.parseDouble(linea[0]);
            double pMaximo = Double.parseDouble(linea[1]);
            int nVagonetas = Integer.parseInt(linea[2]);
            int cantC = Integer.parseInt(linea[3]); //cantidad de compartimentos
            oven = new Horno(volMaximo, pMaximo, nVagonetas, cantC);
            Vagoneta wagon = new Vagoneta();
            while ((line = br.readLine()) != null) {
                //COMPARTIMENTOS
                linea = line.split(",");
                int id = Integer.parseInt(linea[0]);
                int alto = Integer.parseInt(linea[1]);
                int ancho = Integer.parseInt(linea[2]);
                int largo = Integer.parseInt(linea[3]);
                wagon.agregarCompartimento(id-1, id, ancho, largo, alto);
            }
            for(int i=0;i<nVagonetas;i++){
                Vagoneta wagonAux=new Vagoneta();
                oven.agregarVagoneta(i, wagonAux);
            }
            return oven;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void cargarPedidos(List<Pedido> lPedidos){
        //int idP, int idS, int cant, Date entrega,int priorCliente)
        System.out.print("Archivo de Pedidos: ");
        String csvFile = "C:\\Users\\Natalia\\SkyDrive\\Documentos\\2018-2\\pedidos.csv";
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            while ((line = br.readLine()) != null) {
                String[] linea = line.split(",");
                int idP=Integer.parseInt(linea[0]);
                int idS=Integer.parseInt(linea[1]);
                int cant=Integer.parseInt(linea[2]);
                Date entrega=formatter.parse(linea[3]);
                int priorCliente=Integer.parseInt(linea[4]);
                Pedido pActual=new Pedido(idP,idS,cant,entrega,priorCliente);
                lPedidos.add(pActual);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException ex) {
            Logger.getLogger(Algoritmos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void ejecutar() {
        //CARGA DE DATOS: horno, productos, pedidos
        Horno oven = datosHorno();
        List<Set> lSets = new ArrayList();
        List<Producto> lProductos = new ArrayList();
        List<Pieza> lPiezas = new ArrayList();
        cargarDatos(lSets, lProductos, lPiezas);
        List<Pedido> lPedidos=new ArrayList();
        cargarPedidos(lPedidos);
        //ALGORITMO MEMETICO
        
        //System.out.println("Termino de cargar");
    }
}
