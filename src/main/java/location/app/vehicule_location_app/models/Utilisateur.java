package location.app.vehicule_location_app.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "T_Utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private int id;
    private String nom;
    private String prenom;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String login;
    private String password;
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<NotificationReception> notifications = new ArrayList<>();

    public Utilisateur() {}

    public Utilisateur(int id, String nom, String prenom,
                       Role role, String login, String password) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.login = login;
        this.password = password;
    }

    public void addNotification(NotificationReception notification) {
        notifications.add(notification);
        notification.setUtilisateur(this);
    }
    public boolean estAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean estEmploye() {
        return this.role == Role.EMPLOYEE;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Utilisateur that = (Utilisateur) obj;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public List<NotificationReception> getNotifications() {
        return notifications;
    }
    public void setNotifications(List<NotificationReception> notifications) {
        this.notifications = notifications;
    }
}