package location.app.vehicule_location_app.models;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "T_Vehicule")
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehicule")
    private int id;

    @ManyToMany(mappedBy = "vehicules")
    private List<Reservation> reservations = new ArrayList<>();

    private String immatriculation;
    private String marque;
    private String modele;
    private double tarif;
    @Enumerated(EnumType.STRING)
    private Statut statut;
    private String photo;

    public Vehicule() {}

    public Vehicule(String immatriculation, String marque, String modele, double tarif, String photo) {
        this.immatriculation = immatriculation;
        this.marque = marque;
        this.modele = modele;
        this.tarif = tarif;
        this.photo = photo;
        this.setStatut(Statut.DISPONIBLE);
    }

    public Vehicule(int id, String marque, String modele, double tarif,
                    Statut statut, String immatriculation, String photo) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.tarif = tarif;
        this.statut = statut;
        this.immatriculation = immatriculation;
        this.photo = photo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicule vehicule = (Vehicule) o;
        return Objects.equals(immatriculation, vehicule.immatriculation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(immatriculation);
    }

    @Override
    public String toString() {
        return marque + " " + modele + " (" + immatriculation + ")";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public double getTarif() {
        return tarif;
    }

    public void setTarif(double tarif) {
        this.tarif = tarif;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
