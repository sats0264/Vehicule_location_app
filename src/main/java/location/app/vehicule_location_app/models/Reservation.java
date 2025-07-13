package location.app.vehicule_location_app.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "T_Reservation")
public class Reservation{

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "facture_id")
    private Facture facture;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation")
    private int id;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    @Enumerated(EnumType.STRING)
    private Statut statut;

    @ManyToOne
    private Client client = null;

    @OneToMany(mappedBy = "classe", cascade = CascadeType.PERSIST)
    private List<Vehicule> vehicules  = new ArrayList<Vehicule>();
    @OneToMany(mappedBy = "classe", cascade = CascadeType.PERSIST)
    private List<Chauffeur> chauffeurs = new ArrayList<Chauffeur>();


}
