package presentation;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.sql.*;


public class Interface1 extends JFrame {
    // Base de données
    private static final String DB_URL = "jdbc:mysql://localhost:3306/emploidutemps_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    // Composants de l'interface
    private JTextField nomEnseignantField, matriculeEnseignantField, contactEnseignantField;
    private JComboBox<String> classeComboBox, heureDebutField, jourComboBox;
    private JTextField  matiereField, enseignantField;
    private JButton chercherEnseignantButton, enregistrerEnseignantButton, modifierEnseignantButton, supprimerEnseignantButton;
    private JButton enregistrerSeanceButton, requetesButton;
    private JTable enseignantsTable, seancesTable;
    private DefaultTableModel enseignantsTableModel, seancesTableModel;

    public Interface1() {
        super("Gestion des Emplois du Temps");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel pour les formulaires
        JPanel formulairesPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        formulairesPanel.add(creerPanelEnseignants());
        formulairesPanel.add(creerPanelSeances());
        mainPanel.add(formulairesPanel, BorderLayout.NORTH);

        // Panel pour les tableaux
        JPanel tableauxPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        tableauxPanel.setBorder(BorderFactory.createTitledBorder("Données"));
        tableauxPanel.add(creerPanelTableEnseignants());
        tableauxPanel.add(creerPanelTableSeances());
        mainPanel.add(tableauxPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);

        // Chargement initial des données dans les tables
        chargerEnseignants();
        chargerSeances();
    }

    // Panel pour les informations des enseignants
    private JPanel creerPanelEnseignants() {
        JPanel enseignantPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        enseignantPanel.setBorder(BorderFactory.createTitledBorder("Gestion des Enseignants"));

        enseignantPanel.add(new JLabel("Nom :"));
        nomEnseignantField = new JTextField(20);
        enseignantPanel.add(nomEnseignantField);

        enseignantPanel.add(new JLabel("Matricule :"));
        matriculeEnseignantField = new JTextField(20);
        enseignantPanel.add(matriculeEnseignantField);

        enseignantPanel.add(new JLabel("Contact :"));
        contactEnseignantField = new JTextField(20);
        enseignantPanel.add(contactEnseignantField);

        chercherEnseignantButton = new JButton("CHERCHER");
        chercherEnseignantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chercherEnseignant();
            }
        });
        enseignantPanel.add(chercherEnseignantButton);

        enregistrerEnseignantButton = new JButton("ENREGISTRER");
        enregistrerEnseignantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enregistrerEnseignant();
            }
        });
        enseignantPanel.add(enregistrerEnseignantButton);

        modifierEnseignantButton = new JButton("MODIFIER");
        modifierEnseignantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifierEnseignant();
            }
        });
        enseignantPanel.add(modifierEnseignantButton);

        supprimerEnseignantButton = new JButton("SUPPRIMER");
        supprimerEnseignantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                supprimerEnseignant();
            }
        });
        enseignantPanel.add(supprimerEnseignantButton);

        return enseignantPanel;
    }

    // Panel pour les informations des séances
    private JPanel creerPanelSeances() {
        JPanel seancePanel = new JPanel(new GridLayout(8, 2, 10, 10));
        seancePanel.setBorder(BorderFactory.createTitledBorder("Gestion des Séances"));

        seancePanel.add(new JLabel("Classe :"));
        classeComboBox = new JComboBox<>(new String[] {"1er", "2eme", "3eme", "4eme", "5eme", "6eme"});
        //chargerClassesDansComboBox();
        seancePanel.add(classeComboBox);
        
        seancePanel.add(new JLabel("Matière :"));
        matiereField = new JTextField(20);
        seancePanel.add(matiereField);
        
        
        seancePanel.add(new JLabel("Jour :"));
        jourComboBox = new JComboBox<>(new String[] {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"});
        //chargerEnseignantsDansComboBox();
        seancePanel.add(jourComboBox);
        
        seancePanel.add(new JLabel("Heure :"));
        heureDebutField = new JComboBox<>(new String[] {"1ère H","2ème H","3ème H","4ème H","5ème H","6ème H","1ère et 2ème H","2ème et 3ème H",
        		  "3ème et 4ème H",
        		  "4ème et 5ème H",
        		  "5ème et 6ème H"
        		});
        //chargerMatieresDansComboBox();
        seancePanel.add(heureDebutField);
        
        seancePanel.add(new JLabel("Enseignant :"));
        enseignantField = new JTextField(20);
        seancePanel.add(enseignantField);
        

        

        

        enregistrerSeanceButton = new JButton("ENREGISTRER");
        enregistrerSeanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enregistrerSeance();
            }
        });
        seancePanel.add(enregistrerSeanceButton);

        requetesButton = new JButton("REQUETES");
        requetesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ouvrez l'interface 2 (Interface de requêtes)
                new Interface2();
            }
        });
        seancePanel.add(requetesButton);

        return seancePanel;
    }

    // Panel pour le tableau des enseignants
    private JPanel creerPanelTableEnseignants() {
        JPanel panel = new JPanel(new BorderLayout());
        enseignantsTableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Matricule", "Contact"}, 0);
        enseignantsTable = new JTable(enseignantsTableModel);
        JScrollPane scrollPane = new JScrollPane(enseignantsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // Panel pour le tableau des séances
    private JPanel creerPanelTableSeances() {
        JPanel panel = new JPanel(new BorderLayout());
        seancesTableModel = new DefaultTableModel(
                new String[]{"ID", "Classe", "Matière", "Enseignant", "Jour", "Heure"}, 0);
        seancesTable = new JTable(seancesTableModel);
        JScrollPane scrollPane = new JScrollPane(seancesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    

    // Fonction pour chercher un enseignant
    private void chercherEnseignant() {
        String nom = nomEnseignantField.getText();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM enseignants WHERE nom = ?")) {
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            if (rs.isBeforeFirst()) {
            	enseignantsTableModel.setRowCount(0);
                while (rs.next()) {
                	enseignantsTableModel.addRow(new Object[]{
                			rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("matricule"),
                            rs.getString("contact")
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "Aucun enseignant trouvé avec ce nom.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche de l'enseignant.");
        }
    }

    // Fonction pour enregistrer un enseignant
    private void enregistrerEnseignant() {
        String nom = nomEnseignantField.getText();
        String matricule = matriculeEnseignantField.getText();
        String contact = contactEnseignantField.getText();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO enseignants (nom, matricule, contact) VALUES (?, ?, ?)")) {
            stmt.setString(1, nom);
            stmt.setString(2, matricule);
            stmt.setString(3, contact);
            stmt.executeUpdate();
            chargerEnseignants(); // Recharger la table des enseignants
            JOptionPane.showMessageDialog(this, "Enseignant enregistré avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de l'enseignant.");
        }
    }

    // Fonction pour modifier un enseignant
    private void modifierEnseignant() {
        String nom = nomEnseignantField.getText();
        String matricule = matriculeEnseignantField.getText();
        String contact = contactEnseignantField.getText();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE enseignants SET nom=?, contact = ? WHERE matricule = ?")) {
        	stmt.setString(1, nom);
            stmt.setString(2, contact);
            stmt.setString(3, matricule);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                chargerEnseignants(); // Recharger la table des enseignants
                JOptionPane.showMessageDialog(this, "Enseignant modifié avec succès.");
            } else {
                JOptionPane.showMessageDialog(this, "Aucun enseignant trouvé avec cette matricule.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification de l'enseignant.");
        }
    }

    // Fonction pour supprimer un enseignant
    private void supprimerEnseignant() {
        String matricule = matriculeEnseignantField.getText();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM enseignants WHERE matricule = ?")) {
            stmt.setString(1, matricule);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                chargerEnseignants(); // Recharger la table des enseignants
                JOptionPane.showMessageDialog(this, "Enseignant supprimé avec succès.");
            } else {
                JOptionPane.showMessageDialog(this, "Aucun enseignant trouvé avec cette matricule.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'enseignant.");
        }
    }

    // Fonction pour enregistrer une séance
    private void enregistrerSeance() {
        String classe = (String) classeComboBox.getSelectedItem();
        String matiere = (String) matiereField.getText();
        String enseignant = (String) enseignantField.getText();
        String jour = (String) jourComboBox.getSelectedItem();
        String heureDebutStr = (String) heureDebutField.getSelectedItem();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Récupérer les IDs de la classe, de la matière et de l'enseignant
            
            //int enseignantId = getIdFromName("enseignants", enseignant, conn);

           /* // Convertir les heures de début et de fin en objets Time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime heureDebut = LocalTime.parse(heureDebutStr, formatter);
            LocalTime heureFin = LocalTime.parse(heureFinStr, formatter);*/

            // Insérer la séance dans la base de données
            String sql = "INSERT INTO seances (classe, matiere, enseignant, jour, heure_debut) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, classe);
                stmt.setString(2, matiere);
                stmt.setString(3, enseignant);
                stmt.setString(4, jour);
                stmt.setString(5, (heureDebutStr));
                stmt.executeUpdate();
            }
            chargerSeances(); // Recharger la table des séances
            JOptionPane.showMessageDialog(this, "Séance enregistrée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement de la séance.");
        }
    }

    // Fonction utilitaire pour obtenir l'ID d'une table à partir d'un nom
    private int getIdFromName(String tableName, String name, Connection conn) throws SQLException {
        String sql = "SELECT id FROM " + tableName + " WHERE nom = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Aucun enregistrement trouvé pour " + name + " dans la table " + tableName);
            }
        }
    }
    
    

    // Charger les enseignants dans la table des enseignants
    private void chargerEnseignants() {
        enseignantsTableModel.setRowCount(0); // Effacer les données existantes
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM enseignants")) {
            while (rs.next()) {
                enseignantsTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("matricule"),
                        rs.getString("contact")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des enseignants.");
        }
    }

    // Charger les séances dans la table des séances
    private void chargerSeances() {
        seancesTableModel.setRowCount(0); // Effacer les données existantes
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT s.id,  classe,  matiere, enseignant, jour, heure_debut " +
                             "FROM seances s ")) {
            while (rs.next()) {
                seancesTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("classe"),
                        rs.getString("matiere"),
                        rs.getString("enseignant"),
                        rs.getString("jour"),
                        rs.getString("heure_debut")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des séances.");
        }
    }

    // Charger les classes dans le ComboBox
    private void chargerClassesDansComboBox() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nom FROM classes")) {
            while (rs.next()) {
                classeComboBox.addItem(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des classes.");
        }
    }

    // Charger les matières dans le ComboBox
    private void chargerMatieresDansComboBox() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nom FROM matieres")) {
            /*while (rs.next()) {
                matiereComboBox.addItem(rs.getString("nom"));
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des matières.");
        }
    }
    


    // Charger les enseignants dans le ComboBox
    private void chargerEnseignantsDansComboBox() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nom FROM enseignants")) {
           /* while (rs.next()) {
                enseignantComboBox.addItem(rs.getString("nom"));
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des enseignants.");
        }
    }

    public static void main(String[] args) {
        try {
            // Charger le pilote JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur : Pilote JDBC MySQL introuvable.");
            return;
        }
        new Interface1();
    }
}