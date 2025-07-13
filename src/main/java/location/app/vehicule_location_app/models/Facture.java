package location.app.vehicule_location_app.models;

import jakarta.persistence.*;

@Entity(name = "T_Facture")
public class Facture{

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facture")
    private int id;
    private double montant;

}
