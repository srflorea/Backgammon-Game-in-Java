import java.util.*;

/**
 * Clasa inc are este implementat algoritmul expectiminimax.
 * @author Razvan
 *
 */
public class ExpectiMiniMax {

	public static final int INF = 1000;
	
	int jucator;
	
	public ExpectiMiniMax(int jucator) {
		this.jucator = jucator;
	}
	
	/**
	 * MEtoda ce implementeaza algoritmul expectiminimax. Aceasta va trata 3 cazuri: cazul 1: eu sunt sa mut, atunci voi incerca sa imi maximizez
	 * scorul, cazul 2: adversarul este sa mute, atunci incercams a minimizam scorul acestuia si cazul 3: inc are generam toata posibilitatile 
	 * pentru doua zaruri.
	 * @param tabla_joc reprezinta tabla de la care se pleaca pentru a determina mutarea optima
	 * @param jucator reprezinta jucatorul care va muta
	 * @param adancime reprezinta adancimea pana la care voi merge in recursivitate in arbore
	 * @return
	 */
	public Pair expectiminimax(TablaJoc tabla_joc,int jucator, int adancime) {
		float a;
		if(adancime == 0) {
			return new Pair(tabla_joc.get_score(jucator), tabla_joc);
		}
		
		Pair next;
		TablaJoc nextMove = null;
		
		if(adancime % 3 == 1 ) {
			a = INF;
			List<TablaJoc> posibilitati = tabla_joc.mutariPosibileFinal(jucator == 0 ? -1 : 1);
			for(int i = 0; i < posibilitati.size(); i++) {
				next = expectiminimax(posibilitati.get(i), jucator, adancime - 1);
				if(a > next.first()) {
					a = next.first();
					nextMove = posibilitati.get(i);
				}
			}
		}
		else if(adancime % 3 == 0) {
			a = -INF;
			List<TablaJoc> posibilitati = tabla_joc.mutariPosibileFinal(jucator == 0 ? -1 : 1);
			for(int i = 0; i < posibilitati.size(); i++) {
				next = expectiminimax(posibilitati.get(i), jucator, adancime - 1);
				if(a < next.first()) {
					a = next.first();
					nextMove = posibilitati.get(i);
				}
			}
		}
		else {
			a = 0;
			TablaJoc tabla_noua;
			for(int i = 1; i <= 6; i++) {
				for(int j = i; j <= 6; j++) {
					tabla_noua = (TablaJoc)(tabla_joc.clone());
					tabla_noua.zar1 = j;
					tabla_noua.zar2 = i;
					next = expectiminimax(tabla_noua, 1 - jucator, adancime - 1);
					float b = (float)1/21;
					a = a + b * next.first();
				}
			}
		}
		
		return new Pair(a, nextMove);
	}
}
