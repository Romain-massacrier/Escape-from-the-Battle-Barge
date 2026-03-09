package fr.campus.escapebattlebarge.domain.item;

/*
 * Classe de base de tous les objets du jeu (arme, potion, pouvoir).
 * Elle fournit les propriétés communes: nom et type.
 */
/** Base commune de tous les objets manipulés en jeu. */
public abstract class Item {
    private final String name;
    private final ItemType type;

    // Crée un objet avec son nom et son type.
    protected Item(String name, ItemType type) {
        this.name = name;
        this.type = type;
    }

    // Retourne le nom affiché de l'objet.
    public String getName() { return name; }
    // Retourne la catégorie de l'objet.
    public ItemType getType() { return type; }

    // Retourne un format texte simple pour logs et debug.
    @Override
    public String toString() {
        return name + " (" + type + ")";
    }

}
