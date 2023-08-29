import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.entity.util.EntityQuery

import java.sql.Timestamp

// Define conditions
partyCondition1 = EntityCondition.makeCondition("partyIdFrom", "Bank")
partyCondition2 = EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE")

// Combine conditions
combinedCondition = EntityCondition.makeCondition([partyCondition1, partyCondition2], EntityOperator.AND)

// Fetch related parties
partyMap = ["partyIdFrom": "Bank", "roleTypeIdTo": "EMPLOYEE"]
relatedPartiesResult = runService("getRelatedParties", partyMap)
relatedPartyIds = relatedPartiesResult.relatedPartyIdList

// Collect all IDs except the first one
remainingPartyIds = relatedPartyIds.drop(1)

// Create a list of conditions for each related party
partyConditions = remainingPartyIds.collect { partyId ->
    EntityCondition.makeCondition("partyId", partyId)
}

// Combine party conditions with OR operator
finalCondition = EntityCondition.makeCondition(partyConditions, EntityOperator.OR)

// Fetch employee data using EntityQuery
personData = EntityQuery.use(delegator)
        .from("Person")
        .where(finalCondition)
        .queryList()

// Store employee data in the context
context.employeesData = personData



