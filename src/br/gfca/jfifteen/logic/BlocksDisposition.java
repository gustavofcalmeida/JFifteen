package br.gfca.jfifteen.logic;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.gfca.jfifteen.exception.InvalidDispositionException;

/**
 * 
 */
public class BlocksDisposition implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private byte[] casas;

	/**
	 * 
	 * @param casas
	 * @throws InvalidDispositionException
	 */
	public BlocksDisposition (byte[] casas) throws InvalidDispositionException {
		if (casas.length != 17) {
			throw new InvalidDispositionException(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("LDB_Array_com_tamanho_inadequado"));
		}

		if (!formaDisposicao(casas)) {
			throw new InvalidDispositionException(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("LDB_Array_com_disposicao_inadequada"));
		}

		this.casas = casas;
	}

	/**
	 * 
	 */
	public BlocksDisposition () {
		casas = new byte[17];

		randomizaCasas();

		if ((numeroDeInversoes() % 2) == 1) {
			byte aux = casas[15];
			casas[15] = casas[14];
			casas[14] = aux;
		}
	}

	/**
	 * 
	 */
	private void randomizaCasas () {
		List<Byte> l = new ArrayList<Byte>();
		
		for (byte i = 1; i <= 15; i++) {
			l.add(i);
		}
		Collections.shuffle(l);

		Iterator<Byte> it = l.iterator();
		for (byte i = 1; i <= 15; i++) {
			casas[i] = it.next();
		}
		casas[16] = 0;
	}

	/**
	 * 
	 * @return
	 */
	private byte numeroDeInversoes () {
		byte inversoes = 0;
		for (int i = 1; i <= 15; i++) {
			for (int j = i + 1; j <= 15; j++) {
				if (casas[i] > casas[j]) {
					inversoes++;
				}
			}
		}
		return inversoes;
	}

	/**
	 * 
	 * @param casas
	 * @return
	 */
	private boolean formaDisposicao (byte[] casas) {
		boolean[] imagem = new boolean[16];
		for (byte i = 0; i < imagem.length; i++) {
			imagem[i] = false;
		}

		for (byte i = 1; i <= 16; i++) {
			if (casas[i] >= 0 && casas[i] <= 15) {
				imagem[casas[i]] = true;
			}
			else {
				return false;
			}
		}

		for (byte i = 0; i <= 15; i++) {
			if (imagem[i] == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return
	 */
	public byte[] toArray () {
		return (byte[])casas.clone();
	}
}