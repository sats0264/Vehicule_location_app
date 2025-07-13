package location.app.vehicule_location_app.models;

import jakarta.persistence.*;

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
}