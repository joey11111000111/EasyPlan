package com.github.joey11111000111.EasyPlan.core;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by joey on 2015.11.01..
 */
public class BusStop {

    // static ----------------------------------------------------------
    static final BusStop[] allStops;

    static {
        try {
            // get the 'city.xml' file as an InputStream
            InputStream is = BusStop.class.getResourceAsStream("city.xml");
            // create a document builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // parse the xml file and normalize it
            Document document = builder.parse(is);
            document.getDocumentElement().normalize();

        }
        catch (Exception e) {
            // TODO: log the exception
            System.exit(1);
        }
    }//static

    private static Map<Integer, Integer> getAllReachablesOf(Element element) {
        Map<Integer, Integer> reachables = new HashMap<Integer, Integer>();
        NodeList connections = element.getElementsByTagName("connection");

        // iterate through the list and fill the map
        for (int i = 0; i < connections.getLength(); i++) {
        }

        return null;
    }

    private static int getNumericContentOfTag(Node node, String tagName) {
        Element parent = (Element)node;
        NodeList tags = parent.getElementsByTagName(tagName);
        // there is only one appearance of a certain tag in a connection element, so the first one is the needed
        Element neededElement = (Element)tags.item(0);
        // get all child nodes (the content is also a child node)
        NodeList childNodes = neededElement.getChildNodes();

        // the parent node only has text-content, so there is only one child node; get the text
        String text = childNodes.item(0).getNodeValue().trim();
        return Integer.parseInt(text);
    }

    static BusStop getStop(int id) {
        if (id < 0 || id >= allStops.length)
            throw new IndexOutOfBoundsException("given bus stop id is out of range: " + id);

        return allStops[id];
    }

    static int[] getReachableIdsOf(int id) {
        Set<Integer> keyIds = allStops[id].reachableStops.keySet();
        int[] reachableIds = new int[keyIds.size()];
        int i = 0;
        for (int reId : keyIds)
            reachableIds[i++] = reId;
        return reachableIds;
    }

    // member ----------------------------------------------------------
    public final int id;
    public final int x;
    public final int y;
    final Map<Integer, Integer> reachableStops;

    public BusStop(int id, int x, int y, Map<Integer, Integer> reachableStops) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.reachableStops = reachableStops;
    }

    public int[] getReachableIds() {
        return BusStop.getReachableIdsOf(id);
    }

    public int travelTimeTo(int id) {
        if (!reachableStops.containsKey(id))
            throw new IllegalArgumentException("given " + id + " bus stop is not reachable from "
                    + this.id);
        return reachableStops.get(id);
    }

    public boolean isStationReachable() {
        return reachableStops.containsKey(0);
    }


}//class
