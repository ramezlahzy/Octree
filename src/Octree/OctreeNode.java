package Octree;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class OctreeNode<T> {
    Range rangeX, rangeY, rangeZ;
    OctreeNode[] children;
    private ArrayList<Point> data;
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
        maxCapacity = Integer.parseInt(prop.getProperty("MaximumEntriesOctreeNode"));
    }

    public void insert(Object x, Object y, Object z, String... data) throws DBAppException, IOException {
        if (children == null) {
            Point p = new Point(x, y, z);
            p.addData(data[0]);
            this.data.add(p);
            if (this.data.size() > maxCapacity)
                split();
            return;
        }
        children = new OctreeNode[8];
        for (int i = 0; i < children.length; i++) {
            OctreeNode child = children[i];
            if (child.rangeX.contains(x) && child.rangeY.contains(y) && child.rangeZ.contains(z)) {
                child.insert(x, y, z, data);
                break;
            }
        }
    }

    private void split() throws DBAppException, IOException {
        Range[] rangesX = rangeX.split(), rangesY = rangeY.split(), rangesZ = rangeZ.split();
        for (int i = 0; i < children.length; i++)
            children[i] = new OctreeNode<T>(rangesX[i >> 2 & 1].min, rangesX[i >> 2 & 1].max, rangesY[i >> 1 & 1].min, rangesY[i >> 1 & 1].max, rangesZ[i & 1].min, rangesZ[i & 1].max);
        for (Point p : data)
            here:for (OctreeNode child : children)
                if (child.rangeX.contains(p.x) && child.rangeY.contains(p.y) && child.rangeZ.contains(p.z)) {
                    for (String data : p.getDuplicates())
                        child.insert(p.x, p.y, p.z, data);
                    break here;
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

    public ArrayList<Point> getData() {
        return data;
    }

}
