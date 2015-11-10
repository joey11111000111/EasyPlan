package com.github.joey11111000111.EasyPlan.core;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joey on 2015.11.01..
 */
public class Core {

    public static class NameConflictException extends Exception {
        public NameConflictException(String message) {
            super(message);
        }
    }

    static final String SAVE_PATH = System.getProperty("user.home") + "/.EasyPlan/savedServices";
    private List<BusService> services;
    private BusService currentService;
    private int currentServiceIndex;

    public Core() {
        readSavedServices();
        currentService = null;
        currentServiceIndex = -1;
    }

    private void readSavedServices() {
        services = new ArrayList<BusService>();
        String lnSep = System.getProperty("line.separator");
        FileInputStream fis;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(SAVE_PATH);
            ois = new ObjectInputStream(fis);
            try {
                BusService service = (BusService)ois.readObject();
                service.initTransientFields();
                services.add(service);
            } catch (EOFException eofe) {
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
                System.err.println("cnfe happened during the reading process: " + lnSep + cnfe);
            }

        } catch (FileNotFoundException fnfe) {
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.err.println("ioe happened during the reading process: " + lnSep + ioe);
        } finally {
            if (ois != null)
                try {
                    ois.close();
                } catch (IOException ioe) {
                    System.err.println("ioe happened while trying to close the ois: " + lnSep + ioe);
                }
        }
    }//readSavedServices

    public void saveServices() {
        // When there are no services, the reader will know that by not finding the save file
        String lnSep = System.getProperty("line.separator");
        if (services.size() == 0) {
            File saveFile = new File(SAVE_PATH);
            if (saveFile.exists())
                while (!saveFile.delete())
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        System.err.println("Cannot delete file, interrupted sleeping: " + lnSep + ie);
                    }
            return;
        }

        // if the current service was modified, apply the changes
        if (currentService != null)
            currentService.applyChanges();

        FileOutputStream fos;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(SAVE_PATH);
            oos = new ObjectOutputStream(fos);
            for (BusService service : services)
                oos.writeObject(service);
        } catch (FileNotFoundException fnfe) {
            System.err.println("Cannot find save file when trying to save services: " + lnSep + fnfe);
        } catch (IOException ioe) {
            System.err.println("ioe happened when trying to save the services: " + lnSep + ioe);
        } finally {
           if (oos != null)
               try {
                   oos.close();
               } catch (IOException ioe) {
                   System.err.println("cannot close oos: " + lnSep + ioe);
               }
        }
    }//writeServices

    public void createNewService() {
        // if there already is a service called "new service" then do nothing
        for (int i = 0; i < services.size(); i++)
            if (services.get(i).getCurrentServiceData().getName().equals("new service"))
                return;

        currentServiceIndex = services.size();
        currentService = new BusService();
        services.add(currentService);
    }

    public void deleteCurrentService() {
        if (currentService == null)
            return;
        services.remove(currentServiceIndex);
        currentService = null;
        currentServiceIndex = -1;
    }

    public void selectService(String name) {
        if (name == null)
            throw new NullPointerException("the name of the selected service cannot be null");
        if (currentService != null)
            currentService.discardChanges();

        for (int i = 0; i < services.size(); i++) {
            BusService service = services.get(i);
            if (service.getCurrentServiceData().getName().equals(name)) {
                currentService = service;
                currentServiceIndex = i;
                return;
            }
        }
        throw new IllegalArgumentException("the bus service with the given name '"
                + name + "' doesn't exist");
    }

    public String[] getServiceNames() {
        String[] names = new String[services.size()];
        for (int i = 0; i < names.length; i++)
            names[i] = services.get(i).getCurrentServiceData().getName();
        return names;
    }

    public TouchedStops getCurrentStops() {
        if (currentService == null)
            return null;
        return currentService.getCurrentStops();
    }

    public BasicServiceData getCurrentServiceData() {
        if (currentService == null)
            return null;
        return currentService.getCurrentServiceData();
    }

    public void applyChanges() throws NameConflictException{
        if (currentService == null)
            return;
        String name = currentService.getCurrentServiceData().getName();
        for (int i = 0; i < services.size(); i++) {
            if (i == currentServiceIndex)
                continue;
            String serviceName = services.get(i).getAppliedName();
            if (name.equals(serviceName))
                throw new NameConflictException("given name '" + name + "' is already in use");
        }

        currentService.applyChanges();
    }
    public boolean discardChanges() {
        if (currentService != null)
            return currentService.discardChanges();
        return false;
    }

    public Timetable getCurrentTimetable() {
        if (currentService == null)
            return null;
        return currentService.getTimeTable();
    }

    public Timetable[] getAllTimetables() {
        Timetable[] tables = new Timetable[services.size()];
        for (int i = 0; i < tables.length; i++)
            tables[i] = services.get(i).getTimeTable();
        return tables;
    }



}//class
