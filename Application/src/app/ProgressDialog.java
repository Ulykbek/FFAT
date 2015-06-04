package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class ProgressDialog extends JDialog{
    private JLabel fileNameLabel;
    private JButton carveBtn;
    private Process process;
    private JProgressBar progressBar;
    private JButton cancelBtn;
    public ProgressDialog(Window parent,File f){
        super(parent,"Carving files..",ModalityType.APPLICATION_MODAL);
        setSize(400, 300);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int)((d.getWidth()-this.getWidth())/2);
        int y = (int)((d.getHeight() - this.getHeight())/2);
        setLocation(x, y);
        setLayout(new FlowLayout());
        
        fileNameLabel = new JLabel();
        fileNameLabel.setText(f.toString());
        add(fileNameLabel);
        
        carveBtn = new JButton("Start Carving");
        add(carveBtn);
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        add(progressBar);
        carveBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                                    try{
                    
                    
   process = Runtime.getRuntime().exec("perl //Users//ulykbek//Desktop//Vish//pdf.pl "+f);
                    process.waitFor();
                    if(process.exitValue() == 0){
                        System.out.println("Command Successful");
                        setVisible(false);
                        JOptionPane.showMessageDialog(ProgressDialog.this,
    "Carving Finished Successfully");
                    }else{
                        System.out.println("Command Failure");
                    }
                    }
                    catch(Exception exp)
                    {
                    System.out.println("Exception: "+ exp.toString());
                    }
            }
        });
        
        

        
        cancelBtn = new JButton("Cancel Carving");
        add(cancelBtn);
        //pack();
        setVisible(true);
        
    }
    public void setMax(int value){
        progressBar.setMaximum(value);
    }
    public void setValue(int value){
        progressBar.setValue(value);
    }
    
}
