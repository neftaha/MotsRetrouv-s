package fr.univparis8.app.motsretrouves.data;

import fr.univparis8.app.motsretrouves.model.GameTheme;

import java.util.ArrayList;
import java.util.List;

public class GameThemeRepository {

    public List<GameTheme> getGameThemes() {
        // sample data
        List<GameTheme> themes = new ArrayList<>();
        themes.add(new GameTheme(1, "Les Confessions"));
        themes.add(new GameTheme(2, "Du contrat social"));
        themes.add(new GameTheme(3, "Emile ou de l'éducation"));
        themes.add(new GameTheme(4, "Rêveries du promeneur solitaire"));
        themes.add(new GameTheme(5, "Du contrat social\n"));
        themes.add(new GameTheme(5, "Émile "));
        themes.add(new GameTheme(5, "Lettre à d'Alembert "));
        themes.add(new GameTheme(5, "La Nouvelle Héloïse "));
        themes.add(new GameTheme(5, "Narcisse ou l'Amant de lui-même"));

        return themes;
    }

}
