package location.app.vehicule_location_app.models;

import jakarta.persistence.*;

@Entity(name = "T_Carte_Bancaire")
public class CarteBancaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carte_bancaire")
    private int id;

    private String numeroCarte;
    private int cvc;
    private String titulaire;
    private String dateExpiration;
    private double solde;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public CarteBancaire(int id) {
        this.id = id;
    }

    public CarteBancaire(String titulaire, String numeroCarte, int cvc, String dateExpiration, Client client, double solde) {
        this.titulaire = titulaire;
        this.numeroCarte = numeroCarte;
        this.cvc = cvc;
        this.dateExpiration = dateExpiration;
        this.client = client;
        this.solde = solde;
    }

    public CarteBancaire() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulaire() {
        return titulaire;
    }

    public void setTitulaire(String titulaire) {
        this.titulaire = titulaire;
    }

    public String getNumeroCarte() {
        return numeroCarte;
    }

    public void setNumeroCarte(String numeroCarte) {
        this.numeroCarte = numeroCarte;
    }

    public int getCvc() {
        return cvc;
    }

    public void setCvc(int cvc) {
        this.cvc = cvc;
    }

    public String getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(String dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public void crediter(double montant) {
		if(montant > 0) {
			solde += montant;
		}
	}
	
	public void debiter(double montant) {
		if(montant > 0) // le montant est-il valide ?
			{
			if(montant <= solde) // le solde est-il suffisant ? 
				{
				solde -= montant;
			}
		}
	}

    // Création d'une carte bancaire associée à un client (ex : client connecté)
    public static CarteBancaire creerUnNouveauCompte(String titulaire, String numeroCarte, int cvc, String dateExpiration, Client client, double solde) {
        return new CarteBancaire(titulaire, numeroCarte, cvc, dateExpiration, client, solde);
    }

}
