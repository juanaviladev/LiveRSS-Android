package es.juanavila.liverss.presentation.common


/*
 *  Copyright 2017 Google Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


/**
 * A lifecycle-aware observable that sends only new updates after subscription, used for events like
 * navigation and Snackbar messages.
 *
 *
 * This avoids a common problem with events: on configuration change (like rotation) an update
 * can be emitted if the observer is active. This LiveData only calls the observable if there's an
 * explicit call to setValue() or call().
 *
 *
 * Note that only one observer is going to be notified of changes.
 */
class BufferedLiveEvent<T> : MutableLiveData<T?>() {
    private val mPending = AtomicBoolean(false)
    private val values: Queue<T> = LinkedList()

    private var isActive: Boolean = false
    private var observedAtLeastOneTime: Boolean = false

    override fun onActive() {
        isActive = true
        while (values.isNotEmpty() && isActive) {
            value = values.poll()
        }
    }

    override fun onInactive() {
        isActive = false
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {
        observedAtLeastOneTime = true

        // Observe the internal MutableLiveData
        super.observe(owner, Observer { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(value: T?) {
        mPending.set(true)
        if (isActive) {
            super.setValue(value)
        } else if(observedAtLeastOneTime) {
            values.add(value)
        }
    }

    fun clear() {
        values.clear()
    }

    companion object {
        private const val TAG = "BufferedLiveEvent"
    }
}