package si.um.feri.mori.raster;

import com.badlogic.gdx.utils.Array;

public class Garden {
    public String _id;
    public String name;
    public String owner;
    public int width;
    public int height;
    public Array<Element> elements;
    public double latitude;
    public double longitude;
    public String location;
    public String createdAt;
    public String updatedAt;
    public int __v;

    public static class Element {
        public int x;
        public int y;
        public String type;
        public String crop;
        public String plantedDate;
        public String wateredDate;
        public String _id;
    }
}
