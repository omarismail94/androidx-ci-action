/*
 * Copyright 2021 The Android Open Source Project
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

package dev.androidx.ci.util

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Suppress("UNCHECKED_CAST")
class LazyComputedValue<T>(
    val compute: suspend () -> T
) {
    private var computed: Any? = NOT_COMPUTED
    private val mutex = Mutex()
    suspend fun get(): T {
        if (computed !== NOT_COMPUTED) {
            return computed as T
        }
        mutex.withLock {
            if (computed !== NOT_COMPUTED) {
                return computed as T
            }
            return compute().also {
                computed = it
            }
        }
    }

    companion object {
        private val NOT_COMPUTED = Any()
    }
}