package com.iamkamrul.dateced

private typealias SimpleDateFormatPattern = String

internal object DateCedPattern {
    fun matchPattern(input:String):SimpleDateFormatPattern{
        patterns.forEach {
            if (it.first.matches(input)) return it.second
        }
        throw IllegalArgumentException("Error Occurred! $input , Format incorrect")
    }

}

private val patterns = listOf(
    Pair(first = """^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$""".toRegex(), second = "yyyy-MM-dd HH:mm:ss"),
    Pair(first = """^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$""".toRegex(), second = "yyyy-MM-dd HH:mm"),
    Pair(first = """^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2} (AM|PM)$""".toRegex(), second = "yyyy-MM-dd hh:mm:ss aa"),
    Pair(first = """^\d{4}-\d{2}-\d{2} \d{2}:\d{2} (AM|PM)$""".toRegex(), second = "yyyy-MM-dd hh:mm aa"),

    Pair(first = """^\d{4}-\d{2}-\d{2}$""".toRegex(), second = "yyyy-MM-dd"),
    Pair(first = """^\d{2}-\d{2}-\d{4}$""".toRegex(), second = "dd-MM-yyyy"),

    Pair(first = """^\d{2}-\d{2}-\d{4} \d{2}:\d{2}:\d{2}$""".toRegex(), second = "dd-MM-yyyy HH:mm:ss"),
    Pair(first = """^\d{2}-\d{2}-\d{4} \d{2}:\d{2}$""".toRegex(), second = "dd-MM-yyyy HH:mm"),
    Pair(first = """^\d{2}-\d{2}-\d{4} \d{2}:\d{2}:\d{2} (AM|PM)$""".toRegex(), second = "dd-MM-yyyy hh:mm:ss aa"),
    Pair(first = """^\d{2}-\d{2}-\d{4} \d{2}:\d{2} (AM|PM)$""".toRegex(), second = "dd-MM-yyyy hh:mm aa"),

    Pair(first = """^\d{2} (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) \d{4}$""".toRegex(), second = "dd MMM yyyy"),
    Pair(first = """^\d{2} (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) \d{4} \d{2}:\d{2}:\d{2} (AM|PM)$""".toRegex(), second = "dd MMM yyyy hh:mm:ss aa"),
    Pair(first = """^\d{2} (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) \d{4} \d{2}:\d{2} (AM|PM)$""".toRegex(), second = "dd MMM yyyy hh:mm aa"),

    Pair(first = """^\d{2} (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) \d{4} \d{2}:\d{2}:\d{2}$""".toRegex(), second = "dd MMM yyyy HH:mm:ss"),
    Pair(first = """^\d{2} (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) \d{4} \d{2}:\d{2}$""".toRegex(), second = "dd MMM yyyy HH:mm"),


    Pair(first = """^\d{2}:\d{2}:\d{2}$""".toRegex(), second = "HH:mm:ss"),
    Pair(first = """^\d{2}:\d{2}$""".toRegex(), second = "HH:mm"),

    Pair(first ="""^\d{2}:\d{2}:\d{2} (AM|PM)$""".toRegex(), second = "hh:mm:ss aa"),
    Pair(first ="""^\d{2}:\d{2} (AM|PM)$""".toRegex(), second = "hh:mm aa"),

    Pair(first = """^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}Z$""".toRegex(), second = "yyyy-MM-dd'T'HH:mm:ss'Z'"),
    Pair(first = """^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z$""".toRegex(), second = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
    Pair(first = """^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{6}Z$""".toRegex(), second = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"),
)
