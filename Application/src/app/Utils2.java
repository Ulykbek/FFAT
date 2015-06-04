package app;

public class Utils2 {
    public static String getNumberOne(String name){
        int pointIndex = name.lastIndexOf(".");
        
        if(pointIndex==-1){
            return null;
        }
        
        if (pointIndex==name.length()-1) {
            return null;
        }
        
        
        return name.substring(1,pointIndex-1);
    }
    public static String getNumberTwo(String name){
        int pointIndex = name.lastIndexOf(".");
        int pointIndex2 = name.lastIndexOf(")");
        if(pointIndex==-1){
            return null;
        }
        
        if (pointIndex==name.length()-1) {
            return null;
        }
        
        
        return name.substring(pointIndex+1,pointIndex2);
    }
}
