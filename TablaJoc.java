import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa ce va retine tabla curenta de joc. Implementeaza clasa Cloneable pentru a realiza copii ale unei table.
 * @author Razvan
 *
 */
public class TablaJoc implements Cloneable {

	private int[] tabla;
	public int[] casa;
	
	int[] mutari;
	int index;
	
	int zar1, zar2;
	
	final static int NR = 26;
	
	/**
	 * Constructor tabla in care este initializata aceasta
	 */
	public TablaJoc() {
		setTabla(new int[26]);
		for(int i = 0; i < NR; i++) {
			tabla[i] = 0;
		}
		casa = new int[2];
		casa[0] = casa[1] = 0;
		
		mutari = new int[20];
		for(int i = 0; i < 20; i++)
			mutari[i] = -1;
		index = 0;
		
		//initializare tabla pentru jucatorul 0(alb)
		tabla[6]  = -5;
		tabla[8]  = -3;
		tabla[13] = -5;
		tabla[24] = -2;
		
		//initializare tabla pentru jucatorul 1(negru)
		tabla[1]  = 2;
		tabla[12] = 5;
		tabla[17] = 3;
		tabla[19] = 5;
	}
	
	/**
	 * Metoda ce suprascrie metoda clone() din clasa Cloneable si care realizeaza o copie a unei anumite table
	 */
	protected Object clone() {
        TablaJoc b = null;
        try {
            b = (TablaJoc) super.clone();
        } catch (CloneNotSupportedException e) {
            System.err.println("Exceptie prinsa");
        }
        b.tabla = (int[])tabla.clone();
        b.casa = (int[])casa.clone();
        b.mutari = (int[])mutari.clone();
        b.index = index;
        return b;
    }
	
	/**
	 * Metoda ce creaza o lista de table in care actualizate cu noile mutari dictate de zarurile aruncate. Mai intai se obtine o lista de table
	 * actualizate cu rpimul zar, dupa care s eface actualizarea si pentru cel de-al doilea
	 * @param jucator reprezinta jucatorul care realizeaza mutarile
	 * @return intoarce o lista cu tablele actualizate cu ambele zaruri
	 */
	public List<TablaJoc> mutariPosibileFinal(int jucator) {
		List<TablaJoc> lista, lista2,lista_finala = new ArrayList<TablaJoc>();
		lista = mutariPosibile(jucator, this, zar1);
		if(zar1 != zar2) {
			if(lista.isEmpty()) {
				lista = mutariPosibile(jucator, this, zar2);
				//System.out.println("eraaaaaaa goala");
				if(lista.isEmpty())
					return lista;
				else {
					for(int i = 0; i < lista.size(); i++) {
						lista2 = mutariPosibile(jucator, lista.get(i), zar1);
						lista_finala.addAll(lista2);
						if(lista_finala.isEmpty()) 
							return lista;
					}
					lista = new ArrayList<TablaJoc>();
					lista.addAll(lista_finala);
					return lista;
				}
			}
			for(int i = 0; i < lista.size(); i++) {
				lista2 = mutariPosibile(jucator, lista.get(i), zar2);
				if(lista2.isEmpty()) return lista;
				lista_finala.addAll(lista2);
			}
			lista = new ArrayList<TablaJoc>();
			lista.addAll(lista_finala);
		} else {
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < lista.size(); j++) {
					lista2 = mutariPosibile(jucator, lista.get(j), zar1);
					lista_finala.addAll(lista2);
					}
				if(!lista_finala.isEmpty()) {
					lista = new ArrayList<TablaJoc>();
					lista.addAll(lista_finala);
					lista_finala = new ArrayList<TablaJoc>();
				} else break;
				}
			}
		return lista;
	}
	
	/**
	 * Metoda ce pe baza listei intorse de metoda "mutariUnZar" creaza o lista de  table cu mutarile actualizate 
	 * @param jucator reprezinta jucatorul care realizeaza mutarea
	 * @param tabla_curenta reprezinta tabla curenta
	 * @param zar reprezinta zarul 
	 * @return intoarce lista de table actualizate
	 */
	public List<TablaJoc> mutariPosibile(int jucator, TablaJoc tabla_curenta, int zar) {
		List<TablaJoc> lista_table = new ArrayList<TablaJoc>(); 
		List<Integer> lista = mutariUnZar(jucator,tabla_curenta, zar);
		for(int i = 0; i < lista.size(); i++) {
			TablaJoc curent = null;
			curent = (TablaJoc)tabla_curenta.clone();
			if(lista.get(i) == 25 || lista.get(i) == 0) {
				curent.mutari[curent.index] = 30;
				curent.index++;
				curent.mutari[curent.index] = zar;
				curent.index++;
				
				curent.tabla[lista.get(i)] -= 1;
			} else {
				curent.mutari[curent.index] = lista.get(i);
				curent.index++;
				curent.mutari[curent.index] = zar;
				curent.index++;
				
				curent.tabla[lista.get(i)] -= jucator;
			}
			
			if(lista.get(i) + zar * jucator > 24 || lista.get(i) + zar * jucator < 1)
				curent.casa[jucator == -1 ? 0 : 1] += 1;
			else {
				if(curent.tabla[lista.get(i) + zar * jucator] == -jucator ) {
					curent.tabla[lista.get(i) + zar * jucator] += jucator;
					curent.tabla[jucator == -1 ? 0 : 25] += 1;
				}
				curent.tabla[lista.get(i) + zar * jucator] += jucator;
			}
			
			lista_table.add(curent);
		}
		return lista_table;
	}
	
	/**
	 * Metoda ce obtine mutarile posibile pentru un anumit zar intr-un anumit context
	 * @param jucator reprezinta jucatorul pentru care trebuie realziata mutarea
	 * @param tabla_curenta reprezinta tabla ape care se muta
	 * @param zar reprezinta zarul
	 * @return intoarce o lista cu toata pozitiile de pe care se pot face mutari cu acest zar
	 */
	public List<Integer> mutariUnZar(int jucator,TablaJoc tabla_curenta, int zar) {
		List<Integer> lista = new ArrayList<Integer>();
		int casa;
 		if(jucator == -1)
 			casa = 25;
 		else casa = 0;
		if(tabla_curenta.tabla[casa] != 0) {
			if(mutareValida(casa, zar,tabla_curenta, jucator)) {
				lista.add(casa);
			}
		} else if(poateScoatePiese(jucator,tabla_curenta)) {
			int d;
			if(jucator == -1)
				d = 6;
			else d = 19;
			int nr = 0;
 			for(int i = d; i != (25 - casa - zar * jucator) ; i+=jucator) {
 				if(25 - i >= zar && tabla_curenta.tabla[i] * jucator > 0) {
 					nr++;
 				}
 			}
 			if(nr != 0) {
 				for(int i = d; i != (25 - casa -zar*jucator); i+=jucator) {
 					if(tabla_curenta.tabla[i] * jucator > 0) {
 						if(mutareValida(i, zar, tabla_curenta, jucator)) {
 							lista.add(i);
 						}
 					}
 				}
 				if(tabla_curenta.tabla[25 - casa - zar*jucator]*jucator > 0)
 					lista.add(25 - casa - zar*jucator);
 			}
 			else
 				for(int i = 25 - casa - zar*jucator; i != 25 - casa; i+=jucator) {
 					if(tabla_curenta.tabla[i] * jucator > 0) {
 						if(mutareValida(i, zar,tabla_curenta, jucator)) {
 							lista.add(i);
 							break;
 						}
 					}
 				}
 		} else {
			for(int i = 1; i < 25; i++) {
				if(tabla_curenta.tabla[i] * jucator > 0) {
					if(mutareValida(i, zar,tabla_curenta, jucator)) {
						lista.add(i);
					}
				}
			}
		}
 		return lista;
	}
	
	/**
	 * Metoda ce verifica da ca o anumita mutare este valida
	 * @param from reprezinta pozitia de pe care se doreste a fi mutata o piesa
	 * @param span reprezinta numarul de pozitii cu care se doreste a fi mutata piesa
	 * @param tabla_curenta reprezinta tabla pe care se doreste a se realiza mutarea
	 * @param player reprezinta jucatorul care efectueaza mutarea
	 * @return intoarce true sau false
	 */
	public boolean mutareValida(int from, int span,TablaJoc tabla_curenta, int player) {
		boolean valida = true;
		int to = from + span * player;
		if(to < 1 || to > 24) {
			valida = valida && poateScoatePiese(player,tabla_curenta);
		} else {
			if(tabla_curenta.tabla[to] * (-player) >=2) {
				valida =false;
			}
		}
		return valida;
	}
	
	/**
	 * Metoda ce verifiva da un anumit jucator poate scoate piese
	 * @param player reprezinta jucatorul
	 * @param tabla_curenta reprezinta tabla curenta
	 * @return intoarce o valoare booleana
	 */
	public boolean poateScoatePiese(int player, TablaJoc tabla_curenta) {
		boolean poate = true;
		if(player == -1) {
			for(int i = 7; i < 25; i++) {
				poate = poate && (tabla_curenta.tabla[i] >= 0);
			}
		} else {
			for(int i = 1; i < 19; i++) {
				poate = poate && (tabla_curenta.tabla[i] <= 0);
			}
		}
		return poate;
	}
	 /**
	  * Metoda ce intoarce scorul unei anumite table
	  * @param jucator reprezinta jucatorul din perspectiva caruia se face evaluarea
	  * @return intoarce scorul tablei
	  */
	public float get_score(int jucator) {
		Evaluare eval = new Evaluare();
		float scor = eval.scor_tabla(this, jucator);
		return scor;
	}

	public int[] getTabla() {
		return tabla;
	}
	public void setTabla(int[] tabla) {
		this.tabla = tabla;
	}
	
	public int[] getCasa() {
		return casa;
	}
	public void setCasa(int[] casa) {
		this.casa = casa;
	}
}
