package com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview


import com.google.gson.annotations.SerializedName

data class FurnishDetails(
    @SerializedName("A/C")
    var aC: String = "0",
    @SerializedName("Artwork")
    var artwork: String = "0",
    @SerializedName("Bed Frames")
    var bedFrames: String = "0",
    @SerializedName("Blinds")
    var blinds: String = "0",
    @SerializedName("Carpets")
    var carpets: String = "0",
    @SerializedName("Chairs")
    var chairs: String = "0",
    @SerializedName("Curtains")
    var curtains: String = "0",
    @SerializedName("Cutlery")
    var cutlery: String = "0",
    @SerializedName("Dishes")
    var dishes: String = "0",
    @SerializedName("Dishwashers")
    var dishwashers: String = "0",
    @SerializedName("Dressers")
    var dressers: String = "0",
    @SerializedName("Fridge")
    var fridge: String = "0",
    @SerializedName("Lamps")
    var lamps: String = "0",
    @SerializedName("Lighting Fixtures")
    var lightingFixtures: String = "0",
    @SerializedName("Mattresses")
    var mattresses: String = "0",
    @SerializedName("Microwaves")
    var microwaves: String = "0",
    @SerializedName("Mirrors")
    var mirrors: String = "0",
    @SerializedName("Ovens")
    var ovens: String = "0",
    @SerializedName("Pans")
    var pans: String = "0",
    @SerializedName("Pots")
    var pots: String = "0",
    @SerializedName("Rugs")
    var rugs: String = "0",
    @SerializedName("Sofa")
    var sofa: String = "0",
    @SerializedName("Stoves")
    var stoves: String = "0",
    @SerializedName("TV")
    var tV: String = "0",
    @SerializedName("Tables")
    var tables: String = "0",
    @SerializedName("Utensils")
    var utensils: String = "0",
    @SerializedName("Vases")
    var vases: String = "0",
    @SerializedName("Wall Sconces")
    var wallSconces: String = "0",
    @SerializedName("Wardrobes")
    var wardrobes: String = "0",
    @SerializedName("Washing Machine")
    var washingMachine: String = "0"
)