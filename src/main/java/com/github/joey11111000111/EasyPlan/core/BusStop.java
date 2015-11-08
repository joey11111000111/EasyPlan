package com.github.joey11111000111.EasyPlan.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;

/**
 * Created by joey on 2015.11.01..
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
            // TODO: log the exception
            System.out.println("exception happened " + e);
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

    public static int getXCoordOf(int id) {
        if (!validId(id))
            throw new IndexOutOfBoundsException("id is out of range: " + id);
        return allStops[id].x;
    }
    public static int getYCoordOf(int id) {
        if (!validId(id))
            throw new IndexOutOfBoundsException("id is out of range: " + id);
        return allStops[id].y;
    }
    public static int getXCoordOfStation() {
        return allStops[0].x;
    }
    public static int getYCoordOfStation() {
        return allStops[0].y;
    }

    public static int[] getReachableIdsOf(int id) {
        Set<java.lang.Integer> keyIds = allStops[id].reachableStops.keySet();
        int[] reachableIds = new int[keyIds.size()];
        int i = 0;
        for (int reId : keyIds)
            reachableIds[i++] = reId;
        return reachableIds;
    }

    public static int getIdOfStation() {
        return allStops[0].id;
    }
    public static int[] getReachableIdsOfStation() {
        return getReachableIdsOf(0);
    }

    public static boolean validId(int index) {
        return index >= 0 && index < allStops.length;
    }

    public static boolean isReachableToFrom(int toId, int fromId) {
        if (!validId(toId))
            throw new IndexOutOfBoundsException("toId is out of range: " + toId);
        if (!validId(fromId))
            throw new IndexOutOfBoundsException("fromId is out of range: " + fromId);
        return allStops[fromId].reachableStops.containsKey(toId);
    }

    public static boolean isStationReachableFrom(int fromId) {
        if (!validId(fromId))
            throw new IndexOutOfBoundsException("fromId is out of range: " + fromId);
        return allStops[fromId].reachableStops.containsKey(0);
    }

    public static boolean isReachableFromStation(int id) {
        if (!validId(id))
            throw new IndexOutOfBoundsException("id is out of range: " + id);
        return allStops[0].reachableStops.containsKey(id);
    }

    public static boolean isStation(int id) {
        if (!validId(id))
            throw new IndexOutOfBoundsException("id is out of range: " + id);
        return id == 0;
    }

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
    public static int travelTimeToFromStation(int toId) {
        if (!validId(toId))
            throw new IndexOutOfBoundsException("toId is out of range: " + toId);
        if (!allStops[0].reachableStops.containsKey(toId))
            throw new IllegalArgumentException("the bus stop at '" + toId + "' is not reachable from the station");
        return allStops[0].reachableStops.get(toId);
    }
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

//    public String toString() {
//        String lnSep = System.getProperty("line.separator");
//        StringBuilder sb = new StringBuilder();
//        sb.append("id: ").append(id).append("  x: ").append(x).append("  y: ").append(y).append(lnSep);
//        for (Map.Entry<java.lang.Integer, java.lang.Integer> entry : reachableStops.entrySet()) {
//            sb.append("   ").append("id: ").append(entry.getKey())
//                    .append(" travel time: ").append(entry.getValue())
//                    .append(lnSep);
//        }
//        return sb.toString();
//    }

}//class
