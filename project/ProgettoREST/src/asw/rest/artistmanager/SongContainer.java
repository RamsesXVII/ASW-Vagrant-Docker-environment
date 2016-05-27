package asw.rest.artistmanager;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.persistence.*;

import java.net.URI;
import java.util.*;

import javax.ejb.*;



@Stateless
@Path("/songs")
public class SongContainer {
	@Context
	private UriInfo uriInfo;

    @PersistenceContext(unitName="artist-manager-pu")
    private EntityManager em;

    public SongContainer() { }

    /* GET: Restituisce la collezione di tutte le canzoni */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Collection<Song> getSong() {
		try {
			Collection<Song> songs = em.createQuery("SELECT p FROM Song p").getResultList();
			if (songs == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
			} else {
				return songs;
			}
		} catch (Exception e) {
			String errorMessage = "Error while finding all songs: " + e.getMessage();
    		throw new WebApplicationException(
				Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				                        .entity(errorMessage).type("text/plain").build());
		}
    }
    
    /* GET: Restituisce la collezione di tutte le canzoni di un artista */
    @Path("getArtistSongs/{id}")
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public Collection<Song> getSong(
			@PathParam("id") int idArtista) {
		try {
			Query query = this.em.createNamedQuery("findSongsByArtistId"); 
			query.setParameter("id", new Long(idArtista));
			Collection<Song> songs = query.getResultList();
			if (songs == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
			} else {
				return songs;
			}
		} catch (Exception e) {
			String errorMessage = "Error while finding all songs: " + e.getMessage();
    		throw new WebApplicationException(
				Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				                        .entity(errorMessage).type("text/plain").build());
		}
    }

    /* POST: Aggiunge una nuova canzone
     * sulla base di un form con campi id, description e price */
    @POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createSong(
			@FormParam("idArtista") int idArtista,
			@FormParam("name") String name,
			@FormParam("year") String year) {
		/* fa questa ricerca per evitare che venga sollevata un'eccezione al momento del commit */
    	Song p = null;
    	Artist artist = em.find(Artist.class, idArtista);
		if (artist!=null&& ( (name!=null && year!=null) && (!name.isEmpty() && !year.isEmpty()) )) {
	    	p = new Song(artist,name, year);
			try {
				em.persist(p);
	            return Response.created(URI.create("/" + p.getId())).entity(p).build();
			} catch (Exception e) {
	    		String errorMessage = "Error while creating Song " + p.toString() + ": " + e.getMessage();
	    		throw new WebApplicationException(
					Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				                        .entity(errorMessage).type("text/plain").build());
			}
		} else {
    		String errorMessage = "Error while creating Song: the artist doesn't exist";
    		throw new WebApplicationException(
			Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			                        .entity(errorMessage).type("text/plain").build());
		}
    }
}