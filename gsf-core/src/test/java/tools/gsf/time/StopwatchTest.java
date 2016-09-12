/*
 * Copyright 2016 Function1, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.gsf.time;

import org.junit.Test;

/**
 * @author Tony Field
 * @since 2016-07-19
 */
public class StopwatchTest {

    @Test
    public void noParams() {
        Stopwatch timer2 = LoggerStopwatch.getInstance();
        timer2.start();

        for (int i = 0; i < 25; i++) {
            Stopwatch timer = LoggerStopwatch.getInstance();
            timer.start();
            sleep();
            timer.split("Split 1");
            sleep();
            timer.split("Split 2");
            timer.split("Split 3");
            sleep();
            timer.elapsed("Elapsed 1");
            sleep();
            timer2.split("Loop iteration {} split", i);
            sleep();
        }
        timer2.elapsed("Timer2 split");
    }

    @Test
    public void params() {
        Stopwatch timer2 = LoggerStopwatch.getInstance();
        timer2.start();

        for (int i = 0; i < 25; i++) {
            Stopwatch timer = LoggerStopwatch.getInstance();
            timer.start();
            sleep();
            timer.split("Split 1 {}", "PARAM1");
            sleep();
            timer.split("Split 2 {} {}", "PARAM1", "param2");
            timer.split("Split 3 {} {} {}", "param1", "PARAM2", "pArAm3");
            sleep();
            timer.elapsed("Elapsed 1 {}", "PARAM");
            sleep();
            timer2.split("Loop iteration {} split", i);
            sleep();
        }
        timer2.elapsed("Timer2 split");
    }

    @Test
    public void fast() {
        Stopwatch timer2 = LoggerStopwatch.getInstance();
        timer2.start();

        for (int i = 0; i < 25; i++) {
            Stopwatch timer = LoggerStopwatch.getInstance();
            timer.start();
            timer.split("Split 1");
            timer.split("Split 2");
            timer.split("Split 3");
            timer.elapsed("Elapsed 1");
            timer2.split("Loop iteration {} split", i);
        }
        timer2.elapsed("Timer2 split");
    }


    private void sleep() {
        try {
            Thread.sleep(12);
        } catch (InterruptedException e) {

        }
    }
}
