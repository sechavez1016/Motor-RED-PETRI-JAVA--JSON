/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package red_petrimodelamiento;

import java.util.Scanner;
import java.io.FileReader;
import org.json.simple.*;
import org.json.simple.parser.*;
/**
 *
 * @author 57321
 */
public class Red_PetriModelamiento {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int t, p, n, op = 0, opcion = 0;
        int[] u;
        int[][] demenos;
        int[][] demas;
        Scanner leer = new Scanner(System.in);
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("red.json"));
            JSONObject jsonObj = (JSONObject) obj;
            JSONArray m_i = (JSONArray) jsonObj.get("m_i");
            JSONArray lugares = (JSONArray) jsonObj.get("lugares");
            JSONArray transiciones = (JSONArray) jsonObj.get("transiciones");

            System.out.println("*****RED DE PETRI*****");
            System.out.println("RED DE PETRI -> (P,T,D-,D+)");
            System.out.println(" (P) -> Lugares de la red de petri ");
            System.out.println(" (T) -> Transiciones");
            System.out.println(" (D-) -> Matriz que guarda los lugares de entrada de las transiciones");
            System.out.println(" (D+) -> Matriz que guarda los lugares de de salida de las transiciones");
            System.out.println("*****CARGAR MATRICES*****");
            System.out.println("*****Cargar Matriz D-");
            demenos = dmenos(transiciones, lugares);
            System.out.println("*****Cargar Matriz D+*****");
            demas = dmas(transiciones, lugares);
            System.out.println("*****NUMERO DE TOKENS*****");
            System.out.println("Dijite el numeor de tokens: ");
            u = marcados(m_i);

            do {
                System.out.println("*****MENU*****");
                System.out.println(" 1 Iprimir Matriz D-");
                System.out.println(" 2 Imprimir Matriz D+");
                System.out.println(" 3 Transicion Habilitada");
                System.out.println(" 4 Imprimir vector μ");
                System.out.println(" 5 Disparar transicion habilitada");
                System.out.println(" 6 Salir");
                t = transiciones.size();
                p = lugares.size();
                opcion = leer.nextInt();
                switch (opcion) {
                    case 1: {
                        System.out.println("*****MATRIZ D-*****");
                        imprimirMatriz(demenos, t, p);
                        break;
                    }
                    case 2: {
                        System.out.println("*****MATRIZ D+*****");
                        imprimirMatriz(demas, t, p);
                        break;
                    }
                    case 3: {
                        System.out.println("*****TRANSICION HABILITADA*****");
                        if (Habilitada(demenos, p, t, u) != 0) {
                            System.out.println("La transicion habilitada: t" + Habilitada(demenos, p, t, u));
                        } else {
                            System.out.println("NO HAY TRANSICION HABILITADA");
                        }

                        break;
                    }
                    case 4: {
                        System.out.println("*****IMPRIMIR VECTOR μ*****");
                        System.out.print("μ = (");
                        for (int i = 0; i < p; i++) {
                            if (i != p - 1) {
                                System.out.print(u[i] + ", ");
                            } else {
                                System.out.print(u[i] + ")");
                            }
                        }
                        System.out.println(" ");
                    }
                    break;

                    case 5: {
                        System.out.println("*****DISPARAR TRANSICION HABILITADA*****");
                        if (Habilitada(demenos, p, t, u) != 0) {
                            Disparo(demenos, demas, p, t, u);
                        } else {
                            System.out.println("NO HAY TRANSICION HABILITADA");
                        }

                        break;
                    }

                }
            } while (opcion != 6);
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    static public boolean Token(int[] u, int nt) {
        if (u[nt] != 0) {
            return true;
        } else {
            return false;
        }

    }

    static public int lugaresentrada(int[][] demenos, int p, int t) {
        //Imprime los los lugares que entran a la transicion
        int c = 0;
        for (int col = 0; col < p; col++) {
            if (demenos[t][col] !=0) {
                c++;
            }
        }
        return c;
    }

    static public int[] TransicionesLugares(int[][] demenos, int p, int t) {
        //Selecciona los lugares de las entradas a las transiciones, y guarda en un vector su hubicacion
        int[] r = new int[lugaresentrada(demenos, p, t)];
        int c = 0;
        for (int col = 0; col < p; col++) {
            if (demenos[t][col] != 0) {
                r[c] = col;
                c++;
            }
        }
        return r;
    }

    static public int Habilitada(int[][] demenos, int p, int t, int[] u) {
        for (int fil = 0; fil < t; fil++) {
            int r = 0;
            int c = lugaresentrada(demenos, p, fil);//Numero de lugares de entrada
            int[] aux = TransicionesLugares(demenos, p, fil);//Vector que guarda la ubicacion de los lugares de entrada
            for (int col = 0; col < c; col++) {
                if (u[aux[col]] != 0) {
                    r++;
                }
            }
            //System.out.println("Transicion :" +fil+" C: "+c+ " R: "+r);
            if (r == c) {
                int n = 1 + fil;
                return n;
            }

        }
        return 0;
    }
    static public int nToken (int [][] demas, int p, int t){
        //Miramos el numero de transiciones de saldia que entran al lugar
        int r=0;
        r = demas[t][p];
        return r;
    }

    static public void Disparo(int[][] demenos, int[][] demas, int p, int t, int[] u) {
        int ta = Habilitada(demenos, p, t, u);//Numero de la transicion
        ta = ta - 1;//Generamos la ubicacion apta para el manejar como vector
        int[] aux = TransicionesLugares(demas, p, ta);//Guarda lugares de salida de la transicion
        for (int i = 0; i < aux.length; i++) {
            if(nToken(demas,aux[i],ta)>1){
                u[aux[i]]=u[aux[i]]+nToken(demas,aux[i],ta);
            }else{
            u[aux[i]] = u[aux[i]] + 1;//Le sumamos el token
            }
        }
        int[] aux1 = TransicionesLugares(demenos, p, ta);//Guarda lugares de entrada de la transicion
        for (int i = 0; i < aux1.length; i++) {
            u[aux1[i]] = u[aux1[i]] - 1;//Descontamos el token disparado de los lugares de entrada de la transicion
        }
        System.out.print("μ = (");
        for (int i = 0; i < p; i++) {
            if (i != p - 1) {
                System.out.print(u[i] + ", ");
            } else {
                System.out.print(u[i] + ")");
            }
        }
        System.out.println(" ");

    }

    static public int[][] dmenos(JSONArray transiciones, JSONArray lugares) {
        int[][] x = new int[transiciones.size()][lugares.size()];
        for (int fil = 0; fil < transiciones.size(); fil++) {
           for (int col = 0; col < lugares.size(); col++) {
               x[fil][col] = 0;
           }
        }
        for(int i = 0; i < lugares.size(); i++){
                    JSONArray l = (JSONArray) lugares.get(i);
                    x[Math.toIntExact((long) l.get(0))][Math.toIntExact((long) l.get(1))] = 1 ;
        }
        
        imprimirMatriz(x, transiciones.size(), lugares.size());
        return x;
    }

    static public int[][] dmas(JSONArray transiciones, JSONArray lugares) {
        int[][] x = new int[transiciones.size()][lugares.size()];
        for (int fil = 0; fil < transiciones.size(); fil++) {
           for (int col = 0; col < lugares.size(); col++) {
               x[fil][col] = 0;
           }
        }
        for(int i = 0; i < transiciones.size(); i++){
                    JSONArray l = (JSONArray) transiciones.get(i);
                    x[Math.toIntExact((long) l.get(1))][Math.toIntExact((long) l.get(0))] = 1 ;
        }
        
        imprimirMatriz(x, transiciones.size(), lugares.size());
        return x;
    }

    static public void imprimirMatriz(int[][] m, int t, int p) {
        int c, c1, fil, col;
        for (fil = 0; fil < t; fil++) {
            for (col = 0; col < p; col++) {
                System.out.print(m[fil][col] + " ");
            }
            System.out.println(" ");
        }
    }

    static public int[] marcados(JSONArray m_i) {
        int[] x = new int[m_i.size()];
        System.out.println("Dijitaremos el numero de tokens de cada lugar");
        for (int i = 0; i < m_i.size(); i++) {
            x[i] = Math.toIntExact((long) m_i.get(i));
        }
        return x;
    }

    static public int[][] GenerarMatrizD(int[][] demenos, int[][] demas, int t, int p) {
        int[][] d = new int[t][p];

        // d = -Dmenos + Dmas
        for (int fil = 0; fil < t; fil++) {
            for (int col = 0; col < p; col++) {
                d[fil][col] = demas[fil][col] - demenos[fil][col];
            }
        }
        imprimirMatriz(d, t, p);
        return d;

    }

    static public Lista GeneraGrafo(int[][] demenos, int t, int p) {
        Lista nueva = new Lista();
        for (int fil = 0; fil < t; fil++) {
            for (int col = 0; col < p; col++) {
                if (fil == 0) {
                    nueva.añadir(fil, col, p);
                }
            }
        }
        return nueva;
    }

}

class Lista {

    nodo cab;
    nodo fil;

    Lista() {
        this.cab = null;
        this.fil = null;
    }

    void añadir(int t, int p, int u) {
        nodo nuevo = new nodo(t, p, u);
        nuevo.sig = cab;
        cab = nuevo;
    }

    void imprimirLista() {
        nodo aux = this.cab;
        while (aux != null) {
            System.out.print("|" + aux.t + "|" + aux.p + "|" + aux.u + "|");
            if (aux.sig != null) {
                System.out.print("-");
            }
            aux = aux.sig;
        }
        System.out.println(" ");
    }
}

class nodo {

    int t; //Transicion 
    int p; //Lugar
    int u;//Numero de token
    nodo sig;
    nodo atr;

    nodo(int t, int p, int u) {
        this.t = t;
        this.p = p;
        this.u = u;
    }
}
