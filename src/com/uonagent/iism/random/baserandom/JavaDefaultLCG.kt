package com.uonagent.iism.random.baserandom

import java.util.*

class JavaDefaultLCG: BaseRandom {
    private val generator = Random()

    override fun next() = generator.nextDouble()
}