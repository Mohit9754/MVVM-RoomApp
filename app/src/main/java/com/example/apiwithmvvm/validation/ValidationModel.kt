package com.example.apiwithmvvm.validation


class ValidationModel{

    var type: Validation.Type? = null
    var data: String = ""
    var isProfileSelected:Boolean? = null
    var tag: String? = null

    constructor(type:Validation.Type?,data:String,tag:String? = null) {

        this.type = type
        this.data = data
        this.tag = tag
    }

    constructor(type:Validation.Type?,isProfileSelected:Boolean?,tag:String? = null) {

        this.type = type
        this.isProfileSelected = isProfileSelected
        this.tag = tag
    }

}