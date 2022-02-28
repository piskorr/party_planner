package polsl.pam.partyplanner.dto

class InviteView {
    var itemId: String
    var itemType: String

    constructor() : this("","") {}
    constructor(itemId: String, itemType: String) {
        this.itemId = itemId
        this.itemType = itemType
    }


}