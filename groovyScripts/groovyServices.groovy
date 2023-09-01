import org.apache.ofbiz.base.util.UtilMisc
import java.sql.Timestamp

// Function to create a party role and associated contact mechanisms
Map<String, Object> createPartyRoleAndContactMech() {
    try {
        // Call the OFBiz service to create the person role and contact mechanisms
        result = runService("createPersonRoleAndContactMechs", parameters)

        // Check if the service call was successful
        partyId = result.get("partyId")

        // Prepare data for creating a party relationship and role
        relationshipData = [
                "partyIdTo": partyId.toString(),
                "partyIdFrom": "Bank",
                "roleTypeIdTo": parameters.get("roleTypeId").toString(),
                "roleTypeIdFrom": "ORGANIZATION_ROLE",
                "partyRelationshipTypeId": "EMPLOYMENT"
        ]

        // Call the OFBiz service to create a party relationship and role
        res = runService("createPartyRelationshipAndRole", relationshipData)

        // Check if the service call was successful
        return res

    } catch (Exception e) {
        // Log and handle the error
        println("\nAn error occurred: ${e.message}")
        return UtilMisc.toMap("error": "An error occurred while creating the party.")
    }
}

// Function to delete a party role and associated contact mechanisms
Map<String, Object> deletePartyRoleAndContactMech() {
    try {
        // Fetch the fromDate from the PartyRelationship
        fromDate = from("PartyRelationship")
                .where("partyIdTo", parameters.partyIdTo, "partyIdFrom", "Bank")
                .queryOne()
                .fromDate

        thruDate = new Timestamp(System.currentTimeMillis());
        // Prepare data for deleting the party relationship
        relationshipData = [
                "partyIdTo": parameters.partyIdTo,
                "partyIdFrom": "Bank",
                "roleTypeIdTo": parameters.roleTypeIdTo,
                "roleTypeIdFrom": parameters.roleTypeIdFrom,
                "fromDate": fromDate,
                "thruDate":thruDate
        ]

        // Call the OFBiz service to delete the party relationship
        runService("updatePartyRelationship", relationshipData)

    } catch (Exception e) {
        // Log and handle the error
        println("\nAn error occurred: ${e.message}")
        return UtilMisc.toMap("error": "An error occurred while deleting the party relationship.")
    }
}

