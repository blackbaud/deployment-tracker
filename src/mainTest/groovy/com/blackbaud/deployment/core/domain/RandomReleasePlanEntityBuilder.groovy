package com.blackbaud.deployment.core.domain
import static com.blackbaud.deployment.api.ClientARandom.aRandom

class RandomReleasePlanEntityBuilder extends ReleasePlanEntity.ReleasePlanEntityBuilder{

    public RandomReleasePlanEntityBuilder() {
        id(aRandom.id())
        .created(aRandom.zonedDateTime())
        .closed(aRandom.zonedDateTime())
        .notes(aRandom.text(20))
    }
}
