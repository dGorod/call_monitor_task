package com.dgorod.callmonitor.data

/**
 * Singleton object to store calls queried counts since app process start.
 */
object CallsQueryStorage {
    private val storage = mutableMapOf<Long, Int>()

    /**
     * Gets call queried amount. Increments it for every query.
     *
     * @param key unique ID of a call.
     * @return times queried.
     */
    fun getQueryCount(key: Long): Int {
        var count = storage[key] ?: 0
        storage[key] = ++count
        return count
    }

    /**
     * Drops all storage.
     */
    fun clear() {
        storage.clear()
    }
}