package com.rtaborda.popularmoviesapp.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by Rui on 22/09/2015.
 */
public interface VideoColumns {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String LANGUAGE = "language"; //iso_639_1;

    @DataType(DataType.Type.TEXT)
    @NotNull
    String KEY = "key";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String NAME = "name";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String SITE = "site";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String SIZE = "size";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String TYPE = "type";

    @DataType(DataType.Type.INTEGER)
    @References(table = FavouriteDatabase.FAVOURITES, column = FavouriteTitleColumns._ID)
    String FAVOURITE_ID = "favourite_id";
}
