package fr.univparis8.app.motsretrouves.di.modules;

import android.content.Context;

import fr.univparis8.app.motsretrouves.data.sqlite.DbHelper;
import fr.univparis8.app.motsretrouves.data.sqlite.GameDataSQLiteDataSource;
import fr.univparis8.app.motsretrouves.data.xml.WordXmlDataSource;
import fr.univparis8.app.motsretrouves.data.GameDataSource;
import fr.univparis8.app.motsretrouves.data.WordDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by abdularis on 18/07/17.
 */

@Module
public class DataSourceModule {

    @Provides
    @Singleton
    DbHelper provideDbHelper(Context context) {
        return new DbHelper(context);
    }

    @Provides
    @Singleton
    GameDataSource provideGameRoundDataSource(DbHelper dbHelper) {
        return new GameDataSQLiteDataSource(dbHelper);
    }

//    @Provides
//    @Singleton
//    WordDataSource provideWordDataSource(DbHelper dbHelper) {
//        return new WordSQLiteDataSource(dbHelper);
//    }

    @Provides
    @Singleton
    WordDataSource provideWordDataSource(Context context) {
        return new WordXmlDataSource(context);
    }

}
