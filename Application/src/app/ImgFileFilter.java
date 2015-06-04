package app;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ImgFileFilter extends FileFilter{

    public boolean accept(File f) {
        String name = f.getName();
        String extension = Utils.getFileExtension(name);
        if(extension==null){
            return false;
        }
        if(extension.equals("img")){
            return true;
        }
        return false;
    }

    public String getDescription() {
        
        return "Image Files Filter (*.img)";
    }
    
}
