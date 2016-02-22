package br.gfca.jfifteen.logic;

import br.gfca.jfifteen.exception.InvalidBlockException;
import br.gfca.jfifteen.exception.InvalidDispositionException;
import br.gfca.jfifteen.exception.InvalidPositionException;

/**
 * 
 */
public class Board {
	private byte[][] casas;
	private byte linhaCasaVazia,
				 colunaCasaVazia;

	private byte[] casasArray;

	public static final byte ACIMA 		= 1;
	public static final byte ABAIXO 	= 2;
	public static final byte A_ESQUERDA = 3;
	public static final byte A_DIREITA 	= 4;
	public static final byte DISTANTE 	= 5;

	/**
	 * 
	 */
	public Board () {
		casas = new byte[5][5];
		reinicia();
	}

	/**
	 * 
	 * @param listaDeBlocos
	 */
	public void reinicia (BlocksDisposition listaDeBlocos) {
		casasArray = listaDeBlocos.toArray();
		encheMatriz();
	}

	/**
	 * 
	 */
	public void reinicia () {
		reinicia(new BlocksDisposition());
	}

	/**
	 * 
	 * @return
	 */
	public BlocksDisposition getListaDeBlocos () {
		try {
			return new BlocksDisposition(casasArray);
		}
		catch (InvalidDispositionException nfle) {
			nfle.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public byte[] toArray () {
		return casasArray;
	}

	/**
	 * 
	 */
	private void encheMatriz () {
		for (byte l = 1; l <= 4; l++) {
			for (byte c = 1; c <= 4; c++) {
				casas[l][c] = casasArray[(l - 1) * 4 + c];

				if (casasArray[(l - 1) * 4 + c] == 0) {
					linhaCasaVazia  = l;
					colunaCasaVazia = c;
				}
			}
		}
	}

	/**
	 * 
	 * @param linha
	 * @param coluna
	 * @return
	 * @throws InvalidPositionException
	 */
	public byte getBloco (byte linha, byte coluna) throws InvalidPositionException {
		if (linha < 1 || linha > 4 || coluna < 1 || coluna > 4) {
			throw new InvalidPositionException();
		}
		return casas[linha][coluna];
	}

	/**
	 * 
	 * @param bloco
	 * @return
	 * @throws InvalidBlockException
	 */
	public byte[] getPosicaoBloco (byte bloco) throws InvalidBlockException {
		if (bloco < 0 || bloco > 15) {
			throw new InvalidBlockException();
		}

		for (byte l = 1; l <= 4; l++) {
			for (byte c = 1; c <= 4; c++) {
				if (casas[l][c] == bloco) {
					byte[] posicao = {l,c};
					return posicao;
				}
			}
		}
		byte[] posicao = {0,0};
		return posicao;
	}

	/**
	 * 
	 * @param linha
	 * @param coluna
	 * @return
	 * @throws InvalidPositionException
	 */
	public byte movePeca (byte linha, byte coluna) throws InvalidPositionException {
		if (linha < 1 || linha > 4 || coluna < 1 || coluna > 4) {
			throw new InvalidPositionException();
		}

		byte posicaoRelativa = posicaoRelativaAoVazio(linha,coluna);

		if (posicaoRelativa != DISTANTE) {
			casas[linhaCasaVazia][colunaCasaVazia] = casas[linha][coluna];
			casasArray[(linhaCasaVazia - 1) * 4 + colunaCasaVazia] = casasArray[(linha - 1) * 4 + coluna];

			linhaCasaVazia = linha;
			colunaCasaVazia = coluna;
			casasArray[(linha - 1) * 4 + coluna] = 0;
		}
		
		return posicaoRelativa;
	}

	/**
	 * 
	 * @return
	 */
	public boolean estaOrdenado () {
		if (casasArray[16] != 0) {
			return false;
		}

		for (byte i = 1; i <= 15; i++) {
			if (casasArray[i] != i) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param l
	 * @param c
	 * @return
	 * @throws InvalidPositionException
	 */
	public byte posicaoRelativaAoVazio (byte l, byte c) throws InvalidPositionException {
		if (l < 1 || l > 4 || c < 1 || c > 4) {
			throw new InvalidPositionException();
		}

		if (linhaCasaVazia == l + 1 && colunaCasaVazia == c) 
			return ACIMA;
		else if (linhaCasaVazia == l - 1 && colunaCasaVazia == c) 
			return ABAIXO;
		else if (colunaCasaVazia == c + 1 && linhaCasaVazia == l)
			return A_ESQUERDA;
		else if (colunaCasaVazia == c - 1 && linhaCasaVazia == l)
			return A_DIREITA;
		else
			return DISTANTE;
	}
	
	public byte getLinhaCasaVazia() {
		return linhaCasaVazia;
	}
	
	public byte getColunaCasaVazia() {
		return colunaCasaVazia;
	}
}