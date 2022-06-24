package jp.mydns.kokoichi0206.firstapp

// Special function
fun main() {
    // TODO: Quit my job

    /**
     * Excellent hi!
     */
    print("Hi")

    var name: String? = "me"
    // String template
    name?.let {
        print("I am $name and length is ${name.length}")
    }

    val nonNullableName = name ?: "John Doe"

    whenExpression()

}

fun addOne(a: Int) : Int {
    return a + 1
}

fun loopExamples() {
    var x = 1
    while (x < 100) {
        print("$x")
        x++
    }
    println()
    println("loop is done")

    println("-----")
    for (num in 1..120) {
        print("$num")
    }
    println("-----")
    for (i in 1 until 10) {
        print("$i ")
    }
    println("-----")
    for (i in 10 downTo 1 step 2) {
        print("$i ")
    }
    for (i in 10.downTo(1).step(2)) {
        print("$i ")
    }
}

fun whenExpression() {
    val age = 123
    when (age) {
        !in 0..20 -> println("you are adult")
        in 18..20 -> println("you are g")
    }

    var y: Any = 32
    when (y) {
        is Int -> println("$y is an Int")
        is Double -> println("$y is Double")
        else -> println("$y is pieen")
    }
}

fun variableType() {
    // Int
    val age = 31
    // 8 bit
    val myByte: Byte = 13
    // 16 bit
    val myShort: Short = 1234
    // 32 bit
    val myInt: Int = 1234678890
    // 64 bit
    val myLong: Long = 123

    val myFloat: Float = 13.231F
    val myDouble: Double = 13.232324

    // Characters, only one word!
    val letterChar = 'A'
    val myString = "Aaie"
}
