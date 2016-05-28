package asw.rest.artistmanager;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/")
public class ArtistManagerApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(ArtistResource.class);
        classes.add(SongContainer.class);
        classes.add(SongResource.class);
        classes.add(ArtistContainer.class);
        return classes;
    }
}
