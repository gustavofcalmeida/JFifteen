package br.gfca.jfifteen.util;

import javax.swing.ImageIcon;

public class ImageLoader {
	
	private static final String RESOURCE_PATH = "br/gfca/jfifteen/img/";
	private static final String SKINS_PATH = RESOURCE_PATH + "skin/";
	
	/**
	 * Obtém uma imagem que está como um recurso
	 * dentro das pastas de código fonte.
	 * @param nome o nome do arquivo de imagem.
	 * @return A imagem.
	 */
	public static ImageIcon getResource( String nome ) {
		return new ImageIcon( ImageLoader.class.getClassLoader().getResource( ImageLoader.RESOURCE_PATH + nome ) );
	}
	
	public static ImageIcon getSkin( int num ) {
		return new ImageIcon( ImageLoader.class.getClassLoader().getResource( ImageLoader.SKINS_PATH + num + ".png" ) );
	}
}