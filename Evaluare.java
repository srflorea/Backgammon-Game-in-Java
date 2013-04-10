/**
 * Aceasta clasa contine metode ce vor evalua o anumita tabla dupa anumite reguli.
 * @author Razvan
 *
 */
public class Evaluare {

	/**
	 * Metoda ce va calcula scorul unei table, ajutandu-se de celelalte metode.
	 * @param tabla_joc reprezinta tabla pentru care trebuie efectuata evaluarea
	 * @param jucator reprezinta jucatorul pentru care din perspectiva caruia de realizeaza evaluarea
	 * @return
	 */
	public float scor_tabla(TablaJoc tabla_joc, int jucator) {
		
		int eval = evalueaza(tabla_joc, jucator);
		boolean blocheaza = false;
		if(tabla_joc.getTabla()[jucator == 0 ? 25 : 0] != 0) {
			eval += blocare(tabla_joc, jucator);
			blocheaza = true;
		}
		if(eval <= 2 * 24 *24 && !blocheaza)
			eval += poarta(tabla_joc, jucator);
		
		eval -= piese_singure(tabla_joc, jucator);
		return (float) eval;
	}
	
	/**
	 * Meotda ce evalueaza tabla adunand la valoarea scorului curent in cazul in care pullurile jucatorului sunt
	 * mai aproape de casa acestuia(valoare maxima in cazul in care acestea sunt in casa), si scazand din valoarea curenta 
	 * in cazul in care oponentul apropie pullurile de casa lui. De asemenea, valoarea va scade daca jucatorul va avea
	 * pulluri pe bara si va aduna la aceasta daca oponentul va avea pulluri pe bara.
	 * @param tabla_joc reprezinta tabla pentru care trebuie efectuata evaluarea
	 * @param jucator reprezinta jucatorul pentru care din perspectiva caruia de realizeaza evaluarea
	 * @return
	 */
	public int evalueaza(TablaJoc tabla_joc, int jucator) {
		
		int valoare = 0;
		for(int i = 1; i < 25; i++) {
			if(tabla_joc.getTabla()[i] > 0) 
				valoare += jucator * tabla_joc.getTabla()[i] * i - (1 - jucator) * tabla_joc.getTabla()[i] * i;
			 else 
				valoare -= jucator * tabla_joc.getTabla()[i] * (24 - i - 1) + (1 - jucator) * tabla_joc.getTabla()[i] * (24-i-1);
		}
		
		valoare += tabla_joc.getCasa()[jucator] * 24;
		valoare -= tabla_joc.getCasa()[1-jucator] * 24;
		valoare -= tabla_joc.getTabla()[jucator == 0 ? 0 : 25] * 24;
		valoare += tabla_joc.getTabla()[jucator == 0 ? 25 : 0] * 24;
		return valoare;
	}
	
	/**
	 * Metoda ce va fi apelata numai in cazul in care oponentul are piese pe bara. Va puncta tabla curenta
	 * daca aceasta il blocheaza pe oponent sa introduca piesa de pe bara in casa noastra.
	 * @param tabla_joc reprezinta tabla pentru care trebuie efectuata evaluarea
	 * @param jucator reprezinta jucatorul pentru care din perspectiva caruia de realizeaza evaluarea
	 * @return
	 */
	public int blocare(TablaJoc tabla_joc, int jucator) {
		
		int nr = 0;
		int index = (1 - jucator) * 24;
		while((tabla_joc.getTabla()[index] * jucator + jucator - 1) > (jucator + tabla_joc.getTabla()[index] * (1 - jucator)) && (nr < 7)) {
			nr++;
			index += 2 * jucator - 1;
		}
		return nr * 24;
	}
	
	/**
	 * Meotda ce creste valoarea unei table inc are jucatorul are "porti" in apropierea casei adversarului.
	 * @param tabla_joc reprezinta tabla pentru care trebuie efectuata evaluarea
	 * @param jucator reprezinta jucatorul pentru care din perspectiva caruia de realizeaza evaluarea
	 * @return
	 */
	public int poarta(TablaJoc tabla_joc, int jucator) {
		
		int piese = 0;
		int piese2 = 0;
		for(int i = (jucator + 14 * (1 - jucator)); i < (12 * jucator + 24 * (1 - jucator)); i++) {
			if((jucator * tabla_joc.getTabla()[i] - 1 + jucator) > (jucator + (1 - jucator) * tabla_joc.getTabla()[i]))
				piese ++;
			else {
				if(piese > piese2)
					piese2 = piese;
				piese = 0;
			}
		}
		if(piese > piese2)
			piese2 = piese;
		
		return piese2 * 24;
	}
	
	/**
	 * Metoda ce penalizeaza o tabla ce ofera posibilitatea oponentului de scoate o piese a jucatorului pe bara.
	 * @param tabla_joc reprezinta tabla pentru care trebuie efectuata evaluarea
	 * @param jucator reprezinta jucatorul pentru care din perspectiva caruia de realizeaza evaluarea
	 * @return
	 */
	public int piese_singure(TablaJoc tabla_joc, int jucator) {
		
		int nr_singure = 0;
		if(jucator == 1) { 
			int i = 24;
			while(i >= 1 && tabla_joc.getTabla()[i] >= 0)
				i--;
			for(int j = 1; j < i; j++) {
				if(tabla_joc.getTabla()[i] == 1)
					nr_singure++;
			}
		}
		else {
			int i = 1;
			while(i <= 24 && tabla_joc.getTabla()[i] <= 0)
				i++;
			for(int j = i + 1; j <=24; j++) {
				if(tabla_joc.getTabla()[i] == -1)
					nr_singure++;
			}
		}
		return nr_singure;
	}
}
