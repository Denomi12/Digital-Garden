package si.um.feri.maprri.raster;

import com.badlogic.gdx.utils.Array;

public class GameManager {
    private static Array<Garden> gardens;

    public static void setGardens(Array<Garden> gardens_list) {
        gardens = gardens_list;
    }

    public static Array<Garden> getGardens() {
        return gardens;
    }
}
