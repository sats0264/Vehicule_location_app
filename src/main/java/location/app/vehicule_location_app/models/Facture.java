package location.app.vehicule_location_app.models;

import jakarta.persistence.*;

import java.time.temporal.ChronoUnit;

@Entity(name = "T_Facture")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facture")
    private int id;

    private double montant;

    @OneToOne
    @JoinColumn(name = "reservation_id", unique = true)
    private Reservation reservation;

    public Facture() {}

    public Facture(int id, double montant) {
        this.setId(id);
        this.setMontant(montant);
    }

    public Facture(Reservation reservation) {
        this.setReservation(reservation);
        this.setMontant(reservation.getVehicules().stream()
                .mapToDouble(Vehicule::getTarif)
                .sum());
    }

    public int getId() {
        return id;
    }
    public double getMontant() {
        return montant;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setMontant(double montant) {
        this.montant = montant;
    }
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        if (reservation != null) {
            reservation.setFacture(this);
        }
    }
}
