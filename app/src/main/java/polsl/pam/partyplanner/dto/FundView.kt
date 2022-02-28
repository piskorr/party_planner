package polsl.pam.partyplanner.dto

class FundView {
    var fundTitle: String
    var fundValue: String

    constructor() : this("", "") {}
    constructor(fundTitle: String, fundValue: String) {
        this.fundTitle = fundTitle
        this.fundValue = fundValue
    }
}