import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Main {
	public static byte[] readMessage(DataInputStream in) {
		byte[] message = null;
		// There's no way in java to know if the socket closed on the other side
		// We can only read something and see if it throws a java.io.EOFException
		try {
			byte size = in.readByte();
			message = new byte[size];
			in.readFully(message);
		} catch (EOFException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return message;
	}
	
	public static void sendMessage(final byte[] message, DataOutputStream out) {
		byte size = (byte) message.length;
		try {
			out.writeByte(size);
			out.write(message);
		} catch (SocketException e) {
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Metoda in care se va primi de la server informatiile despre urmatoarea mutare, actualizeaza tabla cu mutarile adversarului si apeleaza metodele corespunzatore
	 * pentru obtinerea urmatoare mutare optima pentru mine
	 * @param args
	 */
	public static void main(String args[]) {
		if (args.length < 4) {
			System.out.println("Usage: ./client server_hostname server_port opponent_level(1=dumb, 5, 7, 8) own_level(1=dumb, 5, 7, 8)");
			return;
		}
		// variabile pentru conexiune
		Socket socket = null;
		DataOutputStream out = null;
        DataInputStream in = null;
        
        int var;

		try {
			// realizez conexiunea la server
			socket = new Socket(args[0], Integer.parseInt(args[1]));
			// scriu in out pe socket
			out = new DataOutputStream(socket.getOutputStream());
			// citesc din in de pe socket
            in = new DataInputStream(socket.getInputStream());
            
            // trimit primul mesaj - dificulatea adversarului
            byte[] message = new byte[1];
            message[0] = Byte.parseByte(args[2]);
            Main.sendMessage(message, out);
            
            // primesc raspuns cu culoarea mea
            message = Main.readMessage(in);
            if (message[0] == 1) {
            	System.out.println("sunt jucatorul negru");
            	var = 1;
            } else if (message[0] == 0) {
            	System.out.println("sunt jucatorul alb");
            	var = -1;
            } else {
            	System.out.println("mesaj invalid; eroare!");
            	var = 0;
            }
            
            TablaJoc tabla_joc = new TablaJoc();
            //tabla_joc.afisareTabla();
            Evaluare eval = new Evaluare();

          
            while (true) {
            	message = Main.readMessage(in);
            	if(message[0] == 87 || message[0] == 76)
            		break;
            	int lung = message.length;
            	if(lung == 2) {
            		tabla_joc.zar1 = message[0] > message[1] ? message[0] : message[1];
            		tabla_joc.zar2 = message[0] > message[1] ? message[1] : message[0];;
            	}
            	else {
            		int[] tabla = tabla_joc.getTabla();
            		for(int i = 0; i < lung - 2; i+=2) {
            			if(message[i] == 30) {
            				int casa;
            				if(var == -1)
            					casa = 0;
            				else casa =25;
            				tabla[casa] -= 1;
            				if(tabla[casa + message[i + 1]*(-var)] == var) {
        						tabla[casa + message[i + 1]*(-var)] -= var; 
        						tabla[var == -1 ? 25 : 0] += 1;
        					}
        					tabla[casa + message[i + 1]*(-var)] -= var; 
            			} else {
            				tabla[message[i]] += var;
            				if(message[i] + message[i+1]*(-var) > 24 || message[i] + message[i+1]*(-var) < 1) {
            					tabla_joc.casa[var == -1 ? 1 : 0] += 1;
            				} else {
            					if(tabla[message[i] + message[i + 1]*(-var)] == var) {
            						tabla[message[i] + message[i + 1]*(-var)] -= var; 
            						tabla[var == -1 ? 25 : 0] += 1;
            					}
            					tabla[message[i] + message[i + 1]*(-var)] -= var; 
            				}
            			}
            		}
            		tabla_joc.setTabla(tabla);
            		tabla_joc.zar1 = message[lung - 2] > message[lung - 1] ? message[lung - 2] : message[lung - 1];
            		tabla_joc.zar2 = message[lung - 2] > message[lung - 1] ? message[lung - 1] : message[lung - 2];
            	}
       
            	ExpectiMiniMax da = new ExpectiMiniMax(var == -1 ? 0 : 1);
            	Pair p = da.expectiminimax(tabla_joc, var == -1 ? 0 : 1, 3);
            	byte[] yourResponse;
            	int lungime = 0;
            	if(p.second() == null) {
            		yourResponse = new byte[0];
            	}
            	else {
            		for(int i = 0 ;i < p.second().mutari.length; i++) {
            			if(p.second().mutari[i] != -1)
            				lungime++;
            		}
            		
            		tabla_joc.setTabla(p.second().getTabla());
            		tabla_joc.setCasa(p.second().getCasa());
            		tabla_joc.index = 0;
            		tabla_joc.mutari = new int[20];
            		for(int i = 0; i < 20; i++)
            			tabla_joc.mutari[i] = -1;
            	
            		yourResponse = new byte[lungime];
            		for(int i = 0; i < lungime; i++) {
            			yourResponse[i] = (byte)p.second().mutari[i];
            		}
            	}
            	Main.sendMessage(yourResponse, out);
            	
            }
            socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} 
	}
}
