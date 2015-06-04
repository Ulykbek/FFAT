package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;

/** **/
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileSystemView;

import javax.imageio.ImageIO;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import java.io.*;
import java.nio.channels.FileChannel;

import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/** **/

public class SearchPanel extends JPanel{
    
    private JSplitPane splitPane;
    private JPanel leftPanel;
    private JPanel rightPanel;
    
    private JPanel rPT;
    private JPanel rPB;
    private JSplitPane splitPane1;
    
    private JLabel rPTLabel;
    private JTextField rPTField;
    private JButton rPTBtn;
    
    private JTree tree;
    /** **/
    
    /** Title of the application */
    public static final String APP_TITLE = "FileBro";
    /** Used to open/edit/print files. */
    private Desktop desktop;
    /** Provides nice icons and names for files. */
    private FileSystemView fileSystemView;

    /** currently selected File. */
    private File currentFile;

    /** Main GUI container */
    private JPanel gui;

    /** File-system tree. Built Lazily */
    //private JTree tree;
    private DefaultTreeModel treeModel;

    /** Directory listing */
    private JTable table;
    private JProgressBar progressBar;
    /** Table model for File[]. */
    private FileTableModel fileTableModel;
    private ListSelectionListener listSelectionListener;
    private boolean cellSizesSet = false;
    private int rowIconPadding = 6;

    /* File controls. */
    private JButton openFile;
    private JButton printFile;
    private JButton editFile;

    /* File details. */
    private JLabel fileName;
    private JTextField path;
    private JLabel date;
    private JLabel size;
    private JCheckBox readable;
    private JCheckBox writable;
    private JCheckBox executable;
    private JCheckBox isSel;
    private JRadioButton isDirectory;
    private JRadioButton isFile;

    /* GUI options/containers for new File/Directory creation.  Created lazily. */
    private JPanel newFilePanel;
    private JRadioButton newTypeFile;
    private JTextField name;
    private JPanel searchPanel;
    /** **/
    private KeywordSearch ks;
    private NLP nlp;
    public SearchPanel(){
       
       this.setLayout(new BorderLayout());
       
       leftPanel = new JPanel();
       leftPanel.setBackground(new Color(255,255,255));
       leftPanel.setLayout(new BorderLayout());
       
       rightPanel = new JPanel();
       rightPanel.setLayout(new BorderLayout());
       
       rPT = new JPanel();
       rPT.setBackground(new Color(255, 255, 255));
       rPT.setLayout(new GridBagLayout());
       
       GridBagConstraints gbc = new GridBagConstraints();
       
       rPTLabel = new JLabel("Enter keyword to search");
       gbc.anchor = GridBagConstraints.FIRST_LINE_START;
       gbc.fill = GridBagConstraints.VERTICAL;
       gbc.gridx = 0;
       gbc.gridy = 0;
       rPT.add(rPTLabel,gbc);
       rPTField = new JTextField(10);
       gbc.anchor = GridBagConstraints.FIRST_LINE_START;
       gbc.fill = GridBagConstraints.VERTICAL;
       gbc.gridx = 0;
       gbc.gridy = 1;
       rPT.add(rPTField,gbc);
       rPTBtn = new JButton("Search");
       gbc.anchor = GridBagConstraints.FIRST_LINE_START;
       gbc.fill = GridBagConstraints.VERTICAL;
       gbc.gridx = 0;
       gbc.gridy = 2;
       rPT.add(rPTBtn,gbc);
       rPTBtn.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e) {
               
               try {
                   String keyword = rPTField.getText();
               //System.out.println(keyword);
               ks = new KeywordSearch(currentFile, keyword);
              if(ks.getNumberOfMarches() > 0){
                for(int i = 0; i < ks.getListOfFiles().size(); i++){
                    nlp = new NLP(currentFile,ks.getListOfFiles().get(i));
                    //new Plot(keyword, nlp.getNamesList());
                }
            }
               if(ks.getNumberOfMarches()==0){
                   JOptionPane.showMessageDialog(SearchPanel.this,
    "OOOps! There is no Match in Directory");
               }
               } catch (Exception ex) {
                   System.out.println("OOOps! There is no file!");

               }
               
           }
       });
       
       rPB = new JPanel();
       rPB.setBackground(new Color(255,255,255));
       
       splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,rPT,rPB);
       splitPane1.setDividerLocation(125);
       rightPanel.add(splitPane1);
       
       splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftPanel,rightPanel);
       splitPane.setOneTouchExpandable(true);
       splitPane.setDividerLocation(200);
       add(splitPane);
       
            gui = new JPanel(new BorderLayout(3,3));
            gui.setBorder(new EmptyBorder(5,5,5,5));

            fileSystemView = FileSystemView.getFileSystemView();
            desktop = Desktop.getDesktop();

            
            
            table = new JTable();
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setAutoCreateRowSorter(true);
            table.setShowVerticalLines(false);

            listSelectionListener = new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent lse) {
                    int row = table.getSelectionModel().getLeadSelectionIndex();
                    setFileDetails( ((FileTableModel)table.getModel()).getFile(row) );
                }
            };
            table.getSelectionModel().addListSelectionListener(listSelectionListener);
            JScrollPane tableScroll = new JScrollPane(table);
            rPB.add(tableScroll);
            
            // the File tree
            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
            treeModel = new DefaultTreeModel(root);

            TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent tse){
                    DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
                    showChildren(node);
                    setFileDetails((File)node.getUserObject());
                }
            };

            // show the file system roots.
            File[] roots = fileSystemView.getRoots();
            for (File fileSystemRoot : roots) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
                root.add( node );
                File[] files = fileSystemView.getFiles(fileSystemRoot, true);
                for (File file : files) {
                    if (file.isDirectory()) {
                        node.add(new DefaultMutableTreeNode(file));
                    }
                }
                //
            }

            tree = new JTree(treeModel);
            tree.setRootVisible(false);
            tree.addTreeSelectionListener(treeSelectionListener);
            tree.setCellRenderer(new FileTreeCellRenderer());
            tree.expandRow(0);
            JScrollPane treeScroll = new JScrollPane(tree);

            // as per trashgod tip
            tree.setVisibleRowCount(15);

            Dimension preferredSize = treeScroll.getPreferredSize();
            Dimension widePreferred = new Dimension(
                200,
                (int)preferredSize.getHeight());
            treeScroll.setPreferredSize( widePreferred );

            // details for a File
            JPanel fileMainDetails = new JPanel(new BorderLayout(4,2));
            fileMainDetails.setBorder(new EmptyBorder(0,6,0,6));

            JPanel fileDetailsLabels = new JPanel(new GridLayout(0,1,2,2));
            fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

            JPanel fileDetailsValues = new JPanel(new GridLayout(0,1,2,2));
            fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);

            fileDetailsLabels.add(new JLabel("File!", JLabel.TRAILING));
            fileName = new JLabel();
            fileDetailsValues.add(fileName);
            fileDetailsLabels.add(new JLabel("Path/name!", JLabel.TRAILING));
            path = new JTextField(5);
            path.setEditable(false);
            fileDetailsValues.add(path);
            fileDetailsLabels.add(new JLabel("Last Modified!", JLabel.TRAILING));
            date = new JLabel();
            fileDetailsValues.add(date);
            fileDetailsLabels.add(new JLabel("File size!", JLabel.TRAILING));
            size = new JLabel();
            fileDetailsValues.add(size);
            fileDetailsLabels.add(new JLabel("Type!", JLabel.TRAILING));

            JPanel flags = new JPanel(new FlowLayout(FlowLayout.LEADING,4,0));

            isDirectory = new JRadioButton("Directory!");
            flags.add(isDirectory);

            isFile = new JRadioButton("File!");
            flags.add(isFile);
            fileDetailsValues.add(flags);

            JToolBar toolBar = new JToolBar();
            // mnemonics stop working in a floated toolbar
            toolBar.setFloatable(false);

            JButton locateFile = new JButton("Locate");
            locateFile.setMnemonic('l');

            locateFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        System.out.println("Locate: " + currentFile.getParentFile());
                        desktop.open(currentFile.getParentFile());
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                    gui.repaint();
                }
            });
            toolBar.add(locateFile);

            openFile = new JButton("Open");
            openFile.setMnemonic('o');

            openFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        System.out.println("Open: " + currentFile);
                        desktop.open(currentFile);
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                    gui.repaint();
                }
            });
            toolBar.add(openFile);

            editFile = new JButton("Edit");
            editFile.setMnemonic('e');
            editFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        desktop.edit(currentFile);
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                }
            });
            toolBar.add(editFile);

            printFile = new JButton("Print");
            printFile.setMnemonic('p');
            printFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        desktop.print(currentFile);
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                }
            });
            toolBar.add(printFile);

            // Check the actions are supported on this platform!
            openFile.setEnabled(desktop.isSupported(Desktop.Action.OPEN));
            editFile.setEnabled(desktop.isSupported(Desktop.Action.EDIT));
            printFile.setEnabled(desktop.isSupported(Desktop.Action.PRINT));
            
            isSel = new JCheckBox("Select  ");
            
            flags.add(new JLabel("::  Flags"));
            readable = new JCheckBox("Read  ");
            readable.setMnemonic('a');
            flags.add(readable);
            
            
            writable = new JCheckBox("Write  ");
            writable.setMnemonic('w');
            flags.add(writable);

            executable = new JCheckBox("Execute");
            executable.setMnemonic('x');
            flags.add(executable);

            int count = fileDetailsLabels.getComponentCount();
            for (int ii=0; ii<count; ii++) {
                fileDetailsLabels.getComponent(ii).setEnabled(false);
            }

            count = flags.getComponentCount();
            for (int ii=0; ii<count; ii++) {
                flags.getComponent(ii).setEnabled(false);
            }

            JPanel fileView = new JPanel(new BorderLayout(3,3));

            fileView.add(toolBar,BorderLayout.NORTH);
            fileView.add(fileMainDetails,BorderLayout.CENTER);

      
            leftPanel.add(treeScroll);
            rPB.add(toolBar);
            
            JPanel simpleOutput = new JPanel(new BorderLayout(3,3));
            progressBar = new JProgressBar();
            simpleOutput.add(progressBar, BorderLayout.EAST);
            progressBar.setVisible(false);

            gui.add(simpleOutput, BorderLayout.SOUTH);
    }
    

    public void showRootFile() {
        // ensure the main files are displayed
        tree.setSelectionInterval(0,0);
    }

    private TreePath findTreePath(File find) {
        for (int ii=0; ii<tree.getRowCount(); ii++) {
            TreePath treePath = tree.getPathForRow(ii);
            Object object = treePath.getLastPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)object;
            File nodeFile = (File)node.getUserObject();

            if (nodeFile==find) {
                return treePath;
            }
        }
        // not found!
        return null;
    }

    private void showErrorMessage(String errorMessage, String errorTitle) {
        JOptionPane.showMessageDialog(
            gui,
            errorMessage,
            errorTitle,
            JOptionPane.ERROR_MESSAGE
            );
    }

    private void showThrowable(Throwable t) {
        t.printStackTrace();
        JOptionPane.showMessageDialog(
            gui,
            t.toString(),
            t.getMessage(),
            JOptionPane.ERROR_MESSAGE
            );
        gui.repaint();
    }

    /** Update the table on the EDT */
    private void setTableData(final File[] files) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (fileTableModel==null) {
                    fileTableModel = new FileTableModel();
                    table.setModel(fileTableModel);
                }
                table.getSelectionModel().removeListSelectionListener(listSelectionListener);
                fileTableModel.setFiles(files);
                table.getSelectionModel().addListSelectionListener(listSelectionListener);
                if (!cellSizesSet) {
                    //Icon icon = fileSystemView.getSystemIcon(files[0]);

                    // size adjustment to better account for icons
                    //table.setRowHeight( icon.getIconHeight()+rowIconPadding );

                    setColumnWidth(0,-1);
                    setColumnWidth(1,-1);
                    setColumnWidth(2,60);
                    setColumnWidth(3,-1);
                    table.getColumnModel().getColumn(3).setMaxWidth(120);
                    setColumnWidth(4,-1);
                    setColumnWidth(5,-1);
                    setColumnWidth(6,-1);
                    setColumnWidth(7,-1);
                    setColumnWidth(8,-1);
                    setColumnWidth(9,-1);

                    cellSizesSet = true;
                }
            }
        });
    }

    private void setColumnWidth(int column, int width) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (width<0) {
            // use the preferred width of the header..
            JLabel label = new JLabel( (String)tableColumn.getHeaderValue() );
            Dimension preferred = label.getPreferredSize();
            // altered 10->14 as per camickr comment.
            width = (int)preferred.getWidth()+14;
        }
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }

    /** Add the files that are contained within the directory of this node.
    Thanks to Hovercraft Full Of Eels for the SwingWorker fix. */
    private void showChildren(final DefaultMutableTreeNode node) {
        tree.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
                File file = (File) node.getUserObject();
                if (file.isDirectory()) {
                    File[] files = fileSystemView.getFiles(file, true); //!!
                    if (node.isLeaf()) {
                        for (File child : files) {
                            if (child.isDirectory()) {
                                publish(child);
                            }
                        }
                    }
                    setTableData(files);
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
                for (File child : chunks) {
                    node.add(new DefaultMutableTreeNode(child));
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                tree.setEnabled(true);
            }
        };
        worker.execute();
    }

    /** Update the File details view with the details of this File. */
    private void setFileDetails(File file) {
        currentFile = file;
        Icon icon = fileSystemView.getSystemIcon(file);
        fileName.setIcon(icon);
        fileName.setText(fileSystemView.getSystemDisplayName(file));
        //path.setText(file.getPath());
        date.setText(new Date(file.lastModified()).toString());
        size.setText(file.length() + " bytes");
        readable.setSelected(file.canRead());
        writable.setSelected(file.canWrite());
        executable.setSelected(file.canExecute());
        isDirectory.setSelected(file.isDirectory());

        isFile.setSelected(file.isFile());

        JFrame f = (JFrame)gui.getTopLevelAncestor();
        if (f!=null) {
            f.setTitle(
                APP_TITLE +
                " :: " +
                fileSystemView.getSystemDisplayName(file) );
        }

        gui.repaint();
    }


}

/** A TableModel to hold File[]. */
class FileTableModel extends AbstractTableModel {

    private File[] files;
    private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private String[] columns = {
        "Select",
        "Icon",
        "File",
        "Size",
        "Last Modified",
        "R",
        "W",
        "E",
        "D",
        "F",
    };

    FileTableModel() {
        this(new File[0]);
    }

    FileTableModel(File[] files) {
        this.files = files;
    }

    public Object getValueAt(int row, int column) {
        File file = files[row];
        switch (column) {
            case 0:
                return false ;
            case 1:
                return fileSystemView.getSystemIcon(file);
            case 2:
                return fileSystemView.getSystemDisplayName(file);
            case 3:
                return file.length();
            case 4:
                return file.lastModified();
            case 5:
                return file.canRead();
            case 6:
                return file.canWrite();
            case 7:
                return file.canExecute();
            case 8:
                return file.isDirectory();
            case 9:
                return file.isFile();
            default:
                System.err.println("Logic Error");
        }
        return "";
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return Boolean.class;
            case 1:
                return ImageIcon.class;
            case 3:
                return Long.class;
            case 4:
                return Date.class;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return Boolean.class;
        }
        return String.class;
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    public int getRowCount() {
        return files.length;
    }

    public File getFile(int row) {
        return files[row];
    }

    public void setFiles(File[] files) {
        this.files = files;
        fireTableDataChanged();
    }
    public boolean isCellEditable(int row, int col){
        switch(col){
            case 0:
                return true;
            default:
                return false;
        }
    }
}

/** A TreeCellRenderer for a File. */
class FileTreeCellRenderer extends DefaultTreeCellRenderer {

    private FileSystemView fileSystemView;

    private JLabel label;

    FileTreeCellRenderer() {
        label = new JLabel();
        label.setOpaque(true);
        fileSystemView = FileSystemView.getFileSystemView();
    }

    @Override
    public Component getTreeCellRendererComponent(
        JTree tree,
        Object value,
        boolean selected,
        boolean expanded,
        boolean leaf,
        int row,
        boolean hasFocus) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        File file = (File)node.getUserObject();
        label.setIcon(fileSystemView.getSystemIcon(file));
        label.setText(fileSystemView.getSystemDisplayName(file));
        label.setToolTipText(file.getPath());

        if (selected) {
            label.setBackground(backgroundSelectionColor);
            label.setForeground(textSelectionColor);
        } else {
            label.setBackground(backgroundNonSelectionColor);
            label.setForeground(textNonSelectionColor);
        }

        return label;
    }    
    
}

class KeywordSearch {
    private int count = 0;
    private ArrayList<String> list;
    public KeywordSearch(File dir, String keyword) throws IOException{
        if(dir.exists()) // Directory exists then proceed.
    	{ 
    	  Scanner in  = new Scanner(System.in);
    	  Pattern p = Pattern.compile(keyword); // keyword = keyword to search in files.
    	  list = new ArrayList<String>(); // list of files.
          
    	  for(File f : dir.listFiles())
    	  {
    	    if(!f.isFile()) continue;
    	    try
    	    {
    	      FileInputStream fis = new FileInputStream(f);
    	      byte[] data = new byte[fis.available()];
    	      fis.read(data);
    	      String text = new String(data);
    	      Matcher m = p.matcher(text);
              
    	      if(m.find())
    	      {
    	        list.add(f.getName()); // add file to found-keyword list.
                count++;
    	      }
    	      fis.close();
    	    } 
    	    catch(Exception e)
    	    {
    	      System.out.print("\n\t Error processing file : "+f.getName());
    	    }

    	  }
    	  System.out.println("\n\t List : "+list); // list of files containing keyword.

          
          System.out.println("Number of Matches: "+ count);

    	} // IF directory exists then only process.
    	else
    	{
    	  System.out.print("\n Directory doesn't exist.");
    	}
    }
    public int getNumberOfMarches(){
        return count;
    }
    public ArrayList<String> getListOfFiles(){
        return list;
    }
}