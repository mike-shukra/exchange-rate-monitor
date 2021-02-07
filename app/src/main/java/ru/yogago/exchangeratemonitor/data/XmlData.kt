package ru.yogago.exchangeratemonitor.data

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root


@Root(name = "ValCurs", strict = false)
class ValCurs @JvmOverloads constructor(
    @field: ElementList(inline = true)
    var valute: List<Valute>? = null
)

@Root(name = "Valute", strict = false)
class Valute  @JvmOverloads constructor(
    @field: Element(name = "NumCode")
    var numCode: String? = null,

    @field: Element(name = "CharCode")
    var charCode: String? = null,

    @field: Element(name = "Nominal")
    var nominal: String? = null,

    @field: Element(name = "Name")
    var name: String? = null,

    @field: Element(name = "Value")
    var value: String? = null
)