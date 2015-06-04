package app;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class ConverterDialog extends JDialog{
        private static JButton button = new JButton("Browse");
        private static JButton button1 = new JButton("Convert");
        private static JFileChooser fileChooser = new JFileChooser();
        private JLabel fileLabel = new JLabel("File in CSV format: ");
        private JTextField textField = new JTextField(15);
        public ConverterDialog(Window parent){
        super(parent,"Conver from CSC to ARFF",ModalityType.APPLICATION_MODAL);
        setSize(400, 300);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int)((d.getWidth()-this.getWidth())/2);
        int y = (int)((d.getHeight() - this.getHeight())/2);
        setLocation(x, y);
        setLayout(new FlowLayout());
        
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setCurrentDirectory(new File("/Users/ulykbek"));
        add(fileLabel);
        add(textField);
        add(button);
        add(button1);
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String s = e.getActionCommand();
                if(s.equals("Browse")){
                    fileChooser.showOpenDialog(ConverterDialog.this);
                    String CSVfile = fileChooser.getSelectedFile().toString();
                    String CSVfileParent = fileChooser.getSelectedFile().getParent().toString();
                    textField.setText(CSVfile);
                            button1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String s = e.getActionCommand();
                if(s.equals("Convert")){
                    try {
                        CSV2ARFF(CSVfile,CSVfileParent);
                    } catch (IOException ex) {
                        Logger.getLogger(ConverterDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
                }
            }
        });
        


        setVisible(true);
        
        }
        public void CSV2ARFF(String CSVfile, String CSVfileParent) throws IOException{
                
                CSVLoader csvLoader = new CSVLoader();
		csvLoader.setSource(new File(CSVfile));
		Instances dataset = csvLoader.getDataSet();
		System.out.println(dataset.toSummaryString());
		
		ArffSaver arffSaver = new ArffSaver();
		arffSaver.setInstances(dataset);
		arffSaver.setFile(new File(CSVfileParent+"/NewConverted.arff"));
		arffSaver.writeBatch();
            
        }
}
