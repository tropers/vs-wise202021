package distributedmutex

import middleware.Stub

/**
 * DistributedMutualExclusion interface
 * defines the minimal functionality of a distributed
 * mutual exclusion lock
 */
interface IDistributedMutex {
    fun acquire(stubs: List<Stub>)
    fun release(stubs: List<Stub>)
}
