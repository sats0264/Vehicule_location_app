package location.app.vehicule_location_app.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "T_Client")
public class Client{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_client")
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private int pointFidelite;

    @OneToMany(mappedBy = "client")
    private List<Reservation> reservations = new ArrayList<Reservation>();
}
