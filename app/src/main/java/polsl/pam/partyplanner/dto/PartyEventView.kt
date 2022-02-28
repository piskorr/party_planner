package polsl.pam.partyplanner.dto

class PartyEventView {
    var title: String
    var date: String
    var uid: String

    constructor() : this("", "", "") {}

    constructor(
        title: String,
        date: String,
        uid: String,
    ) {
        this.title = title
        this.date = date
        this.uid = uid
    }
}