package com.colaenjoyer.imagefilters.ui;


import com.colaenjoyer.imagefilters.utils.ImageFiltersOperations;

import java.util.Arrays;
import java.util.List;

import static com.colaenjoyer.imagefilters.utils.CommandUtils.getCommandExecution;
import static com.colaenjoyer.imagefilters.utils.UiUtils.*;

public class Fixture {
    private Fixture() {}

    public static List<MenuItem> getSelectionMenuItems() {
        return Arrays.asList(
            MenuItem.builder()
                .title("asciifilter")
                .selectionChar('a')
                .function(getCommandExecution(ImageFiltersOperations::executeSelection, 'a'))
                .build(), 
            MenuItem.builder()
                .title("pixelsort")
                .selectionChar('p')
                .function(getCommandExecution(ImageFiltersOperations::executeSelection, 'p'))
                .build(), 
            MenuItem.builder()
                .title("lowpass")
                .selectionChar('l')
                .function(getCommandExecution(ImageFiltersOperations::executeSelection, 'l'))
                .build() 
            );
    }

    public static List<MenuBar> getSelectionMenuBars() {
        return Arrays.asList(
                getParagraphMenuBar("selectionParagraph", "Select a filter:"),
                getParagraphMenuBar("asciiFilterInfo", "Asciifilter - Converts Image into Ascii art."),
                getParagraphMenuBar("pixelSortInfo", "Pixelsort - Sorts image pixels by color."),
                getParagraphMenuBar("lowpassInfo", "Lowpass filter - Blur images"),
                getEmptyMenuBar(),
                getSelectionMenuBar("filterSelection", getSelectionMenuItems()),
                getSelectionMenuBar("quit", List.of(
                    MenuItem.builder()
                        .title("quit")
                        .selectionChar('q')
                        .function(getCommandExecution(ImageFiltersOperations::executeSelection, 'q'))
                        .build()
                ))
        );
    }

    public static Menu getSelectionMenu() {
        return Menu.builder().title("imagefilters").width(46).height(38).menuBars(getSelectionMenuBars()).build();
    }
}
