// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/** FindMeetingQuery, at the core, COMPRESSES each event into 1 event and finds the possible range slots -- it performs this twice .. 1) With Guest and Mandatory attendees considered and 2) With only Mandatory attendees considered 
 * Time Complexity: O(e + r) // e = amount of events  r = is the amount of range spaces from each event (We have to search through every event and look through every range)
 * Space Complexity: O(e + r) // e = amount of events  r = is the amount of range spaces from each event (In the worst case, no events are overlapping and we have to store every event therefore increasing the more possible range)
 * NOTE: This is for simplicity purposes. As the other utility helper functions have more complex time complexity that wasn't included.
*/
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // If: 1) No attendees AND no guest attendees. OR 2) There are no events currently on calendar and the meeting duration is less than whole day. Therefore whole day is open for that meeting.
    if ( (request.getAttendees().isEmpty() && request.getOptionalAttendees().isEmpty()) || (events.isEmpty() && request.getDuration() <= TimeRange.WHOLE_DAY.duration())) return Arrays.asList(TimeRange.WHOLE_DAY);

    // Check that duration of meeting is within boundaries of a day
    else if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) return Arrays.asList();

    // Convert Collection to ArrayList
    ArrayList<Event> listOfEvents = new ArrayList<Event>(events);


    //  Check if we have NO attendees. If we have just 1 person attending any event from any part of the day, then we can return the whole day.
    if (checkNoMandatoryAttendees(request, listOfEvents) && request.getOptionalAttendees().isEmpty()) return Arrays.asList(TimeRange.WHOLE_DAY);

    // Compress events of ALL GUEST AND MANDATORY people.
    ArrayList<TimeRange> eventsGuestMandatory = compressEvents(listOfEvents);
  
    // Get possible ranges of Guest and Mandatory
    ArrayList<TimeRange> possibleRanges = getPossibleRanges(eventsGuestMandatory);
 
    if (request.getAttendees().isEmpty() && possibleRanges.isEmpty()) return Arrays.asList();

    // Check to make sure event duration can fit. 1) Not greater than length of day OR 2) By available event space.
    Boolean durationCanFitInPossibleRange = cantFitDurationInPossibleRange(request, possibleRanges);
    if (cantFitDurationInDay(request, eventsGuestMandatory) && !durationCanFitInPossibleRange) return Arrays.asList();

    // Check if any guest overlaps with our possibleRanges
    ArrayList<String> optionalEventAttendees = new ArrayList<String>(request.getOptionalAttendees());
    Boolean optionalGuestOverlap = checkIfGuestOverlap(listOfEvents, optionalEventAttendees, possibleRanges);
    
    // Only return ranges if 1) no Guest event overlaps with Requested Event and 2) if event is not greater possible event range.
    if (!optionalGuestOverlap && !durationCanFitInPossibleRange) return possibleRanges;

     // Remove all list of events that have optional people.
     ArrayList<String> requestedEventOptionalAttendees = new ArrayList<String>(request.getOptionalAttendees()); 
    
     listOfEvents = removeEventsOfOptionalAttendees(requestedEventOptionalAttendees, listOfEvents);
 
     // Compress Events of only MANDATORY PEOPLE!
     ArrayList<TimeRange> eventsRange = compressEvents(listOfEvents);
 
     // Check to make sure event duration can fit.
     if (cantFitDurationInDay(request, eventsRange)) return Arrays.asList();
 
     // Get possible meeting slots
     return getPossibleRanges(eventsRange);

  }

  public ArrayList<TimeRange> compressEvents(ArrayList<Event> listOfEvents) {
    ArrayList<TimeRange> eventsRange = new ArrayList<TimeRange>();
    // Loop events and combine overlapping events as part of same event
    for (int i = 0; i < listOfEvents.size(); i++) {
      // If overlap we try to extend event.
      if ((i != 0 && listOfEvents.get(i).getWhen().overlaps(listOfEvents.get(i - 1).getWhen())) || (i != 0 && listOfEvents.get(i).getWhen().start() == eventsRange.get(eventsRange.size() - 1).end())) {
        int overlappingEventEndTime = listOfEvents.get(i).getWhen().end();

        int previousEventStartTime = eventsRange.get(eventsRange.size() - 1).start();
        int previousEventEndTime = eventsRange.get(eventsRange.size() - 1).end();

        // We are altering ONLY THE PREVIOUS event! We're simply asking the question: Can I extend this event to the right more?
        if (overlappingEventEndTime > previousEventEndTime) eventsRange.set(eventsRange.size() - 1, TimeRange.fromStartEnd(previousEventStartTime, overlappingEventEndTime, false));
      } else {
        // We create a new event, since this one doesn't overlap!
        eventsRange.add(listOfEvents.get(i).getWhen());
      }
    }
    return eventsRange;
  }

  public ArrayList<TimeRange> getPossibleRanges(ArrayList<TimeRange> eventsRange) {
    ArrayList<TimeRange> possibleRanges = new ArrayList<TimeRange>();

    // Try to get the first range if first meeting doesn't start at 0.
    if (eventsRange.get(0).start() != 0) {
      possibleRanges.add(TimeRange.fromStartEnd(0, eventsRange.get(0).start(), false));
    }

    // Loop through and get ranges.
    //   |-----|   |-----| == Events
    //         |---|       == Range we want to get.
    //
    for (int i = 1; i < eventsRange.size(); i++) {
      int nextEventStartTime = eventsRange.get(i).start();
      int lastEventEndTime = eventsRange.get(i - 1).end();
      possibleRanges.add(TimeRange.fromStartEnd(lastEventEndTime, nextEventStartTime, false));
    }

    // Try to get the last range if last event doesn't end at 1440.
    if (eventsRange.get(eventsRange.size() - 1).end() != 1440) {
      int lastEventEndTime = eventsRange.get(eventsRange.size() - 1).end();
      possibleRanges.add(TimeRange.fromStartEnd(lastEventEndTime, 1440, false));
    }

    return possibleRanges;
  }

  public Boolean checkNoMandatoryAttendees(MeetingRequest request, ArrayList<Event> listOfEvents) {
    ArrayList<String> requestedEventAttendees = new ArrayList<String>(request.getAttendees()); 

    Boolean noMatchingAttendeesForRequestedEvent = true;

    for (int i = 0; i < listOfEvents.size(); i++) {
      Set<String> eventAttendees = listOfEvents.get(i).getAttendees();

      for (String nameInEvent : eventAttendees) {
        for (String nameInRequestedEvent : requestedEventAttendees) {
          if (nameInEvent == nameInRequestedEvent) {
            noMatchingAttendeesForRequestedEvent = false;
            break;
          }
        }
        // Bubble answer up
        if (!noMatchingAttendeesForRequestedEvent) break;
      }
      // Bubble answer up
      if (!noMatchingAttendeesForRequestedEvent) break;
    }

    // If the meeting requested has no members from any other meetings during the day then that meeting can occur any day.
    return noMatchingAttendeesForRequestedEvent;
  }

  public Boolean cantFitDurationInDay(MeetingRequest request, ArrayList<TimeRange> eventsGuestMandatory) {
    int currentEventMeetingDuration = 0;
    for (int i = 0; i < eventsGuestMandatory.size(); i++) {
      currentEventMeetingDuration += eventsGuestMandatory.get(i).end() -  eventsGuestMandatory.get(i).start();
    }
  
    if (request.getDuration() + currentEventMeetingDuration > TimeRange.WHOLE_DAY.duration()) return true;
    else return false;
  }

  public Boolean cantFitDurationInPossibleRange(MeetingRequest request, ArrayList<TimeRange> possibleRange) {
    int possibleEventMeetingDuration = 0;
    for (int i = 0; i < possibleRange.size(); i++) {
      possibleEventMeetingDuration += possibleRange.get(i).end() -  possibleRange.get(i).start();
    }
  
    if (request.getDuration() > possibleEventMeetingDuration) return true;
    else return false;
  }

  public Boolean checkIfGuestOverlap(ArrayList<Event> listOfEvents, ArrayList<String> optionalEventAttendees, ArrayList<TimeRange> possibleRanges) {
    for (int i = 0; i < listOfEvents.size(); i++) {
      // Grab event attendees
      Event event = listOfEvents.get(i);
      Set<String> eventNames = event.getAttendees();
      for (String eventName : eventNames) {
        for (String requestedEventName : optionalEventAttendees) {
          if (eventName == requestedEventName) {
            for (TimeRange range : possibleRanges)
            if (event.getWhen().overlaps(range)) return true;
          }
        }
      }
      // If that optional guest event overlaps with our requested event break and true
    }
    return false;
  }

  public ArrayList<Event> removeEventsOfOptionalAttendees(ArrayList<String> requestedEventOptionalAttendees, ArrayList<Event> listOfEvents) {
    int indexShift = 0;
    for (int i = 0; i < listOfEvents.size(); i++) {
      Boolean foundOptionalPersonAttendingEvent = false;

      // If there is any event that contains an optional person from the Meeting Request then delete it from listOfEvents 
      Set<String> eventAttendees = listOfEvents.get(i).getAttendees();
      for (String nameInEvent : eventAttendees) {
        for (String optionalNameInEvent : requestedEventOptionalAttendees) {
          if (nameInEvent == optionalNameInEvent) {
            listOfEvents.remove(i - indexShift);
            foundOptionalPersonAttendingEvent = true;
            indexShift++;
          }

          if (foundOptionalPersonAttendingEvent) break;
        }
      }
    }
    return listOfEvents;
  }
}