/*******************************************************************************
 * Copyright 2013-2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nntool.qos.testserver.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import at.alladin.nntool.qos.testserver.ServerPreferences.TestServerServiceEnum;
import at.alladin.nntool.qos.testserver.util.TestServerConsole;

/**
 * @author lb
 */
public abstract class AbstractJob<R> implements Runnable {

    /**
     *
     */
    protected final AtomicBoolean isRunning = new AtomicBoolean(true);
    /**
     *
     */
    protected final AtomicReference<JobState> state = new AtomicReference<AbstractJob.JobState>(JobState.INIT);
    /**
     *
     */
    protected final ConcurrentHashMap<JobState, JobCallback> callbackMap = new ConcurrentHashMap<>();
    /**
     *
     */
    protected final TestServerServiceEnum service;
    /**
     *
     */
    protected R result = null;

    /**
     * @param service
     */
    public AbstractJob(TestServerServiceEnum service) {
        this.service = service;
    }

    /**
     * the main execution procedure, a result can be returned
     * @throws Exception
     */
    public abstract R execute() throws Exception;

    /**
     * needed to create a new instance of s service
     *
     * @return
     */
    public abstract AbstractJob<R> getNewInstance();

    /**
     * id used by the service manager to maintain a service map
     *
     * @return
     */
    public abstract String getId();

    /**
     *
     */
    public synchronized void stop() {
        log("STOPPING SERVICE!", 0);
        isRunning.set(false);
        dispatchEvent(JobState.STOP, result);
    }

    /**
     *
     */
    public void start() {
        isRunning.set(true);
        dispatchEvent(JobState.START, result);
    }

    /**
     *
     */
    public void interrupt() {
        Thread.currentThread().interrupt();
    }

    /**
     * @return
     */
    public synchronized R getResult() {
        while (isRunning.get());

        return result;
    }

    /**
     * @param result
     */
    public synchronized void setResult(R result) {
        this.result = result;
    }

    /**
     * @param msg
     * @param verboseLevelNeeded
     */
    public void log(String msg, int verboseLevelNeeded) {
        TestServerConsole.log("[" + getService().getName() + "]: " + msg, verboseLevelNeeded, getService());
    }

    /**
     * @param t
     * @param verboseLevelNeeded
     */
    public void error(Throwable t, int verboseLevelNeeded) {
        TestServerConsole.error("[" + getService().getName() + "]", t, verboseLevelNeeded, getService());
    }

    /**
     * @return
     */
    public TestServerServiceEnum getService() {
        return service;
    }

    /**
     * @return
     */
    public JobState getState() {
        return state.get();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AbstractJob [isRunning=" + isRunning + ", state=" + state
                + ", callbackMap=" + callbackMap + ", service=" + service
                + ", result=" + result + "]";
    }

    /**
     * @return
     */
    public boolean getIsRunning() {
        return isRunning.get();
    }

    /**
     * @param onState
     * @param callback
     */
    public void setCallback(JobState onState, JobCallback callback) {
        callbackMap.put(onState, callback);
    }

    /**
     * @param newState
     * @param res
     */
    public void dispatchEvent(JobState newState, R res) {
        state.set(newState);

        JobCallback callback = callbackMap.get(newState);
        if (callback != null) {
            callback.onEvent(this, newState, res);
        }
    }

    /**
     * job states
     *
     * @author lb
     */
    public static enum JobState {
        INIT,
        START,
        RUN,
        STOP,
        ERROR
    }
}
