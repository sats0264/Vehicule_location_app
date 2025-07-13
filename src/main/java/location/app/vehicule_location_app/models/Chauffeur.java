package location.app.vehicule_location_app.models;

import jakarta.persistence.*;

@Entity(name = "T_Chauffeur")
public class Chauffeur{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chauffeur")
    private int id;
    @ManyToOne
    private Reservation reservation = null;
    private String nom;
    private String prenom;

    @Enumerated(EnumType.STRING)
    private Statut statut;
    private String photo;
}
