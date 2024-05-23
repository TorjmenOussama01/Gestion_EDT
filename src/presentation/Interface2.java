package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.sql.*;


public class Interface2 extends JFrame {

    // Base de données (mêmes informations que l'Interface1)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/emploidutemps_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static String last="";

    // Composants de l'interface
    private JComboBox<String> classeComboBox1, classeComboBox2 ;
    private JButton chercherSeancesButton, chercherEDTButton;
    private JTable seancesTable;
    private DefaultTableModel seancesTableModel;
    private JTextField idSeanceField,matiereField;
    private JButton supprimerSeanceButton;

    public Interface2() {
        super("Requêtes Emplois du Temps");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ferme seulement cette fenêtre
        setSize(800, 400);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel pour la recherche de séances par matière et classe
        JPanel rechercheSeancesPanel = new JPanel(new FlowLayout());
        rechercheSeancesPanel.setBorder(BorderFactory.createTitledBorder("Rechercher Séances"));
        rechercheSeancesPanel.add(new JLabel("Classe :"));
        classeComboBox1 = new JComboBox<>(new String[] {"1er", "2eme", "3eme", "4eme", "5eme", "6eme"});
        //chargerClassesDansComboBox(classeComboBox1);
        rechercheSeancesPanel.add(classeComboBox1);
        rechercheSeancesPanel.add(new JLabel("Matière :"));
        matiereField = new JTextField(20);
        rechercheSeancesPanel.add(matiereField);
        chercherSeancesButton = new JButton("CHERCHER");
        chercherSeancesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chercherSeancesParMatiereClasse();
                last="matier";
            }
        });
        rechercheSeancesPanel.add(chercherSeancesButton);
        mainPanel.add(rechercheSeancesPanel, BorderLayout.NORTH);

        // Panel pour le tableau des séances
        JPanel seancesTablePanel = new JPanel(new BorderLayout());
        seancesTableModel = new DefaultTableModel(
                new String[]{"ID", "Classe", "Matière", "Enseignant", "Jour", "Heure Début", "Contact Enseignant"}, 0);
        seancesTable = new JTable(seancesTableModel);
        JScrollPane seancesScrollPane = new JScrollPane(seancesTable);
        seancesTablePanel.add(seancesScrollPane, BorderLayout.CENTER);
        mainPanel.add(seancesTablePanel, BorderLayout.CENTER);

        // Panel pour la recherche d'emploi du temps par classe
        JPanel rechercheEDTPanel = new JPanel(new FlowLayout());
        rechercheEDTPanel.setBorder(BorderFactory.createTitledBorder("Rechercher Emploi du Temps"));
        rechercheEDTPanel.add(new JLabel("Classe :"));
        classeComboBox2 = new JComboBox<>(new String[] {"1er", "2eme", "3eme", "4eme", "5eme", "6eme"});
        //chargerClassesDansComboBox(classeComboBox2);
        rechercheEDTPanel.add(classeComboBox2);
        chercherEDTButton = new JButton("CHERCHER");
        chercherEDTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chercherEmploiDuTempsParClasse();
                last="class";
            }
        });
        rechercheEDTPanel.add(chercherEDTButton);
        mainPanel.add(rechercheEDTPanel, BorderLayout.SOUTH);

      
     // Panel pour le champ ID et le bouton supprimer
        JPanel supprimerPanel = new JPanel(new FlowLayout());
        supprimerPanel.add(new JLabel("ID Séance :"));
        idSeanceField = new JTextField(10);
        supprimerPanel.add(idSeanceField);
        supprimerSeanceButton = new JButton("SUPPRIMER");
        supprimerSeanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                supprimerSeanceParID();
            }
        });
        supprimerPanel.add(supprimerSeanceButton);
        seancesTablePanel.add(supprimerPanel, BorderLayout.SOUTH); // Ajouter en bas du tableau

        mainPanel.add(seancesTablePanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    // Chercher les séances par matière et classe
    private void chercherSeancesParMatiereClasse() {
        String classe = (String) classeComboBox1.getSelectedItem();
        String matiere = (String) matiereField.getText();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT s.id,  classe,  matiere, e.nom AS enseignant,e.contact AS contact, s.jour, s.heure_debut " +
                             "FROM seances s " +
                             "JOIN enseignants e ON s.enseignant = e.matricule " +
                             "WHERE classe = ? AND matiere = ?")) {
            stmt.setString(1, classe);
            stmt.setString(2, matiere);
            ResultSet rs = stmt.executeQuery();

            // Remplir le tableau seancesTable
            seancesTableModel.setRowCount(0);
            while (rs.next()) {
                seancesTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("classe"),
                        rs.getString("matiere"),
                        rs.getString("enseignant"),
                        rs.getString("jour"),
                        rs.getString("heure_debut"),
                        rs.getString("Contact")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche des séances.");
        }
    }

    // Chercher l'emploi du temps par classe
    private void chercherEmploiDuTempsParClasse() {
        String classe = (String) classeComboBox2.getSelectedItem();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT s.id,  classe,  matiere, e.nom AS enseignant,e.contact AS contact, s.jour, s.heure_debut " +
                             "FROM seances s " +
                             "JOIN enseignants e ON s.enseignant = e.matricule  " +
                             "WHERE classe = ? " +
                             "ORDER BY s.jour, s.heure_debut")) {
            stmt.setString(1, classe);
            ResultSet rs = stmt.executeQuery();

            // Remplir le tableau edtTable
            seancesTableModel.setRowCount(0);
            while (rs.next()) {
            	seancesTableModel.addRow(new Object[]{
                		rs.getInt("id"),
                        rs.getString("classe"),
                        rs.getString("matiere"),
                        rs.getString("enseignant"),
                        rs.getString("jour"),
                        rs.getString("heure_debut"),
                        rs.getString("Contact")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la recherche de l'emploi du temps.");
        }
    }

   

    private void supprimerSeanceParID() {
        String idSeanceStr = idSeanceField.getText();

        try {
            int idSeance = Integer.parseInt(idSeanceStr);

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM seances WHERE id = ?")) {
                stmt.setInt(1, idSeance);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Séance supprimée avec succès.");
                    if(last=="matier") {
                    	chercherSeancesParMatiereClasse(); // Rafraîchir le tableau des séances
                    }else if(last=="class") {
                    	chercherEmploiDuTempsParClasse();
                    }
                    
                } else {
                    JOptionPane.showMessageDialog(this, "Aucune séance trouvée avec cet ID.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un ID de séance valide.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la séance.");
        }
    }
}
