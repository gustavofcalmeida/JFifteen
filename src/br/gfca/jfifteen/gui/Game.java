package br.gfca.jfifteen.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import br.gfca.jfifteen.exception.InvalidDispositionException;
import br.gfca.jfifteen.exception.InvalidPositionException;
import br.gfca.jfifteen.graphics.BlocksCoordinates;
import br.gfca.jfifteen.graphics.Coordinates;
import br.gfca.jfifteen.logic.BlocksDisposition;
import br.gfca.jfifteen.logic.Board;
import br.gfca.jfifteen.util.ImageLoader;

/**
 * 
 */
public class Game extends JFrame implements ActionListener, ItemListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String ls = System.getProperty("line.separator");
	
	private static final int NUM_SKINS = 5;
	private static final int CHALLENGE_SKIN = 0;	
	private static final int SKIN_SIDE = 484;
		
	private Board tabuleiro;
	private Graficos graficos;
	
	private OpenFile openDialog;

	private JMenuBar menuBar;
	private JMenu menuJogo,
				  menuOpcoes,
				  menuAjuda;
	private JMenuItem novo,
					  sair,
					  customize,
					  challenge,
					  comoJogar,
					  sobre;
	private JCheckBoxMenuItem miniatura;

	private ImageIcon figuraPrincipal,
					  figuraMiniatura,
					  figuraGrande;
	private ImageIcon[] figurasBlocos;

	private boolean mouseDesativado;

	/**
	 * 
	 */
	public Game () {
		super(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("JG_JQuinze"));
		
		this.openDialog = new OpenFile();

		tabuleiro = new Board();
		graficos = new Graficos();
		getContentPane().add(graficos,BorderLayout.CENTER);
		figuraPrincipal = ImageLoader.getResource("background.png");
		figurasBlocos = new ImageIcon[16];

		setCoordenadasFiguras();

		setListenerNodos();
		setConfiguracoesJanela();
		setMenus();

		mouseDesativado = false;

		pack();
		this.carregaSkin( this.selectRandomSkin() );
		setSize(671 + getInsets().left + getInsets().right,
				510 + getInsets().top + menuBar.getHeight() + getInsets().bottom);
		setLocationRelativeTo(null);
		this.setVisible(true);
		graficos.repaint();
	}

	/**
	 * @return
	 */
	private ImageIcon selectRandomSkin() {
		int selected = (int)(Math.random() * NUM_SKINS + 1);
		return ImageLoader.getSkin( selected );
	}

	/**
	 * 
	 */
	private void setCoordenadasFiguras () {
		graficos.setPosicoesXYBlocos(tabuleiro.toArray());
		graficos.setPosicaoFiguraGrande(Graficos.ESCONDER);
	}

	/**
	 * 
	 */
	private void carregaSkin ( ImageIcon skin ) {
		figuraGrande = skin;

		Image matriz = figuraGrande.getImage();
		Image blocoVazio = ImageLoader.getResource("block.png").getImage();
				
		int i = 1;
		for (byte l = 0; l < 3; l++) {
			for (byte c = 0; c < 4; c++, i++) {
				Image bloco = this.graficos.createImage(121, 121);
				
				bloco.getGraphics().drawImage(blocoVazio, 0, 0, this.graficos);
				bloco.getGraphics().drawImage(matriz, 2, 2, 119, 119, c * 121 + 2, l * 121 + 2, c * 121 + 2 + 117, l * 121 + 2 + 117, this.graficos);
				figurasBlocos[i] = new ImageIcon(bloco);
			}
		}
		for (byte c = 0; c < 3; c++, i++) {
			Image bloco = this.graficos.createImage(121, 121);
			
			bloco.getGraphics().drawImage(blocoVazio, 0, 0, this.graficos);
			bloco.getGraphics().drawImage(matriz, 2, 2, 119, 119, c * 121 + 2, 3 * 121 + 2, c * 121 + 2 + 117, 3 * 121 + 2 + 117, this.graficos);
			figurasBlocos[i] = new ImageIcon(bloco);
		}
		
		figuraMiniatura = new ImageIcon(matriz.getScaledInstance(146, 146, Image.SCALE_SMOOTH));
	}

	/**
	 * 
	 */
	private void setListenerNodos () {
		addMouseListener (new MouseAdapter() {
			public void mousePressed (MouseEvent evento) {
				if (!graficos.threadMovimento.isAlive() && !mouseDesativado) { // impede que Threads concorrentes sejam criadas
					evento.translatePoint(-getInsets().left -Coordinates.DIST_MARGEM_X,
										  -getInsets().top -menuBar.getHeight() -Coordinates.DIST_MARGEM_Y);

					byte linha = (byte)Math.ceil((evento.getY() + 1) / 121.0);
					byte coluna = (byte)Math.ceil((evento.getX() + 1) / 121.0);
					movePeca(linha, coluna);
				}
			}
		});
		
		addKeyListener (new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (!graficos.threadMovimento.isAlive() && !mouseDesativado) { // impede que Threads concorrentes sejam criadas
					byte linha = -1;
					byte coluna = -1;
					if ( e.getKeyCode() == KeyEvent.VK_UP ) {
						linha = (byte)(Game.this.tabuleiro.getLinhaCasaVazia() + 1);
						coluna = (byte)(Game.this.tabuleiro.getColunaCasaVazia());
					}
					else if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
						linha = (byte)(Game.this.tabuleiro.getLinhaCasaVazia() - 1);
						coluna = (byte)(Game.this.tabuleiro.getColunaCasaVazia());
					}
					else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						linha = (byte)(Game.this.tabuleiro.getLinhaCasaVazia());
						coluna = (byte)(Game.this.tabuleiro.getColunaCasaVazia() + 1);
					}
					else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						linha = (byte)(Game.this.tabuleiro.getLinhaCasaVazia());
						coluna = (byte)(Game.this.tabuleiro.getColunaCasaVazia() - 1);
					}
					movePeca(linha, coluna);
				}
			}
		});
	}

	/**
	 * 
	 */
	private void setConfiguracoesJanela () {
		addWindowListener(new WindowAdapter() {
			public void windowClosing (WindowEvent evento) {
				System.exit(0);
			}
		});

		ImageIcon i = ImageLoader.getResource("icon.png");
		setIconImage(i.getImage());

		setResizable(false);
	}

	/**
	 * 
	 */
	private void setMenus () {
		menuBar = new JMenuBar();

		setMenuJogo();
		setMenuOpcoes();
		setMenuAjuda();

		menuBar.add(menuJogo);
		menuBar.add(menuOpcoes);
		menuBar.add(menuAjuda);

		setJMenuBar(menuBar);
	}

	/**
	 * 
	 */
	private void setMenuJogo () {
		menuJogo = new JMenu(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("JG_Jogo"));

		novo = new JMenuItem(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("JG_Novo"));
		menuJogo.add(novo);
		novo.addActionListener(this);
		customize = new JMenuItem(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("menuitem_new_customized"));
		menuJogo.add(customize);
		customize.addActionListener(this);
		menuJogo.addSeparator();
		challenge = new JMenuItem(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("menuitem_challenge"));
		menuJogo.add(challenge);
		challenge.addActionListener(this);
		menuJogo.addSeparator();
		sair = new JMenuItem(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("JG_Sair"));
		menuJogo.add(sair);
		sair.addActionListener(this);
	}

	/**
	 * 
	 */
	private void setMenuOpcoes () {
		menuOpcoes = new JMenu(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("JG_Opcoes"));

		miniatura = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("JG_Ocultar_miniatura"));
		menuOpcoes.add(miniatura);
		miniatura.addItemListener(this);
	}

	/**
	 * 
	 */
	private void setMenuAjuda () {
		menuAjuda = new JMenu(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("JG_Ajuda"));

		comoJogar = new JMenuItem(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("JG_Como_jogar"),ImageLoader.getResource("howToPlay.gif"));
		menuAjuda.add(comoJogar);
		comoJogar.addActionListener(this);
		menuAjuda.addSeparator();
		sobre = new JMenuItem(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("JG_Sobre_JQuinze"),ImageLoader.getResource("about.gif"));
		menuAjuda.add(sobre);
		sobre.addActionListener(this);
	}

	/**
	 * 
	 * @param event
	 */
	public void actionPerformed (ActionEvent event) {
		if (event.getSource() == novo)
			performNewRandom();
		else if (event.getSource() == customize)
			performNewCustomized();
		else if (event.getSource() == challenge)
			performChallenge();
		else if (event.getSource() == sair)
			performSair();
		else if (event.getSource() == comoJogar)
			performComoJogar();
		else if (event.getSource() == sobre)
			performSobre();
	}

	/**
	 * 
	 * @param event
	 */
	public void itemStateChanged (ItemEvent event) {
		if (event.getSource() == miniatura)
			performMiniatura(event);
	}

	/**
	 * 
	 */
	private void performNewRandom () {
		mouseDesativado = true;
		this.performNew( this.selectRandomSkin() );
		mouseDesativado = false;
	}
	
	private void performNewCustomized() {
		mouseDesativado = true;
		
		int answer = this.openDialog.showOpenDialog( this );
		if ( answer == JFileChooser.APPROVE_OPTION ) {
			BufferedImage image = null;
			try {
				image = ImageIO.read( this.openDialog.getSelectedFile() );
			}
			catch ( Exception e ) {
				JOptionPane.showMessageDialog( this, java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("dialog_error_message") + ls + java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("dialog_error_prefix") + e.getMessage(),
						java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("dialog_title_error"), JOptionPane.ERROR_MESSAGE );
			}
			
			if ( image != null ) {				
				ImageIcon skin = new ImageIcon ( image.getScaledInstance( SKIN_SIDE, SKIN_SIDE, Image.SCALE_SMOOTH ) );
				this.performNew( skin );
			}
		}
		mouseDesativado = false;
	}
	
	private void performNew( ImageIcon skin ) {
		tabuleiro.reinicia();
		setCoordenadasFiguras();
		this.carregaSkin( skin );
		graficos.repaint();
	}
	
	private void performChallenge() {
		mouseDesativado = true;
		byte[] blocks = {-1,1,2,3,4,5,6,7,8,9,10,11,12,13,15,14,0}; // unsolvable
//		byte[] blocks = {-1,1,2,3,4,5,6,7,8,9,10,15,11,13,14,12,0}; // easily solvable
		try {
			tabuleiro.reinicia( new BlocksDisposition( blocks ) );
		} catch (InvalidDispositionException e) {
			// Should never get here
		}
		setCoordenadasFiguras();
		this.carregaSkin( ImageLoader.getSkin( CHALLENGE_SKIN ) );
		graficos.repaint();
		mouseDesativado = false;
	}

	/**
	 * 
	 */
	private void performSair () {
		System.exit(0);
	}

	/**
	 * 
	 */
	private void performComoJogar () {
		String text = java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_help").getString("help_text");

		JTextArea texto = new JTextArea( text, 20, 45 );
		texto.setFont(new Font("Monospaced",Font.PLAIN,14));
		texto.setEditable(false);
		JScrollPane scroll = new JScrollPane(texto);

		JOptionPane.showMessageDialog(Game.this,
									  scroll,
									  java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("JG_Como_jogar2"),
									  JOptionPane.PLAIN_MESSAGE,
									  ImageLoader.getResource("decoration.png"));
	}

	/**
	 * 
	 */
	private void performSobre () {
		String text = java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_about").getString("about_text");
		
		JOptionPane.showMessageDialog(Game.this,
									  text,
									  java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("JG_Sobre_JQuinze2"),
									  JOptionPane.PLAIN_MESSAGE,
									  ImageLoader.getResource("decoration.png"));
	}

	/**
	 * 
	 * @param event
	 */
	private void performMiniatura (ItemEvent event) {
		switch (event.getStateChange()) {
			case ItemEvent.DESELECTED: {
				graficos.setExibicaoMiniatura(Graficos.EXIBIR);
				break;
			}
			case ItemEvent.SELECTED: {
				graficos.setExibicaoMiniatura(Graficos.ESCONDER);
				break;
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public BlocksDisposition getListaDeBlocos () {
		return tabuleiro.getListaDeBlocos();
	}

	/**
	 * 
	 * @param linha
	 * @param coluna
	 */
	private void movePeca (byte linha, byte coluna) {
		try {
			switch (tabuleiro.movePeca(linha,coluna)) {
				case Board.ACIMA: moveBaixo(linha,coluna);
					 break;
				case Board.ABAIXO: moveCima(linha,coluna);
					 break;
				case Board.A_ESQUERDA: moveDireita(linha,coluna);
					 break;
				case Board.A_DIREITA: moveEsquerda(linha,coluna);
					 break;
			}
		}
		catch (InvalidPositionException pie) {} // captura a exceção quando o usuário clica fora de um bloco
	}

	/**
	 * 
	 * @param linha
	 * @param coluna
	 */
	private void moveCima (byte linha, byte coluna) {
		try {
			graficos.moveBloco(Board.ACIMA, tabuleiro.getBloco(linha,coluna));
		}
		catch (InvalidPositionException pie) {
			pie.printStackTrace();
		}
		verificaOrdenacao();
	}

	/**
	 * 
	 * @param linha
	 * @param coluna
	 */
	private void moveBaixo (byte linha, byte coluna) {
		try {
			graficos.moveBloco(Board.ABAIXO, tabuleiro.getBloco(linha,coluna));
		}
		catch (InvalidPositionException pie) {
			pie.printStackTrace();
		}
		verificaOrdenacao();
	}

	/**
	 * 
	 * @param linha
	 * @param coluna
	 */
	private void moveEsquerda (byte linha, byte coluna) {
		try {
			graficos.moveBloco(Board.A_ESQUERDA, tabuleiro.getBloco(linha,coluna));
		}
		catch (InvalidPositionException pie) {
			pie.printStackTrace();
		}
		verificaOrdenacao();
	}

	/**
	 * 
	 * @param linha
	 * @param coluna
	 */
	private void moveDireita (byte linha, byte coluna) {
		try {
			graficos.moveBloco(Board.A_DIREITA, tabuleiro.getBloco(linha,coluna));
		}
		catch (InvalidPositionException pie) {
			pie.printStackTrace();
		}
		verificaOrdenacao();
	}

	/**
	 * 
	 */
	private void verificaOrdenacao () {
		if (tabuleiro.estaOrdenado()) {
			mouseDesativado = true;
			graficos.exibeFiguraGrande();
		}
	}

	/**
	 * 
	 */
	private class Graficos extends JPanel implements Runnable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private final long DELAY = 5;
		public Thread threadMovimento; // público para JogoGrafico saber quando está ativa

		private byte movimento;
		private byte numeroBloco;

		private int[] posicoesXBlocos,
					  posicoesYBlocos;
		private int posicaoXMiniatura,
					posicaoYMiniatura,
					posicaoXFiguraGrande,
					posicaoYFiguraGrande;

		public static final byte ESCONDER = 0;
		public static final byte EXIBIR = 1;

		/**
		 * 
		 */
		public Graficos () {
			super();

			threadMovimento = new Thread(this); // construção para evitar NullPointerException no início

			posicoesXBlocos = new int[16];
			posicoesYBlocos = new int[16];

			posicaoXMiniatura = Coordinates.X_MINIATURA;
			posicaoYMiniatura = Coordinates.Y_MINIATURA;
		}

		/**
		 * 
		 * @param g
		 */
		public void paintComponent (Graphics g) {
			super.paintComponent(g);

			figuraPrincipal.paintIcon(this,g,0,0);
			figuraMiniatura.paintIcon(this,g,posicaoXMiniatura,posicaoYMiniatura);
			for (byte i = 1; i <= 15; i++) {
				figurasBlocos[i].paintIcon(this,g,posicoesXBlocos[i],posicoesYBlocos[i]);
			}
			figuraGrande.paintIcon(this,g,posicaoXFiguraGrande,posicaoYFiguraGrande);
		}

		/**
		 * 
		 * @param listaDeBlocos
		 */
		public void setPosicoesXYBlocos (byte[] listaDeBlocos) {
			for (byte i = 1; i <= 16; i++) {
				if (listaDeBlocos[i] != 0) {
					posicoesXBlocos[listaDeBlocos[i]] = BlocksCoordinates.PONTOS[i].x;
					posicoesYBlocos[listaDeBlocos[i]] = BlocksCoordinates.PONTOS[i].y;
				}
			}
		}

		/**
		 * 
		 * @param acao
		 */
		public void setPosicaoFiguraGrande (byte acao) {
			switch (acao) {
				case ESCONDER: {
					posicaoXFiguraGrande = Integer.MIN_VALUE;
					posicaoYFiguraGrande = Integer.MIN_VALUE;
					break;
				}
				case EXIBIR: {
					posicaoXFiguraGrande = Coordinates.X_GRANDE;
					posicaoYFiguraGrande = Coordinates.Y_GRANDE;
					break;
				}
			}
		}

		/**
		 * 
		 * @param acao
		 */
		public void setExibicaoMiniatura (byte acao) {
			switch (acao) {
				case ESCONDER: {
					posicaoXMiniatura = Integer.MIN_VALUE;
					posicaoYMiniatura = Integer.MIN_VALUE;
					break;
				}
				case EXIBIR: {
					posicaoXMiniatura = Coordinates.X_MINIATURA;
					posicaoYMiniatura = Coordinates.Y_MINIATURA;
					break;
				}
			}
			repaint();
		}

		/**
		 * 
		 */
		public void run () {
			try {
				switch (movimento) {
					case Board.ACIMA: {
						for (int i = 1; i <= 5; i++) {
							posicoesYBlocos[numeroBloco] -= 20;
							repaint();
							Thread.sleep(DELAY);
						}
						posicoesYBlocos[numeroBloco] -= 21;
						repaint();
	
						break;
					}
					case Board.ABAIXO: {
						for (int i = 1; i <= 5; i++) {
							posicoesYBlocos[numeroBloco] += 20;
							repaint();
							Thread.sleep(DELAY);
						}
						posicoesYBlocos[numeroBloco] += 21;
						repaint();
	
						break;
					}
					case Board.A_ESQUERDA: {
						for (int i = 1; i <= 5; i++) {
							posicoesXBlocos[numeroBloco] -= 20;
							repaint();
							Thread.sleep(DELAY);
						}
						posicoesXBlocos[numeroBloco] -= 21;
						repaint();
	
						break;
					}
					case Board.A_DIREITA: {
						for (int i = 1; i <= 5; i++) {
							posicoesXBlocos[numeroBloco] += 20;
							repaint();
							Thread.sleep(DELAY);
						}
						posicoesXBlocos[numeroBloco] += 21;
						repaint();
	
						break;
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 
		 * @param movimento
		 * @param numeroBloco
		 */
		public void moveBloco (byte movimento, byte numeroBloco) {
			this.movimento = movimento;
			this.numeroBloco = numeroBloco;

			threadMovimento = new Thread(this);
			threadMovimento.start();
		}

		/**
		 * 
		 */
		public void exibeFiguraGrande () {
			setPosicaoFiguraGrande(EXIBIR);
			repaint();
		}
	}
}