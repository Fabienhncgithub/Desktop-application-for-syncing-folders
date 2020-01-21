public abstract class Fichier {
    private final String nom;

    public Fichier(String nom) {
        this.nom = nom;
    }
    
    public String getNom() { return nom; }

    protected String formatAffichage(int decalage) {
        String res = "";
        for (int i = 0; i < decalage; ++i)
            res += "\t";
        return res;
    }
    
    @Override
    public String toString() {
        return formatAffichage(0);
    }
    
    public abstract int taille();
    public abstract void ajoutFichier(Fichier f);
    
}