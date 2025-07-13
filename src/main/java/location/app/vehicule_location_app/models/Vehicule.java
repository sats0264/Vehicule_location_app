package location.app.vehicule_location_app.models;


import jakarta.persistence.*;

@Entity(name = "T_Vehicule")
public class Vehicule{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehicule")
    private int id;

    @ManyToOne
    private Reservation reservation = null;
    private String marque;
    private String modele;
    private double tarif;
    @Enumerated(EnumType.STRING)
    private Statut statut;
    private String immatriculation;
    private String photo;
}
