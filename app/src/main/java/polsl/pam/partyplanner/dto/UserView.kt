package polsl.pam.partyplanner.dto

class UserView {
    lateinit var login: String
    lateinit var fullName: String

    constructor(){

    }

    constructor(login: String, fullName: String) {
        this.login = login
        this.fullName = fullName
    }


}
