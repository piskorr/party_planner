package polsl.pam.partyplanner.dto

class PartyEvent {
    var uid: String
    var owner: String
    var title: String
    var description: String
    var date: String
    var address: String
    var bankInfo: String

    constructor() : this("", "", "", "", "", "", "") {}

    constructor(
        uid: String,
        owner: String,
        title: String,
        description: String,
        date: String,
        address: String,
        bankInfo: String
    ) {
        this.uid = uid
        this.owner = owner
        this.title = title
        this.description = description
        this.date = date
        this.address = address
        this.bankInfo = bankInfo
    }


}