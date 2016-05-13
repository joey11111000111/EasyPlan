package com.github.joey11111000111.EasyPlan.dao;

import com.github.joey11111000111.EasyPlan.core.BusStop;
import com.github.joey11111000111.EasyPlan.core.Core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;

/**
 * Implementation of the {@link iCityReader} interface. Includes logging.
 */
public class CityReader implements iCityReader {

    /**
     * The <a href="http://www.slf4j.org/">slf4j</a> logger object for this class.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(CityReader.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public List<iBusStopData> readCityStops() {
        List<iBusStopData> allStopsList = new LinkedList<>();
        try {
            // get the 'city.xml' file as an InputStream
            InputStream is = CityReader.class.getClassLoader().getResourceAsStream("city.xml");
            // create a document builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // parse the xml file and normalize it
            Document document = builder.parse(is);
            document.getDocumentElement().normalize();

            // create the bus station data
            NodeList stationNodes = document.getElementsByTagName("bus_station");
            // there is only one element with the above tag name
            Element stationElement = (Element)stationNodes.item(0);
            allStopsList.add(createBusStopDataObject(stationElement));

            // create all the bus stop data
            NodeList busStops = document.getElementsByTagName("bus_stop");
            for (int i = 0; i < busStops.getLength(); i++) {
                Element currentStop = (Element)busStops.item(i);
                allStopsList.add(createBusStopDataObject(currentStop));
            }
        }
        catch (Exception e) {
            LOGGER.error("city.xml is corrupt!", e);
            System.exit(1);
        }

        return allStopsList;
    }//readCityStops

    /**
     * This method reads the data of the specified bus stop element, refactors it
     * to the form specified by the {@link iBusStopData} and returns it.
     * @param bsElement the bus stop element whose data shell be read, refactored and returned
     * @return the read and refactored data of the specified bus stop element
     */
    private iBusStopData createBusStopDataObject(Element bsElement) {
        int id, x, y;
        // get id
        id = getNumericContentOfTag(bsElement, "id");
        // get positions
        Element posNode = (Element)bsElement.getElementsByTagName("position").item(0);
        x = getNumericContentOfTag(posNode, "x");
        y = getNumericContentOfTag(posNode, "y");
        // get reachables
        Map<Integer, Integer> reachables = getAllReachablesOf(bsElement);

        // create and return bus stop data
        iBusStopData busStopData = new BusStopData();
        busStopData.setId(id);
        busStopData.setX(x);
        busStopData.setY(y);
        busStopData.setReachableStops(reachables);
        return busStopData;
    }//createBusStopDataObject

    /**
     * Parses the tag with the specified name and returns its content as a number.
     * @param parent The parent {@link Element} of the given tag
     * @param tagName the name of the tag whose content shell be returned as a number
     * @return the content of the specified tag as a single number
     */
    private static int getNumericContentOfTag(Element parent, String tagName) {
        NodeList tags = parent.getElementsByTagName(tagName);
        // there is only one appearance of a certain tag in a connection element, so the first one is the needed
        Element neededElement = (Element)tags.item(0);
        // get all child nodes (the content is also a child node)
        NodeList childNodes = neededElement.getChildNodes();

        // the parent node only has text-content, so there is only one child node; get the text
        String text = childNodes.item(0).getNodeValue().trim();
        return java.lang.Integer.parseInt(text);
    }

    /**
     * Collects and returns all the reachable-data from the given bus stop element.
     * @param busStop the bus stop element whose reachable-data shell be read and returned
     * @return an (id - travel time) structured {@link Map} containing the id -s of the reachable stops
     * from the given stop along with the travel times
     */
    private static Map<Integer, Integer> getAllReachablesOf(Element busStop) {
        Map<Integer, Integer> reachables = new HashMap<>();
        NodeList connections = busStop.getElementsByTagName("connection");

        // iterate through the list and fill the map
        for (int i = 0; i < connections.getLength(); i++) {
            Element connection = (Element)connections.item(i);
            int refId = getNumericContentOfTag(connection, "refid");
            int travelTime = getNumericContentOfTag(connection, "travel_time");
            reachables.put(refId, travelTime);
        }

        return reachables;
    }
}//class
