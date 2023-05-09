package Octree;

import java.util.ArrayList;

public class Point{
    Object x,y,z;
    ArrayList<String> duplicates;
    public Point(Object x, Object y, Object z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.duplicates = new ArrayList<>();
    }
    public void addData(String data){
        this.duplicates.add(data);
    }
    public ArrayList<String> getDuplicates(){
        return duplicates;
    }
}