package asw.rest.productmanager;

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
	private String name;

    @XmlElement
	private String country;

	public Artist() {
	}

    public Artist(int id, String name, String country) {
		this.id = id;
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
		return "Product[" +
			"id=" + id +
			", name=" + name +
			", country=" + country +
			"]";
	}

}
