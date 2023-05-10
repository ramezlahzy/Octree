package Octree;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class OctreeNode<T> {
    Range rangeX, rangeY, rangeZ;
    OctreeNode[] children;
    private ArrayList<Point<T>> data;
    static int maxCapacity;

    public OctreeNode(Object minX, Object maxX, Object minY, Object maxY, Object minZ, Object maxZ) throws IOException {
        rangeX = new Range(minX, maxX);
        rangeY = new Range(minY, maxY);
        rangeZ = new Range(minZ, maxZ);
        data = new ArrayList<>();
        Properties prop = new Properties();
        String fileName = "src/main/java/resources/TablesMetaData/DBApp.config";
        InputStream is = new FileInputStream(fileName);
        prop.load(is);
        maxCapacity = Integer.parseInt(prop.getProperty("MaximumEntriesinOctreeNode"));
    }

    public void insert(Object x, Object y, Object z, T... data) throws DBAppException, IOException {
        if (children == null) {
            boolean found = false;
            ArrayList<Point<T>> list = getData();
            for (int i = 0; i < list.size(); i++) {
                Point<T> p = list.get(i);
                if (p.x.equals( x) && p.y.equals( y) && p.z.equals(z)) {
                    for (T datum : data) {
                        p.duplicates.add(datum);
                    }
                    return;
                }
            }
            Point p = new Point(x, y, z);
            p.addData(data[0]);

            this.data.add(p);
            if (this.data.size() > maxCapacity)
                split();
            return;
        }
        for (int i = 0; i < children.length; i++) {
            OctreeNode child = children[i];
            if (child.rangeX.contains(x) && child.rangeY.contains(y) && child.rangeZ.contains(z)) {
                child.insert(x, y, z, data);
                return;
            }
        }
        throw new DBAppException("invalid Range");
    }

    private void split() throws DBAppException, IOException {
        children = new OctreeNode[8];
        Range[] rangesX = rangeX.split(), rangesY = rangeY.split(), rangesZ = rangeZ.split();
        for (int i = 0; i < children.length; i++)
            children[i] = new OctreeNode<T>(rangesX[i >> 2 & 1].min, rangesX[i >> 2 & 1].max, rangesY[i >> 1 & 1].min, rangesY[i >> 1 & 1].max, rangesZ[i & 1].min, rangesZ[i & 1].max);
        here:
        for (Point<T> p : data) {
            for (OctreeNode child : children)
                if (child.rangeX.contains(p.x) && child.rangeY.contains(p.y) && child.rangeZ.contains(p.z)) {
                    for (T data : p.getDuplicates())
                        child.insert(p.x, p.y, p.z, data);
                    continue here;
                }
            throw new DBAppException("invalid Range " + p.x + " " + p.y + " " + p.z);
        }
        data.clear();
    }

    public ArrayList<String> search(Object x, Object y, Object z) {
        if (children == null) {
            ArrayList<String> result = new ArrayList<>();
            for (Point p : data)
                if (p.x.equals(x) && p.y.equals(y) && p.z.equals(z))
                    result.addAll(p.getDuplicates());
            return result;
        }
        for (OctreeNode child : children)
            if (child.rangeX.contains(x) && child.rangeY.contains(y) && child.rangeZ.contains(z))
                return child.search(x, y, z);
        return new ArrayList<>();
    }

    public void delete(Object x, Object y, Object z) {
        if (children == null) {
            for (int i = 0; i < data.size(); i++) {
                Point p = data.get(i);
                if (p.x.equals(x) && p.y.equals(y) && p.z.equals(z)) {
                    data.remove(i);
                    i--;
                }
            }
            return;
        }
        for (OctreeNode child : children)
            if (child.rangeX.contains(x) && child.rangeY.contains(y) && child.rangeZ.contains(z))
                child.delete(x, y, z);

        int points = 0;
        for (OctreeNode child : children)
            if (child.children != null)
                return;
            else
                points += child.data.size();
        if (points <= maxCapacity) {
            data = new ArrayList<>();
            for (int i = 0; i < children.length; i++) {
                OctreeNode child = children[i];
                ArrayList<Point> arr = child.getData();
                for (Point p : arr)
                    data.add(p);
            }
            children = null;
        }
    }

    public ArrayList<Point<T>> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "OctreeNode{" +
                "rangeX=" + rangeX +
                ", rangeY=" + rangeY +
                ", rangeZ=" + rangeZ +
                ", data=" + data +
                ", children=\n" + Arrays.toString(children) +
                "}\n";
    }

    public void print() {
        System.out.println(this);
    }

    public static void main(String[] args) throws IOException, DBAppException {
        OctreeNode<String> octreeNode = new OctreeNode<>(0, 1000, "a", "z", new Date("1/2/2000"), new Date("1/2/2030"));
        octreeNode.insert(1, "a", new Date("1/2/2002"), "ramez");
        octreeNode.insert(700, "z", new Date("1/2/2009"), "nashaat");
        octreeNode.insert(700, "z", new Date("1/2/2009"), "lahzy");
        System.out.println("before");
        System.out.println(octreeNode.search(700, "z", new Date("1/2/2009")));
        octreeNode.print();
        octreeNode.delete(700, "z", new Date("1/2/2009"));

        System.out.println("after");
        octreeNode.delete(700, "z", new Date("1/2/2009"));
        octreeNode.print();


    }
}
