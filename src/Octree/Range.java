package Octree;


import java.util.Date;

public class Range {
    Object min, max;

    public Range(Object min, Object max) {
        this.min = min;
        this.max = max;

    }

    public Range[] split() throws DBAppException {
        Object mid = null;
        if (min instanceof String)
            mid = getMiddleString((String) min, (String) max, Math.min(((String) max).length(), ((String) min).length()));
        else if (min instanceof Integer || min instanceof Double)
            mid = (Integer) min + (Integer) max / 2;
        else if (min instanceof Date)
            mid = new Date((((Date) min).getTime() + ((Date) max).getTime()) / 2);
        else
            throw new DBAppException("Unsupported data type");
        Range[] ranges = new Range[2];
        ranges[0] = new Range(min, mid);
        ranges[1] = new Range(mid, max);
        return ranges;
    }

    public boolean contains(Object x) {
        if (x instanceof String)
            return ((String) x).compareTo((String) min) >= 0 && ((String) x).compareTo((String) max) <= 0;
        else if (x instanceof Integer)
            return (Integer) x >= (Integer) min && (Integer) x <= (Integer) max;
        else if (x instanceof Double)
            return (Double) x >= (Double) min && (Double) x <= (Double) max;
        else if (x instanceof Date)
            return ((Date) x).compareTo((Date) min) >= 0 && ((Date) x).compareTo((Date) max) <= 0;
        else
            return false;
    }

    static String getMiddleString(String S, String T, int N) {
        int[] a1 = new int[N + 1];
        for (int i = 0; i < N; i++)
            a1[i + 1] = (int) S.charAt(i) - 97 + (int) T.charAt(i) - 97;
        for (int i = N; i >= 1; i--) {
            a1[i - 1] += a1[i] / 26;
            a1[i] %= 26;
        }
        for (int i = 0; i <= N; i++) {
            if ((a1[i] & 1) != 0)
                if (i + 1 <= N)
                    a1[i + 1] += 26;
            a1[i] = a1[i] / 2;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= N; i++)
            sb.append((char) (a1[i] + 97));
        return sb.toString();
    }

    public static void main(String[] args) {
        Date min = new Date("12/12/2012");
        Date max = new Date("12/12/2013");
        Date mid = new Date((((Date) min).getTime() + ((Date) max).getTime()) / 2);
//        System.out.println(mid);
    }

    @Override
    public String toString() {
        return  min +
                "-> " + max ;
    }
}
