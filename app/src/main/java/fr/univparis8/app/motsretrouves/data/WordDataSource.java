package fr.univparis8.app.motsretrouves.data;

import fr.univparis8.app.motsretrouves.model.Word;

import java.util.List;

/**
 * Created by abdularis on 18/07/17.
 */

public interface WordDataSource {

    List<Word> getWords();

}
