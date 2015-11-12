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
    private final ServiceData serviceData;

    public Core() {
        serviceData = new ServiceData();
        readSavedServices();
    }

    public ServiceData getServiceData() {
        return serviceData;
    }
    public int getServiceCount() {
        return services.size();
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
                while (true) {
                    BusService service = (BusService)ois.readObject();
                    service.initTransientFields();
                    services.add(service);
                }
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
            if (services.get(i).getAppliedName().equals("new service"))
                return;

        BusService newService = new BusService();
        services.add(newService);
        serviceData.setCurrentService(newService);
    }

    public void deleteCurrentService() {
        if (!serviceData.hasSelectedService())
            return;
        services.remove(serviceData.getSelectedService());
        serviceData.setCurrentService(null);
    }

    public void selectService(String name) {
        if (name == null)
            throw new NullPointerException("the name of the selected service cannot be null");
        if (serviceData.hasSelectedService())
            if (name.equals(serviceData.getName()))
                return;

        for (int i = 0; i < services.size(); i++) {
            BusService service = services.get(i);
            if (service.getAppliedName().equals(name)) {
                serviceData.setCurrentService(service);
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

    public void applyChanges() throws NameConflictException {
        // if there is nothing to change or change to, then do nothing
        if (!serviceData.hasSelectedService())
            return;
        if (!serviceData.isModified())
            return;

        String name = serviceData.getName();
        BusService currentService = serviceData.getSelectedService();
        for (int i = 0; i < services.size(); i++) {
            BusService service = services.get(i);
            if (service == currentService)
                continue;
            String serviceName = service.getAppliedName();
            if (name.equals(serviceName))
                throw new NameConflictException("given name '" + name + "' is already in use");
        }

        currentService.applyChanges();
    }


    public Timetable[] getAllTimetables() {
        Timetable[] tables = new Timetable[services.size()];
        for (int i = 0; i < tables.length; i++)
            tables[i] = services.get(i).getTimeTable();
        return tables;
    }



}//class
