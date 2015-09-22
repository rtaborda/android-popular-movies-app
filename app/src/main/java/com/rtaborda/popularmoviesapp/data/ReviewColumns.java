package com.rtaborda.popularmoviesapp.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by Rui on 22/09/2015.
 */
public interface ReviewColumns {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(DataType.Type.TEXT) @NotNull
    String AUTHOR = "author";

    @DataType(DataType.Type.TEXT) @NotNull
    String CONTENT = "content";

    @DataType(DataType.Type.INTEGER)
    @References(table = FavouriteDatabase.FAVOURITES, column = FavouriteTitleColumns._ID)
    String FAVOURITE_ID = "favourite_id";
}
