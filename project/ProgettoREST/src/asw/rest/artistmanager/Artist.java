package asw.rest.artistmanager;


import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.*;


@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Artist implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlAttribute
	private int id;

    @XmlElement
    @Column(nullable=false)
	private String name;

    @XmlElement
    @Column(nullable=false)
	private String country;
    
    @OneToMany(mappedBy="artist")
	private List<Song> song;

	public Artist() {
	}

    public Artist(String name, String country) {
		this.name = name;
		this.country = country;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "Artist[" +
			"id=" + id +
			", name=" + name +
			", country=" + country +
			"]";
	}
	
	public List<Song> getSong() {
		return song;
	}
	public void setSong(List<Song> song) {
		this.song = song;
	}

}
