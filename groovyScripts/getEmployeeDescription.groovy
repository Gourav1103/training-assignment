import org.apache.ofbiz.party.contact.ContactMechWorker

// Extract the party ID from parameters
partyId = parameters.partyId

// Fetch person data using the party ID
personData = from("Person").where(["partyId": partyId]).queryOne()
context.person = personData

// Configure whether to show old contact mechanisms
showOld = false

// Fetch contact descriptions for the party
contactDescription = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, showOld)

// Iterate through each contact description
contactDescription.each { contact ->
    // Extract contact information
    contactType = contact.contactMech.getString("contactMechTypeId")
    postalAddress = contact.postalAddress
    telecomNumber = contact.telecomNumber

    // Handle email contacts
    if (contactType == "EMAIL_ADDRESS") {
        contact.partyContactMechPurposes.each { purpose ->
            emailPurpose = purpose.getString("contactMechPurposeTypeId")
            if (emailPurpose == "PRIMARY_EMAIL") {
                context.emailPrimaryId = contact.contactMech.getString("infoString")
                context.primaryEmailContactId = contact.contactMech.getString("contactMechId")
            } else {
                context.emailOtherId = contact.contactMech.getString("infoString")
                context.otherEmailContactId = contact.contactMech.getString("contactMechId")
            }
        }
    }

    // Handle postal address
    if (postalAddress) {
        context.postalAddress = postalAddress
    }

    // Handle telecom number
    if (telecomNumber) {
        context.telecomNumber = telecomNumber
    }
}
