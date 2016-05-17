# EasyPlan
A Java implementation of a bus service designer desktop application.
It allows the user to specify to which bus stops in what order shell a service go to.
Also what name the bus service shell have, how many minutes to wait before the following bus,
what time of the day the first bus shell leave and the time of day when the service is finished, thus
not sending out anymore buses.
All bus servivces are automatically saved when the program quits.
Offers a feature to show the timetable of the bus services. Also gives help when editing a bus service with functions like
undo and discard changes.

There are certain rules to follow when creating bus services. These are the following:
- A bus stop must not go to a bus stop more than twice
- If the bus stop returns to the bus station, than it is finished, no more stops can be included
- The maximum wait time between two following buses must be less than one whole day
- All bus services must have unique names

#### Prerequisites:
  - Java 1.8 (includes JavaFX)
  - [Apache Maven](https://maven.apache.org).
