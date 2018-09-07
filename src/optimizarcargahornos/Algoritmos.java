package optimizarcargahornos;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Natalia Palomares Melgarejo
 */
public class Algoritmos {
    Horno oven;
    GestorSets gSets;
    GestorProducto gProd; //conjunto de productos
    GestorPiezas gPiezas; //conjunto de piezas
    List<Pedido> lPedidos;
    //ESTRUCTURAS AUXILIARES
    boolean[][] mDimension; //indica las piezas que caben en cada compartimento
    
    public Algoritmos() {
        gSets=new GestorSets();
        gProd=new GestorProducto();
        gPiezas = new GestorPiezas();
        lPedidos=new ArrayList();
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
            for(int i=0;i<Vagoneta.nCompartimentos;i++){
                wagon.getPesoLimite(i);
                wagon.getVolLimite(i);
            }
            return oven;
        } catch (IOException e) {
        }
        return null;
    }
    public void cargarDatos() {
        System.out.print("Archivo de Sets: ");
        String csvFile = "C:\\Users\\Natalia\\SkyDrive\\Documentos\\2018-2\\setsProdPiezas.csv";

        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int cant = 0, tipo = 0,i=0;
            while ((line = br.readLine()) != null) {
                String[] linea = line.split(",");
                if (cant == 0) {
                    cant = Integer.parseInt(linea[0]);
                    i=0;
                    tipo++;
                    switch(tipo){
                        case 1: this.gSets.completarIni(cant);
                                break;
                        case 2: this.gProd.completarIni(cant);
                                break;
                        case 3: this.gPiezas.completarIni(cant);
                                break;
                    }
                    continue;
                } else if (tipo == 1) {//SETS
                    Set setActual = new Set(Integer.parseInt(linea[0]), linea[1], linea[2], Integer.parseInt(linea[3]), linea[4]);
                    gSets.addRSet(i++,'A',Integer.parseInt(linea[5]));
                    gSets.add(setActual);
                } else if (tipo == 2) {//PRODUCTO
                    Producto prodActual = new Producto(Integer.parseInt(linea[0]), linea[1], Integer.parseInt(linea[2]), linea[3]);
                    gProd.addRProd(i++,'A',Integer.parseInt(linea[4]));
                    gProd.add(prodActual);
                } else {//PIEZA
                    String descripcion = linea[1] + " " + linea[2] + " " + linea[3];
                    int alto = Integer.parseInt(linea[4]);
                    int ancho = Integer.parseInt(linea[5]);
                    int largo = Integer.parseInt(linea[6]);
                    Double peso = Double.parseDouble(linea[7]);
                    Pieza pActual = new Pieza(Integer.parseInt(linea[0]), descripcion, alto, ancho, largo, peso);
                    gPiezas.addRPiezas(i,'A',Integer.parseInt(linea[8]));//cantidad de piezas terminadas en el almacen
                    gPiezas.addRPiezas(i++, 'Q',Integer.parseInt(linea[9]));//cantidad pendiente a hornear 
                    gPiezas.add(pActual);
                }
                cant--;
            }
        } catch (IOException e) {
        }
    }

    public void cargarPedidos(){
        //int idP, int idS, int cant, Date entrega,int priorCliente)
        System.out.print("Archivo de Pedidos: ");
        String csvFile = "C:\\Users\\Natalia\\SkyDrive\\Documentos\\2018-2\\pedidos.csv";
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            while ((line = br.readLine()) != null) {
                String[] linea = line.split(",");
                int idP=Integer.parseInt(linea[0]);
                int idS=Integer.parseInt(linea[1]);
                int cant=Integer.parseInt(linea[2]);
                LocalDate entrega=LocalDate.parse(linea[3],formatter);
                int priorCliente=Integer.parseInt(linea[4]);
                Pedido pActual=new Pedido(idP,idS,cant,entrega,priorCliente);
                gSets.addRSet(idS-1, 'P', cant);
                gSets.addRSet(idS-1, 'R', pActual.calcularPrioridad());
                lPedidos.add(pActual);
            }
        } catch (IOException e) {
        }
    }
    public void crearEstructuraAuxiliares(){
        //MATRIZ DE DIMENSIONES
        int cantPiezas=this.gPiezas.size();
        mDimension=new boolean[cantPiezas][Vagoneta.nCompartimentos];
        for(int j=0;j<Vagoneta.nCompartimentos;j++){
            int maximoDC=Vagoneta.lCompartimentos[j].maximo();
            int minimoDC=Vagoneta.lCompartimentos[j].minimo();
            int medioDC=Vagoneta.lCompartimentos[j].medio();
            for(int i=0;i<cantPiezas;i++){
                if(this.gPiezas.cabeEnCompartimento(i,maximoDC,minimoDC,medioDC))
                    mDimension[i][j]=true;
                else mDimension[i][j]=false;
            }
        }
        //ESTRUCTURAS RESUMEN
        for(int i=0;i<this.gSets.size();i++){
            //Completando RESUMEN SETS
            int cantFS=this.gSets.calcularFaltante(i);
            double pPromS=this.gSets.pPromedio(i);
            int[] listaProd=this.gSets.productos(i);
            for(int j=0;j<listaProd.length;j++){
                //Completando RESUMEN PRODUCTOS
                //La cantidad pedida del producto es la cantidad faltante de sets
                this.gProd.addRProd(listaProd[j]-1, 'P', cantFS);
                //Se calcula la cantidad faltante de productos
                int cantFP=this.gProd.calcularFaltante(listaProd[j]-1);
                double pPromP=this.gProd.pPromedio(listaProd[j]-1,pPromS);
                int[] listaPieza=this.gProd.piezas(listaProd[j]-1);
                for(int k=0;k<listaPieza.length;k++){
                    //Completando RESUMEN PIEZAS
                    this.gPiezas.addRPiezas(listaPieza[k]-1, 'P', cantFP);
                    this.gPiezas.calcularFaltante(listaPieza[k]-1);
                    this.gPiezas.setpPromedio(listaPieza[k]-1,pPromP);
                }
            }
        }
         
    }
    
    public void ejecutar() {
        //CARGA DE DATOS: horno, productos, pedidos
        this.oven = datosHorno();
        cargarDatos();
        cargarPedidos();
        //ESTRUCTURAS AUXILIARES: se crea y completa la matriz de dimensiones y resumen
        crearEstructuraAuxiliares();
        //ALGORITMO MEMETICO
        
        //System.out.println("Termino de cargar");
    }
}
