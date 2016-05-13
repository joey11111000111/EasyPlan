package com.github.joey11111000111.EasyPlan.dao;

import java.util.List;

/**
 * This interface offers the service of reading the contents of the city.xml file
 * and wrap it in the form specified by {@link iBusStopData}.
 */
public interface iCityReader {

    /**
     * Reads the city.xml file, puts its contents into the form specified by {@link iBusStopData}
     * and returns a list containing all the read and refactored data.
     * @return a list containing all the read and refactored data from the city file
     */
    List<iBusStopData> readCityStops();

}
