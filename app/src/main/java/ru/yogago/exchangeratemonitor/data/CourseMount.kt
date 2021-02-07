package ru.yogago.exchangeratemonitor.data

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "ValCurs", strict = false)
class CourseMount @JvmOverloads constructor(
    @field: ElementList(inline = true)
    var valute: List<Record>? = null
)

@Root(name = "Record", strict = false)
class Record @JvmOverloads constructor(
    @field: Element(name = "Nominal")
    var nominal: String? = null,
    @field: Element(name = "Value")
    var value: String? = null
)