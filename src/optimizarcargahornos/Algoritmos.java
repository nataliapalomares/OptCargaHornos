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
    Horno oven;
    List<Set> lSets; //conjunto de sets
    List<Producto> lProductos; //conjunto de productos
    List<Pieza> lPiezas; //conjunto de piezas
    List<Pedido> lPedidos;
    //ESTRUCTURAS AUXILIARES
    boolean[][] mDimension; //indica las piezas que caben en cada compartimento 
    int[][] rSets;//resumen de sets (pedidos,almacen,faltante)
    int[][] rProd;//resumen de productos (pedidos,almacen,faltante)
    int[][] rPiezas;//resumen de piezas (pedidos,almacen,faltante)
    
    public Algoritmos() {
        lSets = new ArrayList();
        lProductos = new ArrayList();
        lPiezas = new ArrayList();
        lPedidos=new ArrayList();
    }

    public void cargarDatos() {
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
            line = br.readLine();
            String[] linea = line.split(",");
            double volMaximo = Double.parseDouble(linea[0]);
            double pMaximo = Double.parseDouble(linea[1]);
            int nVagonetas = Integer.parseInt(linea[2]);
            int cantC = Integer.parseInt(linea[3]); //cantidad de compartimentos
            Horno oven = new Horno(volMaximo, pMaximo, nVagonetas, cantC);
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
    public void cargarPedidos(){
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
    public void crearEstructuraAuxiliares(){
        //MATRIZ DE DIMENSIONES
        int cantPiezas=this.lPiezas.size();
        mDimension=new boolean[cantPiezas][Vagoneta.nCompartimentos];
        for(int j=0;j<Vagoneta.nCompartimentos;j++){
            int maximoDC=Vagoneta.lCompartimentos[j].maximo();
            int minimoDC=Vagoneta.lCompartimentos[j].minimo();
            int medioDC=Vagoneta.lCompartimentos[j].medio();
            for(int i=0;i<cantPiezas;i++){
                if(this.lPiezas.get(i).cabeEnCompartimento(maximoDC,minimoDC,medioDC))
                    mDimension[i][j]=true;
                else mDimension[i][j]=false;
            }
        }
        //RESUMEN DE SETS
        int cantSets=this.lSets.size();
         
    }
    public void cargarDatosAlmacen(){
        this.rSets=new int[this.lSets.size()][3];
        this.rProd=new int[this.lProductos.size()][3];
        this.rPiezas=new int[this.lPiezas.size()][3];
        System.out.print("Archivo de Almacen: ");
        String csvFile = "C:\\Users\\Natalia\\SkyDrive\\Documentos\\2018-2\\almacenes.csv";
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int cant=0;
            String tipo="";
            while ((line = br.readLine()) != null) {
                String[] linea = line.split(",");
                if(cant==0){
                    tipo=linea[0];
                    cant=Integer.parseInt(linea[1]);
                    continue;
                }
                int id=Integer.parseInt(linea[0]);
                int cantidad=Integer.parseInt(linea[1]);
                if(tipo=="S"){//PENDIENTE: verificar como comparar strings
                    //SETS
                    rSets[id][1]=cantidad; //cantidad en almacen;
                }
                else if(tipo=="P"){
                    //PRODUCTOS
                    rProd[id][1]=cantidad;
                }
                else{
                    rPiezas[id][1]=cantidad;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void ejecutar() {
        //CARGA DE DATOS: horno, productos, pedidos
        this.oven = datosHorno();
        cargarDatos();
        cargarDatosAlmacen();
        cargarPedidos();
        //CREACION DE ESTRUCTURAS AUXILIARES
        crearEstructuraAuxiliares();
        //ALGORITMO MEMETICO
        
        //System.out.println("Termino de cargar");
    }
}
