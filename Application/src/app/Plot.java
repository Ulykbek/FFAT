package app;

import java.util.ArrayList;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class Plot {
    public Plot(String keyword, ArrayList<String> list){
                
                
                Graph graph = new SingleGraph("PLOT");
                for(int i = 0; i < list.size(); i++){
                    System.out.println(keyword);
                    System.out.println(list.get(i));
                    graph.addNode(list.get(i));
                    graph.addEdge(keyword,list.get(i),list.get(i+1));
                
                }
                
//                graph.addNode("A" );
//                graph.addNode("B" );
//                graph.addNode("C" );
//                graph.addEdge("AB", "A", "B");
//                graph.addEdge("BC", "B", "C");
//                graph.addEdge("CA", "C", "A");
                
                graph.display();
    }
}
