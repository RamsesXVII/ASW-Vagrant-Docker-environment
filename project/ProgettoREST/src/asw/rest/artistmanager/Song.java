package asw.rest.artistmanager;
import javax.persistence.*;
import javax.xml.bind.annotation.*;


@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQuery(name= "findSongsByArtistId", query="SELECT s FROM Song s Where s.artist.id= :id")

public class Song implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlAttribute
	private int id;

    @XmlElement
	private String name;

    @XmlElement
	private String year;
    
	@ManyToOne
	private Artist artist;

	public Song() {
	}

    public Song(Artist artist, String name, String year) {
		this.artist=artist;
		this.name = name;
		this.year = year;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	
	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	@Override
	public String toString() {
		return "Song[" +
			"id=" + id +
			", name=" + name +
			", year=" + year +
			"]";
	}
}
