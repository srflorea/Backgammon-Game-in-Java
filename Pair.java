/**
 * Clasa ce reprezinta o pereche formata dintr-o tabla si scorul acesteia obtinut conform metodelor din clasa Evaluare
 * @author Razvan
 *
 */
public class Pair {

	private float score;
	private TablaJoc tabla;
	
	public Pair() {}
	
	public Pair(float score,TablaJoc tabla) {
		this.score = score;
		this.tabla = tabla;
	}
	
	public float first() {
		return score;
	}
	
	public TablaJoc second() {
		return tabla;
	}
}
