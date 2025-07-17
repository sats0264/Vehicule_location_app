package location.app.vehicule_location_app.models;

import jakarta.persistence.*;

@Entity(name = "T_Chauffeur")
public class Chauffeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chauffeur")
    private int id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = true)
    private Reservation reservation;

    private String nom;
    private String prenom;
    private String telephone;
    @Enumerated(EnumType.STRING)
    private Statut statut;
    private String photo;

    public Chauffeur(int id) {
        this.id = id;
    }
    public Chauffeur(int id, String nom, String prenom, Statut statut, String photo) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.statut = statut;
        this.photo = photo;
    }

    public Chauffeur(String nom, String prenom, String telephone, String photo) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.photo = photo;
        this.statut = Statut.DISPONIBLE;
    }

    public Chauffeur() {}

    public int getId() {
        return id;
    }
    public String getNom() {
        return nom;
    }
    public String getPrenom() {
        return prenom;
    }
    public Statut getStatut() {
        return statut;
    }
    public String getPhoto() {
        return photo;
    }
    public Reservation getReservation() {
        return reservation;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    public void setStatut(Statut statut) {
        this.statut = statut;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
