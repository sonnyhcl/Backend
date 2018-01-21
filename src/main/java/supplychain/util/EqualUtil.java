package supplychain.util;

import supplychain.entity.Location;

public class EqualUtil {
    public static boolean IsEqual(Location a, Location b) {
        if (a.getX_coor().equals(b.getX_coor()) && a.getY_coor().equals(b.getY_coor())) {
            return true;
        }
        return false;
    }
}
