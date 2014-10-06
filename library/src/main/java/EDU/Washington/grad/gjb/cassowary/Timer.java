/*
 * Copyright (C) 1988 1999 Greg J. Badros and Alan Borning
 * Copyright (C) 2014 Agens AS
 *
 * Original Smalltalk Implementation by Alan Borning
 * This Java Implementation by Greg J. Badros
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package EDU.Washington.grad.gjb.cassowary;

// Timer, adapted from John P. Russo's C++ Timer class
// LM--Reworked for Java 5+ for more accurate nanosecond timer
public class Timer {
    public Timer() {
        timerIsRunning = false; // Start not yet called.
        elapsedNanoSecs = 0; // No time on timer object yet.
    }

    public void Start() {
        // Stopwatch is now running
        timerIsRunning = true;
        // Look at internal clock and remember reading
        startReading = System.nanoTime();
    }

    public void Stop() {
        timerIsRunning = false; // Stop timer object.
        elapsedNanoSecs += System.nanoTime() - startReading;
    }

    // Clears a Timer of previous elapsed times, so that a new event
    // can be timed.
    public void Reset() {
        timerIsRunning = false; // Start not yet called.
        elapsedNanoSecs = 0; // No time on timer object yet.
    }

    // The data member, "TimerIsRunning" is used to keep track of
    // whether a timer is active, i.e. whether an event is being
    // timed. While we want those using the timer class to know when a
    // timer is active, we do NOT want them to directly access the
    // TimerIsRunning variable. We solve this problem, by making
    // TimerIsRunning private and providing the public "access function"
    // below.

    public boolean IsRunning() {
        return timerIsRunning;
    }

    // This function allows a client to determine the amount of time that has
    // elapsed on a timer object. Note that there are two possibilities:

    // 1) A timer object has been started and stopped. We can detect this
    // case, because the variable "TimerIsRunning" is false.

    // 2) A timer object is "running", i.e. is still in the process of timing
    // an event. It is not expected that this case will occur as frequently
    // as case 1).

    // In either case, this function converts ticks to seconds. Note that
    // since the function TicksPerSecond() returns a value of type double,
    // an implicit type conversion takes place before doing the division
    // required in either case.

    public double ElapsedTime() {
        if (!timerIsRunning) // Normal case
            return (double) elapsedNanoSecs / 1e9;
        else
            return (double) (elapsedNanoSecs + (System.nanoTime() - startReading)) / 1e9;
    }

    private boolean timerIsRunning;
    private long elapsedNanoSecs;
    private long startReading;
}
