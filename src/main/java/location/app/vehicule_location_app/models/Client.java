package location.app.vehicule_location_app.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "T_Client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_client")
    private int id;

    private String nom;
    private String prenom;
    private String email;
    private String adresse;
    private String telephone;
    private String password;
    private int pointFidelite;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<NotificationReception> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CarteBancaire> cartesBancaires = new ArrayList<>();

    public Client() {}
    public Client(int id) {
        this.id = id;
    }
    public Client(int id, String nom, String prenom, String email,
                  String adresse, String telephone, String password) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.adresse = adresse;
        this.telephone = telephone;
        this.password = password;
        this.pointFidelite = 0;
    }
    public Client(String nom, String prenom, String email,
                  String adresse, String telephone, String password) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.adresse = adresse;
        this.telephone = telephone;
        this.password = password;
        this.pointFidelite = 0;
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setClient(this);
    }

    public void addNotification(NotificationReception notification) {
        notifications.add(notification);
        notification.setClient(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id == client.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void addCarteBancaire(CarteBancaire carte) {
        cartesBancaires.add(carte);
        carte.setClient(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPointFidelite() {
        return pointFidelite;
    }

    public void setPointFidelite(int pointFidelite) {
        this.pointFidelite = pointFidelite;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<NotificationReception> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationReception> notifications) {
        this.notifications = notifications;
    }

    public List<CarteBancaire> getCartesBancaires() {
        return cartesBancaires;
    }

    public void setCartesBancaires(List<CarteBancaire> cartesBancaires) {
        this.cartesBancaires = cartesBancaires;
    }
}
