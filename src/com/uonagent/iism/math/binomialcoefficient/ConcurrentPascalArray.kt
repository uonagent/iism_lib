package com.uonagent.iism.math.binomialcoefficient

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinTask
import java.util.concurrent.Future

private const val MAX_N = 66
private const val ARRAY_SIZE = 1024
private const val N_EXCEPTION = "In this type of model the N parameter cannot be greater than $MAX_N"

object ConcurrentPascalArray : BCConcurrentModel {

    private lateinit var futureZero: Future<Long>
    private lateinit var futureOne: Future<Long>

    private val ex = Executors.newSingleThreadExecutor()

    init {
        val e = Executors.newSingleThreadExecutor()
        futureZero = e.submit(Callable { 1L })
        e.shutdown()
    }

    private val array = Array<Future<Long>?>(ARRAY_SIZE) { null }

    override fun get(n: Int, k: Int): Future<Long>? {
        if (n > MAX_N) {
            throw IllegalArgumentException(N_EXCEPTION)
        }
        val index = getIndex(n, k)
        return when (k) {
            0 -> futureZero
            1, n - 1 -> ex.submit(Callable { n.toLong() })
            else -> array[index]
        }
    }

    override fun set(n: Int, k: Int, v: Future<Long>) {
        val index = getIndex(n, k)
        array[index] = v
    }

    private fun getModelK(n: Int, k: Int) = if (k > n / 2) n - k else k

    private fun getIndex(n: Int, k: Int): Int {
        val modelK = getModelK(n, k)
        val m = n - 1
        return (m / 2 - 1) * (m / 2 - (m + 1) % 2) + modelK - 2
    }
}