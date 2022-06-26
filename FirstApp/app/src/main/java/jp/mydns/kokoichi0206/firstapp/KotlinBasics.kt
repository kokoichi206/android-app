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

    val example = ExampleClass()
    example.speed = 3

    val user = User(1, "me")
    val updatedUser = user.copy(name = "new name")
    val (a, b) = updatedUser    // おぉ

    val person = Person(80.0F)

    // safe type cast
    val obj: Any = 1031
    val strObj: String? = obj as? String

    // array
    val numbers = arrayOf(1, 2, 3, 4, 5, 6)
    print(numbers.contentToString())

    // list
    val users = listOf(
        User(1, "a"),
        User(2, "b"),
        User(3, "c"),
        User(4, "d"),
        User(5, "e"),
    )
    // lists have many properties
    val anyTypesList = listOf(1, 2, true, "string value", User(1, "hoge"))

    val mutableUsers = users.toMutableList()
    val newUsers = arrayOf(User(6, "newbees"))
    mutableUsers.addAll(newUsers)
    mutableUsers.removeAt(4)
    println(mutableUsers)
    // This causes the 'IndexOutOfBoundsException'
    // mutableUsers.removeAt(41)
    mutableUsers.sortWith(compareBy<User> { it.name }.thenBy { it.id })

    // map
    val daysOfWeek = mapOf(
        1 to "Monday",
        2 to "Tuesday",
        3 to "Wednesday",
    )
    for (key in daysOfWeek.keys) {
        println("$key is mapped to ${daysOfWeek[key]}")
    }

    // arraylists
    val arrayList = ArrayList<String>()
    val itr = arrayList.iterator()
    while (itr.hasNext()) {
        println(itr.next())
    }

    // Lambda
//    val sum: (Int, Int) -> Int = {a: Int, b: Int -> a + b}
//    println(sum(10, 5))
    val sum = { a: Int, b: Int -> println(a + b) }
}

interface Drivable {
    // interface can define member variables...?
    val maxSpeed: Double
    fun drive(): String

    // interface can define function with a concrete impl...?
    fun brake() {
        println("Braking !!!")
    }
}

open class Animal(
    val life: Float,
) {
    fun eat() {
    }
}

class Person(
    life: Float,
) : Animal(
    life = life,
) {
}

data class User(
    val id: Int,
    val name: String,
)

class ExampleClass() {
    var speed: Int = 100
        get() = field
        set(value) {
            field = if (value > 0) value else throw IllegalArgumentException("Not sutable")
        }
}

fun addOne(a: Int): Int {
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
