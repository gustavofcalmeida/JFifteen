/*
 * OpenFile.java
 *
 * Created on 8 de Fevereiro de 2008, 09:03
 */

package br.gfca.jfifteen.gui;

import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author  Gustavo
 */
public class OpenFile extends javax.swing.JFileChooser {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -8817906134311269097L;
	
	/** Creates new form BeanForm */
    public OpenFile() {
	initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" C�digo Gerado ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setAcceptAllFileFilterUsed(false);
        setCurrentDirectory(null);
        setDialogTitle(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("filechooser_title_open"));
        setFileFilter(getImageFilter());
    }// </editor-fold>//GEN-END:initComponents
    
    private FileNameExtensionFilter getImageFilter() {
	return new FileNameExtensionFilter(java.util.ResourceBundle.getBundle("br/gfca/jfifteen/local_strings").getString("types_of_images"), "jpg", "gif", "jpeg", "png", "bmp");
    }
    
    // Declara��o de vari�veis - n�o modifique//GEN-BEGIN:variables
    // Fim da declara��o de vari�veis//GEN-END:variables
    
}
