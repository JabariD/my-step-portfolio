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


public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // If: 1) No attendees. OR 2) There are no events currently on calendar and the meeting duration is less than whole day. Therefore whole day is open for that meeting.
    if (request.getAttendees().isEmpty() || (events.isEmpty() && request.getDuration() <= TimeRange.WHOLE_DAY.duration())) return Arrays.asList(TimeRange.WHOLE_DAY);

    // Check that duration of meeting is within boundaries of a day
    else if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) return Arrays.asList();

    // Convert Collection to ArrayList
    ArrayList<Event> listOfEvents = new ArrayList<Event>(events);
    
    //  Check if we have no attendees. If we have just 1 person attending any event from any part of the day, then we cannot return the whole day.
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
    if (noMatchingAttendeesForRequestedEvent) return Arrays.asList(TimeRange.WHOLE_DAY);


    // Compress Events 
    ArrayList<TimeRange> eventsRange = compressEvents(listOfEvents);

    // Check to make sure event duration can fit.
    int currentEventMeetingDuration = 0;
    for (int i = 0; i < eventsRange.size(); i++) {
      currentEventMeetingDuration += eventsRange.get(i).end() -  eventsRange.get(i).start();
    }
  
    if (request.getDuration() + currentEventMeetingDuration > TimeRange.WHOLE_DAY.duration()) return Arrays.asList();

    // Get possible meeting slots
    return getPossibleRanges(eventsRange);
  }

  public ArrayList<TimeRange> compressEvents(ArrayList<Event> listOfEvents) {
    ArrayList<TimeRange> eventsRange = new ArrayList<TimeRange>();
    // Loop events and combine overlapping events as part of same event
    for (int i = 0; i < listOfEvents.size(); i++) {
      // If overlap we try to extend event.
      if (i != 0 && listOfEvents.get(i).getWhen().overlaps(listOfEvents.get(i - 1).getWhen())) {
        int overlappingEventEndTime = listOfEvents.get(i).getWhen().end();

        int previousEventStartTime = listOfEvents.get(i - 1).getWhen().start();
        int previousEventEndTime = listOfEvents.get(i - 1).getWhen().end();

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
    for (int i = 1; i < eventsRange.size(); i = i + 2) {
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
}