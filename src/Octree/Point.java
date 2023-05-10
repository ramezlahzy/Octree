package Octree;

import java.util.ArrayList;

public class Point<T>{
    Object x,y,z;
    ArrayList<T> duplicates;
    public Point(Object x, Object y, Object z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.duplicates = new ArrayList<>();
    }
    public void addData(T data){
        this.duplicates.add(data);
    }
    public ArrayList<T> getDuplicates(){
        return duplicates;
    }

    @Override
    public String toString() {
        return duplicates.toString();
    }
}