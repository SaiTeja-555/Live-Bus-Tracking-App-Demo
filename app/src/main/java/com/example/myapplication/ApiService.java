package com.example.myapplication;

import static com.parse.Parse.getApplicationContext;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.sql.Array;
import java.sql.Driver;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ApiService {

    static Location oldLocation = new Location(LocationManager.GPS_PROVIDER);

    static List<ParseObject> getAllBusstops(ParseObject bus) {

        ParseQuery busstopQuery = ParseQuery.getQuery("Busstop");
        if(bus != null) {
            List<String> busstopNumbers = (List<String>) bus.get("busstopNumbers");
            System.out.println(busstopNumbers);
            busstopQuery.whereContainedIn("number", busstopNumbers);
            System.out.println("filter query successfull");
        }

        try {
            System.out.println("busstop query successfull");
            return busstopQuery.find();
        } catch (ParseException e) {
            System.out.println("error in busstop query");
            e.printStackTrace();
        }
        return null;
    }

    static List<ParseObject> getFilteredBuses(ParseObject fromBusstop, ParseObject toBusstop) {
        List<ParseObject> filteredBuses = new ArrayList<ParseObject>();
        ParseQuery busQuery = ParseQuery.getQuery("Bus");
        try {
            List<ParseObject> allBuses = busQuery.find();
            for(ParseObject bus: allBuses) {
                List<String> busstopNumbers = (List<String>) bus.get("busstopNumbers");
                if(busstopNumbers.contains(fromBusstop.get("number")) && busstopNumbers.contains(toBusstop.get("number"))) {
                    System.out.println(bus.get("busNumber"));
                    filteredBuses.add(bus);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return filteredBuses;
    }

    static ParseObject getCurrentBus() {
        ParseQuery busQuery = ParseQuery.getQuery("Bus");
        ParseQuery driverQuery = ParseQuery.getQuery("Driver");
        driverQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        try {
            return busQuery.get(driverQuery.getFirst().getParseObject("curBus").getObjectId());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void updateBusLocationAndDistanceCovered(Location location, ParseObject curBus) {
//        ParseObject curBus = getCurrentBus();
        ParseGeoPoint oldGeoPoint = curBus.getParseGeoPoint("curLocation");
        oldLocation.setLatitude(oldGeoPoint.getLatitude());
        oldLocation.setLongitude(oldGeoPoint.getLongitude());
        double distance = oldLocation.distanceTo(location);
        if(distance >= 10.0) {
            double distanceCovered = curBus.getDouble("distanceCovered");
            curBus.put("distanceCovered", distanceCovered+distance);
            curBus.put("curLocation", new ParseGeoPoint(location.getLatitude(),location.getLongitude()));
            curBus.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        System.out.println("location updated successfully");
                    }
                    else {
                        System.out.println("Error updating the location");
                    }
                }
            });
        }
    }

    static void toggleBusStatus(ParseObject bus) {
        bus.put("isActive", !bus.getBoolean("isActive"));
        try {
            bus.save();
            System.out.println("toggled");
        } catch (ParseException e) {
            System.out.println("error while toggling");
            e.printStackTrace();
        }
    }

    static List<String> getOrderedBusstopNumbers(ParseObject bus) {
        boolean curDirection = bus.getBoolean("curDirection");
        List<String> busstopNumbers = (List<String>) bus.get("busstopNumbers");
        if(curDirection) {
            Collections.reverse(busstopNumbers);
        }
        return busstopNumbers;

    }
    static List<Integer> getDistancesBtwBusstopsBasedOnDirection(ParseObject bus) {
        List<Integer> distancesBtwBusstops = (List<Integer>) bus.get("distancesBtwBusstops");
        List<String> busstopNumbers = (List<String>) bus.get("busstopNumbers");
        boolean curDirection = bus.getBoolean("curDirection");
        if(busstopNumbers.size() != distancesBtwBusstops.size()) {
            if (curDirection) {
                distancesBtwBusstops.add(0);
                Collections.reverse(distancesBtwBusstops);
            } else {
                distancesBtwBusstops.add(0, 0);

            }
        }
        System.out.println(distancesBtwBusstops);
        return distancesBtwBusstops;

    }

    static ParseObject getBusstop(String busstopNumber) {
        ParseQuery busstopQuery = ParseQuery.getQuery("Busstop");
        busstopQuery.whereEqualTo("number", busstopNumber);
        try {
            return busstopQuery.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    static List<ParseObject> getBusstops(List<String> busstopNumbers) {
        List<ParseObject> busstops = new ArrayList<>();
        for(String busstopNumber: busstopNumbers) {
            busstops.add(getBusstop(busstopNumber));
        }
        return busstops;

    }


    static void checkAnyBusstopReachedAndUpdateBusFields(ParseObject bus) {
        List<String> busstopNumbers = getOrderedBusstopNumbers(bus);
        ParseQuery busstopQuery = ParseQuery.getQuery("Busstop");
        int nextBusstopIndex = bus.getInt("nextBusstopIndex");
        ParseGeoPoint busGeoPoint = bus.getParseGeoPoint("curLocation");
        busstopQuery.whereEqualTo("number", busstopNumbers.get(nextBusstopIndex));
        busstopQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject busstop, ParseException e) {
                if(e == null) {
                    ParseGeoPoint busstopGeoPoint = busstop.getParseGeoPoint("location");
                    if(busGeoPoint.distanceInKilometersTo(busstopGeoPoint) <= AppConstants.NEAR_DISTANCE) {
                        System.out.println("next busstop reached");
                        Toast.makeText(getApplicationContext(), "reached "+busstop.getString("name"), Toast.LENGTH_SHORT).show();
                        boolean newBusDirection;
                        int newNextBusstopIndex;
                        if(nextBusstopIndex == busstopNumbers.size()-1) {
                            System.out.println("reached the termianl");
                            //toggle bus direction
                            newBusDirection = !bus.getBoolean("curDirection");
                            bus.put("curDirection", newBusDirection);
                            newNextBusstopIndex = 1;

                        }
                        else {
                            newNextBusstopIndex = nextBusstopIndex + 1;
                        }
                        bus.put("nextBusstopIndex", newNextBusstopIndex);
                        bus.put("distanceCovered", 0.0);
                        bus.saveInBackground(new SaveCallback() {

                            @Override
                            public void done(ParseException e) {
                                if(e == null) {
                                    System.out.println("bus fields updated");
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    static boolean checkBusStartAndUpdateBusFields(ParseObject bus) {
        List<String> busstopNumbers = (List<String>) bus.get("busstopNumbers");
        ParseQuery busstopQuery = ParseQuery.getQuery("Busstop");
        ParseGeoPoint busGeoPoint = bus.getParseGeoPoint("curLocation");
        busstopQuery.whereEqualTo("number", Arrays.asList(new String[]{busstopNumbers.get(0), busstopNumbers.get(busstopNumbers.size()-1)}));
        try {
            List<ParseObject> terminalBusstops = busstopQuery.find();
            if(busGeoPoint.distanceInKilometersTo(terminalBusstops.get(0).getParseGeoPoint("location")) <= AppConstants.NEAR_DISTANCE) {
                bus.put("curDirection", false);
            }
            else if(busGeoPoint.distanceInKilometersTo(terminalBusstops.get(1).getParseGeoPoint("location")) <= AppConstants.NEAR_DISTANCE) {
                bus.put("curDirection", true);
            }
            else {
                return false;
                // need to write code to start service even if he did not reach terminal by asking him the curDirection manually
            }
            bus.put("distanceCovered", 0.0);
            bus.put("nextBusstopIndex", 1);
            bus.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        System.out.println("started bus fields");
                    }
                    else {
                        System.out.println("error in starting bus fields");
                    }
                }
            });
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    static void updateBusTimings(ParseObject bus) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:ss");
        List<String> timings = (List<String>) bus.get("timings");
        int distCoveredInCurStop = bus.getInt("distanceCovered");
        int avgBusSpeed = bus.getInt("avgSpeed");
        List<Integer> busstopDistances = getDistancesBtwBusstopsBasedOnDirection(bus);
        int nextBusstopIndex = bus.getInt("nextBusstopIndex");
        int totalDist = 0;
        int distToBusstop = busstopDistances.get(nextBusstopIndex) - distCoveredInCurStop;
        int timeToReachBusstop = distToBusstop/avgBusSpeed;
//        totalDist += busstopDistances.get(nextBusstopIndex);
        calendar.add(Calendar.SECOND, timeToReachBusstop);
        timings.set(nextBusstopIndex, dateFormat.format(calendar.getTime()));
        for(int busstopIndex = nextBusstopIndex+1; busstopIndex < busstopDistances.size(); busstopIndex++) {
            distToBusstop = busstopDistances.get(busstopIndex);
            timeToReachBusstop = distToBusstop/avgBusSpeed;
            totalDist += distToBusstop;
            calendar.add(Calendar.SECOND, timeToReachBusstop);
            timings.set(busstopIndex, dateFormat.format(calendar.getTime()));
        }
        timeToReachBusstop = totalDist/avgBusSpeed;
        calendar.add(Calendar.SECOND, timeToReachBusstop);
        for(int busstopIndex = nextBusstopIndex-1; busstopIndex >= 0; busstopIndex--) {
            distToBusstop = busstopDistances.get(busstopIndex+1);
            timeToReachBusstop = distToBusstop/avgBusSpeed;
            calendar.add(Calendar.SECOND, timeToReachBusstop);
            timings.set(busstopIndex, dateFormat.format(calendar.getTime()));
        }

//        for(int busstopIndex = 1; busstopIndex < nextBusstopIndex; busstopIndex++) {
//            distToBusstop = busstopDistances.get(busstopIndex);
//            timeToReachBusstop = distToBusstop/avgBusSpeed;
//            calendar.add(Calendar.SECOND, timeToReachBusstop);
//            timings.set(busstopIndex, dateFormat.format(calendar.getTime()));
//        }
        bus.put("timings", timings);
        bus.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if(e == null) {
                    System.out.println("timings updated");
                    System.out.println(timings);
                }
                else {
                    System.out.println("error in updating timings");
                }
            }
        });
    }


    static void futureFeature() {
        Toast.makeText(getApplicationContext(), "This feature will be enabled in future versions", Toast.LENGTH_SHORT).show();
    }



}
