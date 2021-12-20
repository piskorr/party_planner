package polsl.pam.partyplanner.dto

class Event {
    lateinit var uid: String
    lateinit var owner : String
    lateinit var title : String
    lateinit var description : String
    lateinit var theme : String
    lateinit var address : String

    constructor(){}

    constructor(uid: String, owner: String, title: String, description: String, theme: String, address: String) {
        this.uid = uid
        this.owner = owner
        this.title = title
        this.description = description
        this.theme = theme
        this.address = address
    }
}