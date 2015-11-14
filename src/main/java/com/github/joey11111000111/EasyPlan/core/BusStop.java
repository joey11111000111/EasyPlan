package com.github.joey11111000111.EasyPlan.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;

/**
 * The BusStop class manages all the data and operations (including the xml parsing) that are related to bus stops.
 * This class cannot be instantiated, nor modified in any way. Everything is handled through static methods.
 * All the bus stops have a unique id, which start from 0 and are increasing one by one. Thus the 'id' and 'index'
 * words are almost synonyms.
 */
public final class BusStop implements Comparable<BusStop> {

    // static ----------------------------------------------------------
    private static final BusStop[] allStops;

    static {
        List<BusStop> allStopsList = new LinkedList<BusStop>();
        try {
            // get the 'city.xml' file as an InputStream
            InputStream is = BusStop.class.getClassLoader().getResourceAsStream("city.xml");
            // create a document builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // parse the xml file and normalize it
            Document document = builder.parse(is);
            document.getDocumentElement().normalize();

            // create the bus station
            NodeList stationNodes = document.getElementsByTagName("bus_station");
            // there is only one element with the above tag name
            Element stationElement = (Element)stationNodes.item(0);
            allStopsList.add(createBusStopObject(stationElement));

            // create all the bus stops
            NodeList busStops = document.getElementsByTagName("bus_stop");
            for (int i = 0; i < busStops.getLength(); i++) {
                Element currentStop = (Element)busStops.item(i);
                allStopsList.add(createBusStopObject(currentStop));
            }
        }
        catch (Exception e) {
            Core.LOGGER.error("cannot read the city.xml file, which is fatal", e);
            System.exit(1);
        }

        allStops = allStopsList.toArray(new BusStop[0]);
        Arrays.sort(allStops);
    }//static


    private static BusStop createBusStopObject(Element bsElement) {
        int id, x, y;
        // get id
        id = getNumericContentOfTag(bsElement, "id");
        // get positions
        Element posNode = (Element)bsElement.getElementsByTagName("position").item(0);
        x = getNumericContentOfTag(posNode, "x");
        y = getNumericContentOfTag(posNode, "y");

        Map<java.lang.Integer, java.lang.Integer> reachables = getAllReachablesOf(bsElement);
        return new BusStop(id, x, y, reachables);
    }

    private static Map<Integer, Integer> getAllReachablesOf(Element busStop) {
        Map<Integer, Integer> reachables = new HashMap<Integer, Integer>();
        NodeList connections = busStop.getElementsByTagName("connection");

        // iterate through the list and fill the map
        for (int i = 0; i < connections.getLength(); i++) {
            Element connection = (Element)connections.item(i);
            int refId = getNumericContentOfTag(connection, "refid");
            int travelTime = getNumericContentOfTag(connection, "travel_time");
            reachables.put(refId, travelTime);
        }

        return Collections.unmodifiableMap(reachables);
    }

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
     * Returns the X coordinate of the specified bus stop
     * @param id specifies the bus stops, whose X coordinate will be returned
     * @return the X coordinate of the specified bus stop
     * @throws IndexOutOfBoundsException if there isn't a bus stop with the given id
     */
    public static int getXCoordOf(int id) {
        if (!validId(id))
            throw new IndexOutOfBoundsException("id is out of range: " + id);
        return allStops[id].x;
    }
    /**
     * Returns the Y coordinate of the specified bus stop
     * @param id specifies the bus stops, whose Y coordinate will be returned
     * @return the Y coordinate of the specified bus stop
     * @throws IndexOutOfBoundsException if there isn't a bus stop with the given id
     */
    public static int getYCoordOf(int id) {
        if (!validId(id))
            throw new IndexOutOfBoundsException("id is out of range: " + id);
        return allStops[id].y;
    }
    /**
     * Returns the X coordinate of the bus station
     * @return the X coordinate of the bus station
     */
    public static int getXCoordOfStation() {
        return allStops[0].x;
    }
    /**
     * Returns the Y coordinate of the bus station
     * @return the Y coordinate of the bus station
     */
    public static int getYCoordOfStation() {
        return allStops[0].y;
    }

    /**
     * Returns all the bus stop ids that can be the next stop after the given bus stop
     * @param id the current bus stop, from which to go
     * @return all the bus stop ids that can be the next stop after the given bus stop
     */
    public static int[] getReachableIdsOf(int id) {
        Set<java.lang.Integer> keyIds = allStops[id].reachableStops.keySet();
        int[] reachableIds = new int[keyIds.size()];
        int i = 0;
        for (int reId : keyIds)
            reachableIds[i++] = reId;
        return reachableIds;
    }

    /**
     * Returns the id of the bus station
     * @return the id of the bus station
     */
    public static int getIdOfStation() {
        return allStops[0].id;
    }

    /**
     * Returns all the bus stop ids that can be the first stop, starting from the bus station
     * @return all the bus stop ids that can be the first stop, starting from the bus station
     */
    public static int[] getReachableIdsOfStation() {
        return getReachableIdsOf(0);
    }

    /**
     * Returns true if there is a bus stop with the given id
     * @param index the bus stop id whose validity will be checked
     * @return true if there is a bus stop with the given id
     */
    public static boolean validId(int index) {
        return index >= 0 && index < allStops.length;
    }

    /**
     * Returns true, if the bus stop at 'toId' can be the next from 'fromId'
     * @param toId
     * @param fromId
     * @return true when the bus stop at 'toId' can be the next from 'fromId'
     * @throws IndexOutOfBoundsException if either of the given ids are invalid
     */
    public static boolean isReachableToFrom(int toId, int fromId) {
        if (!validId(toId))
            throw new IndexOutOfBoundsException("toId is out of range: " + toId);
        if (!validId(fromId))
            throw new IndexOutOfBoundsException("fromId is out of range: " + fromId);
        return allStops[fromId].reachableStops.containsKey(toId);
    }

    /**
     * Returns true, if the bus station can be the next (and last) stop after
     * the bus stop at 'fromId'
     * @param fromId the current bus stop, from which to go
     * @return true, if the bus station can be the next (and last) stop after
     * the bus stop at 'fromId'
     * @throws IndexOutOfBoundsException when the given id is invalid
     */
    public static boolean isStationReachableFrom(int fromId) {
        if (!validId(fromId))
            throw new IndexOutOfBoundsException("fromId is out of range: " + fromId);
        return allStops[fromId].reachableStops.containsKey(0);
    }

    /**
     * Returns true, if the bus stop with the given id can be the first stop starting from the bus station
     * @param id the bus stop to go from the bus station
     * @return true, if the bus stop with the given id can be the first stop starting from the bus station
     * @throws IndexOutOfBoundsException when the given id is invalid
     */
    public static boolean isReachableFromStation(int id) {
        if (!validId(id))
            throw new IndexOutOfBoundsException("id is out of range: " + id);
        return allStops[0].reachableStops.containsKey(id);
    }

    /**
     * Returns true if the given id belongs to the bus station
     * @param id the id to check
     * @return true if the given id belongs to the bus station
     */
    public static boolean isStation(int id) {
        if (!validId(id))
            throw new IndexOutOfBoundsException("id is out of range: " + id);
        return id == 0;
    }

    /**
     * Returns the time (in minutes) that it takes to go from the bus stop with 'fromId' to the
     * bus stop with 'toId'
     * @param toId the bus stop to go to
     * @param fromId the bus stop to go from
     * @return the travel time in minutes
     * @throws IndexOutOfBoundsException if either of the given ids are invalid
     */
    public static int travelTimeToFrom(int toId, int fromId) {
        if (!validId(fromId))
            throw new IndexOutOfBoundsException("fromId is out range: " + fromId);
        if (!validId(toId))
            throw new IndexOutOfBoundsException("toId is out of range: " + toId);
        if (!allStops[fromId].reachableStops.containsKey(toId))
            throw new IllegalArgumentException("the bus stop '" + toId +
                    "'  is not reachable from '" + fromId + "'");

        return allStops[fromId].reachableStops.get(toId);
    }
    /**
     * Returns the time (in minutes) that it takes to go from the bus station to the
     * bus stop with 'toId'
     * @param toId the bus stop to go to
     * @return the travel time in minutes
     * @throws IndexOutOfBoundsException when to given id is invalid
     */
    public static int travelTimeToFromStation(int toId) {
        if (!validId(toId))
            throw new IndexOutOfBoundsException("toId is out of range: " + toId);
        if (!allStops[0].reachableStops.containsKey(toId))
            throw new IllegalArgumentException("the bus stop at '" + toId + "' is not reachable from the station");
        return allStops[0].reachableStops.get(toId);
    }
    /**
     * Returns the time (in minutes) that it takes to go from the bus stop at 'fromId' to the bus station
     * @param fromId the bus stop to go from
     * @return the travel time in minutes
     * @throws IndexOutOfBoundsException when to given id is invalid
     */
    public static int travelTimeToStationFrom(int fromId) {
        if (!validId(fromId))
            throw new IndexOutOfBoundsException("fromId is out of range: " + fromId);
        if (!allStops[fromId].reachableStops.containsKey(0))
            throw new IllegalArgumentException("the bus station is not reachabel from the given station: " + fromId);
        return allStops[fromId].reachableStops.get(0);
    }

    // member ----------------------------------------------------------
    public final int id;
    public final int x;
    public final int y;
    private final Map<java.lang.Integer, java.lang.Integer> reachableStops;

    private BusStop(int id, int x, int y, Map<java.lang.Integer, java.lang.Integer> reachableStops) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.reachableStops = reachableStops;
    }

    public int compareTo(BusStop otherStop) {
        return this.id - otherStop.id;
    }

}//class
