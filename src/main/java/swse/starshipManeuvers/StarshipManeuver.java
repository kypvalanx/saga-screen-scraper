package swse.starshipManeuvers;

import swse.common.Copyable;
import swse.common.FoundryItem;

class StarshipManeuver extends FoundryItem<StarshipManeuver> implements Copyable<StarshipManeuver> {

    public StarshipManeuver(String name) {
        super(name, "forcePower");
    }

    public static StarshipManeuver create(String name) {
        return new StarshipManeuver(name);
    }

    @Override
    public StarshipManeuver copy() {
        return null;
    }
}
