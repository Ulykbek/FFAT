package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javafx.stage.FileChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

public class MainFrame extends JFrame{
    
    private JMenuBar menuBar;
    private JMenu menuFile;
    private JMenu menuTools;
    private JMenu menuWindow;
    private JMenu menuHelp;
    
    private JMenuItem menuItemImport;
    private JMenuItem menuItemExport;
    private JMenuItem menuItemExit;
    private JMenuItem menuItemConverter;
    
    
    private JFileChooser fileChooser;
    
    private JSplitPane splitPane;
    private JTabbedPane tabPane;
    
    private DMPanel DMPanel;
    private SearchPanel searchPanel;
    private VisioPanel VisioPanel;
    private Process process;
    private ProgressDialog progressDialog;
    private ConverterDialog converterDialog;
    public MainFrame(){
        this.setTitle("Main Window");
        this.setSize(700, 700);
        this.setMinimumSize(this.getSize());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int)((d.getWidth()- this.getWidth())/2);
        int y = (int)((d.getHeight()-this.getHeight())/2);
        this.setLocation(x, y);
        
        menuBar = new JMenuBar();
        menuFile = new JMenu("File");
        menuTools = new JMenu("Tools");
        menuWindow = new JMenu("Window");
        menuHelp = new JMenu("Help");
        menuItemImport = new JMenuItem("Import and Carve");
        menuItemExport = new JMenuItem("Export");
        menuItemExit = new JMenuItem("Exit");
        menuItemConverter = new JMenuItem("Converter");
        this.setJMenuBar(menuBar);
        menuBar.add(menuFile);
        menuBar.add(menuTools);
        menuBar.add(menuWindow);
        menuBar.add(menuHelp);
        menuFile.add(menuItemImport);
        menuFile.add(menuItemExport);
        menuFile.addSeparator();
        menuFile.add(menuItemExit);
        menuTools.add(menuItemConverter);
        
        fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new ImgFileFilter());
        
        //progressDialog = new ProgressDialog(this);
        
        menuItemConverter.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                converterDialog = new ConverterDialog((Window)getParent());
            }
        });

        
        menuItemImport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(fileChooser.showOpenDialog(MainFrame.this)==JFileChooser.APPROVE_OPTION){
                    System.out.println(fileChooser.getSelectedFile());
                    File f = fileChooser.getSelectedFile();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            progressDialog = new ProgressDialog((Window)getParent(),f);
                            
                        }
                    });
//                    try{
//   process = Runtime.getRuntime().exec("perl //Users//ulykbek//Desktop//Vish//pdf.pl "+fileChooser.getSelectedFile());
//                    process.waitFor();
//                    progressDialog.setVisible(false);
//                    if(process.exitValue() == 0){
//                        System.out.println("Command Successful");
//                        
//                    }else{
//                        System.out.println("Command Failure");
//                    }
//                    }
//                    catch(Exception exp)
//                    {
//                    System.out.println("Exception: "+ exp.toString());
//                    }
                }
                
            }
        });
        
        tabPane = new  JTabbedPane();
        tabPane.setBackground(new Color(255, 255, 255));
        add(tabPane);
        
        searchPanel = new SearchPanel();
        tabPane.add("Search",searchPanel);
        
        DMPanel = new DMPanel();
        tabPane.add("Data Mining",DMPanel);
        
        
        
        VisioPanel = new VisioPanel();
        tabPane.add("Visualization",VisioPanel);
        
        setVisible(true);
        
    }
}
